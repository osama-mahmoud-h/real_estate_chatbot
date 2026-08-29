import { useNewChat } from '../../application/useNewChat';

export function NewChatButton({ onDone }: { onDone?: () => void }) {
  const newChat = useNewChat();

  return (
    <button
      type="button"
      onClick={() => {
        newChat();
        onDone?.();
      }}
      className="flex h-[42px] w-full items-center justify-center gap-2 rounded-control bg-paper text-sm font-semibold text-forest-deep transition-colors hover:bg-paper-sunk focus-visible:outline-none focus-visible:ring-[3px] focus-visible:ring-forest-bright/40 dark:bg-forest-accent dark:text-paper dark:hover:bg-forest-bright"
    >
      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" aria-hidden="true">
        <path d="M12 5.5v13M5.5 12h13" stroke="currentColor" strokeWidth="2" strokeLinecap="round" />
      </svg>
      New conversation
    </button>
  );
}