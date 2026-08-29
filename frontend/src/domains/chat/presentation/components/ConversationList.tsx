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
      <div className="flex justify-center py-8 text-moss-faint">
        <Spinner className="h-5 w-5" />
      </div>
    );
  }

  if (isError) {
    return <p className="px-3 py-4 text-[13.5px] text-moss-muted">Couldn’t load conversations.</p>;
  }

  if (!conversations || conversations.length === 0) {
    return (
      <p className="px-3 py-4 text-[13.5px] leading-relaxed text-moss-muted">
        No conversations yet. Start a new one above.
      </p>
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
