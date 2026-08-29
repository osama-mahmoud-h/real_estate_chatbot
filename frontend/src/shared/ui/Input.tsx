import { forwardRef, type InputHTMLAttributes, type ReactNode } from 'react';
import { cn } from '@/shared/lib/cn';

interface InputProps extends InputHTMLAttributes<HTMLInputElement> {
  label?: string;
  error?: string;
  /** Icon rendered inside the field, pinned to the left. */
  leading?: ReactNode;
  /** Node rendered inside the field, pinned to the right (e.g. a password toggle). */
  trailing?: ReactNode;
  /** `pill` is the rounded, borderless field used on the auth screens. */
  variant?: 'default' | 'pill';
}

export const Input = forwardRef<HTMLInputElement, InputProps>(
  ({ label, error, className, id, leading, trailing, variant = 'default', ...props }, ref) => {
    const inputId = id ?? props.name;
    const isPill = variant === 'pill';

    return (
      <div className="w-full">
        {label && (
          <label
            htmlFor={inputId}
            className="mb-2 block text-[13px] font-semibold text-ink-body dark:text-mist"
          >
            {label}
          </label>
        )}
        <div className="relative">
          {leading && (
            <div className="pointer-events-none absolute inset-y-0 left-4 flex items-center text-ink-faint dark:text-mist-faint">
              {leading}
            </div>
          )}
          <input
            ref={ref}
            id={inputId}
            className={cn(
              'block w-full bg-paper-card text-[15px] text-ink transition',
              'placeholder:text-ink-faint focus:outline-none focus:ring-[3px]',
              'dark:bg-night-panel dark:text-mist dark:placeholder:text-mist-faint',
              isPill
                ? 'h-14 rounded-full border border-transparent px-5 shadow-[0_2px_10px_rgba(22,33,27,0.06)] dark:shadow-none'
                : 'h-12 rounded-control border px-4',
              leading && (isPill ? 'pl-[52px]' : 'pl-11'),
              trailing && (isPill ? 'pr-14' : 'pr-12'),
              error
                ? 'border-brick focus:border-brick focus:ring-brick/15 dark:border-brick-bright dark:focus:ring-brick-bright/20'
                : cn(
                    'focus:border-forest-bright focus:ring-forest-soft dark:focus:border-forest-bright dark:focus:ring-forest-accent/25',
                    !isPill &&
                      'border-edge-strong hover:border-ink-faint dark:border-night-strong dark:hover:border-mist-faint',
                  ),
              className,
            )}
            aria-invalid={!!error}
            {...props}
          />
          {trailing && (
            <div className={cn('absolute inset-y-0 flex items-center', isPill ? 'right-3' : 'right-2')}>
              {trailing}
            </div>
          )}
        </div>
        {error && (
          <p
            className={cn(
              'mt-1.5 text-xs font-medium text-brick dark:text-brick-bright',
              isPill && 'px-5',
            )}
          >
            {error}
          </p>
        )}
      </div>
    );
  },
);

Input.displayName = 'Input';