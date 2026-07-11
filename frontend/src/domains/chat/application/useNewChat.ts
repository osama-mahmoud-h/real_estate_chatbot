import { useCallback } from 'react';
import { useChatStore } from './chatStore';

/** Resets the view to an empty conversation (the id is created on first send). */
export function useNewChat() {
  const startNewChat = useChatStore((s) => s.startNewChat);
  return useCallback(() => startNewChat(), [startNewChat]);
}
