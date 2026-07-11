import { NewChatButton } from './NewChatButton';
import { ConversationList } from './ConversationList';
import { UserProfileButton } from './UserProfileButton';

/** Left panel: brand + new chat + history (top), user profile pinned bottom. */
export function Sidebar({ onNavigate }: { onNavigate?: () => void }) {
  return (
    <div className="flex h-full w-72 flex-col bg-ink-900 text-slate-300">
      <div className="flex items-center gap-2.5 px-4 pb-2 pt-4">
        <div className="flex h-9 w-9 items-center justify-center rounded-xl bg-brand-gradient text-base shadow-lift">
          🏠
        </div>
        <div className="leading-tight">
          <p className="text-sm font-semibold text-white">Estate Assistant</p>
          <p className="text-[11px] text-slate-500">AI property search</p>
        </div>
      </div>

      <div className="shrink-0 p-3">
        <NewChatButton onDone={onNavigate} />
      </div>

      <div className="flex-1 overflow-y-auto px-2 pb-2 scrollbar-dark">
        <p className="px-3 pb-1.5 pt-2 text-[11px] font-semibold uppercase tracking-wider text-slate-500">
          Recent
        </p>
        <ConversationList onSelect={onNavigate} />
      </div>

      <div className="shrink-0 border-t border-white/10 p-2">
        <UserProfileButton />
      </div>
    </div>
  );
}
