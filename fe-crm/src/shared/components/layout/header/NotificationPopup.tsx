import { useRef, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { FiBell, FiCheck, FiCheckSquare, FiList, FiTrash2 } from 'react-icons/fi';
import {
    useNotificationList,
    useUnreadCount,
    useMarkNotifications,
    useDeleteNotifications,
} from '@/shared/notifications/useNotifications';
import type { NotificationResult } from '@/shared/notifications/notificationService';
import { ActionButton } from '@/shared/components/ActionButton';
import { useConfirm } from '@/shared/confirm/useConfirm';
import { MODULE_ROUTES, recordPath } from '@/shared/utils/moduleRoutes';
import type { RelatedModule } from '@/shared/types/related';
import { formatISODate } from '@/shared/utils/date';

interface Props {
    onClose: () => void;
}

/**
 * Suy phân hệ từ tiền tố `type` (phần trước dấu `_`).
 * Loại không gắn bản ghi cụ thể (vd `handover_all`) trả null → chỉ đánh dấu đã đọc, không điều hướng.
 */
const moduleOf = (type: string | null): RelatedModule | null => {
    const prefix = type?.split('_')[0];
    return prefix && prefix in MODULE_ROUTES ? (prefix as RelatedModule) : null;
};

export const NotificationPopup = ({ onClose }: Props) => {
    const ref = useRef<HTMLDivElement>(null);
    const navigate = useNavigate();
    const { data: items = [], isLoading } = useNotificationList();
    const { markOne, markAll } = useMarkNotifications();
    const { deleteBulk, deleteAll } = useDeleteNotifications();
    const { confirm } = useConfirm();

    const [selectMode, setSelectMode] = useState(false);
    const [selected, setSelected] = useState<Set<number>>(new Set());

    /** Bật/tắt chế độ chọn — tắt thì bỏ luôn danh sách đang tick. */
    const toggleSelectMode = () => {
        setSelectMode(v => !v);
        setSelected(new Set());
    };

    /** Tick/bỏ tick một thông báo trong chế độ chọn. */
    const toggleOne = (id: number) => {
        setSelected(prev => {
            const next = new Set(prev);
            if (next.has(id)) next.delete(id); else next.add(id);
            return next;
        });
    };

    const allSelected = items.length > 0 && selected.size === items.length;

    /** Bấm thông báo: chế độ chọn thì tick, ngược lại đánh dấu đã đọc + mở bản ghi đích. */
    const handleClick = (n: NotificationResult) => {
        if (selectMode) { toggleOne(n.id); return; }
        if (!n.isRead) markOne.mutate(n.id);
        const module = moduleOf(n.type);
        if (module && n.targetId != null) navigate(recordPath(module, n.targetId));
        onClose();
    };

    /** Xóa các thông báo đang tick (hỏi xác nhận trước). */
    const handleDeleteSelected = async () => {
        if (selected.size === 0) return;
        const ok = await confirm({
            message: `Xác nhận xóa ${selected.size} thông báo đã chọn?`,
            confirmLabel: 'Xóa',
            confirmDanger: true,
        });
        if (!ok) return;
        deleteBulk.mutate([...selected]);
        setSelectMode(false);
        setSelected(new Set());
    };

    /** Dọn sạch hộp thông báo (hỏi xác nhận trước). */
    const handleDeleteAll = async () => {
        const ok = await confirm({
            message: 'Xóa tất cả thông báo? Danh sách sẽ trống.',
            confirmLabel: 'Xóa',
            confirmDanger: true,
        });
        if (!ok) return;
        deleteAll.mutate();
        setSelectMode(false);
        setSelected(new Set());
    };

    useEffect(() => {
        const handler = (e: MouseEvent) => {
            const target = e.target as HTMLElement;
            // Popup xác nhận/cảnh báo render ở gốc app (ngoài ref) — bấm trong đó không được đóng popup
            if (target.closest('[data-modal-layer]')) return;
            if (ref.current && !ref.current.contains(target)) {
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
                {items.length > 0 && (
                    <div className="flex items-center gap-1">
                        <button
                            title="Đánh dấu đã đọc tất cả"
                            onClick={() => markAll.mutate()}
                            className="flex items-center gap-1.5 px-2.5 py-1.5 rounded hover:bg-gray-100 text-gray-500 hover:text-text-main text-sm"
                        >
                            <FiCheckSquare size={15} />
                        </button>
                        <button
                            title={selectMode ? 'Thoát chế độ chọn' : 'Chọn thông báo để xóa'}
                            onClick={toggleSelectMode}
                            className={`flex items-center gap-1.5 px-2.5 py-1.5 rounded hover:bg-gray-100 text-sm ${selectMode ? 'bg-gray-100 text-text-main' : 'text-gray-500 hover:text-text-main'}`}
                        >
                            <FiList size={15} />
                        </button>
                        <button
                            title="Xóa tất cả thông báo"
                            onClick={handleDeleteAll}
                            className="flex items-center gap-1.5 px-2.5 py-1.5 rounded hover:bg-red-50 text-gray-500 hover:text-danger text-sm"
                        >
                            <FiTrash2 size={15} />
                        </button>
                    </div>
                )}
            </div>

            {/* Chọn tất cả */}
            {selectMode && items.length > 0 && (
                <label className="flex items-center gap-2 px-4 py-2 border-b border-gray-100 text-xs text-gray-500 cursor-pointer">
                    <input
                        type="checkbox"
                        checked={allSelected}
                        onChange={() => setSelected(allSelected ? new Set() : new Set(items.map(n => n.id)))}
                    />
                    Chọn tất cả
                </label>
            )}

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
                            onClick={() => handleClick(n)}
                            className={`w-full text-left px-4 py-3 border-b border-gray-50 hover:bg-gray-50 ${n.isRead ? '' : 'bg-blue-50/50'}`}
                        >
                            <div className="flex items-start gap-2">
                                {/* Ô đánh dấu thuần hiển thị — <input> lồng trong <button> là HTML không hợp lệ */}
                                {selectMode && (
                                    <span
                                        className={`mt-1 w-3.5 h-3.5 shrink-0 rounded-sm border flex items-center justify-center ${selected.has(n.id) ? 'bg-primary border-primary text-white' : 'border-gray-300'}`}
                                    >
                                        {selected.has(n.id) && <FiCheck size={11} />}
                                    </span>
                                )}
                                {!n.isRead && <span className="mt-1.5 w-2 h-2 rounded-full bg-primary shrink-0" />}
                                <div className={n.isRead && !selectMode ? 'pl-4' : ''}>
                                    <div className="text-sm font-medium text-text-main">{n.title}</div>
                                    <div className="text-xs text-gray-500 mt-0.5">{n.content}</div>
                                    <div className="text-[11px] text-gray-400 mt-1">{formatISODate(n.createdAt)}</div>
                                </div>
                            </div>
                        </button>
                    ))
                )}
            </div>

            {/* Thanh hành động của chế độ chọn */}
            {selectMode && items.length > 0 && (
                <div className="flex items-center justify-between gap-1.5 px-4 py-2.5 border-t border-gray-100">
                    <span className="text-xs text-gray-500">Đã chọn {selected.size}</span>
                    <div className="flex items-center gap-1.5">
                        <ActionButton onClick={toggleSelectMode}>Hủy</ActionButton>
                        <ActionButton
                            variant="dangerSolid"
                            icon={FiTrash2}
                            disabled={selected.size === 0}
                            onClick={handleDeleteSelected}
                        >
                            Xóa
                        </ActionButton>
                    </div>
                </div>
            )}
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
