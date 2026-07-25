import { useCallback, useState } from 'react';
import axios from 'axios';
import { FiShield, FiEdit2, FiTrash2 } from 'react-icons/fi';
import { useRoleGroups } from '../hooks/useRoleGroups';
import { useCreateGroup } from '../hooks/useCreateGroup';
import { useUpdateGroup } from '../hooks/useUpdateGroup';
import { useDeleteGroup } from '../hooks/useDeleteGroup';
import GroupList from '../components/GroupList';
import GroupFormModal from '../components/GroupFormModal';
import MembersTab from '../components/MembersTab';
import PermissionsTab from '../components/PermissionsTab';
import type { RoleGroup } from '../types/phanQuyenTypes';
import { ConfirmModal } from '@/shared/components/ConfirmModal';
import { ActionButton } from '@/shared/components/ActionButton';
import { useAlert } from '@/shared/alert/useAlert';
import { useConfirm } from '@/shared/confirm/useConfirm';

type ActiveTab = 'members' | 'permissions';

type ModalState =
    | { type: 'none' }
    | { type: 'create' }
    | { type: 'edit'; group: RoleGroup };

/** Trang quản lý nhóm người dùng và phân quyền. */
const PhanQuyenPage = () => {
    const { showAlert } = useAlert();
    const { confirm } = useConfirm();
    const [selectedGroup, setSelectedGroup] = useState<RoleGroup | null>(null);
    const [deleteConfirmGroup, setDeleteConfirmGroup] = useState<RoleGroup | null>(null);
    /** Tab Phân quyền đang có thay đổi chưa lưu — chặn rời đi làm mất dữ liệu. */
    const [permissionsDirty, setPermissionsDirty] = useState(false);

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

    /** Hỏi trước khi bỏ các thay đổi quyền chưa lưu. Trả true nếu được phép rời đi. */
    const canLeavePermissions = useCallback(async () => {
        if (!permissionsDirty) return true;
        const ok = await confirm({
            message: 'Bỏ các thay đổi quyền chưa lưu?',
            confirmLabel: 'Bỏ thay đổi',
            confirmDanger: true,
        });
        if (ok) setPermissionsDirty(false);
        return ok;
    }, [permissionsDirty, confirm]);

    const handleSelectGroup = async (group: RoleGroup) => {
        if (group.id === selectedGroup?.id) return;
        if (!(await canLeavePermissions())) return;
        setSelectedGroup(group);
        setActiveTab('members');
    };

    const handleChangeTab = async (tab: ActiveTab) => {
        if (tab === activeTab) return;
        if (activeTab === 'permissions' && !(await canLeavePermissions())) return;
        setActiveTab(tab);
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
                                <div className="flex items-center gap-1.5">
                                    <ActionButton
                                        variant="secondary"
                                        icon={FiEdit2}
                                        onClick={() => setModal({ type: 'edit', group: selectedGroup })}
                                    >
                                        Sửa
                                    </ActionButton>
                                    {!selectedGroup.isSystem && (
                                        <ActionButton
                                            variant="danger"
                                            icon={FiTrash2}
                                            onClick={() => handleDelete(selectedGroup)}
                                            disabled={deleteMutation.isPending}
                                        >
                                            Xóa
                                        </ActionButton>
                                    )}
                                </div>
                            </div>

                            {/* Tabs */}
                            <div className="flex gap-1 border-b border-gray-200 mb-5">
                                {(['members', 'permissions'] as ActiveTab[]).map(tab => (
                                    <button
                                        key={tab}
                                        onClick={() => handleChangeTab(tab)}
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
                            {activeTab === 'permissions' && (
                                <PermissionsTab
                                    roleId={selectedGroup.id}
                                    roleName={selectedGroup.name}
                                    onDirtyChange={setPermissionsDirty}
                                />
                            )}
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
