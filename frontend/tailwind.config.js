/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{ts,tsx}'],
  darkMode: 'class',
  theme: {
    extend: {
      fontFamily: {
        sans: [
          'Inter',
          'ui-sans-serif',
          'system-ui',
          '-apple-system',
          'Segoe UI',
          'Roboto',
          'sans-serif',
        ],
      },
      colors: {
        primary: {
          50: '#eef2ff',
          100: '#e0e7ff',
          200: '#c7d2fe',
          300: '#a5b4fc',
          400: '#818cf8',
          500: '#6366f1',
          600: '#4f46e5',
          700: '#4338ca',
          800: '#3730a3',
          900: '#312e81',
        },
        accent: {
          400: '#a78bfa',
          500: '#8b5cf6',
          600: '#7c3aed',
        },
        ink: {
          800: '#141827',
          900: '#0d1020',
          950: '#080a16',
        },
      },
      backgroundImage: {
        'brand-gradient': 'linear-gradient(135deg, #7c3aed 0%, #4f46e5 100%)',
        'brand-gradient-soft': 'linear-gradient(135deg, #ede9fe 0%, #e0e7ff 100%)',
      },
      boxShadow: {
        soft: '0 1px 2px rgba(15, 23, 42, 0.04), 0 4px 16px rgba(15, 23, 42, 0.06)',
        lift: '0 10px 30px -10px rgba(79, 70, 229, 0.35)',
        glass: '0 8px 32px rgba(15, 23, 42, 0.10)',
      },
      keyframes: {
        'bounce-dot': {
          '0%, 80%, 100%': { transform: 'scale(0.6)', opacity: '0.4' },
          '40%': { transform: 'scale(1)', opacity: '1' },
        },
        'fade-in': {
          from: { opacity: '0', transform: 'translateY(6px)' },
          to: { opacity: '1', transform: 'translateY(0)' },
        },
        'enter-left': {
          from: { opacity: '0', transform: 'translateX(-14px) translateY(6px) scale(0.97)' },
          to: { opacity: '1', transform: 'translateX(0) translateY(0) scale(1)' },
        },
        'enter-right': {
          from: { opacity: '0', transform: 'translateX(14px) translateY(6px) scale(0.97)' },
          to: { opacity: '1', transform: 'translateX(0) translateY(0) scale(1)' },
        },
        float: {
          '0%, 100%': { transform: 'translateY(0)' },
          '50%': { transform: 'translateY(-8px)' },
        },
        'blob-drift': {
          '0%, 100%': { transform: 'translate(0, 0) scale(1)' },
          '33%': { transform: 'translate(20px, -30px) scale(1.08)' },
          '66%': { transform: 'translate(-15px, 15px) scale(0.94)' },
        },
      },
      animation: {
        'bounce-dot': 'bounce-dot 1.4s infinite ease-in-out both',
        'fade-in': 'fade-in 0.25s ease-out',
        'enter-left': 'enter-left 0.34s cubic-bezier(0.22, 1, 0.36, 1) both',
        'enter-right': 'enter-right 0.34s cubic-bezier(0.22, 1, 0.36, 1) both',
        float: 'float 6s ease-in-out infinite',
        'blob-drift': 'blob-drift 18s ease-in-out infinite',
      },
    },
  },
  plugins: [],
};
