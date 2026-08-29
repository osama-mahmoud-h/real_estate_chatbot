import type { ReactNode } from 'react';
import { BrandMark } from '@/shared/ui/BrandMark';

/** Swap for a local file (e.g. '/auth-background.jpg' in public/) to drop the CDN. */
const BACKGROUND_IMAGE =
  'https://images.unsplash.com/photo-1600596542815-ffad4c1539a9?auto=format&fit=crop&q=80&w=2000';

interface AuthShellProps {
  title: string;
  subtitle: string;
  children: ReactNode;
  footer: ReactNode;
}

export function AuthShell({ title, subtitle, children, footer }: AuthShellProps) {
  return (
    <div className="relative flex min-h-screen w-full items-center justify-center p-4 sm:p-8">
      {/* Photograph */}
      <div
        className="fixed inset-0 bg-cover bg-center bg-no-repeat"
        style={{ backgroundImage: `url('${BACKGROUND_IMAGE}')` }}
        aria-hidden="true"
      />
      {/* Flat forest scrim — keeps the card legible without blurring the photo */}
      <div className="fixed inset-0 bg-forest-deep/80 dark:bg-night/85" aria-hidden="true" />

      <div className="relative z-10 flex w-full max-w-[420px] flex-col items-center gap-5 py-6">
        <div className="w-full rounded-[28px] bg-paper-sunk px-6 py-9 dark:bg-night-panel sm:px-8">
          <div className="flex flex-col items-center gap-4 text-center">
            <BrandMark className="h-12 w-12 rounded-2xl" iconClassName="h-[26px] w-[26px]" />
            <div className="flex flex-col gap-2">
              <h1 className="font-serif text-[32px] font-medium leading-tight tracking-[-0.01em] text-ink dark:text-mist">
                {title}
              </h1>
              <p className="mx-auto max-w-[300px] text-[14px] leading-relaxed text-ink-muted dark:text-mist-muted">
                {subtitle}
              </p>
            </div>
          </div>

          <div className="mt-7">{children}</div>

          <p className="mt-6 text-center text-sm text-ink-muted dark:text-mist-muted">{footer}</p>
        </div>

        <p className="font-mono text-[10.5px] uppercase tracking-[0.12em] text-moss">
          Cairo · Alexandria · North Coast
        </p>
      </div>
    </div>
  );
}