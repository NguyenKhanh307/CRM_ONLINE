import { useState, useRef, useEffect, useCallback, useLayoutEffect } from 'react';
import { createPortal } from 'react-dom';
import { FiSearch, FiCheck, FiChevronDown, FiChevronUp } from 'react-icons/fi';

export interface SelectOption {
    value: string;
    label: string;
}

interface SearchableSelectProps {
    options: SelectOption[];
    value: string;
    onChange: (value: string) => void;
    placeholder?: string;
    searchPlaceholder?: string;
    className?: string;
    // nhãn hiển thị khi `value` có giá trị nhưng không nằm trong `options`
    // danh sách lookup của form lấy qua list API — vốn bị lọc owner_id cho nhân viên, lọc
    // dataAccessFromYear và giới hạn 500 dòng — nên bản ghi trỏ tới khóa ngoại ngoài phạm vi đó
    // sẽ không có option tương ứng. Không có nhãn dự phòng thì ô hiện y hệt như chưa chọn,
    // người dùng tưởng dữ liệu bị mất. Truyền tên khóa ngoại backend đã trả sẵn (*Name) vào đây
    fallbackLabel?: string | null;
    // bật chế độ tìm kiếm phía server: nhận từ khóa đã debounce để nơi dùng tự gọi api
    // có prop này nghĩa là `options` đã được server lọc sẵn -> component KHÔNG lọc lại lần nữa
    // (lọc thêm sẽ cắt mất kết quả trả về). Dùng khi bảng nguồn quá lớn để nạp sẵn — ví dụ đơn hàng
    // và liên hệ đều 10.000 bản ghi, nạp 500 dòng thì gõ mã nào cũng "Không tìm thấy".
    // ở chế độ này BẮT BUỘC truyền kèm `fallbackLabel` để bản ghi đang chọn vẫn hiện đúng tên
    // khi nó không nằm trong trang kết quả hiện tại
    onSearchChange?: (q: string) => void;
    // đang chờ kết quả tìm kiếm (chỉ dùng cùng `onSearchChange`)
    loading?: boolean;
}

// toạ độ + bề rộng để đặt panel dropdown theo nút trigger (position: fixed)
interface PanelRect {
    top: number;
    left: number;
    width: number;
}

// khoảng cách tối thiểu tới mép màn hình khi phải lật panel lên trên
const EDGE_GAP = 8;

