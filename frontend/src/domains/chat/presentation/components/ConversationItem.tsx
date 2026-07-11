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
        'group relative flex items-center gap-2 rounded-lg px-3 py-2 text-sm transition-colors',
        active
          ? 'bg-white/10 text-white'
          : 'text-slate-400 hover:bg-white/5 hover:text-slate-200',
      )}
    >
      {active && (
        <span className="absolute inset-y-1.5 left-0 w-0.5 rounded-full bg-accent-400" />
      )}
      <button
        type="button"
        onClick={onSelect}
        className="flex min-w-0 flex-1 items-center gap-2.5 text-left"
      >
        <ChatIcon className={active ? 'text-accent-400' : 'text-slate-500'} />
        <span className="truncate">{conversation.title || 'Untitled chat'}</span>
      </button>
      <button
        type="button"
        onClick={(e) => {
          e.stopPropagation();
          onDelete();
        }}
        aria-label="Delete conversation"
        className="shrink-0 rounded p-1 text-slate-500 opacity-0 transition hover:bg-white/10 hover:text-red-400 focus:opacity-100 group-hover:opacity-100"
      >
        <TrashIcon />
      </button>
    </div>
  );
}

function ChatIcon({ className }: { className?: string }) {
  return (
    <svg
      width="16"
      height="16"
      viewBox="0 0 24 24"
      fill="none"
      className={cn('shrink-0', className)}
      aria-hidden="true"
    >
      <path
        d="M21 12a8 8 0 01-11.6 7.1L4 20l1-4.5A8 8 0 1121 12z"
        stroke="currentColor"
        strokeWidth="2"
        strokeLinejoin="round"
      />
    </svg>
  );
}

function TrashIcon() {
  return (
    <svg width="15" height="15" viewBox="0 0 24 24" fill="none" aria-hidden="true">
      <path
        d="M4 7h16M9 7V5a1 1 0 011-1h4a1 1 0 011 1v2m-8 0v12a1 1 0 001 1h6a1 1 0 001-1V7"
        stroke="currentColor"
        strokeWidth="2"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
    </svg>
  );
}
