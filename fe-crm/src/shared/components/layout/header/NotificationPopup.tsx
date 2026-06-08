import { useRef, useEffect, useState } from 'react';
import { FiBell, FiSliders, FiCheckSquare } from 'react-icons/fi';

const STATUS_OPTIONS = ['Tất cả', 'Chưa đọc'];
const TYPE_OPTIONS = ['Tất cả', 'Hoạt động', 'Hệ thống'];

interface Props {
    onClose: () => void;
}

export const NotificationPopup = ({ onClose }: Props) => {
    const [showFilters, setShowFilters] = useState(false);
    const [status, setStatus] = useState('Tất cả');
    const [type, setType] = useState('Tất cả');
    const [sender, setSender] = useState('');
    const ref = useRef<HTMLDivElement>(null);

    useEffect(() => {
        const handler = (e: MouseEvent) => {
            if (ref.current && !ref.current.contains(e.target as Node)) {
                onClose();
            }
        };
        document.addEventListener('mousedown', handler);
        return () => document.removeEventListener('mousedown', handler);
    }, [onClose]);

    return (
        <div
            ref={ref}
            className="absolute right-0 top-full mt-2 w-[380px] bg-white rounded-card shadow-lg border border-gray-200 z-50"
        >
            {/* Header */}
            <div className="flex items-center justify-between px-4 py-3 border-b border-gray-100">
                <span className="font-semibold text-md text-text-main">Thông báo</span>
                <div className="flex items-center gap-1">
                    <button
                        title="Đánh dấu đã đọc tất cả"
                        className="flex items-center gap-1.5 px-2.5 py-1.5 rounded hover:bg-gray-100 text-gray-500 hover:text-text-main text-sm"
                    >
                        <FiCheckSquare size={15} />

                    </button>
                    <button
                        onClick={() => setShowFilters(v => !v)}
                        className={`p-1.5 rounded hover:bg-gray-100 ${showFilters ? 'text-primary bg-blue-50' : 'text-gray-500'}`}
                        title="Bộ lọc"
                    >
                        <FiSliders size={16} />
                    </button>
                </div>
            </div>

            {/* Filter panel */}
            {showFilters && (
                <div className="px-4 py-3 border-b border-gray-100 bg-gray-50 flex flex-wrap gap-3">
                    {/* Trạng thái */}
                    <div className="flex flex-col gap-1 flex-1 min-w-[100px]">
                        <span className="text-sm text-gray-500">Trạng thái</span>
                        <select
                            value={status}
                            onChange={e => setStatus(e.target.value)}
                            className="px-2 py-1.5 bg-white border border-gray-200 rounded text-sm text-text-main focus:outline-none focus:border-primary cursor-pointer"
                        >
                            {STATUS_OPTIONS.map(o => (
                                <option key={o}>{o}</option>
                            ))}
                        </select>
                    </div>

                    {/* Loại thông báo */}
                    <div className="flex flex-col gap-1 flex-1 min-w-[110px]">
                        <span className="text-sm text-gray-500">Loại thông báo</span>
                        <select
                            value={type}
                            onChange={e => setType(e.target.value)}
                            className="px-2 py-1.5 bg-white border border-gray-200 rounded text-sm text-text-main focus:outline-none focus:border-primary cursor-pointer"
                        >
                            {TYPE_OPTIONS.map(o => (
                                <option key={o}>{o}</option>
                            ))}
                        </select>
                    </div>

                    {/* Người gửi */}
                    <div className="flex flex-col gap-1 w-full">
                        <span className="text-sm text-gray-500">Người gửi</span>
                        <input
                            type="text"
                            value={sender}
                            onChange={e => setSender(e.target.value)}
                            placeholder="Tìm theo tên người gửi..."
                            className="px-2 py-1.5 bg-white border border-gray-200 rounded text-sm text-text-main placeholder-gray-400 focus:outline-none focus:border-primary"
                        />
                    </div>
                </div>
            )}

            {/* Empty state */}
            <div className="flex items-center justify-center py-14 text-sm text-gray-400">
                Bạn chưa có thông báo nào!!
            </div>
        </div>
    );
};

interface NotificationButtonProps {
    count?: number;
}

export const NotificationButton = ({ count = 0 }: NotificationButtonProps) => {
    const [open, setOpen] = useState(false);

    return (
        <div className="relative">
            <button
                onClick={() => setOpen(v => !v)}
                className="relative p-2 rounded hover:bg-gray-100 text-gray-500 hover:text-text-main"
                title="Thông báo"
            >
                <FiBell size={18} />
                {count > 0 && (
                    <span className="absolute -top-0.5 -right-0.5 min-w-[16px] h-4 px-0.5 bg-red-500 text-white text-[10px] font-bold rounded-full flex items-center justify-center">
                        {count > 99 ? '99+' : count}
                    </span>
                )}
            </button>
            {open && <NotificationPopup onClose={() => setOpen(false)} />}
        </div>
    );
};
