import { useState } from 'react';
import { FiShield } from 'react-icons/fi';
import type { ModulePermission } from '@/features/phan-quyen/types/phanQuyenTypes';
import { INITIAL_MODULES } from '@/features/phan-quyen/constants/phanQuyenData';
import PhanHeSection from '@/features/phan-quyen/components/PhanHeSection';
import { useAlert } from '@/shared/alert/useAlert';

/** Trang quản lý phân quyền — tổ chức theo phân hệ dạng accordion. */
const PhanQuyenPage = () => {
    const [modules, setModules] = useState<ModulePermission[]>(INITIAL_MODULES);
    const { showAlert } = useAlert();

    /** Toggle mở/đóng accordion của 1 phân hệ. */
    const handleToggleOpen = (id: string) => {
        setModules(prev =>
            prev.map(m => m.id === id ? { ...m, isOpen: !m.isOpen } : m)
        );
    };

    /** Tick/bỏ tick toàn bộ quyền trong 1 phân hệ. */
    const handleToggleAll = (moduleId: string, checked: boolean) => {
        setModules(prev =>
            prev.map(m =>
                m.id === moduleId
                    ? { ...m, permissions: m.permissions.map(p => ({ ...p, checked })) }
                    : m
            )
        );
    };

    /** Toggle 1 quyền cụ thể trong phân hệ. */
    const handleTogglePermission = (moduleId: string, permId: string) => {
        setModules(prev =>
            prev.map(m =>
                m.id === moduleId
                    ? {
                        ...m,
                        permissions: m.permissions.map(p =>
                            p.id === permId ? { ...p, checked: !p.checked } : p
                        ),
                    }
                    : m
            )
        );
    };

    const handleSave = () => {
        showAlert('Lưu phân quyền thành công!');
    };

    return (
        <div className="p-6 bg-bg-main min-h-screen">
            {/* Page header */}
            <div className="flex items-center justify-between mb-5">
                <div className="flex items-center gap-2">
                    <FiShield size={20} className="text-primary" />
                    <h1 className="text-xl font-semibold text-text-main">Phân quyền</h1>
                </div>
                <button
                    className="px-4 py-1.5 bg-primary text-white text-md rounded-btn hover:bg-blue-600 transition-colors"
                    onClick={handleSave}
                >
                    Lưu thay đổi
                </button>
            </div>

            {/* Module list */}
            <div className="space-y-3">
                {modules.map(m => (
                    <PhanHeSection
                        key={m.id}
                        module={m}
                        onToggleOpen={handleToggleOpen}
                        onToggleAll={handleToggleAll}
                        onTogglePermission={handleTogglePermission}
                    />
                ))}
            </div>
        </div>
    );
};

export default PhanQuyenPage;
