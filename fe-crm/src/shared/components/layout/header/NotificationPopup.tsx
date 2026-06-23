import { useRef, useEffect, useState } from 'react';
import { FiBell, FiCheckSquare } from 'react-icons/fi';
import { useNotificationList, useUnreadCount, useMarkNotifications } from '@/shared/notifications/useNotifications';
import { formatISODate } from '@/shared/utils/date';

interface Props {
    onClose: () => void;
}

export const NotificationPopup = ({ onClose }: Props) => {
    const ref = useRef<HTMLDivElement>(null);
    const { data: items = [], isLoading } = useNotificationList();
    const { markOne, markAll } = useMarkNotifications();

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
                <button
                    title="Đánh dấu đã đọc tất cả"
                    onClick={() => markAll.mutate()}
                    className="flex items-center gap-1.5 px-2.5 py-1.5 rounded hover:bg-gray-100 text-gray-500 hover:text-text-main text-sm"
                >
                    <FiCheckSquare size={15} />
                </button>
            </div>

            {/* List */}
            <div className="max-h-[360px] overflow-y-auto">
                {isLoading ? (
                    <div className="flex items-center justify-center py-14 text-sm text-gray-400">Đang tải...</div>
                ) : items.length === 0 ? (
                    <div className="flex items-center justify-center py-14 text-sm text-gray-400">
                        Bạn chưa có thông báo nào!!
                    </div>
                ) : (
                    items.map((n) => (
                        <button
                            key={n.id}
                            onClick={() => !n.isRead && markOne.mutate(n.id)}
                            className={`w-full text-left px-4 py-3 border-b border-gray-50 hover:bg-gray-50 ${n.isRead ? '' : 'bg-blue-50/50'}`}
                        >
                            <div className="flex items-start gap-2">
                                {!n.isRead && <span className="mt-1.5 w-2 h-2 rounded-full bg-primary shrink-0" />}
                                <div className={n.isRead ? 'pl-4' : ''}>
                                    <div className="text-sm font-medium text-text-main">{n.title}</div>
                                    <div className="text-xs text-gray-500 mt-0.5">{n.content}</div>
                                    <div className="text-[11px] text-gray-400 mt-1">{formatISODate(n.createdAt)}</div>
                                </div>
                            </div>
                        </button>
                    ))
                )}
            </div>
        </div>
    );
};

export const NotificationButton = () => {
    const [open, setOpen] = useState(false);
    const { data: count = 0 } = useUnreadCount();

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
