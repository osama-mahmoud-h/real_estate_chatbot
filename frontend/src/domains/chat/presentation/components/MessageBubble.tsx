import { cn } from '@/shared/lib/cn';
import { formatTime } from '@/shared/lib/formatTime';
import { Markdown } from '@/shared/ui/Markdown';
import { isUserRole } from '../../domain/MessageRole';
import type { Message } from '../../domain/Message';

export function MessageBubble({ message }: { message: Message }) {
  const isUser = isUserRole(message.role);
  const time = formatTime(message.createdAt);

  return (
    <div
      className={cn(
        'flex flex-col gap-[7px]',
        isUser ? 'items-end animate-enter-right' : 'items-start animate-enter-left',
      )}
    >
      <div
        className={cn(
          'max-w-[85%] break-words px-[17px] py-3.5 text-[15px] leading-[1.62] sm:max-w-[600px]',
          isUser
            ? 'rounded-bubble rounded-br-[4px] bg-forest text-paper dark:bg-forest-accent'
            : 'rounded-bubble rounded-bl-[4px] border border-edge bg-paper-card text-ink dark:border-night-edge dark:bg-night-panel dark:text-mist',
        )}
      >
        {isUser ? message.content : <Markdown>{message.content}</Markdown>}
      </div>
      {time && (
        <span className="px-1 font-mono text-[11px] text-ink-faint dark:text-mist-faint">
          {time}
        </span>
      )}
    </div>
  );
}