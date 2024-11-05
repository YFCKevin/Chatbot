package com.yfckevin.chatbot.advisors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.AdvisedRequest;
import org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.MessageAggregator;
import org.springframework.ai.document.Document;
import org.springframework.ai.model.Content;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Slf4j
public class MyVectorStoreChatMemoryAdvisor extends AbstractChatMemoryAdvisor<VectorStore> {
    private static final String DOCUMENT_METADATA_CONVERSATION_ID = "conversationId";
    private static final String DOCUMENT_METADATA_MESSAGE_TYPE = "messageType";
    private static final String DOCUMENT_METADATA_PROJECT_NAME = "projectName";
    private static final String DOCUMENT_METADATA_MEMBER_ID = "memberId";
    private static final String DOCUMENT_METADATA_CHAT_CHANNEL = "chatChannel";
    private static final String DOCUMENT_METADATA_CREATION_DATE = "creationDate";
    private static final String DEFAULT_SYSTEM_TEXT_ADVISE = """

			Use the long term conversation memory from the LONG_TERM_MEMORY section to provide accurate answers.

			---------------------
			LONG_TERM_MEMORY:
			{long_term_memory}
			---------------------

			""";

    private final String systemTextAdvise;

    public MyVectorStoreChatMemoryAdvisor(VectorStore vectorStore) {
        this(vectorStore, DEFAULT_SYSTEM_TEXT_ADVISE);
    }

    public MyVectorStoreChatMemoryAdvisor(VectorStore vectorStore, String systemTextAdvise) {
        super(vectorStore);
        this.systemTextAdvise = systemTextAdvise;
    }

    public MyVectorStoreChatMemoryAdvisor(VectorStore vectorStore, String defaultConversationId,
                                          int chatHistoryWindowSize) {
        this(vectorStore, defaultConversationId, chatHistoryWindowSize, DEFAULT_SYSTEM_TEXT_ADVISE);
    }

    public MyVectorStoreChatMemoryAdvisor(VectorStore vectorStore, String defaultConversationId,
                                          int chatHistoryWindowSize, String systemTextAdvise) {
        super(vectorStore, defaultConversationId, chatHistoryWindowSize);
        this.systemTextAdvise = systemTextAdvise;
    }

    @Override
    public AdvisedRequest adviseRequest(AdvisedRequest request, Map<String, Object> context) {

        String advisedSystemText = request.systemText() + System.lineSeparator() + this.systemTextAdvise;

        var searchRequest = SearchRequest.query(request.userText())     //根據使用者輸入的文字（request.userText()）創立一個search request，再來使用 SearchRequest.query(...) 查詢與當前對話 ID 匹配的長期記憶
                .withTopK(this.doGetChatMemoryRetrieveSize(context))    //查詢的檔案數量
                .withFilterExpression(DOCUMENT_METADATA_CONVERSATION_ID + "== '" + this.doGetConversationId(context) +"'");
        List<Document> documents = this.getChatMemoryStore().similaritySearch(searchRequest);

        String longTermMemory = documents.stream()
                .map(Content::getContent).sorted().distinct()
                .collect(Collectors.joining(System.lineSeparator()));
        log.info(longTermMemory);

        Map<String, Object> advisedSystemParams = new HashMap<>(request.systemParams());
        advisedSystemParams.put("long_term_memory", longTermMemory);

        AdvisedRequest advisedRequest = AdvisedRequest.from(request)
                .withSystemText(advisedSystemText)
                .withSystemParams(advisedSystemParams)
                .build();

        UserMessage userMessage = new UserMessage(request.userText(), request.media());
        this.getChatMemoryStore().write(toDocuments(List.of(userMessage), this.doGetConversationId(context)));

        return advisedRequest;
    }

    @Override
    public ChatResponse adviseResponse(ChatResponse chatResponse, Map<String, Object> context) {

        List<Message> assistantMessages = chatResponse.getResults().stream().map(g -> (Message) g.getOutput()).toList();

        this.getChatMemoryStore().write(toDocuments(assistantMessages, this.doGetConversationId(context)));

        return chatResponse;
    }

    @Override
    public Flux<ChatResponse> adviseResponse(Flux<ChatResponse> fluxChatResponse, Map<String, Object> context) {

        return new MessageAggregator().aggregate(fluxChatResponse, chatResponse -> {
            List<Message> assistantMessages = chatResponse.getResults()
                    .stream()
                    .map(g -> (Message) g.getOutput())
                    .toList();

            this.getChatMemoryStore().write(toDocuments(assistantMessages, this.doGetConversationId(context)));
        });
    }

    private List<Document> toDocuments(List<Message> messages, String conversationId) {

        String[] chatIdParts = conversationId.split("_");

        String projectName = chatIdParts.length > 0 ? chatIdParts[0] : "default_projectName";
        String memberId = chatIdParts.length > 1 ? chatIdParts[1] : "default_memberId";
        String chatChannel = chatIdParts.length > 2 ? chatIdParts[2] : "default_chatChannel";

        List<Document> docs = messages.stream()
                .filter(m -> m.getMessageType() == MessageType.USER || m.getMessageType() == MessageType.ASSISTANT)
                .map(message -> {
                    var metadata = new HashMap<>(message.getMetadata() != null ? message.getMetadata() : new HashMap<>());

                    metadata.put(DOCUMENT_METADATA_PROJECT_NAME, projectName);
                    metadata.put(DOCUMENT_METADATA_MEMBER_ID, memberId);
                    metadata.put(DOCUMENT_METADATA_CHAT_CHANNEL, chatChannel);
                    metadata.put(DOCUMENT_METADATA_CREATION_DATE, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

                    metadata.put(DOCUMENT_METADATA_CONVERSATION_ID, conversationId);
                    metadata.put(DOCUMENT_METADATA_MESSAGE_TYPE, message.getMessageType().name());

                    return new Document(message.getContent(), metadata);
                })
                .toList();

        return docs;
    }
}
