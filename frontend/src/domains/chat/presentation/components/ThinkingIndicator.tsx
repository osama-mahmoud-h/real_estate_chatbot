export function ThinkingIndicator() {
  return (
    <div className="flex animate-enter-left items-center gap-[7px]">
      <div className="flex items-center gap-1.5 rounded-bubble rounded-bl-[4px] border border-edge bg-paper-card px-[18px] py-[15px] dark:border-night-edge dark:bg-night-panel">
        <span className="sr-only">Sarah is looking</span>
        <Dot delay="0s" />
        <Dot delay="0.18s" />
        <Dot delay="0.36s" />
      </div>
      <span className="font-mono text-[11px] text-ink-faint dark:text-mist-faint">
        Sarah is looking…
      </span>
    </div>
  );
}

function Dot({ delay }: { delay: string }) {
  return (
    <span
      className="h-[7px] w-[7px] animate-bounce-dot rounded-full bg-forest-accent dark:bg-forest-bright"
      style={{ animationDelay: delay }}
    />
  );
}