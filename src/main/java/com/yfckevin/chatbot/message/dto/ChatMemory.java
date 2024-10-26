package com.yfckevin.chatbot.message.dto;

import lombok.Data;
import org.springframework.data.annotation.Transient;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

import java.util.List;

@Node("Document")
@Data
public class ChatMemory {
    @Id
    private String id;
    @Transient
    private List<Double> embedding;
    private String text;
    @Property("metadata.messageType")
    private String messageType;
    @Property("metadata.conversationId")
    private String conversationId;
    @Property("metadata.projectName")
    private String projectName;
    @Property("metadata.memberId")
    private String memberId;
    @Property("metadata.chatChannel")
    private String chatChannel;

    // for inventory
    @Property("metadata.inventory_id")
    private String inventoryId;
    @Property("metadata.type")
    private String type;
    @Property("metadata.creationDate")
    private String creationDate;
}
