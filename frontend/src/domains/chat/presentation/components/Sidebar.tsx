import { BrandLockup } from '@/shared/ui/BrandMark';
import { NewChatButton } from './NewChatButton';
import { ConversationList } from './ConversationList';
import { UserProfileButton } from './UserProfileButton';

/** Left panel: brand + new chat + history (top), user profile pinned bottom. */
export function Sidebar({ onNavigate }: { onNavigate?: () => void }) {
  return (
    <div className="flex h-full w-72 flex-col border-r border-[#0E2A1F] bg-forest-deep dark:border-night-edge dark:bg-night-bar">
      <div className="px-[18px] pb-[18px] pt-5">
        <BrandLockup />
      </div>

      <div className="shrink-0 px-3.5">
        <NewChatButton onDone={onNavigate} />
      </div>

      <div className="scrollbar-dark flex-1 overflow-y-auto px-2.5 pb-2">
        <p className="px-[18px] pb-2.5 pt-[26px] font-mono text-[10px] uppercase tracking-[0.14em] text-moss-faint">
          Recent clients
        </p>
        <ConversationList onSelect={onNavigate} />
      </div>

      <div className="shrink-0 border-t border-paper/10 p-3.5 dark:border-night-edge">
        <UserProfileButton />
      </div>
    </div>
  );
}