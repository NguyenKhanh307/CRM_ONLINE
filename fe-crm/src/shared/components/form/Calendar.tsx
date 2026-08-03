import { useState, useEffect } from 'react';
import { FiChevronLeft, FiChevronRight } from 'react-icons/fi';

interface CalendarProps {
    // ngày đang chọn dạng ISO yyyy-mm-dd (hoặc rỗng)
    value: string;
    // gọi khi chọn một ngày — trả ISO yyyy-mm-dd
    onSelect: (iso: string) => void;
}

const WEEKDAYS = ['CN', 'T2', 'T3', 'T4', 'T5', 'T6', 'T7'];
const MONTHS = [
    'Tháng 1', 'Tháng 2', 'Tháng 3', 'Tháng 4', 'Tháng 5', 'Tháng 6',
    'Tháng 7', 'Tháng 8', 'Tháng 9', 'Tháng 10', 'Tháng 11', 'Tháng 12',
];

const pad = (n: number) => String(n).padStart(2, '0');
const toISO = (y: number, m: number, d: number) => `${y}-${pad(m + 1)}-${pad(d)}`;

// lưới lịch chọn ngày (dd/mm/yyyy) thuần — không tự dựng portal
// dùng chung cho DateInput và DateTimeInput; component cha lo việc đặt nổi
export const Calendar = ({ value, onSelect }: CalendarProps) => {
    // tháng đang hiển thị: lấy từ value nếu hợp lệ, mặc định tháng hiện tại
    const initial = value ? new Date(value) : new Date();
    const base = isNaN(initial.getTime()) ? new Date() : initial;
    const [viewYear, setViewYear] = useState(base.getFullYear());
    const [viewMonth, setViewMonth] = useState(base.getMonth());

    // khi value đổi từ ngoài (vd mở lại modal), nhảy lịch tới tháng của value
    useEffect(() => {
        if (!value) return;
        const d = new Date(value);
        if (!isNaN(d.getTime())) {
            setViewYear(d.getFullYear());
            setViewMonth(d.getMonth());
        }
    }, [value]);

    const firstDay = new Date(viewYear, viewMonth, 1).getDay();      // thứ của ngày 1
    const daysInMonth = new Date(viewYear, viewMonth + 1, 0).getDate();

    const prevMonth = () => {
        if (viewMonth === 0) { setViewMonth(11); setViewYear((y) => y - 1); }
        else setViewMonth((m) => m - 1);
    };
    const nextMonth = () => {
        if (viewMonth === 11) { setViewMonth(0); setViewYear((y) => y + 1); }
        else setViewMonth((m) => m + 1);
    };

    const today = new Date();
    const todayISO = toISO(today.getFullYear(), today.getMonth(), today.getDate());

    // các ô: đệm đầu tháng bằng null, rồi 1..daysInMonth
    const cells: (number | null)[] = [
        ...Array(firstDay).fill(null),
        ...Array.from({ length: daysInMonth }, (_, i) => i + 1),
    ];

    return (
        <div className="p-2 w-64">
            {/* Header đổi tháng/năm */}
            <div className="flex items-center justify-between mb-2 px-1">
                <button type="button" onClick={prevMonth}
                    className="p-1 rounded hover:bg-gray-100 text-text-main">
                    <FiChevronLeft size={16} />
                </button>
                <span className="text-md font-medium text-text-main">
                    {MONTHS[viewMonth]} {viewYear}
                </span>
                <button type="button" onClick={nextMonth}
                    className="p-1 rounded hover:bg-gray-100 text-text-main">
                    <FiChevronRight size={16} />
                </button>
            </div>

            {/* Hàng thứ */}
            <div className="grid grid-cols-7 mb-1">
                {WEEKDAYS.map((w) => (
                    <div key={w} className="text-center text-sm text-gray-400 py-1">{w}</div>
                ))}
            </div>

            {/* Lưới ngày */}
            <div className="grid grid-cols-7 gap-y-1">
                {cells.map((day, idx) => {
                    if (day === null) return <div key={`e${idx}`} />;
                    const iso = toISO(viewYear, viewMonth, day);
                    const isSelected = iso === value;
                    const isToday = iso === todayISO;
                    return (
                        <button
                            key={iso}
                            type="button"
                            onClick={() => onSelect(iso)}
                            className={`h-8 w-8 mx-auto flex items-center justify-center rounded-full text-md transition-colors ${
                                isSelected
                                    ? 'bg-primary text-white'
                                    : isToday
                                        ? 'text-primary font-medium hover:bg-gray-100'
                                        : 'text-text-main hover:bg-gray-100'
                            }`}
                        >
                            {day}
                        </button>
                    );
                })}
            </div>
        </div>
    );
};
