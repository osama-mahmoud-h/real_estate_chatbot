import { forwardRef, type InputHTMLAttributes, type ReactNode } from 'react';
import { cn } from '@/shared/lib/cn';

interface InputProps extends InputHTMLAttributes<HTMLInputElement> {
  label?: string;
  error?: string;
  /** Node rendered inside the field, pinned to the right (e.g. a password toggle). */
  trailing?: ReactNode;
}

export const Input = forwardRef<HTMLInputElement, InputProps>(
  ({ label, error, className, id, trailing, ...props }, ref) => {
    const inputId = id ?? props.name;
    return (
      <div className="w-full">
        {label && (
          <label htmlFor={inputId} className="mb-1.5 block text-sm font-medium text-slate-700">
            {label}
          </label>
        )}
        <div className="relative">
          <input
            ref={ref}
            id={inputId}
            className={cn(
              'block w-full rounded-xl border bg-white px-4 py-2.5 text-sm text-slate-900 shadow-sm transition',
              'placeholder:text-slate-400 focus:outline-none focus:ring-4',
              trailing && 'pr-11',
              error
                ? 'border-red-400 focus:border-red-500 focus:ring-red-100'
                : 'border-slate-200 hover:border-slate-300 focus:border-primary-500 focus:ring-primary-100',
              className,
            )}
            aria-invalid={!!error}
            {...props}
          />
          {trailing && (
            <div className="absolute inset-y-0 right-2 flex items-center">{trailing}</div>
          )}
        </div>
        {error && <p className="mt-1.5 text-xs font-medium text-red-600">{error}</p>}
      </div>
    );
  },
);

Input.displayName = 'Input';
