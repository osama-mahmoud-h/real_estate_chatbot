import type { ReactNode } from 'react';

interface AuthShellProps {
  title: string;
  subtitle: string;
  children: ReactNode;
  footer: ReactNode;
}

/** Split branded layout: a marketing panel on the left, the form on the right. */
export function AuthShell({ title, subtitle, children, footer }: AuthShellProps) {
  return (
    <div className="flex min-h-full">
      {/* Brand panel */}
      <div className="relative hidden w-1/2 overflow-hidden bg-ink-900 lg:flex lg:flex-col">
        <div className="pointer-events-none absolute inset-0">
          <div className="absolute -left-24 top-10 h-80 w-80 animate-blob-drift rounded-full bg-accent-600/40 blur-3xl" />
          <div className="absolute right-0 top-1/3 h-96 w-96 animate-blob-drift rounded-full bg-primary-600/40 blur-3xl [animation-delay:-6s]" />
          <div className="absolute bottom-0 left-1/4 h-72 w-72 animate-blob-drift rounded-full bg-accent-400/30 blur-3xl [animation-delay:-12s]" />
        </div>

        <div className="relative z-10 flex h-full flex-col justify-between p-12 text-white">
          <div className="flex items-center gap-2.5">
            <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-white/10 text-lg font-bold ring-1 ring-white/20 backdrop-blur">
              🏠
            </div>
            <span className="text-lg font-semibold tracking-tight">Estate Assistant</span>
          </div>

          <div className="max-w-md">
            <h2 className="text-4xl font-extrabold leading-tight tracking-tight">
              Find your next property with a conversation.
            </h2>
            <p className="mt-4 text-base leading-relaxed text-white/70">
              Ask in plain language and let the AI search listings, compare options, and surface the
              places that fit — instantly.
            </p>
          </div>

          <div className="flex items-center gap-3 text-sm text-white/60">
            <div className="flex -space-x-2">
              {['from-accent-400 to-accent-600', 'from-primary-400 to-primary-600', 'from-sky-400 to-indigo-500'].map(
                (g, i) => (
                  <span
                    key={i}
                    className={`h-8 w-8 rounded-full bg-gradient-to-br ${g} ring-2 ring-ink-900`}
                  />
                ),
              )}
            </div>
            Trusted by house hunters everywhere
          </div>
        </div>
      </div>

      {/* Form panel */}
      <div className="flex w-full items-center justify-center bg-gradient-to-b from-white to-slate-50 px-4 py-12 lg:w-1/2">
        <div className="w-full max-w-sm">
          <div className="mb-8 lg:hidden">
            <div className="mx-auto mb-4 flex h-12 w-12 items-center justify-center rounded-2xl bg-brand-gradient text-xl shadow-lift">
              🏠
            </div>
          </div>

          <h1 className="text-2xl font-bold tracking-tight text-slate-900">{title}</h1>
          <p className="mt-1.5 text-sm text-slate-500">{subtitle}</p>

          <div className="mt-8">{children}</div>

          <div className="mt-6 text-center text-sm text-slate-500">{footer}</div>
        </div>
      </div>
    </div>
  );
}
