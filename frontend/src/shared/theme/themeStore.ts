import { create } from 'zustand';
import { persist } from 'zustand/middleware';

export type Theme = 'light' | 'dark';

const STORAGE_KEY = 'estate.theme';

/** Reads the OS-level colour-scheme preference (defaults to light). */
function systemTheme(): Theme {
  if (typeof window === 'undefined' || !window.matchMedia) return 'light';
  return window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
}

/** Toggles the `dark` class on <html>, which drives all Tailwind `dark:` styles. */
export function applyTheme(theme: Theme): void {
  if (typeof document === 'undefined') return;
  const root = document.documentElement;
  root.classList.toggle('dark', theme === 'dark');
  root.style.colorScheme = theme;
}

interface ThemeState {
  theme: Theme;
  setTheme: (theme: Theme) => void;
  toggle: () => void;
}

export const useThemeStore = create<ThemeState>()(
  persist(
    (set, get) => ({
      theme: systemTheme(),
      setTheme: (theme) => {
        applyTheme(theme);
        set({ theme });
      },
      toggle: () => get().setTheme(get().theme === 'dark' ? 'light' : 'dark'),
    }),
    {
      name: STORAGE_KEY,
      onRehydrateStorage: () => (state) => {
        // Re-apply the persisted choice to <html> after hydration.
        if (state) applyTheme(state.theme);
      },
    },
  ),
);

/**
 * Applies the persisted (or system) theme to <html> as early as possible, before
 * React renders, to avoid a flash of the wrong theme.
 */
export function initTheme(): void {
  let theme: Theme = systemTheme();
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (raw) {
      const parsed = JSON.parse(raw) as { state?: { theme?: Theme } };
      if (parsed.state?.theme === 'light' || parsed.state?.theme === 'dark') {
        theme = parsed.state.theme;
      }
    }
  } catch {
    /* fall back to system preference */
  }
  applyTheme(theme);
}
