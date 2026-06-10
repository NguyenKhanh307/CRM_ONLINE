import { useState } from 'react';
import axios from 'axios';
import { FiShield, FiEdit2, FiTrash2 } from 'react-icons/fi';
import { useRoleGroups } from '@/features/phan-quyen/hooks/useRoleGroups';
import { useCreateGroup } from '@/features/phan-quyen/hooks/useCreateGroup';
import { useUpdateGroup } from '@/features/phan-quyen/hooks/useUpdateGroup';
import { useDeleteGroup } from '@/features/phan-quyen/hooks/useDeleteGroup';
import GroupList from '@/features/phan-quyen/components/GroupList';
import GroupFormModal from '@/features/phan-quyen/components/GroupFormModal';
import MembersTab from '@/features/phan-quyen/components/MembersTab';
import PermissionsTab from '@/features/phan-quyen/components/PermissionsTab';
import type { RoleGroup } from '@/features/phan-quyen/types/phanQuyenTypes';
import { ConfirmModal } from '@/shared/components/ConfirmModal';
import { useAlert } from '@/shared/alert/useAlert';

type ActiveTab = 'members' | 'permissions';

type ModalState =
    | { type: 'none' }
    | { type: 'create' }
    | { type: 'edit'; group: RoleGroup };

/** Trang quản lý nhóm người dùng và phân quyền. */
const PhanQuyenPage = () => {
    const { showAlert } = useAlert();
    const [selectedGroup, setSelectedGroup] = useState<RoleGroup | null>(null);
    const [deleteConfirmGroup, setDeleteConfirmGroup] = useState<RoleGroup | null>(null);

    const getErrorMsg = (err: unknown): string => {
        if (axios.isAxiosError(err) && err.response?.data?.message)
            return err.response.data.message as string;
        return 'Lỗi kết nối. Kiểm tra backend đang chạy.';
    };
    const [activeTab, setActiveTab] = useState<ActiveTab>('members');
    const [modal, setModal] = useState<ModalState>({ type: 'none' });

    const { data: groups = [], isLoading, isError } = useRoleGroups();
    const createMutation = useCreateGroup();
    const updateMutation = useUpdateGroup();
    const deleteMutation = useDeleteGroup();

    const handleSelectGroup = (group: RoleGroup) => {
        setSelectedGroup(group);
        setActiveTab('members');
    };

    const handleModalSubmit = (data: { code?: string; name: string; description?: string }) => {
        if (modal.type === 'create') {
            createMutation.mutate(data, {
                onSuccess: res => {
                    setModal({ type: 'none' });
                    setSelectedGroup(res.data.data);
                },
                onError: (err) => showAlert('Tạo nhóm thất bại: ' + getErrorMsg(err)),
            });
        } else if (modal.type === 'edit') {
            updateMutation.mutate(
                { id: modal.group.id, name: data.name, description: data.description },
                {
                    onSuccess: res => {
                        setModal({ type: 'none' });
                        setSelectedGroup(res.data.data);
                    },
                    onError: (err) => showAlert('Cập nhật nhóm thất bại: ' + getErrorMsg(err)),
                }
            );
        }
    };

    const handleDelete = (group: RoleGroup) => {
        setDeleteConfirmGroup(group);
    };

    const handleDeleteConfirm = () => {
        if (!deleteConfirmGroup) return;
        deleteMutation.mutate(deleteConfirmGroup.id, {
            onSuccess: () => {
                if (selectedGroup?.id === deleteConfirmGroup.id) setSelectedGroup(null);
                setDeleteConfirmGroup(null);
            },
            onError: (err) => {
                setDeleteConfirmGroup(null);
                showAlert('Xóa nhóm thất bại: ' + getErrorMsg(err));
            },
        });
    };

    const isMutating = createMutation.isPending || updateMutation.isPending;

    return (
        <div className="flex flex-col bg-bg-main" style={{ height: 'calc(100vh - 50px)' }}>
            {/* Page header */}
            <div className="flex items-center gap-2 px-6 py-4 bg-white border-b border-gray-200 shrink-0">
                <FiShield size={20} className="text-primary" />
                <h1 className="text-xl font-semibold text-text-main">Phân quyền</h1>
            </div>

            {/* Body: 2-panel layout */}
            <div className="flex flex-1 overflow-hidden">
                {/* Left panel — group list */}
                <GroupList
                    groups={groups}
                    selectedId={selectedGroup?.id ?? null}
                    onSelect={handleSelectGroup}
                    onCreateClick={() => setModal({ type: 'create' })}
                    isLoading={isLoading}
                    isError={isError}
                />

                {/* Right panel — group detail */}
                <div className="flex-1 overflow-y-auto p-6">
                    {!selectedGroup ? (
                        <div className="flex flex-col items-center justify-center h-full text-gray-400">
                            <FiShield size={40} className="mb-3 opacity-30" />
                            <p className="text-md">Chọn một nhóm bên trái để xem chi tiết</p>
                        </div>
                    ) : (
                        <div>
                            {/* Group header */}
                            <div className="flex items-start justify-between mb-5">
                                <div>
                                    <h2 className="text-xl font-semibold text-text-main">{selectedGroup.name}</h2>
                                    <p className="text-sm text-gray-400 mt-0.5">
                                        Mã: <span className="font-mono">{selectedGroup.code}</span>
                                        {selectedGroup.description && ` — ${selectedGroup.description}`}
                                    </p>
                                </div>
                                <div className="flex items-center gap-2">
                                    <button
                                        onClick={() => setModal({ type: 'edit', group: selectedGroup })}
                                        className="flex items-center gap-1.5 px-3 py-1.5 text-md text-gray-600 border border-gray-300 rounded-btn hover:bg-gray-50 transition-colors"
                                    >
                                        <FiEdit2 size={13} />
                                        Sửa
                                    </button>
                                    {!selectedGroup.isSystem && (
                                        <button
                                            onClick={() => handleDelete(selectedGroup)}
                                            disabled={deleteMutation.isPending}
                                            className="flex items-center gap-1.5 px-3 py-1.5 text-md text-danger border border-danger/30 rounded-btn hover:bg-red-50 disabled:opacity-50 transition-colors"
                                        >
                                            <FiTrash2 size={13} />
                                            Xóa
                                        </button>
                                    )}
                                </div>
                            </div>

                            {/* Tabs */}
                            <div className="flex gap-1 border-b border-gray-200 mb-5">
                                {(['members', 'permissions'] as ActiveTab[]).map(tab => (
                                    <button
                                        key={tab}
                                        onClick={() => setActiveTab(tab)}
                                        className={`px-4 py-2 text-md font-medium border-b-2 transition-colors ${
                                            activeTab === tab
                                                ? 'border-primary text-primary'
                                                : 'border-transparent text-gray-500 hover:text-text-main'
                                        }`}
                                    >
                                        {tab === 'members' ? 'Thành viên' : 'Phân quyền'}
                                    </button>
                                ))}
                            </div>

                            {/* Tab content */}
                            {activeTab === 'members' && <MembersTab roleId={selectedGroup.id} />}
                            {activeTab === 'permissions' && <PermissionsTab roleId={selectedGroup.id} />}
                        </div>
                    )}
                </div>
            </div>

            {/* Modals */}
            {modal.type !== 'none' && (
                <GroupFormModal
                    mode={modal.type}
                    group={modal.type === 'edit' ? modal.group : undefined}
                    onClose={() => setModal({ type: 'none' })}
                    onSubmit={handleModalSubmit}
                    isLoading={isMutating}
                />
            )}

            {deleteConfirmGroup && (
                <ConfirmModal
                    message={`Xóa nhóm "${deleteConfirmGroup.name}"? Hành động này không thể hoàn tác.`}
                    confirmLabel="Xóa"
                    confirmDanger
                    isLoading={deleteMutation.isPending}
                    onConfirm={handleDeleteConfirm}
                    onCancel={() => setDeleteConfirmGroup(null)}
                />
            )}
        </div>
    );
};

export default PhanQuyenPage;
