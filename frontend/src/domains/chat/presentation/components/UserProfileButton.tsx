import { useState, useRef, useEffect } from 'react';
import { Avatar } from '@/shared/ui/Avatar';
import { useAuthStore } from '@/domains/auth/application/authStore';
import { fullName } from '@/domains/auth/domain/User';
import { useLogout } from '@/domains/auth/application/useLogout';

export function UserProfileButton() {
  const user = useAuthStore((s) => s.user);
  const logout = useLogout();
  const [open, setOpen] = useState(false);
  const ref = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (!open) return;
    const onClick = (e: MouseEvent) => {
      if (ref.current && !ref.current.contains(e.target as Node)) setOpen(false);
    };
    document.addEventListener('mousedown', onClick);
    return () => document.removeEventListener('mousedown', onClick);
  }, [open]);

  if (!user) return null;
  const name = fullName(user);

  return (
    <div ref={ref} className="relative">
      {open && (
        <div className="absolute bottom-full left-0 mb-2 w-full overflow-hidden rounded-card border border-edge bg-paper-card py-1 dark:border-night-strong dark:bg-night-panel">
          <button
            type="button"
            onClick={logout}
            className="flex w-full items-center gap-2.5 px-3 py-2.5 text-sm font-medium text-brick transition-colors hover:bg-paper-sunk dark:text-brick-bright dark:hover:bg-white/5"
          >
            <LogoutIcon />
            Log out
          </button>
        </div>
      )}
      <button
        type="button"
        onClick={() => setOpen((v) => !v)}
        className="flex w-full items-center gap-[11px] rounded-control px-1 py-1.5 text-left transition-colors hover:bg-paper/5 focus-visible:outline-none focus-visible:ring-[3px] focus-visible:ring-forest-bright/30"
      >
        <Avatar name={name} />
        <span className="min-w-0 flex-1">
          <span className="block truncate text-[13px] font-medium text-paper">{name}</span>
          <span className="block truncate text-[11px] text-moss-muted">{user.email}</span>
        </span>
        <ChevronIcon />
      </button>
    </div>
  );
}

function ChevronIcon() {
  return (
    <svg
      width="16"
      height="16"
      viewBox="0 0 24 24"
      fill="none"
      className="shrink-0 text-moss-muted"
      aria-hidden="true"
    >
      <path
        d="M6 9l6 6 6-6"
        stroke="currentColor"
        strokeWidth="2"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
    </svg>
  );
}

function LogoutIcon() {
  return (
    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" aria-hidden="true">
      <path
        d="M16 17l5-5-5-5M21 12H9M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"
        stroke="currentColor"
        strokeWidth="1.8"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
    </svg>
  );
}