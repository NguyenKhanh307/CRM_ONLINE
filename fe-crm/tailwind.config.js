/** @type {import('tailwindcss').Config} */
export default {
    content: ['./src/**/*.{ts,tsx}'],
    theme: {
        extend: {
            colors: {
                primary:     '#1677ff',
                danger:      '#ff4d4f',
                success:     '#52c41a',
                warning:     '#faad14',
                'text-main': '#333333',
                'bg-main':   '#f5f5f5',
            },
            fontFamily: {
                sans: ['Inter', 'sans-serif'],
            },
            fontSize: {
                sm:    '12px',
                table: '13px',
                md:    '14px',
                title: '14px',
                lg:    '16px',
                xl:    '20px',
            },
            borderRadius: {
                btn:     '6px',
                section: '6px',
                card:    '8px',
            },
        },
    },
    plugins: [],
}
