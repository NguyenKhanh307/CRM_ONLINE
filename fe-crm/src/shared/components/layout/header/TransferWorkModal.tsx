import { useRef, useState } from 'react';
import { FiMoreHorizontal, FiX, FiUser } from 'react-icons/fi';
import { useActiveUsers } from '@/features/users/hooks/useActiveUsers';
import { useHandoverAll } from '@/features/users/hooks/useHandoverAll';
import { useAuth } from '@/core/auth/useAuth';
import { usePermission } from '@/core/permissions/usePermission';
import { DialogFooter } from '@/shared/components/ModalFooter';
import { useDialogKeyboardNav } from '@/shared/keyboard/useDialogKeyboardNav';

interface ModalProps {
    onClose: () => void;
}

// modal "bàn giao toàn bộ công việc" — chuyển hết 5 module từ người này sang người khác.
// ADMIN/SALES_MANAGER chọn tự do cặp người bàn giao/nhận; nhân viên thường "Người bàn giao"
// luôn khóa = chính mình, chỉ chọn được "Người nhận"
const TransferWorkModal = ({ onClose }: ModalProps) => {
    const { user } = useAuth();
    const { hasRole } = usePermission();
    const isManager = hasRole('ADMIN') || hasRole('SALES_MANAGER');

    const [fromUser, setFromUser] = useState<number | ''>(user?.id ?? '');
    const [toUser, setToUser] = useState<number | ''>('');
    const [reason, setReason] = useState('');
    // lỗi bàn giao hiện tại chỗ modal, không dùng toast toàn cục — cần onError riêng để lấy message
    const [apiError, setApiError] = useState<string | null>(null);

    const { data: usersData, isLoading: isLoadingUsers } = useActiveUsers();
    const users = usersData ?? [];

    const { mutate: handoverFn, isPending } = useHandoverAll();

    const canSubmit = fromUser !== '' && toUser !== '' && fromUser !== toUser;

    const handleSubmit = () => {
        if (!canSubmit) return;
        setApiError(null);
        handoverFn(
            { fromUserId: fromUser as number, toUserId: toUser as number, reason: reason || undefined },
            {
                onSuccess: onClose,
                onError: (err) => {
                    const e = err as { response?: { data?: { message?: string } } };
                    setApiError(e?.response?.data?.message ?? 'Bàn giao thất bại. Vui lòng thử lại.');
                },
            },
        );
    };

    const ref = useRef<HTMLDivElement>(null);
    // không tự focus nút — người dùng phải chọn người giao/người nhận trước
    useDialogKeyboardNav(ref, { onCancel: onClose, autoFocus: 'none' });

    return (
        <div ref={ref} className="fixed inset-0 z-50 flex items-center justify-center bg-black/30">
            <div className="bg-white rounded-card shadow-xl w-[520px] max-w-[95vw]">
                {/* Title bar */}
                <div className="flex items-center justify-between px-5 py-4 border-b border-gray-200">
                    <span className="font-semibold text-lg text-text-main">
                        Bàn giao toàn bộ công việc
                    </span>
                    <button
                        onClick={onClose}
                        className="p-1 rounded hover:bg-gray-100 text-gray-400 hover:text-gray-600"
                    >
                        <FiX size={18} />
                    </button>
                </div>

                <div className="px-5 py-4 space-y-4">
                    {/* Warning box */}
                    <div className="border border-orange-300 bg-orange-50 rounded-section px-4 py-3 text-sm text-orange-800 leading-relaxed">
                        Chức năng này sẽ chuyển toàn bộ Tiềm năng, Khách hàng, Cơ hội, Báo giá và
                        Đơn hàng của người bàn giao sang cho người được bàn giao.
                    </div>

                    {/* User selectors */}
                    {isLoadingUsers ? (
                        <p className="text-sm text-gray-500">Đang tải danh sách người dùng...</p>
                    ) : (
                        <div className="flex gap-4">
                            <div className="flex-1 space-y-1.5">
                                <label className="text-sm text-gray-600">Người bàn giao</label>
                                {isManager ? (
                                    <div className="relative">
                                        <select
                                            value={fromUser}
                                            onChange={e => setFromUser(e.target.value === '' ? '' : Number(e.target.value))}
                                            className="w-full appearance-none px-3 py-2 border border-gray-300 rounded-btn text-sm text-text-main bg-white focus:outline-none focus:border-primary"
                                        >
                                            <option value="">- Chọn người dùng -</option>
                                            {users.map(u => (
                                                <option key={u.id} value={u.id}>{u.fullName}</option>
                                            ))}
                                        </select>
                                        <span className="absolute right-2 top-1/2 -translate-y-1/2 pointer-events-none flex items-center gap-1 text-gray-400">
                                            <svg className="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
                                            </svg>
                                            <FiUser size={14} />
                                        </span>
                                    </div>
                                ) : (
                                    <div className="relative">
                                        <div className="w-full px-3 py-2 border border-gray-200 rounded-btn text-sm text-gray-500 bg-gray-50 cursor-not-allowed">
                                            {user?.fullName}
                                        </div>
                                        <span className="absolute right-2 top-1/2 -translate-y-1/2 pointer-events-none text-gray-400">
                                            <FiUser size={14} />
                                        </span>
                                    </div>
                                )}
                            </div>

                            <div className="flex-1 space-y-1.5">
                                <label className="text-sm text-gray-600">Người nhận bàn giao</label>
                                <div className="relative">
                                    <select
                                        value={toUser}
                                        onChange={e => setToUser(e.target.value === '' ? '' : Number(e.target.value))}
                                        className="w-full appearance-none px-3 py-2 border border-gray-300 rounded-btn text-sm text-text-main bg-white focus:outline-none focus:border-primary"
                                    >
                                        <option value="">- Chọn người dùng -</option>
                                        {users.filter(u => u.id !== fromUser).map(u => (
                                            <option key={u.id} value={u.id}>{u.fullName}</option>
                                        ))}
                                    </select>
                                    <span className="absolute right-2 top-1/2 -translate-y-1/2 pointer-events-none flex items-center gap-1 text-gray-400">
                                        <svg className="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
                                        </svg>
                                        <FiUser size={14} />
                                    </span>
                                </div>
                            </div>
                        </div>
                    )}

                    {/* Reason */}
                    <div className="space-y-1.5">
                        <label className="text-sm text-gray-600">Lý do (tùy chọn)</label>
                        <input
                            type="text"
                            value={reason}
                            onChange={e => setReason(e.target.value)}
                            placeholder="Nhập lý do bàn giao..."
                            className="w-full px-3 py-2 border border-gray-300 rounded-btn text-sm text-text-main bg-white focus:outline-none focus:border-primary"
                        />
                    </div>

                    {apiError && <p className="text-sm text-danger">{apiError}</p>}
                </div>

                {/* Footer */}
                <DialogFooter
                    onCancel={onClose}
                    onConfirm={handleSubmit}
                    confirmLabel={isPending ? 'Đang xử lý...' : 'Bàn giao'}
                    confirmDisabled={!canSubmit || isPending}
                />
            </div>
        </div>
    );
};

// icon "..." trên header, mở TransferWorkModal
export const TransferWorkButton = () => {
    const [open, setOpen] = useState(false);

    return (
        <>
            <button
                onClick={() => setOpen(true)}
                className="p-2 rounded hover:bg-gray-100 text-gray-500 hover:text-text-main"
                title="Bàn giao công việc"
            >
                <FiMoreHorizontal size={18} />
            </button>
            {open && <TransferWorkModal onClose={() => setOpen(false)} />}
        </>
    );
};
