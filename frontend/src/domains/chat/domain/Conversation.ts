/** A conversation summary as shown in the history sidebar. */
export interface Conversation {
  conversationId: number;
  title: string;
  status: string;
  summary?: string | null;
  messageCount?: number;
  createdAt?: string;
  updatedAt?: string;
}
