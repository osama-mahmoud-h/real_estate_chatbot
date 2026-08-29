import { cn } from '@/shared/lib/cn';

/** The Dream Homes roof mark, on its own green tile. */
export function BrandMark({ className, iconClassName }: { className?: string; iconClassName?: string }) {
  return (
    <span
      className={cn(
        'flex h-[38px] w-[38px] shrink-0 items-center justify-center rounded-[11px] bg-forest-accent',
        className,
      )}
    >
      <svg
        viewBox="0 0 24 24"
        fill="none"
        className={cn('h-[21px] w-[21px] text-paper', iconClassName)}
        aria-hidden="true"
      >
        <path
          d="M3.5 10.6 12 4l8.5 6.6"
          stroke="currentColor"
          strokeWidth="1.7"
          strokeLinecap="round"
          strokeLinejoin="round"
        />
        <path
          d="M5.6 10.2V19a1 1 0 0 0 1 1h10.8a1 1 0 0 0 1-1v-8.8"
          stroke="currentColor"
          strokeWidth="1.7"
          strokeLinecap="round"
          strokeLinejoin="round"
        />
        <path
          d="M10 20v-5.2h4V20"
          stroke="currentColor"
          strokeWidth="1.7"
          strokeLinecap="round"
          strokeLinejoin="round"
        />
      </svg>
    </span>
  );
}

/** Wordmark used beside the roof tile. `tone` picks the text colours for the surface it sits on. */
export function BrandLockup({
  tone = 'forest',
  className,
}: {
  tone?: 'forest' | 'paper';
  className?: string;
}) {
  return (
    <div className={cn('flex items-center gap-[11px]', className)}>
      <BrandMark />
      <div className="flex min-w-0 flex-col gap-[3px] leading-none">
        <span
          className={cn(
            'font-serif text-[17px] font-medium tracking-[0.01em]',
            tone === 'forest' ? 'text-paper' : 'text-ink dark:text-mist',
          )}
        >
          Dream Homes
        </span>
        <span
          className={cn(
            'font-mono text-[10px] uppercase tracking-[0.14em]',
            tone === 'forest' ? 'text-moss-muted' : 'text-ink-faint dark:text-mist-faint',
          )}
        >
          Real Estate
        </span>
      </div>
    </div>
  );
}