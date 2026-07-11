import type { Conversation } from './Conversation';
import type { Message } from './Message';

export interface ConversationPage {
  items: Conversation[];
  totalPages: number;
  page: number;
}

/** Port for conversation + message history. Implemented in infrastructure. */
export interface ConversationRepository {
  list(params?: { page?: number; size?: number }): Promise<ConversationPage>;
  create(title?: string): Promise<Conversation>;
  rename(conversationId: number, title: string): Promise<Conversation>;
  remove(conversationId: number): Promise<void>;
  messages(conversationId: number): Promise<Message[]>;
}
