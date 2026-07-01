import {
    FiUserCheck, FiPlay, FiCheck, FiXCircle, FiInbox, FiSearch,
    FiCheckCircle, FiLock, FiRotateCcw,
} from 'react-icons/fi';
import type { IconType } from 'react-icons';
import type { TicketResult } from '../types/ticketTypes';
import type { TicketAction } from '../hooks/useTicketWorkflow';

interface Props {
    ticket: TicketResult;
    onAction: (action: TicketAction) => void;
}

interface Btn { action: TicketAction; label: string; icon: IconType; cls: string; }

/** Tính danh sách hành động hợp lệ theo trạng thái + loại phiếu. */
function actionsFor(t: TicketResult): Btn[] {
    const isReturn = t.type === 'return' || t.type === 'exchange';
    const primary = 'bg-primary text-white hover:opacity-90';
    const success = 'bg-success text-white hover:opacity-90';
    const danger = 'bg-danger text-white hover:opacity-90';
    const neutral = 'border border-gray-300 text-gray-600 hover:bg-gray-50';

    switch (t.status) {
        case 'new':
            return [{ action: 'assign', label: 'Giao xử lý', icon: FiUserCheck, cls: primary }];
        case 'assigned':
            return [{ action: 'start', label: 'Bắt đầu xử lý', icon: FiPlay, cls: primary }];
        case 'in_progress':
        case 'reopened':
            return isReturn
                ? [{ action: 'approve', label: 'Duyệt', icon: FiCheck, cls: success },
                   { action: 'reject', label: 'Từ chối', icon: FiXCircle, cls: danger }]
                : [{ action: 'resolve', label: 'Giải quyết', icon: FiCheckCircle, cls: success }];
        case 'approved':
            return [{ action: 'receive', label: 'Đã nhận hàng', icon: FiInbox, cls: primary }];
        case 'received':
            return [{ action: 'inspect', label: 'Đã kiểm hàng', icon: FiSearch, cls: primary }];
        case 'inspected':
            return [{ action: 'complete', label: 'Hoàn tất', icon: FiCheckCircle, cls: success }];
        case 'rejected':
            return [{ action: 'close', label: 'Đóng phiếu', icon: FiLock, cls: neutral }];
        case 'resolved':
            return [{ action: 'close', label: 'Đóng phiếu', icon: FiLock, cls: neutral },
                    { action: 'reopen', label: 'Mở lại', icon: FiRotateCcw, cls: neutral }];
        case 'closed':
            return [{ action: 'reopen', label: 'Mở lại', icon: FiRotateCcw, cls: neutral }];
        default:
            return [];
    }
}

/** Nút hành động luồng trạng thái phiếu — hiển thị theo status + type. */
export function TicketWorkflowButtons({ ticket, onAction }: Props) {
    const btns = actionsFor(ticket);
    if (btns.length === 0) return null;
    return (
        <div className="flex flex-wrap items-center gap-2">
            {btns.map(b => (
                <button key={b.action} type="button" onClick={() => onAction(b.action)}
                    className={`flex items-center gap-1.5 px-3 py-1.5 rounded-btn text-md font-medium ${b.cls}`}>
                    <b.icon size={14} /> {b.label}
                </button>
            ))}
        </div>
    );
}
