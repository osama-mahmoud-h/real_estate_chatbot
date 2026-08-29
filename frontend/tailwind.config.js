/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{ts,tsx}'],
  darkMode: 'class',
  theme: {
    extend: {
      fontFamily: {
        sans: ['IBM Plex Sans', 'Segoe UI', 'system-ui', '-apple-system', 'sans-serif'],
        serif: ['Newsreader', 'Georgia', 'Times New Roman', 'serif'],
        mono: ['IBM Plex Mono', 'ui-monospace', 'Menlo', 'monospace'],
      },
      colors: {
        // Light surfaces
        paper: {
          DEFAULT: '#FBFAF6',
          sunk: '#F4F1E8',
          card: '#FFFFFF',
        },
        // Brand greens, light and dark alike
        forest: {
          deep: '#14382A',
          DEFAULT: '#1C4F35',
          accent: '#2E7D53',
          bright: '#4E9E73',
          soft: '#E7F0EA',
        },
        // Text on paper
        ink: {
          DEFAULT: '#16211B',
          body: '#33403A',
          muted: '#55625A',
          faint: '#8A9490',
        },
        // Text on forest (sidebar, brand panel)
        moss: {
          bright: '#D6E3DA',
          DEFAULT: '#A9BFB2',
          muted: '#7E9C8A',
          faint: '#5C7A69',
        },
        // Hairlines on paper
        edge: {
          DEFAULT: '#E4E0D4',
          strong: '#D3CDBC',
        },
        // Dark surfaces
        night: {
          DEFAULT: '#10170F',
          bar: '#0B120C',
          panel: '#17281D',
          raised: '#1F3527',
          edge: '#24382B',
          strong: '#2C4232',
        },
        // Text on night
        mist: {
          DEFAULT: '#E8EDE7',
          dim: '#C6D2C7',
          muted: '#9BAA9E',
          faint: '#74857A',
        },
        brick: {
          DEFAULT: '#A33A2A',
          bright: '#C9584A',
          soft: '#F6EBE8',
        },
      },
      borderRadius: {
        control: '10px',
        card: '12px',
        bubble: '14px',
      },
      keyframes: {
        'bounce-dot': {
          '0%, 80%, 100%': { transform: 'translateY(0)', opacity: '0.35' },
          '40%': { transform: 'translateY(-3px)', opacity: '1' },
        },
        'fade-in': {
          from: { opacity: '0', transform: 'translateY(6px)' },
          to: { opacity: '1', transform: 'translateY(0)' },
        },
        'enter-left': {
          from: { opacity: '0', transform: 'translateX(-10px) translateY(4px)' },
          to: { opacity: '1', transform: 'translateX(0) translateY(0)' },
        },
        'enter-right': {
          from: { opacity: '0', transform: 'translateX(10px) translateY(4px)' },
          to: { opacity: '1', transform: 'translateX(0) translateY(0)' },
        },
      },
      animation: {
        'bounce-dot': 'bounce-dot 1.4s infinite ease-in-out both',
        'fade-in': 'fade-in 0.25s ease-out',
        'enter-left': 'enter-left 0.28s cubic-bezier(0.22, 1, 0.36, 1) both',
        'enter-right': 'enter-right 0.28s cubic-bezier(0.22, 1, 0.36, 1) both',
      },
    },
  },
  plugins: [],
};