import { FiPlus, FiShield } from 'react-icons/fi';
import type { RoleGroup } from '../types/phanQuyenTypes';

interface Props {
    groups: RoleGroup[];
    selectedId: number | null;
    onSelect: (group: RoleGroup) => void;
    onCreateClick: () => void;
    isLoading: boolean;
    isError: boolean;
}

// panel trái — danh sách nhóm người dùng
const GroupList = ({ groups, selectedId, onSelect, onCreateClick, isLoading, isError }: Props) => {
    return (
        <div className="w-64 shrink-0 bg-white border-r border-gray-200 flex flex-col h-full">
            {/* Header */}
            <div className="flex items-center justify-between px-4 py-3 border-b border-gray-200">
                <span className="text-md font-semibold text-text-main">Nhóm người dùng</span>
                <button
                    onClick={onCreateClick}
                    className="flex items-center gap-1 text-primary hover:text-blue-700 text-sm transition-colors"
                    title="Tạo nhóm mới"
                >
                    <FiPlus size={16} />
                    <span>Tạo nhóm</span>
                </button>
            </div>

            {/* List */}
            <div className="flex-1 overflow-y-auto py-2">
                {isLoading && (
                    <p className="px-4 py-3 text-sm text-gray-400">Đang tải...</p>
                )}
                {!isLoading && isError && (
                    <p className="px-4 py-3 text-sm text-danger">Lỗi tải dữ liệu. Kiểm tra backend.</p>
                )}
                {!isLoading && !isError && groups.length === 0 && (
                    <p className="px-4 py-3 text-sm text-gray-400">Chưa có nhóm nào.</p>
                )}
                {groups.map(group => (
                    <button
                        key={group.id}
                        onClick={() => onSelect(group)}
                        className={`w-full text-left px-4 py-2.5 flex items-start gap-2.5 transition-colors hover:bg-gray-50 ${
                            selectedId === group.id ? 'bg-blue-50 border-l-2 border-l-primary' : ''
                        }`}
                    >
                        <FiShield
                            size={15}
                            className={`mt-0.5 shrink-0 ${selectedId === group.id ? 'text-primary' : 'text-gray-400'}`}
                        />
                        <div>
                            <p className={`text-md font-medium ${selectedId === group.id ? 'text-primary' : 'text-text-main'}`}>
                                {group.name}
                            </p>
                            {group.description && (
                                <p className="text-sm text-gray-400 truncate max-w-[160px]">{group.description}</p>
                            )}
                            {group.isSystem && (
                                <span className="inline-block text-sm px-1.5 py-0 rounded bg-blue-100 text-blue-600 mt-0.5">
                                    Hệ thống
                                </span>
                            )}
                        </div>
                    </button>
                ))}
            </div>
        </div>
    );
};

export default GroupList;
