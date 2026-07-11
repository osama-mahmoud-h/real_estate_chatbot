const timeFormatter = new Intl.DateTimeFormat(undefined, {
  hour: '2-digit',
  minute: '2-digit',
});

/** Formats an ISO timestamp as a short local time, e.g. "14:03". */
export function formatTime(iso?: string): string {
  if (!iso) return '';
  const date = new Date(iso);
  if (Number.isNaN(date.getTime())) return '';
  return timeFormatter.format(date);
}
