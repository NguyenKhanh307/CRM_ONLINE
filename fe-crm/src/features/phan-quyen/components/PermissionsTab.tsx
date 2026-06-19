import { useMemo } from 'react';
import { useAllPermissions } from '../hooks/useAllPermissions';
import { useRolePermissions } from '../hooks/useRolePermissions';
import { useTogglePermission } from '../hooks/useTogglePermission';
import { MODULE_LABELS } from '../constants/phanQuyenData';
import PhanHeSection from './PhanHeSection';
import { useAlert } from '@/shared/alert/useAlert';
import { useState } from 'react';

interface Props {
    roleId: number;
}

/** Tab phân quyền — accordion các module, toggle gọi API ngay. */
const PermissionsTab = ({ roleId }: Props) => {
    const { showAlert } = useAlert();
    const [openModules, setOpenModules] = useState<Set<string>>(new Set());

    const { data: allPerms = [], isLoading: loadingAll } = useAllPermissions();
    const { data: rolePerms = [], isLoading: loadingRole } = useRolePermissions(roleId);
    const { assign, revoke } = useTogglePermission(roleId);

    const assignedIds = useMemo(() => new Set(rolePerms.map(p => p.id)), [rolePerms]);

    const moduleGroups = useMemo(() => {
        const map = new Map<string, typeof allPerms>();
        allPerms.forEach(perm => {
            if (!map.has(perm.module)) map.set(perm.module, []);
            map.get(perm.module)!.push(perm);
        });
        return map;
    }, [allPerms]);

    const handleToggleOpen = (moduleId: string) => {
        setOpenModules(prev => {
            const next = new Set(prev);
            next.has(moduleId) ? next.delete(moduleId) : next.add(moduleId);
            return next;
        });
    };

    const handleTogglePermission = (permId: number, currentlyChecked: boolean) => {
        if (currentlyChecked) {
            revoke.mutate(permId, {
                onError: () => showAlert('Thu hồi quyền thất bại. Vui lòng thử lại.'),
            });
        } else {
            assign.mutate(permId, {
                onError: () => showAlert('Gán quyền thất bại. Vui lòng thử lại.'),
            });
        }
    };

    const handleToggleAll = (moduleId: string, checked: boolean) => {
        const perms = moduleGroups.get(moduleId) ?? [];
        perms.forEach(perm => {
            const isAssigned = assignedIds.has(perm.id);
            if (checked && !isAssigned) {
                assign.mutate(perm.id, {
                    onError: () => showAlert('Gán quyền thất bại.'),
                });
            } else if (!checked && isAssigned) {
                revoke.mutate(perm.id, {
                    onError: () => showAlert('Thu hồi quyền thất bại.'),
                });
            }
        });
    };

    if (loadingAll || loadingRole) {
        return <p className="text-sm text-gray-400 py-4">Đang tải danh sách quyền...</p>;
    }

    if (moduleGroups.size === 0) {
        return <p className="text-sm text-gray-400 py-4">Chưa có quyền nào trong hệ thống.</p>;
    }

    return (
        <div className="space-y-3">
            {Array.from(moduleGroups.entries()).map(([moduleId, perms]) => (
                <PhanHeSection
                    key={moduleId}
                    moduleId={moduleId}
                    label={MODULE_LABELS[moduleId] ?? moduleId}
                    isOpen={openModules.has(moduleId)}
                    permissions={perms}
                    assignedIds={assignedIds}
                    onToggleOpen={handleToggleOpen}
                    onToggleAll={handleToggleAll}
                    onTogglePermission={handleTogglePermission}
                />
            ))}
        </div>
    );
};

export default PermissionsTab;
