import { useRef, useState, type KeyboardEvent } from 'react';
import { cn } from '@/shared/lib/cn';
import { useMediaQuery } from '@/shared/lib/useMediaQuery';
import { useSendMessage } from '../../application/useSendMessage';

export function MessageComposer() {
  const { send, stop, isSending } = useSendMessage();
  const [value, setValue] = useState('');
  const textareaRef = useRef<HTMLTextAreaElement>(null);
  // The full prompt wraps out of a single-row textarea on a phone.
  const isNarrow = useMediaQuery('(max-width: 640px)');

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
    <div className="shrink-0 px-3 pb-5 pt-4 sm:px-6">
      <div className="mx-auto flex w-full max-w-3xl flex-col gap-2">
        <div
          className={cn(
            'flex items-end gap-2.5 rounded-bubble border border-edge-strong bg-paper-card p-2.5 pl-4 transition',
            'focus-within:border-forest-bright focus-within:ring-[3px] focus-within:ring-forest-soft',
            'dark:border-night-strong dark:bg-night-panel dark:focus-within:ring-forest-accent/25',
          )}
        >
          <textarea
            ref={textareaRef}
            value={value}
            onChange={(e) => setValue(e.target.value)}
            onKeyDown={onKeyDown}
            disabled={isSending}
            rows={1}
            placeholder={
              isSending
                ? 'Waiting for Sarah…'
                : isNarrow
                  ? 'Ask about a listing…'
                  : 'Ask about a listing, a neighbourhood or a price range…'
            }
            className={cn(
              'scrollbar-thin max-h-40 min-h-[42px] flex-1 resize-none bg-transparent py-2.5 text-[15px] leading-[1.5]',
              'text-ink placeholder:text-ink-faint focus:outline-none disabled:opacity-70',
              'dark:text-mist dark:placeholder:text-mist-faint',
            )}
          />

          {isSending ? (
            <button
              type="button"
              onClick={stop}
              aria-label="Stop generating"
              className="flex h-[42px] w-[42px] shrink-0 items-center justify-center rounded-control border border-edge-strong bg-paper-sunk text-ink-body transition-colors hover:bg-edge dark:border-night-strong dark:bg-night-raised dark:text-mist-dim dark:hover:bg-night-strong"
            >
              <StopIcon />
            </button>
          ) : (
            <button
              type="button"
              onClick={() => void submit()}
              disabled={!value.trim()}
              aria-label="Send message"
              className="flex h-[42px] w-[42px] shrink-0 items-center justify-center rounded-control bg-forest text-paper transition-colors hover:bg-forest-deep disabled:cursor-not-allowed disabled:bg-edge disabled:text-ink-faint dark:bg-forest-accent dark:hover:bg-forest-bright dark:disabled:bg-night-strong dark:disabled:text-mist-faint"
            >
              <SendIcon />
            </button>
          )}
        </div>

        <p className="pr-1 text-right font-mono text-[10.5px] tracking-[0.04em] text-ink-faint dark:text-mist-faint">
          {isSending ? 'Sending · click to stop' : 'Enter to send · Shift + Enter for a new line'}
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
        strokeWidth="1.8"
        strokeLinejoin="round"
        strokeLinecap="round"
      />
    </svg>
  );
}

function StopIcon() {
  return (
    <svg width="15" height="15" viewBox="0 0 24 24" fill="currentColor" aria-hidden="true">
      <rect x="6" y="6" width="12" height="12" rx="2.5" />
    </svg>
  );
}