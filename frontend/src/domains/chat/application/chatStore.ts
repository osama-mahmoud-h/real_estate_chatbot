import { create } from 'zustand';
import type { Message } from '../domain/Message';

interface ChatState {
  activeConversationId: number | null;
  thread: Message[];
  isThreadLoading: boolean;
  isSending: boolean;
  error: string | null;
  /** Controller for the in-flight send, so the user can stop it. */
  abortController: AbortController | null;

  startNewChat: () => void;
  setActiveConversationId: (id: number | null) => void;
  setThread: (messages: Message[]) => void;
  setThreadLoading: (loading: boolean) => void;
  appendMessage: (message: Message) => void;
  removeMessage: (messageId: Message['messageId']) => void;
  setSending: (sending: boolean, controller?: AbortController | null) => void;
  setError: (error: string | null) => void;
}

export const useChatStore = create<ChatState>((set) => ({
  activeConversationId: null,
  thread: [],
  isThreadLoading: false,
  isSending: false,
  error: null,
  abortController: null,

  startNewChat: () =>
    set({ activeConversationId: null, thread: [], error: null, isThreadLoading: false }),

  setActiveConversationId: (id) => set({ activeConversationId: id }),
  setThread: (messages) => set({ thread: messages }),
  setThreadLoading: (loading) => set({ isThreadLoading: loading }),

  appendMessage: (message) => set((s) => ({ thread: [...s.thread, message] })),
  removeMessage: (messageId) =>
    set((s) => ({ thread: s.thread.filter((m) => m.messageId !== messageId) })),

  setSending: (sending, controller = null) =>
    set({ isSending: sending, abortController: sending ? controller : null }),

  setError: (error) => set({ error }),
}));
