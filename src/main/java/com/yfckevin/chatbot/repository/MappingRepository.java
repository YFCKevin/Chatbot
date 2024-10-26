package com.yfckevin.chatbot.repository;

import com.yfckevin.chatbot.entity.Mapping;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MappingRepository extends MongoRepository<Mapping, String> {
    List<Mapping> findByDbUri(String uri);
}
