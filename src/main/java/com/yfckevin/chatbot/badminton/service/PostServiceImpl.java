package com.yfckevin.chatbot.badminton.service;

import com.yfckevin.chatbot.ConfigProperties;
import com.yfckevin.chatbot.badminton.dto.PostDTO;
import com.yfckevin.chatbot.badminton.entity.Court;
import com.yfckevin.chatbot.badminton.entity.Post;
import com.yfckevin.chatbot.entity.Mapping;
import com.yfckevin.chatbot.repository.MappingRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

import static com.yfckevin.chatbot.GlobalConstants.*;

@Service
public class PostServiceImpl implements PostService{
    private final MappingRepository mappingRepository;
    private final MongoTemplate badmintonMongoTemplate;
    private final ConfigProperties configProperties;
    private final EmbeddingModel embeddingModel;
    private final DateTimeFormatter ddf;

    public PostServiceImpl(MappingRepository mappingRepository, @Qualifier("badmintonMongoTemplate") MongoTemplate badmintonMongoTemplate, ConfigProperties configProperties, EmbeddingModel embeddingModel, DateTimeFormatter ddf) {
        this.mappingRepository = mappingRepository;
        this.badmintonMongoTemplate = badmintonMongoTemplate;
        this.configProperties = configProperties;
        this.embeddingModel = embeddingModel;
        this.ddf = ddf;
    }

    @Override
    public List<Document> dailyImportPosts() {
        final List<String> mappedIdList = mappingRepository.findByDbUri(BADMINTON_MONGO_URI).stream()
                .map(Mapping::getMappedId).toList();

        final String today = LocalDate.now().atStartOfDay().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        //取得場館資訊 (postId, 城市名和地區)
        final Map<String, String> courtNameAndAddressMap = badmintonMongoTemplate.find(new Query(), Court.class)
                .stream()
                .collect(Collectors.toMap(Court::getPostId, court -> court.getCity().getLabel() + "市" + court.getDistrict().getLabel() + "區"));

        //取得零打貼文資訊
        Query postQuery = new Query();
        postQuery.addCriteria(
                Criteria.where("deletionDate").is(null)
                        .and("startTime").gte(today)
        );

        final List<Post> filterPostList = badmintonMongoTemplate.find(postQuery, Post.class)
                .stream()
                .filter(post -> !mappedIdList.contains(post.getId()))
                .map(post -> constructDateTime(post, courtNameAndAddressMap))
                .limit(30)
                .toList();

        final List<PostDTO> productDTOList = filterPostList.stream().map(post -> {
            PostDTO dto = new PostDTO();
            dto.setId(post.getId());
            String detailAddress = "";
            if (StringUtils.isNotBlank(post.getAddress())) {
                detailAddress = "(" + post.getAddress() + ")";
            }
            dto.setContent(String.format("團主：%s，球館：%s，打球日期時間：%s，費用：%s元，程度：%s，冷氣標示：%s，資訊卡連結：%s",
                    post.getName(),
                    post.getPlace() + detailAddress,
                    post.getTime(),
                    post.getFee(),
                    post.getLevel(),
                    post.getAirConditioner().getLabel(),
                    configProperties.getBadmintonDomain() + "card/" + post.getId()
            ));
            return dto;
        }).toList();

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("type", BADMINTON_POST_METADATA_TYPE);
        metadata.put("creation_date", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        List<Document> productDocs = productDTOList.stream().map(
                product -> {
                    metadata.put("post_id", product.getId());
                    final Document document = new Document(product.getContent(), metadata);
                    final float[] embedded = embeddingModel.embed(document);
                    document.setEmbedding(embedded);
                    return document;
                }).toList();

        List<Mapping> mappingList = new ArrayList<>();
        productDTOList.stream().map(PostDTO::getId).toList()
                .forEach(postId -> {
                    Mapping mapping = new Mapping();
                    mapping.setMappedId(postId);
                    mapping.setDbUri(BADMINTON_MONGO_URI);
                    mapping.setCollectionName(BADMINTON_POST_COLLECTION_NAME);
                    mappingList.add(mapping);
                });
        mappingRepository.saveAll(mappingList);

        return productDocs;
    }


    private Post constructDateTime (Post post, Map<String, String> courtNameAndAddressMap){

        if (post.getStartTime() != null && post.getEndTime() != null) {
            LocalDateTime startDateTime = LocalDateTime.parse(post.getStartTime(), ddf);
            LocalDateTime endDateTime = LocalDateTime.parse(post.getEndTime(), ddf);

            // 取得星期
            DayOfWeek dayOfWeek = startDateTime.getDayOfWeek();
            // 格式化星期
            String dayOfWeekFormatted = dayOfWeek.getDisplayName(TextStyle.FULL, Locale.TAIWAN);
            post.setDayOfWeek(dayOfWeekFormatted);
            final String formattedStartDate = startDateTime.format(DateTimeFormatter.ofPattern("MM/dd"));
            final String formattedStartTime = startDateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
            final String formattedEndTime = endDateTime.format(DateTimeFormatter.ofPattern("HH:mm"));

            post.setTime(formattedStartDate + "(" + dayOfWeekFormatted + ") " + formattedStartTime + " - " + formattedEndTime + " (" + formatDuration(post.getDuration()) + "h)");

            for (Map.Entry<String, String> entry : courtNameAndAddressMap.entrySet()) {
                if (entry.getKey().contains(post.getId())) {
                    post.setAddress(entry.getValue());
                    break;
                }
            }
        }

        return post;
    }


    public String formatDuration(double duration) {
        double hours = duration / 60;
        if (hours == (int) hours) {
            // 如果是整數
            return String.format("%.0f", hours);
        } else {
            String hoursStr = String.format("%.2f", hours);
            if (hoursStr.endsWith("0")) {
                // 去掉末尾的零
                hoursStr = hoursStr.substring(0, hoursStr.length() - 1);
            }
            return hoursStr;
        }
    }
}
