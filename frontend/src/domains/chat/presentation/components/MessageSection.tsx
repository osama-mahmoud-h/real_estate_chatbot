import { ThemeToggle } from '@/shared/ui/ThemeToggle';
import { cn } from '@/shared/lib/cn';
import { useChatStore } from '../../application/chatStore';
import { MessageList } from './MessageList';
import { MessageComposer } from './MessageComposer';

interface MessageSectionProps {
  onOpenSidebar: () => void;
}

export function MessageSection({ onOpenSidebar }: MessageSectionProps) {
  const thread = useChatStore((s) => s.thread);
  const isThreadLoading = useChatStore((s) => s.isThreadLoading);
  const isSending = useChatStore((s) => s.isSending);
  const error = useChatStore((s) => s.error);

  return (
    <section className="relative flex h-full min-w-0 flex-1 flex-col bg-paper-sunk dark:bg-night">
      {/* header · wallpapered thread · composer bar */}
      <header className="z-10 flex h-16 shrink-0 items-center gap-3 border-b border-edge bg-paper px-2 dark:border-night-edge dark:bg-night-panel sm:px-6">
        <button
          type="button"
          onClick={onOpenSidebar}
          className="flex h-11 w-11 items-center justify-center rounded-control text-ink-body transition-colors hover:bg-paper-sunk dark:text-mist dark:hover:bg-white/5 md:hidden"
          aria-label="Open conversations"
        >
          <MenuIcon />
        </button>

        <AgentAvatar />
        <div className="flex flex-col gap-0.5">
          <h1 className="font-serif text-[16.5px] font-medium leading-none text-ink dark:text-mist">
            Sarah
          </h1>
          <span className="text-xs text-ink-muted dark:text-mist-muted">
            Real estate agent · online
          </span>
        </div>

        <ThemeToggle className="ml-auto" />
      </header>

      <div className="thread-canvas scrollbar-thin flex flex-1 flex-col overflow-y-auto">
        <MessageList messages={thread} isThreadLoading={isThreadLoading} isSending={isSending} />
      </div>

      <div className="shrink-0 border-t border-edge bg-paper dark:border-night-edge dark:bg-night-panel">
        {error && (
          <div className="mx-auto w-full max-w-3xl px-6 pt-3">
            <div className="rounded-control border border-brick/25 bg-brick-soft px-4 py-2.5 text-center text-sm text-brick dark:border-brick-bright/30 dark:bg-brick-bright/10 dark:text-brick-bright">
              {error}
            </div>
          </div>
        )}
        <MessageComposer />
      </div>
    </section>
  );
}

/** Sarah's monogram with an online dot. */
export function AgentAvatar({ className }: { className?: string }) {
  return (
    <div
      className={cn(
        'relative flex h-9 w-9 shrink-0 items-center justify-center rounded-control',
        'border border-[#CFE0D6] bg-forest-soft dark:border-night-strong dark:bg-night-raised',
        className,
      )}
    >
      <span className="font-serif text-[17px] font-medium text-forest dark:text-forest-bright">
        S
      </span>
      <span className="absolute -bottom-[3px] -right-[3px] h-[11px] w-[11px] rounded-full border-2 border-paper bg-forest-bright dark:border-night-panel" />
    </div>
  );
}

function MenuIcon() {
  return (
    <svg width="21" height="21" viewBox="0 0 24 24" fill="none" aria-hidden="true">
      <path
        d="M4 6.5h16M4 12h16M4 17.5h16"
        stroke="currentColor"
        strokeWidth="1.9"
        strokeLinecap="round"
      />
    </svg>
  );
}