// custom select với ô tìm kiếm và highlight lựa chọn hiện tại
// panel options render qua portal (position: fixed) để luôn nổi trên section/bảng/modal,
// không bị overflow-hidden hay stacking context của component cha cắt/che
export const SearchableSelect = ({
    options,
    value,
    onChange,
    placeholder = '— Không chọn —',
    searchPlaceholder = 'Tìm kiếm',
    className = '',
    fallbackLabel,
    onSearchChange,
    loading = false,
}: SearchableSelectProps) => {
    const [open, setOpen] = useState(false);
    const [search, setSearch] = useState('');
    const [rect, setRect] = useState<PanelRect | null>(null);
    const containerRef = useRef<HTMLDivElement>(null);
    const panelRef = useRef<HTMLDivElement>(null);
    // bounding box của trigger tại lần đo gần nhất — dùng để lật panel lên trên nếu tràn đáy
    const triggerRectRef = useRef<DOMRect | null>(null);

    // tính lại vị trí panel từ bounding box của nút trigger (mặc định đặt phía dưới)
    const updateRect = useCallback(() => {
        const el = containerRef.current;
        if (!el) return;
        const r = el.getBoundingClientRect();
        triggerRectRef.current = r;
        setRect({ top: r.bottom + 4, left: r.left, width: r.width });
    }, []);

    // Cập nhật vị trí khi mở + khi cuộn/resize để panel luôn bám theo trigger.
    useEffect(() => {
        if (!open) return;
        updateRect();
        const onScrollOrResize = () => updateRect();
        window.addEventListener('scroll', onScrollOrResize, true);
        window.addEventListener('resize', onScrollOrResize);
        return () => {
            window.removeEventListener('scroll', onScrollOrResize, true);
            window.removeEventListener('resize', onScrollOrResize);
        };
    }, [open, updateRect]);

    // Sau khi panel đã render ở vị trí mặc định (phía dưới), đo chiều cao thật rồi lật lên trên
    // nếu tràn đáy màn hình — cùng cách RowContextMenu tự lật khi chạm mép. Chạy trước khi trình
    // duyệt vẽ để không nhấp nháy. Không đưa `rect.top` vào dependency để tránh vòng lặp vô hạn.
    useLayoutEffect(() => {
        if (!open || !rect) return;
        const panelEl = panelRef.current;
        const trigger = triggerRectRef.current;
        if (!panelEl || !trigger) return;
        const height = panelEl.getBoundingClientRect().height;
        const overflowsBottom = trigger.bottom + 4 + height > window.innerHeight - EDGE_GAP;
        if (!overflowsBottom) return;
        const top = Math.max(EDGE_GAP, trigger.top - height - 4);
        setRect((prev) => (prev ? { ...prev, top } : prev));
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [open, rect?.left, rect?.width]);

    // Click-outside: đóng khi click ngoài cả trigger lẫn panel (panel nằm ở portal).
    useEffect(() => {
        const handler = (e: MouseEvent) => {
            const target = e.target as Node;
            if (
                containerRef.current && !containerRef.current.contains(target) &&
                panelRef.current && !panelRef.current.contains(target)
            ) {
                setOpen(false);
                setSearch('');
            }
        };
        document.addEventListener('mousedown', handler);
        return () => document.removeEventListener('mousedown', handler);
    }, []);

    // Server đã lọc rồi thì dùng thẳng options — lọc lại lần nữa sẽ cắt mất kết quả trả về.
    const filtered = onSearchChange
        ? options
        : options.filter((o) => o.label.toLowerCase().includes(search.toLowerCase()));

    // Chế độ tìm kiếm phía server: báo từ khóa ra ngoài sau khi ngừng gõ (cùng nhịp 350ms với DataTable).
    const searchCbRef = useRef(onSearchChange);
    searchCbRef.current = onSearchChange;
    useEffect(() => {
        if (!searchCbRef.current) return;
        const t = setTimeout(() => searchCbRef.current?.(search), 350);
        return () => clearTimeout(t);
    }, [search]);

    // Không tìm thấy option mà value vẫn có giá trị → dùng nhãn dự phòng, đừng để trông như chưa chọn
    const selectedLabel = options.find((o) => o.value === value)?.label
        ?? (value ? (fallbackLabel ?? '') : '');

    const handleSelect = (optValue: string) => {
        onChange(optValue);
        setOpen(false);
        setSearch('');
        // Trả focus về trigger để Enter đi tiếp sang ô kế trong form.
        containerRef.current?.querySelector('button')?.focus();
    };

    const baseCls =
        'w-full border border-gray-300 rounded-btn px-3 py-1.5 text-md text-text-main bg-white ' +
        'focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary transition-colors';

    return (
        <div ref={containerRef} className={`relative ${className}`}>
            {/* Trigger */}
            <button
                type="button"
                data-form-field
                onClick={() => setOpen((prev) => !prev)}
                onKeyDown={(e) => {
                    // Enter mở panel thay vì nhảy sang ô kế tiếp (useFormKeyboardNav).
                    if (e.key === 'Enter' && !open) {
                        e.preventDefault();
                        e.stopPropagation();
                        setOpen(true);
                    }
                }}
                className={`${baseCls} flex items-center justify-between pr-8 text-left`}
            >
                <span className={selectedLabel ? 'text-text-main' : 'text-gray-400'}>
                    {selectedLabel || placeholder}
                </span>
                <span className="absolute inset-y-0 right-3 flex items-center text-gray-400 pointer-events-none">
                    {open ? <FiChevronUp size={14} /> : <FiChevronDown size={14} />}
                </span>
            </button>

            {/* Dropdown — portal ra body, position: fixed, z cao hơn cả modal (z-[9999]) */}
            {open && rect && createPortal(
                <div
                    ref={panelRef}
                    style={{ position: 'fixed', top: rect.top, left: rect.left, width: rect.width }}
                    className="z-[10000] bg-white border border-gray-200 rounded-section shadow-lg"
                >
                    {/* Search */}
                    <div className="flex items-center border-b border-gray-200 px-3 py-2 gap-2">
                        <input
                            autoFocus
                            type="text"
                            value={search}
                            onChange={(e) => setSearch(e.target.value)}
                            onKeyDown={(e) => {
                                if (e.key !== 'Escape') return;
                                e.stopPropagation();
                                setOpen(false);
                                setSearch('');
                                containerRef.current?.querySelector('button')?.focus();
                            }}
                            placeholder={searchPlaceholder}
                            className="flex-1 text-md text-text-main placeholder-gray-400 outline-none bg-transparent"
                        />
                        <FiSearch size={14} className="text-gray-400 flex-shrink-0" />
                    </div>

                    {/* Options */}
                    <ul className="max-h-52 overflow-y-auto py-1">
                        {filtered.length === 0 ? (
                            <li className="px-3 py-2 text-sm text-gray-400">
                                {loading ? 'Đang tìm…' : 'Không tìm thấy'}
                            </li>
                        ) : (
                            filtered.map((opt) => {
                                const isSelected = opt.value === value;
                                return (
                                    <li
                                        key={opt.value}
                                        onClick={() => handleSelect(opt.value)}
                                        className={`flex items-center justify-between px-3 py-2 cursor-pointer text-md hover:bg-gray-50 ${
                                            isSelected ? 'text-primary' : 'text-text-main'
                                        }`}
                                    >
                                        <span>{opt.label}</span>
                                        {isSelected && (
                                            <FiCheck size={14} className="text-primary flex-shrink-0" />
                                        )}
                                    </li>
                                );
                            })
                        )}
                    </ul>
                </div>,
                document.body,
            )}
        </div>
    );
};
