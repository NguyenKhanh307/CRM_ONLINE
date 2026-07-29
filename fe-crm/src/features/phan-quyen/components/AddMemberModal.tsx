import { useState, useMemo, useRef } from 'react';
import { FiX, FiSearch } from 'react-icons/fi';
import { ActionButton } from '@/shared/components/ActionButton';
import { useConfirm } from '@/shared/confirm/useConfirm';
import { useDialogKeyboardNav } from '@/shared/keyboard/useDialogKeyboardNav';
import { SHORTCUTS } from '@/shared/keyboard/shortcuts';
import type { GroupMember } from '../types/phanQuyenTypes';

interface Props {
    allUsers: GroupMember[];
    /** userId đã thuộc một nhóm bất kỳ — mỗi người chỉ thuộc một nhóm nên bị loại khỏi danh sách. */
    assignedUserIds: Set<number>;
    onClose: () => void;
    onAdd: (userId: number) => void;
    isLoading: boolean;
}

const STATUS_LABEL: Record<string, string> = {
    active: 'Hoạt động',
    inactive: 'Chưa kích hoạt',
    locked: 'Đã khóa',
};

/**
 * Modal chọn thành viên để thêm vào nhóm.
 * Ô tìm kiếm là form tra cứu — không nằm trong `<form>` nên Enter vô hại, không có popup lỗi.
 * Nút "Thêm" mới là hành động ghi nên phải qua popup xác nhận.
 */
const AddMemberModal = ({ allUsers, assignedUserIds, onClose, onAdd, isLoading }: Props) => {
    const [search, setSearch] = useState('');
    const { confirmCreate } = useConfirm();

    const ref = useRef<HTMLDivElement>(null);
    useDialogKeyboardNav(ref, { onCancel: onClose, autoFocus: 'none' });

    const candidates = useMemo(() =>
        allUsers.filter(u =>
            !assignedUserIds.has(u.id) &&
            (u.fullName.toLowerCase().includes(search.toLowerCase()) ||
             u.email.toLowerCase().includes(search.toLowerCase()))
        ),
        [allUsers, assignedUserIds, search]
    );

    const handleAdd = async (user: GroupMember) => {
        if (!(await confirmCreate(`thành viên "${user.fullName}" vào nhóm`))) return;
        onAdd(user.id);
    };

    return (
        <div ref={ref} className="fixed inset-0 z-50 flex items-center justify-center bg-black/30">
            <div className="bg-white rounded-card shadow-xl w-full max-w-lg p-6">
                {/* Header */}
                <div className="flex items-center justify-between mb-4">
                    <h2 className="text-lg font-semibold text-text-main">Thêm thành viên</h2>
                    <button onClick={onClose} className="text-gray-400 hover:text-gray-600">
                        <FiX size={18} />
                    </button>
                </div>

                {/* Search */}
                <div className="relative mb-3">
                    <FiSearch size={14} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
                    <input
                        type="text"
                        value={search}
                        onChange={e => setSearch(e.target.value)}
                        placeholder="Tìm theo tên hoặc email..."
                        className="w-full border border-gray-300 rounded-btn pl-8 pr-3 py-2 text-md focus:outline-none focus:border-primary"
                    />
                </div>

                {/* User list */}
                <div className="max-h-72 overflow-y-auto border border-gray-200 rounded-section">
                    {candidates.length === 0 && (
                        <p className="p-4 text-sm text-gray-400 text-center">
                            {search ? 'Không tìm thấy người dùng phù hợp.' : 'Tất cả người dùng đã trong nhóm.'}
                        </p>
                    )}
                    {candidates.map((user, idx) => (
                        <div
                            key={user.id}
                            className={`flex items-center justify-between px-4 py-2.5 ${idx % 2 === 0 ? 'bg-white' : 'bg-gray-50'} border-b border-gray-100 last:border-0`}
                        >
                            <div>
                                <p className="text-md font-medium text-text-main">{user.fullName}</p>
                                <p className="text-sm text-gray-400">{user.email}</p>
                            </div>
                            <div className="flex items-center gap-3">
                                <span className="text-sm text-gray-400">{STATUS_LABEL[user.status] ?? user.status}</span>
                                <ActionButton
                                    variant="primary"
                                    onClick={() => handleAdd(user)}
                                    disabled={isLoading}
                                >
                                    Thêm
                                </ActionButton>
                            </div>
                        </div>
                    ))}
                </div>

                <div className="flex justify-end mt-4">
                    <ActionButton variant="secondary" dialogButton shortcut={SHORTCUTS.CANCEL.keys} onClick={onClose}>
                        Đóng
                    </ActionButton>
                </div>
            </div>
        </div>
    );
};

export default AddMemberModal;
