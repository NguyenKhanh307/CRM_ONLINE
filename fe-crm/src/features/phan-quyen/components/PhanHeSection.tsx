import { useRef, useEffect } from 'react';
import { FiChevronDown, FiChevronRight } from 'react-icons/fi';
import type { GroupPermission } from '@/features/phan-quyen/types/phanQuyenTypes';

interface Props {
    moduleId: string;
    label: string;
    isOpen: boolean;
    permissions: GroupPermission[];
    assignedIds: Set<number>;
    onToggleOpen: (moduleId: string) => void;
    onToggleAll: (moduleId: string, checked: boolean) => void;
    onTogglePermission: (permId: number, currentlyChecked: boolean) => void;
}

/** Accordion section cho 1 phân hệ trong tab phân quyền. */
const PhanHeSection = ({
    moduleId, label, isOpen, permissions, assignedIds,
    onToggleOpen, onToggleAll, onTogglePermission,
}: Props) => {
    const allChecked = permissions.length > 0 && permissions.every(p => assignedIds.has(p.id));
    const someChecked = permissions.some(p => assignedIds.has(p.id));
    const isIndeterminate = someChecked && !allChecked;

    const allCheckboxRef = useRef<HTMLInputElement>(null);

    useEffect(() => {
        if (allCheckboxRef.current) {
            allCheckboxRef.current.indeterminate = isIndeterminate;
        }
    }, [isIndeterminate]);

    return (
        <div className="border border-gray-200 rounded-section bg-white overflow-hidden">
            {/* Header */}
            <div
                className="flex items-center justify-between px-4 py-3 cursor-pointer select-none hover:bg-gray-50 transition-colors"
                onClick={() => onToggleOpen(moduleId)}
            >
                <div className="flex items-center gap-3">
                    <span className="text-gray-400">
                        {isOpen ? <FiChevronDown size={16} /> : <FiChevronRight size={16} />}
                    </span>
                    <span className="text-md font-semibold text-text-main">{label}</span>
                    <span className="text-sm text-gray-400">
                        ({permissions.filter(p => assignedIds.has(p.id)).length}/{permissions.length})
                    </span>
                </div>

                <div className="flex items-center gap-2" onClick={e => e.stopPropagation()}>
                    <label className="flex items-center gap-2 cursor-pointer text-md text-text-main select-none">
                        <input
                            ref={allCheckboxRef}
                            type="checkbox"
                            className="w-4 h-4 accent-primary cursor-pointer"
                            checked={allChecked}
                            onChange={e => onToggleAll(moduleId, e.target.checked)}
                        />
                        Toàn quyền
                    </label>
                </div>
            </div>

            {/* Expandable table */}
            {isOpen && (
                <div className="border-t border-gray-200">
                    <table className="w-full">
                        <thead>
                            <tr className="bg-gray-100 border-b-2 border-gray-300">
                                <th className="text-left px-4 py-2 text-title font-semibold text-text-main w-1/2">
                                    Chức năng
                                </th>
                                <th className="text-center px-4 py-2 text-title font-semibold text-text-main w-20">
                                    Bật
                                </th>
                                <th className="text-left px-4 py-2 text-title font-semibold text-text-main">
                                    Mã quyền
                                </th>
                            </tr>
                        </thead>
                        <tbody>
                            {permissions.map((perm, idx) => {
                                const checked = assignedIds.has(perm.id);
                                return (
                                    <tr key={perm.id} className={idx % 2 === 0 ? 'bg-white' : 'bg-gray-50'}>
                                        <td className="px-4 py-2.5 text-table text-text-main border-b border-gray-200">
                                            {perm.name}
                                        </td>
                                        <td className="px-4 py-2.5 text-center border-b border-gray-200">
                                            <input
                                                type="checkbox"
                                                className="w-4 h-4 accent-primary cursor-pointer"
                                                checked={checked}
                                                onChange={() => onTogglePermission(perm.id, checked)}
                                            />
                                        </td>
                                        <td className="px-4 py-2.5 text-sm text-gray-400 border-b border-gray-200 font-mono">
                                            {perm.code}
                                        </td>
                                    </tr>
                                );
                            })}
                        </tbody>
                    </table>
                </div>
            )}
        </div>
    );
};

export default PhanHeSection;
