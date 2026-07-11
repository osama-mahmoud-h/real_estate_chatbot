import type { MessageRole } from './MessageRole';

/**
 * A chat message. Mirrors the backend `MessageResponse`, plus a few
 * client-only fields used for optimistic rendering.
 */
export interface Message {
  messageId: number | string;
  conversationId: number | null;
  role: MessageRole;
  content: string;
  createdAt?: string;
  /** Structured payload from the assistant (e.g. SQL results / listings). */
  data?: Array<Record<string, unknown>> | null;
  /** True while an optimistic message has not yet been confirmed by the server. */
  pending?: boolean;
}

let clientIdCounter = 0;

/** Stable-enough client id for optimistic messages before the server assigns one. */
export function nextClientMessageId(): string {
  clientIdCounter += 1;
  return `local-${clientIdCounter}`;
}
