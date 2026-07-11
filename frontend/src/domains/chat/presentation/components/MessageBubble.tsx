import { cn } from '@/shared/lib/cn';
import { formatTime } from '@/shared/lib/formatTime';
import { isUserRole } from '../../domain/MessageRole';
import type { Message } from '../../domain/Message';

export function MessageBubble({ message }: { message: Message }) {
  const isUser = isUserRole(message.role);
  const time = formatTime(message.createdAt);

  return (
    <div
      className={cn(
        'flex',
        isUser ? 'justify-end animate-enter-right' : 'justify-start animate-enter-left',
      )}
    >
      <div className={cn('flex max-w-[82%] gap-2.5', isUser && 'flex-row-reverse')}>
        {!isUser && (
          <span className="mt-0.5 flex h-8 w-8 shrink-0 items-center justify-center rounded-full bg-brand-gradient text-[11px] font-bold text-white shadow-sm ring-1 ring-white/20">
            AI
          </span>
        )}
        <div className={cn('flex flex-col gap-1', isUser ? 'items-end' : 'items-start')}>
          <div
            className={cn(
              'whitespace-pre-wrap break-words rounded-2xl px-4 py-2.5 text-sm leading-relaxed',
              isUser
                ? 'rounded-br-md bg-brand-gradient text-white shadow-lift'
                : 'rounded-bl-md border border-slate-200/80 bg-white text-slate-800 shadow-soft dark:border-white/10 dark:bg-ink-800 dark:text-slate-100',
            )}
          >
            {message.content}
          </div>
          {time && (
            <span className="px-1 text-[11px] text-slate-400 dark:text-slate-500">{time}</span>
          )}
        </div>
      </div>
    </div>
  );
}
