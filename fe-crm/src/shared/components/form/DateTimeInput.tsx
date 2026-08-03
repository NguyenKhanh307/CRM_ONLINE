import { useState, useEffect } from 'react';
import { createPortal } from 'react-dom';
import { FiCalendar } from 'react-icons/fi';
import { inputCls } from './formStyles';
import { formatISODate, parseVNDate } from '@/shared/utils/date';
import { Calendar } from './Calendar';
import { useAnchoredPanel } from './useAnchoredPanel';

interface DateTimeInputProps {
    // giá trị ISO yyyy-mm-ddTHH:mm (khớp form state). Rỗng nếu chưa chọn
    value: string;
    // phát ISO yyyy-mm-ddTHH:mm khi có ngày, hoặc '' khi trống
    onChange: (iso: string) => void;
    className?: string;
    placeholder?: string;
}

// ghép chuỗi số thành mặt nạ dd/mm/yyyy (tự chèn dấu '/')
function maskDate(raw: string): string {
    const digits = raw.replace(/\D/g, '').slice(0, 8);
    const dd = digits.slice(0, 2);
    const mm = digits.slice(2, 4);
    const yyyy = digits.slice(4, 8);
    let out = dd;
    if (digits.length >= 3) out += '/' + mm;
    if (digits.length >= 5) out += '/' + yyyy;
    return out;
}

// tách ISO datetime "yyyy-mm-ddTHH:mm" thành phần ngày ISO và phần giờ
function splitDateTime(value: string): { dateISO: string; time: string } {
    if (!value) return { dateISO: '', time: '' };
    const [datePart, timePart = ''] = value.split('T');
    return { dateISO: datePart, time: timePart.slice(0, 5) };
}

// ô nhập ngày giờ: hiển thị & nhập ngày theo dd/mm/yyyy (kèm lịch popup) + ô giờ HH:mm,
// nhưng lưu/emit ISO yyyy-mm-ddTHH:mm. Thay thế native <input type="datetime-local">
export const DateTimeInput = ({
    value,
    onChange,
    className = '',
    placeholder = 'dd/mm/yyyy',
}: DateTimeInputProps) => {
    const { open, setOpen, rect, containerRef, panelRef } = useAnchoredPanel();
    const init = splitDateTime(value);
    const [dateText, setDateText] = useState(formatISODate(init.dateISO));
    const [time, setTime] = useState(init.time);

    // đồng bộ khi value đổi từ ngoài
    useEffect(() => {
        const s = splitDateTime(value);
        setDateText(formatISODate(s.dateISO));
        setTime(s.time);
    }, [value]);

    // phát ISO ghép từ ngày (ISO) + giờ; nếu không có ngày -> ''
    const emit = (dateISO: string, t: string) => {
        if (!dateISO) { onChange(''); return; }
        onChange(t ? `${dateISO}T${t}` : `${dateISO}T00:00`);
    };

    const handleDateBlur = () => {
        if (!dateText.trim()) { emit('', time); return; }
        const iso = parseVNDate(dateText);
        if (iso) { setDateText(formatISODate(iso)); emit(iso, time); }
        else setDateText(formatISODate(init.dateISO));
    };

    const handleTimeChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const t = e.target.value;
        setTime(t);
        const iso = parseVNDate(dateText);
        if (iso) emit(iso, t);
    };

    const handleSelect = (iso: string) => {
        setDateText(formatISODate(iso));
        emit(iso, time);
        setOpen(false);
    };

    return (
        <div className={`flex gap-2 ${className}`}>
            <div ref={containerRef} className="relative flex-1 min-w-[150px]">
                <input
                    type="text"
                    inputMode="numeric"
                    value={dateText}
                    placeholder={placeholder}
                    onChange={(e) => setDateText(maskDate(e.target.value))}
                    onBlur={handleDateBlur}
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
                        <Calendar value={splitDateTime(value).dateISO} onSelect={handleSelect} />
                    </div>,
                    document.body,
                )}
            </div>
            <input
                type="time"
                value={time}
                onChange={handleTimeChange}
                className={`${inputCls} w-24`}
            />
        </div>
    );
};
