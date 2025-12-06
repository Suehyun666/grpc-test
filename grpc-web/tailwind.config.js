export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        brand: {
          primary: '#007DFF',
          secondary: '#00E5FF',
          gradient: 'linear-gradient(135deg, #007DFF 0%, #00E5FF 100%)',
        },
      },
    },
  },
  plugins: [],
}