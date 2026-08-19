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

// tính danh sách hành động hợp lệ theo trạng thái + loại phiếu
function actionsFor(t: TicketResult): Btn[] {
    const isReturn = t.type === 'return' || t.type === 'exchange';
    const primary = 'bg-primary text-white hover:opacity-90';
    const success = 'bg-success text-white hover:opacity-90';
    const danger = 'bg-danger text-white hover:opacity-90';
    const neutral = 'border border-gray-300 text-gray-600 hover:bg-gray-50';

    switch (t.status) {
        case 'new':
            // support/complaint: người tạo đã mặc định là người phụ trách -> bắt đầu xử lý thẳng,
            // bỏ bước "Giao xử lý". return/exchange vẫn cần giao việc trước.
            return isReturn
                ? [{ action: 'assign', label: 'Giao xử lý', icon: FiUserCheck, cls: primary }]
                : [{ action: 'start', label: 'Bắt đầu xử lý', icon: FiPlay, cls: primary }];
        case 'assigned':
            return [{ action: 'start', label: 'Bắt đầu xử lý', icon: FiPlay, cls: primary }];
        case 'in_progress':
        case 'reopened':
            return isReturn
                ? [{ action: 'approve', label: 'Duyệt', icon: FiCheck, cls: success },
                   { action: 'reject', label: 'Từ chối', icon: FiXCircle, cls: danger }]
                : [{ action: 'resolve', label: 'Hoàn thành', icon: FiCheckCircle, cls: success }];
        case 'approved':
            return [{ action: 'receive', label: 'Đã nhận hàng', icon: FiInbox, cls: primary }];
        case 'received':
            return [{ action: 'inspect', label: 'Đã kiểm hàng', icon: FiSearch, cls: primary }];
        case 'inspected':
            return [{ action: 'complete', label: 'Hoàn tất', icon: FiCheckCircle, cls: success }];
        case 'rejected':
            return [{ action: 'close', label: 'Đóng phiếu', icon: FiLock, cls: neutral }];
        case 'resolved':
            // support/complaint: resolved = "Hoàn thành" cuối cùng, không cần bước "Đóng" riêng —
            // chỉ còn "Mở lại" phòng lỡ bấm nhầm. return/exchange giữ nguyên cả hai nút.
            return isReturn
                ? [{ action: 'close', label: 'Đóng phiếu', icon: FiLock, cls: neutral },
                   { action: 'reopen', label: 'Mở lại', icon: FiRotateCcw, cls: neutral }]
                : [{ action: 'reopen', label: 'Mở lại', icon: FiRotateCcw, cls: neutral }];
        case 'closed':
            return [{ action: 'reopen', label: 'Mở lại', icon: FiRotateCcw, cls: neutral }];
        default:
            return [];
    }
}

// nút hành động luồng trạng thái phiếu — hiển thị theo status + type
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
