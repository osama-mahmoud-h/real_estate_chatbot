import { useCallback } from 'react';
import axios from 'axios';
import { useQueryClient } from '@tanstack/react-query';
import { toApiErrorMessage } from '@/shared/api/httpClient';
import { chatApi } from '../infrastructure/chatApi';
import { conversationApi } from '../infrastructure/conversationApi';
import { nextClientMessageId, type Message } from '../domain/Message';
import { useChatStore } from './chatStore';
import { conversationsQueryKey } from './useConversations';

/** Derives a short conversation title from the first user message. */
function deriveTitle(message: string): string {
  const trimmed = message.trim();
  return trimmed.length > 48 ? `${trimmed.slice(0, 48)}…` : trimmed;
}

/**
 * Sends a message through the AI chat workflow with optimistic rendering.
 *
 * - Appends the user's message immediately and shows the thinking indicator.
 * - `stop()` aborts the in-flight request; on abort/error the optimistic
 *   message is removed so the composer can restore the text to resume.
 * - `send()` resolves to `true` only on success (so the composer can clear).
 */
export function useSendMessage() {
  const queryClient = useQueryClient();

  const isSending = useChatStore((s) => s.isSending);
  const appendMessage = useChatStore((s) => s.appendMessage);
  const removeMessage = useChatStore((s) => s.removeMessage);
  const setSending = useChatStore((s) => s.setSending);
  const setError = useChatStore((s) => s.setError);
  const setActiveConversationId = useChatStore((s) => s.setActiveConversationId);

  const send = useCallback(
    async (text: string): Promise<boolean> => {
      const content = text.trim();
      if (!content || useChatStore.getState().isSending) return false;

      let conversationId = useChatStore.getState().activeConversationId;

      const optimistic: Message = {
        messageId: nextClientMessageId(),
        conversationId,
        role: 'USER',
        content,
        createdAt: new Date().toISOString(),
      };
      appendMessage(optimistic);
      setError(null);

      const controller = new AbortController();
      setSending(true, controller);

      try {
        // The /chat endpoint requires an existing conversation, so create one
        // first when starting a brand-new chat.
        if (conversationId === null) {
          const conversation = await conversationApi.create(deriveTitle(content));
          conversationId = conversation.conversationId;
          setActiveConversationId(conversationId);
        }

        const reply = await chatApi.send({
          message: content,
          conversationId,
          signal: controller.signal,
        });

        appendMessage({
          messageId: nextClientMessageId(),
          conversationId: reply.conversationId,
          role: 'ASSISTANT',
          content: reply.message,
          data: reply.data ?? null,
          createdAt: new Date().toISOString(),
        });

        // Keep the active id in sync with what the server reports.
        if (reply.conversationId && reply.conversationId !== conversationId) {
          conversationId = reply.conversationId;
          setActiveConversationId(conversationId);
        }

        // Refresh the sidebar (new conversation / reordered by recency).
        queryClient.invalidateQueries({ queryKey: conversationsQueryKey });
        return true;
      } catch (error) {
        // Roll back the optimistic message; the composer keeps the text.
        removeMessage(optimistic.messageId);
        if (!axios.isCancel(error)) {
          setError(toApiErrorMessage(error, 'The assistant could not respond. Try again.'));
        }
        return false;
      } finally {
        setSending(false);
      }
    },
    [appendMessage, removeMessage, setSending, setError, setActiveConversationId, queryClient],
  );

  const stop = useCallback(() => {
    useChatStore.getState().abortController?.abort();
  }, []);

  return { send, stop, isSending };
}
