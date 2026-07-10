/** Hiển thị một tổ hợp phím dạng <kbd>, ví dụ ['Alt','N'] → Alt + N. */
export const Kbd = ({ keys }: { keys: string[] }) => (
    <span className="flex items-center gap-1">
        {keys.map((k, i) => (
            <span key={i} className="flex items-center gap-1">
                <kbd className="px-1.5 py-0.5 text-[11px] font-mono bg-gray-100 border border-gray-300 rounded text-gray-700">
                    {k}
                </kbd>
                {i < keys.length - 1 && <span className="text-gray-400 text-xs">+</span>}
            </span>
        ))}
    </span>
);
