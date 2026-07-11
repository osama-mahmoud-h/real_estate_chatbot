import { useMutation, useQueryClient } from '@tanstack/react-query';
import { conversationApi } from '../infrastructure/conversationApi';
import { useChatStore } from './chatStore';
import { conversationsQueryKey } from './useConversations';

export function useDeleteConversation() {
  const queryClient = useQueryClient();
  const activeConversationId = useChatStore((s) => s.activeConversationId);
  const startNewChat = useChatStore((s) => s.startNewChat);

  return useMutation({
    mutationFn: (conversationId: number) => conversationApi.remove(conversationId),
    onSuccess: (_data, conversationId) => {
      if (activeConversationId === conversationId) {
        startNewChat();
      }
      queryClient.invalidateQueries({ queryKey: conversationsQueryKey });
    },
  });
}
