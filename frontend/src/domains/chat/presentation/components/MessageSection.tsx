import { ThemeToggle } from '@/shared/ui/ThemeToggle';
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
    <section className="relative flex h-full min-w-0 flex-1 flex-col">
      {/* Soft textured background */}
      <div className="pointer-events-none absolute inset-0 -z-10 bg-slate-50 dark:bg-ink-950">
        <div className="absolute inset-0 bg-[radial-gradient(theme(colors.slate.200)_1px,transparent_1px)] [background-size:22px_22px] opacity-40 dark:bg-[radial-gradient(theme(colors.white/10%)_1px,transparent_1px)] dark:opacity-100" />
        <div className="absolute -top-32 left-1/2 h-72 w-[36rem] -translate-x-1/2 rounded-full bg-primary-200/30 blur-3xl dark:bg-primary-600/20" />
      </div>

      <header className="flex h-14 shrink-0 items-center gap-3 border-b border-slate-200/70 bg-white/70 px-4 backdrop-blur dark:border-white/10 dark:bg-ink-900/70">
        <button
          type="button"
          onClick={onOpenSidebar}
          className="rounded-lg p-1.5 text-slate-500 transition hover:bg-slate-100 dark:hover:bg-white/10 md:hidden"
          aria-label="Open conversations"
        >
          <MenuIcon />
        </button>
        <div className="flex items-center gap-2">
          <span className="h-2 w-2 rounded-full bg-emerald-500 shadow-[0_0_0_3px_rgba(16,185,129,0.15)]" />
          <h1 className="truncate text-sm font-semibold text-slate-700 dark:text-slate-100">
            Real Estate Assistant
          </h1>
        </div>
        <ThemeToggle className="ml-auto" />
      </header>

      <div className="flex flex-1 flex-col overflow-y-auto scrollbar-thin">
        <MessageList messages={thread} isThreadLoading={isThreadLoading} isSending={isSending} />
      </div>

      {error && (
        <div className="mx-auto mb-2 w-full max-w-3xl px-4">
          <div className="rounded-xl border border-red-200 bg-red-50 px-4 py-2 text-center text-sm text-red-700 shadow-sm dark:border-red-500/30 dark:bg-red-500/10 dark:text-red-300">
            {error}
          </div>
        </div>
      )}

      <MessageComposer />
    </section>
  );
}

function MenuIcon() {
  return (
    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" aria-hidden="true">
      <path d="M4 6h16M4 12h16M4 18h16" stroke="currentColor" strokeWidth="2" strokeLinecap="round" />
    </svg>
  );
}
