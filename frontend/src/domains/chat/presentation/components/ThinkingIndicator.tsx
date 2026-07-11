export function ThinkingIndicator() {
  return (
    <div className="flex animate-enter-left justify-start">
      <div className="flex gap-2.5">
        <span className="mt-0.5 flex h-8 w-8 shrink-0 items-center justify-center rounded-full bg-brand-gradient text-[11px] font-bold text-white shadow-sm ring-1 ring-white/20">
          AI
        </span>
        <div className="flex items-center gap-1.5 rounded-2xl rounded-bl-md border border-slate-200/80 bg-white px-4 py-3.5 shadow-soft dark:border-white/10 dark:bg-ink-800">
          <span className="sr-only">Assistant is thinking</span>
          <Dot delay="0s" />
          <Dot delay="0.2s" />
          <Dot delay="0.4s" />
        </div>
      </div>
    </div>
  );
}

function Dot({ delay }: { delay: string }) {
  return (
    <span
      className="h-2 w-2 animate-bounce-dot rounded-full bg-accent-500"
      style={{ animationDelay: delay }}
    />
  );
}
