import { useQuery } from '@tanstack/react-query';
import { conversationApi } from '../infrastructure/conversationApi';

export const conversationsQueryKey = ['conversations'] as const;

/** Loads the conversation history shown in the sidebar. */
export function useConversations() {
  return useQuery({
    queryKey: conversationsQueryKey,
    queryFn: () => conversationApi.list(),
    select: (page) => page.items,
  });
}
