/** Result of sending a message to the AI chat workflow (`ChatResponseDto`). */
export interface ChatReply {
  message: string;
  conversationId: number;
  intent?: string | null;
  resultCount?: number | null;
  executionTimeMs?: number | null;
  data?: Array<Record<string, unknown>> | null;
}

export interface SendMessageInput {
  message: string;
  /** null when starting a brand-new conversation. */
  conversationId: number | null;
  /** Optional abort signal so the user can stop a pending request. */
  signal?: AbortSignal;
}

/** Port for the AI chat endpoint. Implemented in the infrastructure layer. */
export interface ChatRepository {
  send(input: SendMessageInput): Promise<ChatReply>;
}
