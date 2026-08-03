import { useState, useMemo } from 'react';
import { FiUserPlus, FiTrash2, FiLock, FiUnlock, FiEdit2, FiCheck, FiX } from 'react-icons/fi';
import { useRoleMembers } from '../hooks/useRoleMembers';
import { useAllUsers } from '../hooks/useAllUsers';
import { useAllUserRoles } from '../hooks/useAllUserRoles';
import { useAddMember } from '../hooks/useAddMember';
import { useRemoveMember } from '../hooks/useRemoveMember';
import { useRevokeUser } from '../hooks/useRevokeUser';
import { useReactivateUser } from '../hooks/useReactivateUser';
import { useUpdateDataAccess } from '../hooks/useUpdateDataAccess';
import AddMemberModal from './AddMemberModal';
import { ConfirmModal } from '@/shared/components/ConfirmModal';
import { ActionButton } from '@/shared/components/ActionButton';
import { useAlert } from '@/shared/alert/useAlert';
import { useConfirm } from '@/shared/confirm/useConfirm';

interface Props {
    roleId: number;
}

type ConfirmType = 'remove' | 'revoke' | 'reactivate';

interface ConfirmState {
    type: ConfirmType;
    userId: number;
    name: string;
}

const STATUS_BADGE: Record<string, string> = {
    active: 'bg-green-100 text-green-700',
    inactive: 'bg-gray-100 text-gray-500',
    locked: 'bg-red-100 text-red-600',
};
const STATUS_LABEL: Record<string, string> = {
    active: 'Hoạt động',
    inactive: 'Chưa kích hoạt',
    locked: 'Đã khóa',
};

const CONFIRM_CONFIG: Record<ConfirmType, { message: (name: string) => string; label: string; danger: boolean }> = {
    remove: {
        message: name => `Xóa "${name}" khỏi nhóm?`,
        label: 'Xóa',
        danger: true,
    },
    revoke: {
        message: name => `Thu hồi tài khoản của "${name}"? Nhân viên sẽ không thể đăng nhập.`,
        label: 'Thu hồi',
        danger: true,
    },
    reactivate: {
        message: name => `Kích hoạt lại tài khoản của "${name}"?`,
        label: 'Kích hoạt',
        danger: false,
    },
};

