package com.yfckevin.chatbot.message;

import com.yfckevin.chatbot.Advisors.MyVectorStoreChatMemoryAdvisor;
import com.yfckevin.chatbot.Advisors.TokenUsageLogAdvisor;
import com.yfckevin.chatbot.exception.ResultStatus;
import com.yfckevin.chatbot.message.dto.ChatHistoryDTO;
import com.yfckevin.chatbot.message.dto.ChatMemory;
import com.yfckevin.chatbot.utils.ChatUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequestMapping("/ai")
@RestController
public class ChatController {

    private final MessageService messageService;

    public ChatController(MessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * 一般記憶對話(測試)
     * @return
     */
    @PostMapping("/test/chatHistory")
    public ResponseEntity<?> chat (@RequestBody ChatHistoryDTO historyDTO){
        final String chatChannel = historyDTO.getChatChannel();
        final String memberId = historyDTO.getMemberId();
        final String projectName = historyDTO.getProjectName();
        List<ChatMemory> chatMemoryList = messageService.findByProjectNameAndMemberIdAndChatChannel(projectName, memberId, chatChannel);
        return ResponseEntity.ok(chatMemoryList);
    }


    /**
     * 產生chatChannel
     * @param memberId
     * @return
     */
    @GetMapping("/genChatChannel/{memberId}")
    public ResponseEntity<?> genChatChannel (@PathVariable String memberId){
        ResultStatus resultStatus = new ResultStatus();
        if (StringUtils.isBlank(memberId)) {
            resultStatus.setCode("C001");
            resultStatus.setMessage("查無會員");
        } else {
            resultStatus.setCode("C000");
            resultStatus.setMessage("成功");
            resultStatus.setData(ChatUtil.genChannelNum());
        }
        return ResponseEntity.ok(resultStatus);
    }


    /**
     * 取得會員的單一chatChannel的歷史聊天記錄
     * @param historyDTO
     * @return
     */
    @PostMapping("/chatHistory")
    public ResponseEntity<?> chatHistory (@RequestBody ChatHistoryDTO historyDTO){
        final String chatChannel = historyDTO.getChatChannel();
        final String memberId = historyDTO.getMemberId();
        final String projectName = historyDTO.getProjectName();
        List<ChatMemory> chatMemoryList = messageService.findByProjectNameAndMemberIdAndChatChannel(projectName, memberId, chatChannel);
        return ResponseEntity.ok(chatMemoryList);
    }


    /**
     * 取得該會員所有的歷史聊天記錄
     * @param historyDTO
     * @return
     */
    @PostMapping("/memberChatHistory")
    public ResponseEntity<?> memberChatHistory (@RequestBody ChatHistoryDTO historyDTO){
        final String memberId = historyDTO.getMemberId();
        final String projectName = historyDTO.getProjectName();
        List<ChatMemory> chatMemoryList = messageService.findByProjectNameAndMemberId(projectName, memberId);
        return ResponseEntity.ok(chatMemoryList);
    }
}
