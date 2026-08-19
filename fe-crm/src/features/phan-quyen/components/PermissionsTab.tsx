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
    // tên nhóm — chỉ dùng để nêu rõ trong popup xác nhận
    roleName?: string;
    // báo lên trang cha khi có thay đổi chưa lưu, để chặn đổi nhóm/đổi tab làm mất dữ liệu
    onDirtyChange?: (dirty: boolean) => void;
}

// tab phân quyền — accordion các module
// tick checkbox CHỈ đổi bản nháp trong bộ nhớ; phải bấm "Lưu" và qua popup xác nhận thì mới gọi
// api. trước đây mỗi lần tick là một request gán/thu hồi ngay, không hoàn tác được
const PermissionsTab = ({ roleId, roleName, onDirtyChange }: Props) => {
    const { showAlert } = useAlert();
    const { confirm } = useConfirm();
    const [openModules, setOpenModules] = useState<Set<string>>(new Set());

    const { data: allPerms = [], isLoading: loadingAll } = useAllPermissions();
    const { data: rolePerms = [], isLoading: loadingRole } = useRolePermissions(roleId);
    const { assign, revoke } = useTogglePermission(roleId);

    // quyền đã lưu trên server
    const savedIds = useMemo(() => new Set(rolePerms.map(p => p.id)), [rolePerms]);

    // bản nháp đang chỉnh — null nghĩa là chưa nạp xong dữ liệu server
    const [draft, setDraft] = useState<Set<number> | null>(null);
    const [saving, setSaving] = useState(false);
    // baseline tạm thời sau khi lưu thành công — dùng ngay lập tức thay vì đợi `invalidateQueries`
    // refetch xong. không có bước này, `savedIds` (từ server) vẫn cũ trong một nhịp render, khiến
    // `dirty` vẫn true -> thanh "đã thay đổi" không tắt -> bấm Lưu lần 2 gửi lại request đã lưu -> lỗi trùng
    const [optimisticSavedIds, setOptimisticSavedIds] = useState<Set<number> | null>(null);

    // nạp lại bản nháp khi đổi nhóm hoặc khi server trả dữ liệu mới (sau khi lưu xong)
    // `savedKey` là chuỗi id đã sắp xếp — so sánh theo giá trị, không theo tham chiếu Set
    const savedKey = useMemo(() => [...savedIds].sort((a, b) => a - b).join(','), [savedIds]);
    useEffect(() => {
        if (loadingRole) return;
        setDraft(new Set(savedKey ? savedKey.split(',').map(Number) : []));
        setOptimisticSavedIds(null); // dữ liệu server đã tới — bỏ baseline tạm thời
    }, [roleId, savedKey, loadingRole]);

    // baseline để so sánh diff — ưu tiên baseline tạm thời (ngay sau khi lưu) trước server
    const baselineIds = optimisticSavedIds ?? savedIds;
    const current = draft ?? baselineIds;

    const toAssign = useMemo(() => [...current].filter(id => !baselineIds.has(id)), [current, baselineIds]);
    const toRevoke = useMemo(() => [...baselineIds].filter(id => !current.has(id)), [current, baselineIds]);
    const changeCount = toAssign.length + toRevoke.length;
    const dirty = changeCount > 0;

    // báo trạng thái dirty lên cha; khi tab bị gỡ thì trả về sạch để cha không kẹt cờ cũ
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

    // tick một quyền — chỉ đổi bản nháp, chưa gọi api
    const handleTogglePermission = useCallback((permId: number) => {
        setDraft(prev => {
            const next = new Set(prev ?? []);
            next.has(permId) ? next.delete(permId) : next.add(permId);
            return next;
        });
    }, []);

    // bật/tắt toàn bộ quyền của một phân hệ — cũng chỉ đổi bản nháp
    const handleToggleAll = useCallback((moduleId: string, checked: boolean) => {
        const perms = moduleGroups.get(moduleId) ?? [];
        setDraft(prev => {
            const next = new Set(prev ?? []);
            perms.forEach(p => (checked ? next.add(p.id) : next.delete(p.id)));
            return next;
        });
    }, [moduleGroups]);

    const handleReset = () => setDraft(new Set(baselineIds));

    const handleSave = async () => {
        const message = `Cập nhật quyền nhóm${roleName ? ` "${roleName}"` : ''}: `
            + [
                toAssign.length > 0 ? `gán ${toAssign.length} quyền` : null,
                toRevoke.length > 0 ? `thu hồi ${toRevoke.length} quyền` : null,
            ].filter(Boolean).join(', ') + '?';
        if (!(await confirm({ message }))) return;

        // chốt lại trạng thái đích tại thời điểm bấm Lưu — dùng làm baseline mới ngay khi thành công
        const nextIds = new Set(current);

        setSaving(true);
        try {
            // onError rỗng: chặn toast lỗi mặc định, để khối catch bên dưới tự báo một lần
            await Promise.all([
                ...toAssign.map(id => assign.mutate(id, { onError: () => {} })),
                ...toRevoke.map(id => revoke.mutate(id, { onError: () => {} })),
            ]);
            // đóng thanh "đã thay đổi" ngay lập tức, không đợi refetch — tránh bấm Lưu lần 2
            setOptimisticSavedIds(nextIds);
        } catch {
            showAlert('Cập nhật quyền thất bại. Vui lòng thử lại.');
        } finally {
            setSaving(false);
        }
    };

    // ctrl+s lưu — nhãn phím trên nút phải có handler thật, neo callback qua ref của listener
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
