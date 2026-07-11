import { httpClient, unwrap } from '@/shared/api/httpClient';
import type { ApiResponse } from '@/shared/api/ApiResponse';
import type { ChatReply, ChatRepository, SendMessageInput } from '../domain/ChatRepository';

interface ChatResponseDto {
  message: string;
  conversationId: number;
  intent?: string | null;
  resultCount?: number | null;
  executionTimeMs?: number | null;
  data?: Array<Record<string, unknown>> | null;
}

export const chatApi: ChatRepository = {
  async send({ message, conversationId, signal }: SendMessageInput): Promise<ChatReply> {
    const res = await httpClient.post<ApiResponse<ChatResponseDto>>(
      '/chat',
      { message, conversationId },
      { signal },
    );
    return unwrap(res);
  },
};
