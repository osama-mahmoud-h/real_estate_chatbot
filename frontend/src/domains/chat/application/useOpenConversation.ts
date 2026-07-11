import { useCallback } from 'react';
import { conversationApi } from '../infrastructure/conversationApi';
import { toApiErrorMessage } from '@/shared/api/httpClient';
import { useChatStore } from './chatStore';

/** Returns a callback that loads a conversation's messages into the thread. */
export function useOpenConversation() {
  const setActiveConversationId = useChatStore((s) => s.setActiveConversationId);
  const setThread = useChatStore((s) => s.setThread);
  const setThreadLoading = useChatStore((s) => s.setThreadLoading);
  const setError = useChatStore((s) => s.setError);

  return useCallback(
    async (conversationId: number) => {
      setActiveConversationId(conversationId);
      setThread([]);
      setError(null);
      setThreadLoading(true);
      try {
        const messages = await conversationApi.messages(conversationId);
        setThread(messages);
      } catch (error) {
        setError(toApiErrorMessage(error, 'Failed to load this conversation.'));
      } finally {
        setThreadLoading(false);
      }
    },
    [setActiveConversationId, setThread, setThreadLoading, setError],
  );
}
