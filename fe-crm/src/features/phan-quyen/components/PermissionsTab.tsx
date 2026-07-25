import { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { FiRotateCcw, FiSave } from 'react-icons/fi';
import { useAllPermissions } from '../hooks/useAllPermissions';
import { useRolePermissions } from '../hooks/useRolePermissions';
import { useTogglePermission } from '../hooks/useTogglePermission';
import { MODULE_LABELS } from '../constants/phanQuyenData';
import PhanHeSection from './PhanHeSection';
import { ActionButton } from '@/shared/components/ActionButton';
import { useAlert } from '@/shared/alert/useAlert';
import { useConfirm } from '@/shared/confirm/useConfirm';
import { SHORTCUTS, matchesShortcut } from '@/shared/keyboard/shortcuts';

interface Props {
    roleId: number;
    /** Tên nhóm — chỉ dùng để nêu rõ trong popup xác nhận. */
    roleName?: string;
    /** Báo lên trang cha khi có thay đổi chưa lưu, để chặn đổi nhóm/đổi tab làm mất dữ liệu. */
    onDirtyChange?: (dirty: boolean) => void;
}

/**
 * Tab phân quyền — accordion các module.
 *
 * Tick checkbox CHỈ đổi bản nháp trong bộ nhớ; phải bấm "Lưu" và qua popup xác nhận thì mới
 * gọi API. Trước đây mỗi lần tick là một request gán/thu hồi ngay, không hoàn tác được.
 */
const PermissionsTab = ({ roleId, roleName, onDirtyChange }: Props) => {
    const { showAlert } = useAlert();
    const { confirm } = useConfirm();
    const [openModules, setOpenModules] = useState<Set<string>>(new Set());

    const { data: allPerms = [], isLoading: loadingAll } = useAllPermissions();
    const { data: rolePerms = [], isLoading: loadingRole } = useRolePermissions(roleId);
    const { assign, revoke } = useTogglePermission(roleId);

    /** Quyền đã lưu trên server. */
    const savedIds = useMemo(() => new Set(rolePerms.map(p => p.id)), [rolePerms]);

    /** Bản nháp đang chỉnh — null nghĩa là chưa nạp xong dữ liệu server. */
    const [draft, setDraft] = useState<Set<number> | null>(null);
    const [saving, setSaving] = useState(false);

    // Nạp lại bản nháp khi đổi nhóm hoặc khi server trả dữ liệu mới (sau khi lưu xong).
    // `savedKey` là chuỗi id đã sắp xếp — so sánh theo giá trị, không theo tham chiếu Set.
    const savedKey = useMemo(() => [...savedIds].sort((a, b) => a - b).join(','), [savedIds]);
    useEffect(() => {
        if (loadingRole) return;
        setDraft(new Set(savedKey ? savedKey.split(',').map(Number) : []));
    }, [roleId, savedKey, loadingRole]);

    const current = draft ?? savedIds;

    const toAssign = useMemo(() => [...current].filter(id => !savedIds.has(id)), [current, savedIds]);
    const toRevoke = useMemo(() => [...savedIds].filter(id => !current.has(id)), [current, savedIds]);
    const changeCount = toAssign.length + toRevoke.length;
    const dirty = changeCount > 0;

    // Báo trạng thái dirty lên cha; khi tab bị gỡ thì trả về sạch để cha không kẹt cờ cũ.
    useEffect(() => {
        onDirtyChange?.(dirty);
    }, [dirty, onDirtyChange]);
    useEffect(() => () => onDirtyChange?.(false), [onDirtyChange]);

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

    /** Tick một quyền — chỉ đổi bản nháp, chưa gọi API. */
    const handleTogglePermission = useCallback((permId: number) => {
        setDraft(prev => {
            const next = new Set(prev ?? []);
            next.has(permId) ? next.delete(permId) : next.add(permId);
            return next;
        });
    }, []);

    /** Bật/tắt toàn bộ quyền của một phân hệ — cũng chỉ đổi bản nháp. */
    const handleToggleAll = useCallback((moduleId: string, checked: boolean) => {
        const perms = moduleGroups.get(moduleId) ?? [];
        setDraft(prev => {
            const next = new Set(prev ?? []);
            perms.forEach(p => (checked ? next.add(p.id) : next.delete(p.id)));
            return next;
        });
    }, [moduleGroups]);

    const handleReset = () => setDraft(new Set(savedIds));

    const handleSave = async () => {
        const message = `Cập nhật quyền nhóm${roleName ? ` "${roleName}"` : ''}: `
            + [
                toAssign.length > 0 ? `gán ${toAssign.length} quyền` : null,
                toRevoke.length > 0 ? `thu hồi ${toRevoke.length} quyền` : null,
            ].filter(Boolean).join(', ') + '?';
        if (!(await confirm({ message }))) return;

        setSaving(true);
        try {
            await Promise.all([
                ...toAssign.map(id => assign.mutateAsync(id)),
                ...toRevoke.map(id => revoke.mutateAsync(id)),
            ]);
        } catch {
            showAlert('Cập nhật quyền thất bại. Vui lòng thử lại.');
        } finally {
            setSaving(false);
        }
    };

    // Ctrl+S lưu — nhãn phím trên nút phải có handler thật, neo callback qua ref của listener.
    const saveRef = useRef(handleSave);
    saveRef.current = handleSave;
    useEffect(() => {
        if (!dirty || saving) return;
        const onKey = (e: KeyboardEvent) => {
            if (!matchesShortcut(e, SHORTCUTS.SAVE)) return;
            e.preventDefault();
            void saveRef.current();
        };
        document.addEventListener('keydown', onKey);
        return () => document.removeEventListener('keydown', onKey);
    }, [dirty, saving]);

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
                    assignedIds={current}
                    onToggleOpen={handleToggleOpen}
                    onToggleAll={handleToggleAll}
                    onTogglePermission={handleTogglePermission}
                />
            ))}

            {/* Thanh hành động — chỉ hiện khi có thay đổi chưa lưu */}
            {dirty && (
                <div className="sticky bottom-0 flex items-center justify-between gap-3 bg-white border border-primary/30 rounded-section px-4 py-2.5 shadow-md">
                    <span className="text-md text-text-main">
                        Đã thay đổi <b>{changeCount}</b> quyền — chưa lưu
                    </span>
                    <div className="flex items-center gap-1.5">
                        <ActionButton variant="secondary" icon={FiRotateCcw} onClick={handleReset} disabled={saving}>
                            Hoàn tác
                        </ActionButton>
                        <ActionButton
                            variant="primary"
                            icon={FiSave}
                            shortcut={SHORTCUTS.SAVE.keys}
                            onClick={handleSave}
                            disabled={saving}
                        >
                            {saving ? 'Đang lưu…' : 'Lưu'}
                        </ActionButton>
                    </div>
                </div>
            )}
        </div>
    );
};

export default PermissionsTab;
