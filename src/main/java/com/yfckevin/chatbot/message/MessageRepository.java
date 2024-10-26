package com.yfckevin.chatbot.message;

import com.yfckevin.chatbot.message.dto.ChatMemory;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends Neo4jRepository<ChatMemory, String> {

    @Query("MATCH (n) WHERE n.`metadata.type` = $type DELETE n RETURN COUNT(n)")
    int deleteAllBingBaoInventories (@Param(value = "type") String type);
    @Query("MATCH (n) WHERE n.`metadata.conversationId` = $conversationId RETURN n")
    List<ChatMemory> findUserMessages(@Param(value = "conversationId") String conversationId);
    @Query("MATCH (n) WHERE n.`metadata.type` = $type RETURN n")
    List<ChatMemory> findInventoryByType(@Param(value = "type") String type);
    @Query("MATCH (n) WHERE n.`metadata.projectName` = $projectName AND n.`metadata.memberId` = $memberId AND n.`metadata.chatChannel` = $chatChannel RETURN n")
    List<ChatMemory> findByProjectNameAndMemberIdAndChatChannel(@Param(value = "projectName") String projectName, @Param(value = "memberId") String memberId, @Param(value = "chatChannel") String chatChannel);
    @Query("MATCH (n) WHERE n.`metadata.projectName` = $projectName AND n.`metadata.memberId` = $memberId RETURN n")
    List<ChatMemory> findByProjectNameAndMemberId(@Param(value = "projectName") String projectName, @Param(value = "memberId") String memberId);
    @Query("MATCH (n) WHERE n.`metadata.type` = $type RETURN n")
    List<ChatMemory> findPostByType(@Param(value = "type") String type);
}
