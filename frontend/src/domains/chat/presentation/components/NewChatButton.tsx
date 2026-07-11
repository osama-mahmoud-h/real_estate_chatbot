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
      className="flex w-full items-center justify-center gap-2 rounded-xl bg-brand-gradient px-3 py-2.5 text-sm font-semibold text-white shadow-lift transition-all hover:brightness-110 active:scale-[0.98]"
    >
      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" aria-hidden="true">
        <path d="M12 5v14M5 12h14" stroke="currentColor" strokeWidth="2.2" strokeLinecap="round" />
      </svg>
      New chat
    </button>
  );
}
