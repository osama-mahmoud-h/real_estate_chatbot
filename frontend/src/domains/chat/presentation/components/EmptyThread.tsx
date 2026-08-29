import { useSendMessage } from '../../application/useSendMessage';

const SUGGESTIONS = [
  {
    label: 'Villas',
    prompt: 'Show me luxury villas for sale in New Cairo with a private pool',
  },
  {
    label: 'Apartments',
    prompt: 'Find me 3-bedroom apartments in Zamalek under 5 million EGP',
  },
  {
    label: 'Townhouses',
    prompt: "I'm looking for a townhouse or villa for sale in Maadi or October Gardens",
  },
  {
    label: 'New listings',
    prompt: 'What are the most recently listed properties in Cairo?',
  },
];

export function EmptyThread() {
  const { send, isSending } = useSendMessage();

  return (
    /* Centred when it fits; on a phone it starts at the top and scrolls instead. */
    <div className="flex flex-1 flex-col px-6 py-6 sm:justify-center">
      <div className="mx-auto flex w-full max-w-3xl animate-fade-in flex-col gap-[30px]">
        <div className="flex flex-col gap-3">
          <h2 className="font-serif text-[30px] font-normal leading-[1.2] tracking-[-0.01em] text-ink dark:text-mist sm:text-[34px]">
            What kind of home
            <br />
            are you looking for?
          </h2>
          <p className="max-w-[480px] text-[15px] leading-relaxed text-ink-muted dark:text-mist-muted">
            Describe it the way you would to an agent — neighbourhood, budget, number of rooms.
            Sarah searches the live listings and answers with what actually matches.
          </p>
        </div>

        <div className="flex flex-col gap-3">
          <p className="font-mono text-[10px] uppercase tracking-[0.14em] text-ink-faint dark:text-mist-faint">
            Or start with one of these
          </p>

          <div className="grid gap-3 sm:grid-cols-2">
            {SUGGESTIONS.map(({ label, prompt }) => (
              <button
                key={prompt}
                type="button"
                disabled={isSending}
                onClick={() => void send(prompt)}
                className="flex flex-col gap-2.5 rounded-card border border-edge bg-paper-card p-4 text-left transition-colors hover:border-forest-bright focus-visible:outline-none focus-visible:border-forest-bright focus-visible:ring-[3px] focus-visible:ring-forest-soft disabled:opacity-60 dark:border-night-edge dark:bg-night-panel dark:hover:border-forest-bright dark:focus-visible:ring-forest-accent/25"
              >
                <span className="font-mono text-[10px] uppercase tracking-[0.13em] text-ink-faint dark:text-mist-faint">
                  {label}
                </span>
                <span className="text-[14.5px] leading-[1.5] text-ink-body dark:text-mist-dim">
                  {prompt}
                </span>
              </button>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}