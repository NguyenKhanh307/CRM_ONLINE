import { useNavigate } from 'react-router-dom';
import { FiAlertTriangle } from 'react-icons/fi';
import type { DuplicateMatch } from '@/shared/types/duplicate';
import { recordPath } from '@/shared/utils/moduleRoutes';
import type { RelatedModule } from '@/shared/types/related';

const MODULE_LABELS: Record<string, string> = {
    lead: 'Tiềm năng',
    customer: 'Khách hàng',
    contact: 'Liên hệ',
};

const FIELD_LABELS: Record<string, string> = {
    email: 'email',
    phone: 'số điện thoại',
    taxCode: 'mã số thuế',
};

// route bản ghi trùng; tiềm năng chưa có trang chi tiết -> nhảy về danh sách + focus dòng
const pathOf = (m: DuplicateMatch): string =>
    m.module === 'lead' ? `/tiem-nang?focus=${m.id}` : recordPath(m.module as RelatedModule, m.id);

interface DuplicateWarningProps {
    matches: DuplicateMatch[] | undefined;
}

// banner cảnh báo bản ghi trùng email/SĐT/MST
// KHÔNG chặn lưu — khách hàng có thể dùng chung số tổng đài hay email công ty
export const DuplicateWarning = ({ matches }: DuplicateWarningProps) => {
    const navigate = useNavigate();
    if (!matches || matches.length === 0) return null;

    return (
        <div className="mb-4 rounded-btn border border-amber-300 bg-amber-50 px-4 py-3">
            <div className="flex items-center gap-2 text-table font-medium text-amber-800">
                <FiAlertTriangle size={14} />
                Phát hiện {matches.length} bản ghi có thể trùng — kiểm tra trước khi lưu
            </div>
            <ul className="mt-2 space-y-1">
                {matches.map(m => (
                    <li key={`${m.module}-${m.id}`} className="text-table text-amber-900">
                        <span className="text-gray-600">{MODULE_LABELS[m.module] ?? m.module}:</span>{' '}
                        <button
                            onClick={() => navigate(pathOf(m))}
                            className="font-medium text-primary hover:underline"
                        >
                            {m.name}{m.code ? ` (${m.code})` : ''}
                        </button>{' '}
                        <span className="text-gray-600">
                            — trùng {FIELD_LABELS[m.matchedField] ?? m.matchedField}
                            {m.matchedValue ? ` "${m.matchedValue}"` : ''}
                        </span>
                    </li>
                ))}
            </ul>
        </div>
    );
};
