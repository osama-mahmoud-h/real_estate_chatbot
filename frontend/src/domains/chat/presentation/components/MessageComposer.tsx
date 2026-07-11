import { useRef, useState, type KeyboardEvent } from 'react';
import { cn } from '@/shared/lib/cn';
import { useSendMessage } from '../../application/useSendMessage';

export function MessageComposer() {
  const { send, stop, isSending } = useSendMessage();
  const [value, setValue] = useState('');
  const textareaRef = useRef<HTMLTextAreaElement>(null);

  const submit = async () => {
    const trimmed = value.trim();
    if (!trimmed || isSending) return;
    setValue(''); // clear optimistically
    const ok = await send(trimmed);
    if (!ok) {
      setValue(trimmed); // restore so the user can resume/retry
      textareaRef.current?.focus();
    }
  };

  const onKeyDown = (e: KeyboardEvent<HTMLTextAreaElement>) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      void submit();
    }
  };

  return (
    <div className="px-4 pb-4 pt-2">
      <div className="mx-auto w-full max-w-3xl">
        <div
          className={cn(
            'flex items-end gap-2 rounded-3xl border bg-white/90 p-2 shadow-glass backdrop-blur transition',
            'border-slate-200 focus-within:border-primary-300 focus-within:ring-4 focus-within:ring-primary-100',
            'dark:border-white/10 dark:bg-ink-800/80 dark:focus-within:border-primary-500/60 dark:focus-within:ring-primary-500/20',
          )}
        >
          <textarea
            ref={textareaRef}
            value={value}
            onChange={(e) => setValue(e.target.value)}
            onKeyDown={onKeyDown}
            disabled={isSending}
            rows={1}
            placeholder={isSending ? 'Waiting for the assistant…' : 'Message the assistant…'}
            className={cn(
              'max-h-40 min-h-[40px] flex-1 resize-none bg-transparent px-3 py-2 text-sm',
              'text-slate-900 placeholder:text-slate-400 focus:outline-none disabled:opacity-70 scrollbar-thin',
              'dark:text-slate-100 dark:placeholder:text-slate-500',
            )}
          />

          {isSending ? (
            <button
              type="button"
              onClick={stop}
              aria-label="Stop generating"
              className="flex h-10 w-10 shrink-0 items-center justify-center rounded-full bg-slate-900 text-white shadow-sm transition-all hover:bg-slate-800 active:scale-95 dark:bg-slate-100 dark:text-slate-900 dark:hover:bg-white"
            >
              <StopIcon />
            </button>
          ) : (
            <button
              type="button"
              onClick={() => void submit()}
              disabled={!value.trim()}
              aria-label="Send message"
              className="flex h-10 w-10 shrink-0 items-center justify-center rounded-full bg-brand-gradient text-white shadow-lift transition-all hover:brightness-110 active:scale-95 disabled:cursor-not-allowed disabled:bg-slate-300 disabled:bg-none disabled:shadow-none dark:disabled:bg-white/10"
            >
              <SendIcon />
            </button>
          )}
        </div>
        <p className="mt-2 text-center text-[11px] text-slate-400 dark:text-slate-500">
          Press{' '}
          <kbd className="rounded bg-slate-200/70 px-1 font-sans dark:bg-white/10">Enter</kbd> to
          send, <kbd className="rounded bg-slate-200/70 px-1 font-sans dark:bg-white/10">Shift</kbd>+
          <kbd className="rounded bg-slate-200/70 px-1 font-sans dark:bg-white/10">Enter</kbd> for a
          new line.
        </p>
      </div>
    </div>
  );
}

function SendIcon() {
  return (
    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" aria-hidden="true">
      <path
        d="M4 12l16-8-6 16-2.5-6.5L4 12z"
        stroke="currentColor"
        strokeWidth="2"
        strokeLinejoin="round"
        strokeLinecap="round"
      />
    </svg>
  );
}

function StopIcon() {
  return (
    <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor" aria-hidden="true">
      <rect x="6" y="6" width="12" height="12" rx="2" />
    </svg>
  );
}
