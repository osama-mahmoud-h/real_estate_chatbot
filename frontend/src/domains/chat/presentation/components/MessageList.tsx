import { useEffect, useRef } from 'react';
import { Spinner } from '@/shared/ui/Spinner';
import type { Message } from '../../domain/Message';
import { MessageBubble } from './MessageBubble';
import { ThinkingIndicator } from './ThinkingIndicator';
import { EmptyThread } from './EmptyThread';

interface MessageListProps {
  messages: Message[];
  isThreadLoading: boolean;
  isSending: boolean;
}

export function MessageList({ messages, isThreadLoading, isSending }: MessageListProps) {
  const endRef = useRef<HTMLDivElement>(null);

  // Auto-scroll to the newest message / thinking indicator.
  useEffect(() => {
    endRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages.length, isSending]);

  if (isThreadLoading) {
    return (
      <div className="flex flex-1 items-center justify-center text-slate-400">
        <Spinner className="h-6 w-6" />
      </div>
    );
  }

  if (messages.length === 0 && !isSending) {
    return <EmptyThread />;
  }

  return (
    <div className="mx-auto flex w-full max-w-3xl flex-col gap-5 px-4 py-8">
      {messages.map((message) => (
        <MessageBubble key={message.messageId} message={message} />
      ))}
      {isSending && <ThinkingIndicator />}
      <div ref={endRef} />
    </div>
  );
}
