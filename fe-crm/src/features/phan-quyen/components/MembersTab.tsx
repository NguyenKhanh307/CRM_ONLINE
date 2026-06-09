import { useState, useMemo } from 'react';
import { FiUserPlus, FiTrash2 } from 'react-icons/fi';
import { useRoleMembers } from '@/features/phan-quyen/hooks/useRoleMembers';
import { useAllUsers } from '@/features/phan-quyen/hooks/useAllUsers';
import { useAddMember } from '@/features/phan-quyen/hooks/useAddMember';
import { useRemoveMember } from '@/features/phan-quyen/hooks/useRemoveMember';
import AddMemberModal from '@/features/phan-quyen/components/AddMemberModal';
import { useAlert } from '@/shared/alert/useAlert';

interface Props {
    roleId: number;
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

/** Tab danh sách thành viên của nhóm. */
const MembersTab = ({ roleId }: Props) => {
    const [showModal, setShowModal] = useState(false);
    const { showAlert } = useAlert();

    const { data: members = [], isLoading: loadingMembers } = useRoleMembers(roleId);
    const { data: allUsers = [] } = useAllUsers();
    const addMutation = useAddMember(roleId);
    const removeMutation = useRemoveMember(roleId);

    const existingMemberIds = useMemo(() => new Set(members.map(m => m.id)), [members]);

    const handleAdd = (userId: number) => {
        addMutation.mutate(userId, {
            onError: () => showAlert('Thêm thành viên thất bại. Vui lòng thử lại.'),
        });
    };

    const handleRemove = (userId: number, name: string) => {
        if (!confirm(`Xóa "${name}" khỏi nhóm?`)) return;
        removeMutation.mutate(userId, {
            onError: () => showAlert('Xóa thành viên thất bại. Vui lòng thử lại.'),
        });
    };

    return (
        <div>
            {/* Toolbar */}
            <div className="flex justify-between items-center mb-3">
                <p className="text-md text-gray-500">{members.length} thành viên</p>
                <button
                    onClick={() => setShowModal(true)}
                    className="flex items-center gap-1.5 px-3 py-1.5 text-md bg-primary text-white rounded-btn hover:bg-blue-600 transition-colors"
                >
                    <FiUserPlus size={14} />
                    Thêm thành viên
                </button>
            </div>

            {/* Table */}
            <div className="border border-gray-200 rounded-section overflow-hidden">
                <table className="w-full">
                    <thead>
                        <tr className="bg-gray-100 border-b-2 border-gray-300">
                            <th className="text-left px-4 py-2 text-title font-semibold text-text-main">Họ và tên</th>
                            <th className="text-left px-4 py-2 text-title font-semibold text-text-main">Email</th>
                            <th className="text-center px-4 py-2 text-title font-semibold text-text-main w-32">Trạng thái</th>
                            <th className="w-12 px-4 py-2" />
                        </tr>
                    </thead>
                    <tbody>
                        {loadingMembers && (
                            <tr>
                                <td colSpan={4} className="px-4 py-6 text-center text-sm text-gray-400">Đang tải...</td>
                            </tr>
                        )}
                        {!loadingMembers && members.length === 0 && (
                            <tr>
                                <td colSpan={4} className="px-4 py-6 text-center text-sm text-gray-400">
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
                                    <button
                                        onClick={() => handleRemove(member.id, member.fullName)}
                                        disabled={removeMutation.isPending}
                                        className="text-gray-400 hover:text-danger transition-colors"
                                        title="Xóa khỏi nhóm"
                                    >
                                        <FiTrash2 size={14} />
                                    </button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>

            {showModal && (
                <AddMemberModal
                    allUsers={allUsers}
                    existingMemberIds={existingMemberIds}
                    onClose={() => setShowModal(false)}
                    onAdd={handleAdd}
                    isLoading={addMutation.isPending}
                />
            )}
        </div>
    );
};

export default MembersTab;
