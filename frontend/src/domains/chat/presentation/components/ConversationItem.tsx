import { cn } from '@/shared/lib/cn';
import type { Conversation } from '../../domain/Conversation';

interface ConversationItemProps {
  conversation: Conversation;
  active: boolean;
  onSelect: () => void;
  onDelete: () => void;
}

export function ConversationItem({
  conversation,
  active,
  onSelect,
  onDelete,
}: ConversationItemProps) {
  return (
    <div
      className={cn(
        'group flex h-10 items-center gap-2.5 rounded-control text-[13.5px] transition-colors',
        active
          ? 'border-l-[3px] border-forest-bright bg-paper/10 pl-[9px] pr-3 text-paper'
          : 'px-3 text-moss hover:bg-paper/5 hover:text-moss-bright',
      )}
    >
      <button
        type="button"
        onClick={onSelect}
        className="flex min-w-0 flex-1 items-center gap-2.5 text-left focus-visible:outline-none"
      >
        <ChatIcon className={active ? 'text-forest-bright' : 'text-moss-faint'} />
        <span className="truncate">{conversation.title || 'Untitled chat'}</span>
      </button>
      <button
        type="button"
        onClick={(e) => {
          e.stopPropagation();
          onDelete();
        }}
        aria-label="Delete conversation"
        className="shrink-0 rounded text-moss-muted opacity-0 transition hover:text-brick-bright focus:opacity-100 focus:outline-none group-hover:opacity-100"
      >
        <TrashIcon />
      </button>
    </div>
  );
}

function ChatIcon({ className }: { className?: string }) {
  return (
    <svg
      width="15"
      height="15"
      viewBox="0 0 24 24"
      fill="none"
      className={cn('shrink-0', className)}
      aria-hidden="true"
    >
      <path
        d="M21 12a8 8 0 0 1-11.6 7.1L4 20l1-4.5A8 8 0 1 1 21 12z"
        stroke="currentColor"
        strokeWidth="1.8"
        strokeLinejoin="round"
      />
    </svg>
  );
}

function TrashIcon() {
  return (
    <svg width="15" height="15" viewBox="0 0 24 24" fill="none" aria-hidden="true">
      <path
        d="M4 7h16M9 7V5a1 1 0 0 1 1-1h4a1 1 0 0 1 1 1v2m-8 0v12a1 1 0 0 0 1 1h6a1 1 0 0 0 1-1V7"
        stroke="currentColor"
        strokeWidth="1.7"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
    </svg>
  );
}