/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        brand: {
          50: '#E8F1F8',
          100: '#D1F8EF',
          200: '#A1E3F9',
          300: '#578FCA',
          400: '#3674B5',
          500: '#2C5FA0',
          600: '#1E4176',
          700: '#0F2847',
        },
      },
    },
  },
  plugins: [],
}