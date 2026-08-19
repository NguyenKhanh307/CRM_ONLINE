import { useState, useEffect } from 'react';
import { createPortal } from 'react-dom';
import { FiCalendar } from 'react-icons/fi';
import { inputCls } from './formStyles';
import { formatISODate, parseVNDate, maskDate } from '@/shared/utils/date';
import { Calendar } from './Calendar';
import { useAnchoredPanel } from './useAnchoredPanel';

interface DateInputProps {
    // giá trị ISO yyyy-mm-dd (khớp form state). Rỗng nếu chưa chọn
    value: string;
    // phát ISO yyyy-mm-dd khi hợp lệ, hoặc '' khi trống/không hợp lệ
    onChange: (iso: string) => void;
    className?: string;
    placeholder?: string;
}

// ô nhập ngày hiển thị & nhập theo dd/mm/yyyy, kèm lịch popup, nhưng lưu/emit ISO yyyy-mm-dd
// thay thế native <input type="date"> để đồng bộ định dạng trên mọi trình duyệt
// panel lịch render qua portal (position: fixed) để nổi trên cả modal
export const DateInput = ({
    value,
    onChange,
    className = '',
    placeholder = 'dd/mm/yyyy',
}: DateInputProps) => {
    const { open, setOpen, rect, containerRef, panelRef } = useAnchoredPanel();
    const [text, setText] = useState(formatISODate(value));

    // đồng bộ text hiển thị khi value đổi từ ngoài (populate modal sửa, reset form…)
    useEffect(() => {
        setText(formatISODate(value));
    }, [value]);

    // commit giá trị đang gõ thành ISO nếu hợp lệ
    const commit = (raw: string) => {
        if (!raw.trim()) { onChange(''); return; }
        const iso = parseVNDate(raw);
        if (iso) onChange(iso);
    };
    // khi gõ, chỉ cập nhật text hiển thị (chưa commit)
    const handleTextChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setText(maskDate(e.target.value));
    };

    // chuẩn hóa lại hiển thị theo giá trị đã commit; nếu không hợp lệ -> khôi phục value cũ
    const handleBlur = () => {
        if (!text.trim()) { onChange(''); return; }
        const iso = parseVNDate(text);
        if (iso) { onChange(iso); setText(formatISODate(iso)); }
        else setText(formatISODate(value));
    };

    const handleSelect = (iso: string) => {
        onChange(iso);
        setText(formatISODate(iso));
        setOpen(false);
    };

    return (
        <div ref={containerRef} className={`relative ${className}`}>
            <input
                type="text"
                inputMode="numeric"
                value={text}
                placeholder={placeholder}
                onChange={handleTextChange}
                onKeyDown={(e) => { if (e.key === 'Enter') commit(text); }}
                onBlur={handleBlur}
                className={`${inputCls} pr-9`}
            />
            <button
                type="button"
                onClick={() => setOpen((p) => !p)}
                className="absolute inset-y-0 right-2 flex items-center text-gray-400 hover:text-primary"
                tabIndex={-1}
            >
                <FiCalendar size={16} />
            </button>

            {open && rect && createPortal(
                <div
                    ref={panelRef}
                    style={{ position: 'fixed', top: rect.top, left: rect.left }}
                    className="z-[10000] bg-white border border-gray-200 rounded-section shadow-lg"
                >
                    <Calendar value={value} onSelect={handleSelect} />
                </div>,
                document.body,
            )}
        </div>
    );
};