// tab danh sách thành viên của nhóm
const MembersTab = ({ roleId }: Props) => {
    const [showModal, setShowModal] = useState(false);
    const [confirmState, setConfirmState] = useState<ConfirmState | null>(null);
    const [editingYearId, setEditingYearId] = useState<number | null>(null);
    const [yearInput, setYearInput] = useState('');
    const [yearError, setYearError] = useState<string | null>(null);
    const { showAlert } = useAlert();
    const { confirmSave } = useConfirm();

    const { data: members = [], isLoading: loadingMembers } = useRoleMembers(roleId);
    const { data: allUsers = [] } = useAllUsers();
    const { data: userRoleAssignments = [] } = useAllUserRoles();
    const addMutation = useAddMember(roleId);
    const removeMutation = useRemoveMember(roleId);
    const revokeMutation = useRevokeUser(roleId);
    const reactivateMutation = useReactivateUser(roleId);
    const updateYearMutation = useUpdateDataAccess(roleId);

    // mỗi người chỉ thuộc một nhóm — loại người đã có nhóm (bất kỳ) khỏi modal thêm thành viên
    const assignedUserIds = useMemo(() => new Set(userRoleAssignments.map(ur => ur.userId)), [userRoleAssignments]);

    const activeMutation = confirmState?.type === 'remove' ? removeMutation
        : confirmState?.type === 'revoke' ? revokeMutation
        : reactivateMutation;

    const handleAdd = (userId: number) => {
        addMutation.mutate(userId, {
            onError: () => showAlert('Thêm thành viên thất bại. Vui lòng thử lại.'),
        });
    };

    const handleConfirm = () => {
        if (!confirmState) return;
        const mutation = confirmState.type === 'remove' ? removeMutation
            : confirmState.type === 'revoke' ? revokeMutation
            : reactivateMutation;

        mutation.mutate(confirmState.userId as never, {
            onSuccess: () => setConfirmState(null),
            onError: () => {
                setConfirmState(null);
                const errorMsg = confirmState.type === 'remove' ? 'Xóa thành viên thất bại.'
                    : confirmState.type === 'revoke' ? 'Thu hồi tài khoản thất bại.'
                    : 'Kích hoạt tài khoản thất bại.';
                showAlert(errorMsg + ' Vui lòng thử lại.');
            },
        });
    };

    const startEditYear = (userId: number, currentYear: number | null) => {
        setEditingYearId(userId);
        setYearInput(currentYear ? String(currentYear) : '');
        setYearError(null);
    };

    const cancelEditYear = () => {
        setEditingYearId(null);
        setYearError(null);
    };

    const confirmEditYear = async (userId: number) => {
        const year = yearInput ? parseInt(yearInput, 10) : null;
        // lỗi nhập liệu hiện đỏ ngay dưới ô, không dùng popup
        if (yearInput && (isNaN(year!) || year! < 2000 || year! > 2100)) {
            setYearError('Năm phải từ 2000 đến 2100');
            return;
        }
        setYearError(null);

        if (!(await confirmSave('năm xem dữ liệu'))) return;

        updateYearMutation.mutate({ userId, year }, {
            onSuccess: () => cancelEditYear(),
            onError: () => showAlert('Cập nhật năm thất bại. Vui lòng thử lại.'),
        });
    };

    return (
        <div>
            {/* Toolbar */}
            <div className="flex justify-between items-center mb-3">
                <p className="text-md text-gray-500">{members.length} thành viên</p>
                <ActionButton variant="primary" icon={FiUserPlus} onClick={() => setShowModal(true)}>
                    Thêm thành viên
                </ActionButton>
            </div>

            {/* Table */}
            <div className="border border-gray-200 rounded-section overflow-hidden">
                <table className="w-full">
                    <thead>
                        <tr className="bg-gray-100 border-b-2 border-gray-300">
                            <th className="text-left px-4 py-2 text-title font-semibold text-text-main">Họ và tên</th>
                            <th className="text-left px-4 py-2 text-title font-semibold text-text-main">Email</th>
                            <th className="text-center px-4 py-2 text-title font-semibold text-text-main w-32">Trạng thái</th>
                            <th className="text-center px-4 py-2 text-title font-semibold text-text-main w-36">Xem từ năm</th>
                            <th className="w-24 px-4 py-2" />
                        </tr>
                    </thead>
                    <tbody>
                        {loadingMembers && (
                            <tr>
                                <td colSpan={5} className="px-4 py-6 text-center text-sm text-gray-400">Đang tải...</td>
                            </tr>
                        )}
                        {!loadingMembers && members.length === 0 && (
                            <tr>
                                <td colSpan={5} className="px-4 py-6 text-center text-sm text-gray-400">
                                    Chưa có thành viên nào. Nhấn "Thêm thành viên" để bắt đầu.
                                </td>
                            </tr>
                        )}
                        {members.map((member, idx) => (
                            <tr key={member.id} className={idx % 2 === 0 ? 'bg-white' : 'bg-gray-50'}>
                                <td className="px-4 py-2.5 text-table text-text-main border-b border-gray-100">
                                    {member.fullName}
                                </td>
                                <td className="px-4 py-2.5 text-table text-gray-500 border-b border-gray-100">
                                    {member.email}
                                </td>
                                <td className="px-4 py-2.5 text-center border-b border-gray-100">
                                    <span className={`inline-block px-2 py-0.5 rounded text-sm font-medium ${STATUS_BADGE[member.status] ?? 'bg-gray-100 text-gray-500'}`}>
                                        {STATUS_LABEL[member.status] ?? member.status}
                                    </span>
                                </td>
                                <td className="px-4 py-2.5 text-center border-b border-gray-100">
                                    {editingYearId === member.id ? (
                                        <div>
                                            <div className="flex items-center justify-center gap-1">
                                                <input
                                                    type="number"
                                                    min={2000}
                                                    max={2100}
                                                    value={yearInput}
                                                    onChange={e => { setYearInput(e.target.value); setYearError(null); }}
                                                    placeholder="YYYY"
                                                    className={`w-20 text-center border rounded px-1 py-0.5 text-sm focus:outline-none focus:ring-1 focus:ring-primary ${
                                                        yearError ? 'border-danger' : 'border-gray-300'
                                                    }`}
                                                    onKeyDown={e => {
                                                        if (e.key === 'Enter') confirmEditYear(member.id);
                                                        if (e.key === 'Escape') cancelEditYear();
                                                    }}
                                                    autoFocus
                                                />
                                                <button
                                                    onClick={() => confirmEditYear(member.id)}
                                                    disabled={updateYearMutation.isPending}
                                                    className="text-green-600 hover:text-green-700"
                                                >
                                                    <FiCheck size={13} />
                                                </button>
                                                <button
                                                    onClick={cancelEditYear}
                                                    className="text-gray-400 hover:text-gray-600"
                                                >
                                                    <FiX size={13} />
                                                </button>
                                            </div>
                                            {yearError && <p className="text-xs text-danger mt-1">{yearError}</p>}
                                        </div>
                                    ) : (
                                        <div className="flex items-center justify-center gap-1.5">
                                            <span className="text-table text-gray-600">
                                                {member.dataAccessFromYear ?? 'Tất cả'}
                                            </span>
                                            <button
                                                onClick={() => startEditYear(member.id, member.dataAccessFromYear)}
                                                className="text-gray-300 hover:text-gray-500 transition-colors"
                                                title="Chỉnh sửa năm"
                                            >
                                                <FiEdit2 size={12} />
                                            </button>
                                        </div>
                                    )}
                                </td>
                                <td className="px-4 py-2.5 text-center border-b border-gray-100">
                                    <div className="flex items-center justify-center gap-2">
                                        {member.status === 'active' && (
                                            <button
                                                onClick={() => setConfirmState({ type: 'revoke', userId: member.id, name: member.fullName })}
                                                className="text-gray-400 hover:text-yellow-600 transition-colors"
                                                title="Thu hồi tài khoản"
                                            >
                                                <FiLock size={14} />
                                            </button>
                                        )}
                                        {member.status === 'locked' && (
                                            <button
                                                onClick={() => setConfirmState({ type: 'reactivate', userId: member.id, name: member.fullName })}
                                                className="text-gray-400 hover:text-green-600 transition-colors"
                                                title="Kích hoạt lại tài khoản"
                                            >
                                                <FiUnlock size={14} />
                                            </button>
                                        )}
                                        <button
                                            onClick={() => setConfirmState({ type: 'remove', userId: member.id, name: member.fullName })}
                                            className="text-gray-400 hover:text-danger transition-colors"
                                            title="Xóa khỏi nhóm"
                                        >
                                            <FiTrash2 size={14} />
                                        </button>
                                    </div>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>

            {showModal && (
                <AddMemberModal
                    allUsers={allUsers}
                    assignedUserIds={assignedUserIds}
                    onClose={() => setShowModal(false)}
                    onAdd={handleAdd}
                    isLoading={addMutation.isPending}
                />
            )}

            {confirmState && (
                <ConfirmModal
                    message={CONFIRM_CONFIG[confirmState.type].message(confirmState.name)}
                    confirmLabel={CONFIRM_CONFIG[confirmState.type].label}
                    confirmDanger={CONFIRM_CONFIG[confirmState.type].danger}
                    isLoading={activeMutation.isPending}
                    onConfirm={handleConfirm}
                    onCancel={() => setConfirmState(null)}
                />
            )}
        </div>
    );
};

export default MembersTab;
