import { useSendMessage } from '../../application/useSendMessage';

const SUGGESTIONS = [
  'Show me luxury villas for sale in New Cairo with a private pool',
  'Find me 3-bedroom apartments in Zamalek under 5 million EGP',
  "I'm looking for a townhouse or villa for sale in Maadi or October Gardens with a garden and swimming pool",
  'What are the most recently listed properties in Cairo?',
];

export function EmptyThread() {
  const { send, isSending } = useSendMessage();

  return (
    <div className="flex flex-1 flex-col items-center justify-center px-6 py-12 text-center">
      <div className="mb-5 flex h-16 w-16 animate-float items-center justify-center rounded-3xl bg-brand-gradient text-3xl shadow-lift">
        🏠
      </div>
      <h2 className="text-2xl font-bold tracking-tight text-slate-800 dark:text-slate-100">
        How can I help you find a home?
      </h2>
      <p className="mt-2 max-w-md text-sm text-slate-500 dark:text-slate-400">
        Ask about listings, prices, or neighborhoods in plain language — I’ll search for you.
      </p>

      <div className="mt-8 grid w-full max-w-xl gap-2.5 sm:grid-cols-2">
        {SUGGESTIONS.map((s) => (
          <button
            key={s}
            type="button"
            disabled={isSending}
            onClick={() => void send(s)}
            className="group rounded-2xl border border-slate-200 bg-white/70 px-4 py-3 text-left text-sm text-slate-700 shadow-soft transition-all hover:-translate-y-0.5 hover:border-primary-300 hover:shadow-lift disabled:opacity-60 dark:border-white/10 dark:bg-ink-800/60 dark:text-slate-200 dark:hover:border-primary-500/50"
          >
            <span className="line-clamp-2">{s}</span>
            <span className="mt-1 flex items-center gap-1 text-xs font-medium text-primary-500 opacity-0 transition group-hover:opacity-100">
              Ask this
              <svg width="12" height="12" viewBox="0 0 24 24" fill="none" aria-hidden="true">
                <path d="M5 12h14M13 6l6 6-6 6" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
              </svg>
            </span>
          </button>
        ))}
      </div>
    </div>
  );
}
