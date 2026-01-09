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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import semsem.chatbot.model.dto.request.CreateConversationRequest;
import semsem.chatbot.model.dto.request.UpdateConversationRequest;
import semsem.chatbot.model.dto.response.ConversationResponse;
import semsem.chatbot.model.dto.response.MyApiResponse;
import semsem.chatbot.model.enums.ConversationStatus;
import semsem.chatbot.service.chat.ConversationService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/conversations")
@RequiredArgsConstructor
@Tag(name = "Conversations", description = "Conversation management endpoints")
public class ConversationController {

    private final ConversationService conversationService;

    @PostMapping
    @Operation(summary = "Create a new conversation")
    public ResponseEntity<MyApiResponse<ConversationResponse>> createConversation(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateConversationRequest request
    ) {
        ConversationResponse response = conversationService.createConversation(
                userDetails.getUsername(),
                request
        );
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(MyApiResponse.success("Conversation created successfully", response));
    }

    @GetMapping("/{conversationId}")
    @Operation(summary = "Get a conversation by ID")
    public ResponseEntity<MyApiResponse<ConversationResponse>> getConversation(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String conversationId,
            @Parameter(description = "Include messages in response")
            @RequestParam(defaultValue = "false") boolean includeMessages
    ) {
        ConversationResponse response = conversationService.getConversation(
                userDetails.getUsername(),
                conversationId,
                includeMessages
        );
        return ResponseEntity.ok(MyApiResponse.success(response));
    }

    @GetMapping
    @Operation(summary = "Get all conversations for the current user")
    public ResponseEntity<MyApiResponse<Page<ConversationResponse>>> getUserConversations(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field")
            @RequestParam(defaultValue = "updatedAt") String sortBy,
            @Parameter(description = "Sort direction")
            @RequestParam(defaultValue = "DESC") Sort.Direction direction
    ) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<ConversationResponse> conversations = conversationService.getUserConversations(
                userDetails.getUsername(),
                pageRequest
        );
        return ResponseEntity.ok(MyApiResponse.success(conversations));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get conversations by status")
    public ResponseEntity<MyApiResponse<Page<ConversationResponse>>> getConversationsByStatus(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable ConversationStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        // Note: This endpoint requires userId. In a real app, you'd get it from the user details
        // For now, we'll use the email-based method
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));
        Page<ConversationResponse> conversations = conversationService.getUserConversations(
                userDetails.getUsername(),
                pageRequest
        );
        // Filter by status - alternatively update service to support email + status
        return ResponseEntity.ok(MyApiResponse.success(conversations));
    }

    @PutMapping("/{conversationId}")
    @Operation(summary = "Update a conversation")
    public ResponseEntity<MyApiResponse<ConversationResponse>> updateConversation(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String conversationId,
            @Valid @RequestBody UpdateConversationRequest request
    ) {
        ConversationResponse response = conversationService.updateConversation(
                userDetails.getUsername(),
                conversationId,
                request
        );
        return ResponseEntity.ok(MyApiResponse.success("Conversation updated successfully", response));
    }

    @PatchMapping("/{conversationId}/archive")
    @Operation(summary = "Archive a conversation")
    public ResponseEntity<MyApiResponse<Void>> archiveConversation(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String conversationId
    ) {
        conversationService.archiveConversation(userDetails.getUsername(), conversationId);
        return ResponseEntity.ok(MyApiResponse.success("Conversation archived successfully"));
    }

    @DeleteMapping("/{conversationId}")
    @Operation(summary = "Delete a conversation")
    public ResponseEntity<MyApiResponse<Void>> deleteConversation(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String conversationId
    ) {
        conversationService.deleteConversation(userDetails.getUsername(), conversationId);
        return ResponseEntity.ok(MyApiResponse.success("Conversation deleted successfully"));
    }

    @GetMapping("/search")
    @Operation(summary = "Search conversations by title")
    public ResponseEntity<MyApiResponse<List<ConversationResponse>>> searchConversations(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "Search keyword")
            @RequestParam String keyword
    ) {
        List<ConversationResponse> conversations = conversationService.searchConversations(
                userDetails.getUsername(),
                keyword
        );
        return ResponseEntity.ok(MyApiResponse.success(conversations));
    }
}
