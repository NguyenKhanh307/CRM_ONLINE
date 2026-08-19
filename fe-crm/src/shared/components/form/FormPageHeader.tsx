import { ActionButton } from '@/shared/components/ActionButton';
// props của header form thêm mới full-page
interface FormPageHeaderProps {
    title: string;
    saving?: boolean;
    onCancel: () => void;
    onSave: () => void;
}

// header của form thêm mới full-page: tiêu đề + Hủy / Lưu
export const FormPageHeader = ({
    title,
    saving,
    onCancel,
    onSave,
}: FormPageHeaderProps) => (
    <div className="flex items-center justify-between mb-4">
        <h1 className="text-lg font-semibold text-text-main">{title}</h1>
        <div className="flex items-center gap-1.5">
            <ActionButton variant="secondary" onClick={onCancel} disabled={saving}>
                Hủy
            </ActionButton>
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
