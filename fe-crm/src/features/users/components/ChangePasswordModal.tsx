import { useRef, useState, type FormEvent } from 'react';
import { FiX } from 'react-icons/fi';
import { useAlert } from '@/shared/alert/useAlert';
import { useConfirm } from '@/shared/confirm/useConfirm';
import { ModalFooter } from '@/shared/components/ModalFooter';
import { FormField } from '@/shared/components/form/FormField';
import { useFormKeyboardNav } from '@/shared/keyboard/useFormKeyboardNav';
import { collectErrors } from '@/shared/utils/validators';
import { useChangePassword } from '../hooks/useChangePassword';

interface Props {
    onClose: () => void;
}

/** Modal đổi mật khẩu cho người dùng đang đăng nhập. */
export function ChangePasswordModal({ onClose }: Props) {
    const { showAlert } = useAlert();
    const { confirmSave } = useConfirm();
    const { mutate, isPending } = useChangePassword();
    const [currentPassword, setCurrentPassword] = useState('');
    const [newPassword, setNewPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [errors, setErrors] = useState<Record<string, string>>({});

    const clearError = (key: string) =>
        setErrors((prev) => (prev[key] ? { ...prev, [key]: '' } : prev));

    const formRef = useRef<HTMLFormElement>(null);
    useFormKeyboardNav(formRef, {
        onSubmit: () => formRef.current?.requestSubmit(),
        onCancel: onClose,
    });

    const handleSubmit = async (e: FormEvent) => {
        e.preventDefault();

        const found = collectErrors({
            currentPassword: !currentPassword ? 'Vui lòng nhập mật khẩu hiện tại' : null,
            newPassword: !newPassword
                ? 'Vui lòng nhập mật khẩu mới'
                : newPassword.length < 8 ? 'Mật khẩu mới phải có ít nhất 8 ký tự'
                : newPassword === currentPassword ? 'Mật khẩu mới phải khác mật khẩu hiện tại'
                : null,
            confirmPassword: !confirmPassword
                ? 'Vui lòng nhập lại mật khẩu mới'
                : newPassword !== confirmPassword ? 'Mật khẩu nhập lại không khớp' : null,
        });
        setErrors(found);
        if (Object.keys(found).length > 0) return;

        if (!(await confirmSave('mật khẩu'))) return;

        mutate(
            { currentPassword, newPassword },
            {
                onSuccess: () => {
                    showAlert('Đổi mật khẩu thành công.');
                    onClose();
                },
            },
        );
    };

    const inp = 'w-full border border-gray-300 rounded-btn px-3 py-1.5 text-md text-text-main focus:outline-none focus:border-primary';

    return (
        <div className="fixed inset-0 z-[9999] flex items-center justify-center bg-black/40" onClick={onClose}>
            <div className="bg-white rounded-card shadow-lg w-full max-w-md mx-4" onClick={(e) => e.stopPropagation()}>
                <div className="flex items-center justify-between px-5 py-4 border-b border-gray-200">
                    <h2 className="text-lg font-semibold text-text-main">Đổi mật khẩu</h2>
                    <button onClick={onClose} className="p-1 rounded hover:bg-gray-100 text-gray-500"><FiX size={18} /></button>
                </div>
                <form ref={formRef} onSubmit={handleSubmit} noValidate className="px-5 py-4 space-y-3">
                    <FormField label="Mật khẩu hiện tại" required error={errors.currentPassword}>
                        <input
                            type="password"
                            className={inp}
                            autoComplete="current-password"
                            value={currentPassword}
                            onChange={(e) => { setCurrentPassword(e.target.value); clearError('currentPassword'); }}
                        />
                    </FormField>
                    <FormField label="Mật khẩu mới" required hint="Tối thiểu 8 ký tự." error={errors.newPassword}>
                        <input
                            type="password"
                            className={inp}
                            autoComplete="new-password"
                            value={newPassword}
                            onChange={(e) => { setNewPassword(e.target.value); clearError('newPassword'); }}
                        />
                    </FormField>
                    <FormField label="Nhập lại mật khẩu mới" required error={errors.confirmPassword}>
                        <input
                            type="password"
                            className={inp}
                            autoComplete="new-password"
                            value={confirmPassword}
                            onChange={(e) => { setConfirmPassword(e.target.value); clearError('confirmPassword'); }}
                        />
                    </FormField>
                    <ModalFooter onCancel={onClose} saving={isPending} saveLabel="Đổi mật khẩu" />
                </form>
            </div>
        </div>
    );
}
