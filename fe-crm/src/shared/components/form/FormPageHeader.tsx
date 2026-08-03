import { ActionButton } from '@/shared/components/ActionButton';

interface FormPageHeaderProps {
    title: string;
    saving?: boolean;
    onCancel: () => void;
    onSave: () => void;
    // nếu truyền thì hiện nút "Lưu và thêm"
    onSaveAndNew?: () => void;
}

// header của form thêm mới full-page: tiêu đề + Hủy / Lưu và thêm / Lưu
export const FormPageHeader = ({
    title,
    saving,
    onCancel,
    onSave,
    onSaveAndNew,
}: FormPageHeaderProps) => (
    <div className="flex items-center justify-between mb-4">
        <h1 className="text-lg font-semibold text-text-main">{title}</h1>
        <div className="flex items-center gap-1.5">
            <ActionButton variant="secondary" onClick={onCancel} disabled={saving}>
                Hủy
            </ActionButton>
            {onSaveAndNew && (
                <ActionButton variant="outline" onClick={onSaveAndNew} disabled={saving}>
                    Lưu và thêm
                </ActionButton>
            )}
            <ActionButton
                variant="primary"
                onClick={onSave}
                disabled={saving}
                shortcut={saving ? undefined : ['Ctrl', 'S']}
            >
                {saving ? 'Đang lưu…' : 'Lưu'}
            </ActionButton>
        </div>
    </div>
);
