// một tab: khóa, nhãn, và số lượng bản ghi (tùy chọn) hiện dưới dạng badge
export interface TabDef<K extends string = string> {
    key: K;
    label: string;
    count?: number;
}
// props của thanh tab dùng chung cho trang chi tiết (controlled)
interface TabsProps<K extends string> {
    // danh sách tab (khóa, nhãn, số lượng)
    tabs: readonly TabDef<K>[];
    // tab đang được chọn
    active: K;
    // callback khi chọn tab khác
    onChange: (key: K) => void;
}

// thanh tab dùng chung cho trang chi tiết (controlled)
// badge số lượng chỉ hiện khi `count` được truyền
export const Tabs = <K extends string>({ tabs, active, onChange }: TabsProps<K>) => (
    <div className="flex border-b border-gray-200 px-4 overflow-x-auto">
        {tabs.map(tab => (
            <button
                key={tab.key}
                onClick={() => onChange(tab.key)}
                className={`px-4 py-3 text-sm font-medium border-b-2 -mb-px transition-colors whitespace-nowrap flex items-center gap-1.5 ${active === tab.key
                    ? 'border-primary text-primary'
                    : 'border-transparent text-gray-500 hover:text-text-main'
                    }`}
            >
                {tab.label}
                {tab.count != null && (
                    <span className={`px-1.5 rounded text-sm ${active === tab.key ? 'bg-primary text-white' : 'bg-gray-100 text-gray-500'
                        }`}>
                        {tab.count}
                    </span>
                )}
            </button>
        ))}
    </div>
);
