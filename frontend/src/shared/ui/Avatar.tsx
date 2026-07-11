import { cn } from '@/shared/lib/cn';

interface AvatarProps {
  name: string;
  className?: string;
}

/** Circular initials avatar with a brand gradient fill. */
export function Avatar({ name, className }: AvatarProps) {
  const initials = name
    .split(' ')
    .map((part) => part.charAt(0))
    .filter(Boolean)
    .slice(0, 2)
    .join('')
    .toUpperCase();

  return (
    <span
      className={cn(
        'inline-flex h-9 w-9 shrink-0 items-center justify-center rounded-full',
        'bg-brand-gradient text-sm font-semibold text-white shadow-sm ring-1 ring-white/20',
        className,
      )}
      aria-hidden="true"
    >
      {initials || '?'}
    </span>
  );
}
