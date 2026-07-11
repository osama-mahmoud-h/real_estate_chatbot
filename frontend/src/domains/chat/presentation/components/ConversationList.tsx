import { Spinner } from '@/shared/ui/Spinner';
import { useChatStore } from '../../application/chatStore';
import { useConversations } from '../../application/useConversations';
import { useOpenConversation } from '../../application/useOpenConversation';
import { useDeleteConversation } from '../../application/useDeleteConversation';
import { ConversationItem } from './ConversationItem';

export function ConversationList({ onSelect }: { onSelect?: () => void }) {
  const { data: conversations, isLoading, isError } = useConversations();
  const activeConversationId = useChatStore((s) => s.activeConversationId);
  const openConversation = useOpenConversation();
  const deleteConversation = useDeleteConversation();

  if (isLoading) {
    return (
      <div className="flex justify-center py-8 text-slate-500">
        <Spinner className="h-5 w-5" />
      </div>
    );
  }

  if (isError) {
    return <p className="px-3 py-4 text-sm text-slate-500">Couldn’t load conversations.</p>;
  }

  if (!conversations || conversations.length === 0) {
    return (
      <p className="px-3 py-4 text-sm text-slate-500">No conversations yet. Start a new chat.</p>
    );
  }

  return (
    <nav className="flex flex-col gap-0.5">
      {conversations.map((conversation) => (
        <ConversationItem
          key={conversation.conversationId}
          conversation={conversation}
          active={conversation.conversationId === activeConversationId}
          onSelect={() => {
            void openConversation(conversation.conversationId);
            onSelect?.();
          }}
          onDelete={() => deleteConversation.mutate(conversation.conversationId)}
        />
      ))}
    </nav>
  );
}
