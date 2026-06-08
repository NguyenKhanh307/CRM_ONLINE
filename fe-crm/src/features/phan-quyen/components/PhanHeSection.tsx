import { useRef, useEffect } from 'react';
import { FiChevronDown, FiChevronRight } from 'react-icons/fi';
import type { ModulePermission } from '@/features/phan-quyen/types/phanQuyenTypes';

interface Props {
    module: ModulePermission;
    onToggleOpen: (id: string) => void;
    onToggleAll: (id: string, checked: boolean) => void;
    onTogglePermission: (moduleId: string, permId: string) => void;
}

const ROLE_TAG_CLASS: Record<string, string> = {
    Admin:   'bg-blue-100 text-blue-700',
    Manager: 'bg-green-100 text-green-700',
    Staff:   'bg-orange-100 text-orange-700',
    Viewer:  'bg-gray-100 text-gray-600',
};

/** Render tag role màu theo loại role. */
const RoleTag = ({ role }: { role: string }) => (
    <span className={`inline-flex items-center px-2 py-0.5 rounded text-sm font-medium ${ROLE_TAG_CLASS[role] ?? 'bg-gray-100 text-gray-600'}`}>
        {role}
    </span>
);

/** Accordion section cho 1 phân hệ trong trang phân quyền. */
const PhanHeSection = ({ module, onToggleOpen, onToggleAll, onTogglePermission }: Props) => {
    const allChecked = module.permissions.every(p => p.checked);
    const someChecked = module.permissions.some(p => p.checked);
    const isIndeterminate = someChecked && !allChecked;

    const allCheckboxRef = useRef<HTMLInputElement>(null);

    useEffect(() => {
        if (allCheckboxRef.current) {
            allCheckboxRef.current.indeterminate = isIndeterminate;
        }
    }, [isIndeterminate]);

    const handleHeaderClick = () => onToggleOpen(module.id);

    const handleAllChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        e.stopPropagation();
        onToggleAll(module.id, e.target.checked);
    };

    return (
        <div className="border border-gray-200 rounded-section bg-white overflow-hidden">
            {/* Header */}
            <div
                className="flex items-center justify-between px-4 py-3 cursor-pointer select-none hover:bg-gray-50 transition-colors"
                onClick={handleHeaderClick}
            >
                <div className="flex items-center gap-3">
                    <span className="text-gray-400">
                        {module.isOpen ? <FiChevronDown size={16} /> : <FiChevronRight size={16} />}
                    </span>
                    <span className="text-md font-semibold text-text-main">{module.label}</span>
                </div>

                <div
                    className="flex items-center gap-2"
                    onClick={e => e.stopPropagation()}
                >
                    <label className="flex items-center gap-2 cursor-pointer text-md text-text-main select-none">
                        <input
                            ref={allCheckboxRef}
                            type="checkbox"
                            className="w-4 h-4 accent-primary cursor-pointer"
                            checked={allChecked}
                            onChange={handleAllChange}
                        />
                        Toàn quyền
                    </label>
                </div>
            </div>

            {/* Expandable table */}
            {module.isOpen && (
                <div className="border-t border-gray-200">
                    <table className="w-full">
                        <thead>
                            <tr className="bg-gray-100 border-b-2 border-gray-300">
                                <th className="text-left px-4 py-2 text-title font-semibold text-text-main w-1/3">
                                    Chức năng
                                </th>
                                <th className="text-center px-4 py-2 text-title font-semibold text-text-main w-20">
                                    Bật
                                </th>
                                <th className="text-left px-4 py-2 text-title font-semibold text-text-main">
                                    Các role có quyền
                                </th>
                            </tr>
                        </thead>
                        <tbody>
                            {module.permissions.map((perm, idx) => (
                                <tr
                                    key={perm.id}
                                    className={idx % 2 === 0 ? 'bg-white' : 'bg-gray-50'}
                                >
                                    <td className="px-4 py-2.5 text-table text-text-main border-b border-gray-200">
                                        {perm.name}
                                    </td>
                                    <td className="px-4 py-2.5 text-center border-b border-gray-200">
                                        <input
                                            type="checkbox"
                                            className="w-4 h-4 accent-primary cursor-pointer"
                                            checked={perm.checked}
                                            onChange={() => onTogglePermission(module.id, perm.id)}
                                        />
                                    </td>
                                    <td className="px-4 py-2.5 border-b border-gray-200">
                                        <div className="flex flex-wrap gap-1.5">
                                            {perm.roles.map(role => (
                                                <RoleTag key={role} role={role} />
                                            ))}
                                        </div>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            )}
        </div>
    );
};

export default PhanHeSection;
