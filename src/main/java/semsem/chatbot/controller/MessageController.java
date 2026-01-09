package semsem.chatbot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import semsem.chatbot.model.dto.request.CreateMessageRequest;
import semsem.chatbot.model.dto.response.MessageResponse;
import semsem.chatbot.model.dto.response.MessageStatsResponse;
import semsem.chatbot.model.dto.response.MyApiResponse;
import semsem.chatbot.model.enums.MessageRole;
import semsem.chatbot.service.chat.MessageService;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1/conversations/{conversationId}/messages")
@RequiredArgsConstructor
@Tag(name = "Messages", description = "Message management endpoints")
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    @Operation(summary = "Create a new message in a conversation")
    public ResponseEntity<MyApiResponse<MessageResponse>> createMessage(
            @PathVariable String conversationId,
            @Valid @RequestBody CreateMessageRequest request
    ) {
        MessageResponse response = messageService.createMessage(conversationId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(MyApiResponse.success("Message created successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get all messages in a conversation")
    public ResponseEntity<MyApiResponse<List<MessageResponse>>> getConversationMessages(
            @PathVariable String conversationId
    ) {
        List<MessageResponse> messages = messageService.getConversationMessages(conversationId);
        return ResponseEntity.ok(MyApiResponse.success(messages));
    }

    @GetMapping("/paged")
    @Operation(summary = "Get messages in a conversation with pagination")
    public ResponseEntity<MyApiResponse<Page<MessageResponse>>> getConversationMessagesPaged(
            @PathVariable String conversationId,
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "50") int size
    ) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdAt"));
        Page<MessageResponse> messages = messageService.getConversationMessagesPaged(conversationId, pageRequest);
        return ResponseEntity.ok(MyApiResponse.success(messages));
    }

    @GetMapping("/recent")
    @Operation(summary = "Get recent messages in a conversation")
    public ResponseEntity<MyApiResponse<List<MessageResponse>>> getRecentMessages(
            @PathVariable String conversationId,
            @Parameter(description = "Number of recent messages to retrieve")
            @RequestParam(defaultValue = "10") int limit
    ) {
        List<MessageResponse> messages = messageService.getRecentMessages(conversationId, limit);
        return ResponseEntity.ok(MyApiResponse.success(messages));
    }

    @GetMapping("/last")
    @Operation(summary = "Get the last message in a conversation")
    public ResponseEntity<MyApiResponse<MessageResponse>> getLastMessage(
            @PathVariable String conversationId
    ) {
        MessageResponse message = messageService.getLastMessage(conversationId);
        return ResponseEntity.ok(MyApiResponse.success(message));
    }

    @GetMapping("/role/{role}")
    @Operation(summary = "Get messages by role in a conversation")
    public ResponseEntity<MyApiResponse<List<MessageResponse>>> getMessagesByRole(
            @PathVariable String conversationId,
            @PathVariable MessageRole role
    ) {
        List<MessageResponse> messages = messageService.getConversationMessagesByRole(conversationId, role);
        return ResponseEntity.ok(MyApiResponse.success(messages));
    }

    @GetMapping("/search")
    @Operation(summary = "Search messages in a conversation")
    public ResponseEntity<MyApiResponse<List<MessageResponse>>> searchMessages(
            @PathVariable String conversationId,
            @Parameter(description = "Search keyword")
            @RequestParam String keyword
    ) {
        List<MessageResponse> messages = messageService.searchMessages(conversationId, keyword);
        return ResponseEntity.ok(MyApiResponse.success(messages));
    }

    @GetMapping("/since")
    @Operation(summary = "Get messages since a specific timestamp")
    public ResponseEntity<MyApiResponse<List<MessageResponse>>> getMessagesSince(
            @PathVariable String conversationId,
            @Parameter(description = "ISO timestamp (e.g., 2024-01-01T00:00:00Z)")
            @RequestParam Instant since
    ) {
        List<MessageResponse> messages = messageService.getMessagesSince(conversationId, since);
        return ResponseEntity.ok(MyApiResponse.success(messages));
    }

    @GetMapping("/stats")
    @Operation(summary = "Get message statistics for a conversation")
    public ResponseEntity<MyApiResponse<MessageStatsResponse>> getMessageStats(
            @PathVariable String conversationId
    ) {
        MessageStatsResponse stats = messageService.getMessageStats(conversationId);
        return ResponseEntity.ok(MyApiResponse.success(stats));
    }

    @DeleteMapping
    @Operation(summary = "Delete all messages in a conversation")
    public ResponseEntity<MyApiResponse<Void>> deleteConversationMessages(
            @PathVariable String conversationId
    ) {
        messageService.deleteConversationMessages(conversationId);
        return ResponseEntity.ok(MyApiResponse.success("All messages deleted successfully"));
    }
}
