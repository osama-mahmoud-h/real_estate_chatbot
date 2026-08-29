import { cn } from '@/shared/lib/cn';

interface AvatarProps {
  name: string;
  className?: string;
}

/** Square-cornered initials avatar in the brand green. */
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
        'inline-flex h-[34px] w-[34px] shrink-0 items-center justify-center rounded-control',
        'bg-forest-accent text-[12.5px] font-semibold tracking-wide text-paper',
        className,
      )}
      aria-hidden="true"
    >
      {initials || '?'}
    </span>
  );
}