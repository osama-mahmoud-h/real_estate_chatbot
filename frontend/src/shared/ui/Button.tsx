import { forwardRef, type ButtonHTMLAttributes } from 'react';
import { cn } from '@/shared/lib/cn';
import { Spinner } from './Spinner';

type Variant = 'primary' | 'secondary' | 'ghost' | 'danger';
type Size = 'sm' | 'md' | 'lg';

interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: Variant;
  size?: Size;
  loading?: boolean;
}

const variants: Record<Variant, string> = {
  primary:
    'bg-forest text-paper hover:bg-forest-deep disabled:bg-edge disabled:text-ink-faint dark:bg-forest-accent dark:hover:bg-forest-bright dark:disabled:bg-night-strong dark:disabled:text-mist-faint',
  secondary:
    'border border-edge-strong bg-paper-card text-ink-body hover:bg-paper-sunk disabled:opacity-60 dark:border-night-strong dark:bg-night-panel dark:text-mist dark:hover:bg-night-raised',
  ghost:
    'text-ink-muted hover:bg-paper-sunk disabled:opacity-60 dark:text-mist-muted dark:hover:bg-night-panel',
  danger:
    'bg-brick text-paper hover:bg-brick/90 disabled:opacity-60 dark:bg-brick-bright dark:hover:bg-brick-bright/90',
};

const sizes: Record<Size, string> = {
  sm: 'h-[34px] px-3.5 text-[13px]',
  md: 'h-[42px] px-5 text-sm',
  lg: 'h-[50px] px-6 text-[15px]',
};

export const Button = forwardRef<HTMLButtonElement, ButtonProps>(
  ({ variant = 'primary', size = 'md', loading, className, children, disabled, ...props }, ref) => (
    <button
      ref={ref}
      disabled={disabled || loading}
      className={cn(
        'inline-flex items-center justify-center gap-2 rounded-control font-semibold transition-colors duration-150',
        'focus-visible:outline-none focus-visible:ring-[3px] focus-visible:ring-forest-soft dark:focus-visible:ring-forest-accent/35',
        'disabled:cursor-not-allowed',
        variants[variant],
        sizes[size],
        className,
      )}
      {...props}
    >
      {loading && <Spinner className="h-4 w-4" />}
      {children}
    </button>
  ),
);

Button.displayName = 'Button';