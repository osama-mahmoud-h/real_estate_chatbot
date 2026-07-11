import { httpClient, unwrap } from '@/shared/api/httpClient';
import type { ApiResponse, Page } from '@/shared/api/ApiResponse';
import type { Conversation } from '../domain/Conversation';
import type { Message } from '../domain/Message';
import type {
  ConversationPage,
  ConversationRepository,
} from '../domain/ConversationRepository';

export const conversationApi: ConversationRepository = {
  async list({ page = 0, size = 30 } = {}): Promise<ConversationPage> {
    const res = await httpClient.get<ApiResponse<Page<Conversation>>>('/conversations', {
      params: { page, size, sortBy: 'updatedAt', direction: 'DESC' },
    });
    const pageData = unwrap(res);
    return { items: pageData.content, totalPages: pageData.totalPages, page: pageData.number };
  },

  async create(title = 'New chat'): Promise<Conversation> {
    const res = await httpClient.post<ApiResponse<Conversation>>('/conversations', { title });
    return unwrap(res);
  },

  async rename(conversationId: number, title: string): Promise<Conversation> {
    const res = await httpClient.put<ApiResponse<Conversation>>(
      `/conversations/${conversationId}`,
      { title },
    );
    return unwrap(res);
  },

  async remove(conversationId: number): Promise<void> {
    await httpClient.delete(`/conversations/${conversationId}`);
  },

  async messages(conversationId: number): Promise<Message[]> {
    const res = await httpClient.get<ApiResponse<Message[]>>(
      `/conversations/${conversationId}/messages`,
    );
    return unwrap(res);
  },
};
