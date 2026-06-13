import { btnBase } from './formStyles';

interface FormPageHeaderProps {
    title: string;
    saving?: boolean;
    onCancel: () => void;
    onSave: () => void;
    /** Nếu truyền thì hiện nút "Lưu và thêm". */
    onSaveAndNew?: () => void;
}

/**
 * Header của form thêm mới full-page: tiêu đề + Hủy / Lưu và thêm / Lưu.
 */
export const FormPageHeader = ({
    title,
    saving,
    onCancel,
    onSave,
    onSaveAndNew,
}: FormPageHeaderProps) => (
    <div className="flex items-center justify-between mb-4">
        <h1 className="text-lg font-semibold text-text-main">{title}</h1>
        <div className="flex items-center gap-2">
            <button
                type="button"
                onClick={onCancel}
                disabled={saving}
                className={`${btnBase} border border-gray-300 text-gray-600 hover:bg-gray-50 disabled:opacity-50`}
            >
                Hủy
            </button>
            {onSaveAndNew && (
                <button
                    type="button"
                    onClick={onSaveAndNew}
                    disabled={saving}
                    className={`${btnBase} border border-primary text-primary hover:bg-blue-50 disabled:opacity-50`}
                >
                    Lưu và thêm
                </button>
            )}
            <button
                type="button"
                onClick={onSave}
                disabled={saving}
                className={`${btnBase} bg-primary text-white hover:bg-blue-600 disabled:opacity-50`}
            >
                {saving ? 'Đang lưu…' : 'Lưu'}
            </button>
        </div>
    </div>
);
