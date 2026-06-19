import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { FiX } from 'react-icons/fi';
import { userService } from '@/features/users/services/userService';

interface HandoverModalProps {
    open: boolean;
    count: number;
    onClose: () => void;
    onConfirm: (toUserId: number, reason?: string) => void;
    isLoading?: boolean;
}

export const HandoverModal = ({ open, count, onClose, onConfirm, isLoading }: HandoverModalProps) => {
    const [toUserId, setToUserId] = useState<number | ''>('');
    const [reason, setReason] = useState('');

    const { data } = useQuery({
        queryKey: ['users-active'],
        queryFn: () => userService.listActive().then(r => r.data.data.items),
        enabled: open,
    });

    if (!open) return null;

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/30">
            <div className="bg-white rounded-card shadow-xl w-[420px] max-w-[95vw]">
                <div className="flex items-center justify-between px-5 py-4 border-b border-gray-200">
                    <span className="font-semibold text-base text-text-main">
                        Bàn giao {count} bản ghi
                    </span>
                    <button onClick={onClose} className="p-1 rounded hover:bg-gray-100 text-gray-400 hover:text-gray-600">
                        <FiX size={18} />
                    </button>
                </div>

                <div className="px-5 py-4 space-y-3">
                    <div className="space-y-1.5">
                        <label className="text-sm text-gray-600">Người nhận bàn giao</label>
                        <select
                            value={toUserId}
                            onChange={e => setToUserId(e.target.value === '' ? '' : Number(e.target.value))}
                            className="w-full px-3 py-2 border border-gray-300 rounded-btn text-sm text-text-main bg-white focus:outline-none focus:border-primary"
                        >
                            <option value="">- Chọn người dùng -</option>
                            {(data ?? []).map(u => (
                                <option key={u.id} value={u.id}>{u.fullName} ({u.email})</option>
                            ))}
                        </select>
                    </div>

                    <div className="space-y-1.5">
                        <label className="text-sm text-gray-600">Lý do (tuỳ chọn)</label>
                        <input
                            type="text"
                            value={reason}
                            onChange={e => setReason(e.target.value)}
                            placeholder="Nhập lý do bàn giao..."
                            className="w-full px-3 py-2 border border-gray-300 rounded-btn text-sm focus:outline-none focus:border-primary"
                        />
                    </div>
                </div>

                <div className="flex justify-end gap-2 px-5 py-3 border-t border-gray-200">
                    <button
                        onClick={onClose}
                        className="px-4 py-1.5 rounded-btn border border-gray-300 text-sm text-text-main hover:bg-gray-50"
                    >
                        Hủy
                    </button>
                    <button
                        disabled={!toUserId || isLoading}
                        onClick={() => toUserId && onConfirm(toUserId, reason || undefined)}
                        className="px-4 py-1.5 rounded-btn bg-primary text-white text-sm hover:opacity-90 disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                        {isLoading ? 'Đang bàn giao...' : 'Bàn giao'}
                    </button>
                </div>
            </div>
        </div>
    );
};
