import { useRef, useState, type FormEvent } from 'react';
import { FiX } from 'react-icons/fi';
import { useAlert } from '@/shared/alert/useAlert';
import { ModalFooter } from '@/shared/components/ModalFooter';
import { useFormKeyboardNav } from '@/shared/keyboard/useFormKeyboardNav';
import { useChangePassword } from '../hooks/useChangePassword';

interface Props {
    onClose: () => void;
}

/** Modal đổi mật khẩu cho người dùng đang đăng nhập. */
export function ChangePasswordModal({ onClose }: Props) {
    const { showAlert } = useAlert();
    const { mutate, isPending } = useChangePassword();
    const [currentPassword, setCurrentPassword] = useState('');
    const [newPassword, setNewPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');

    const formRef = useRef<HTMLFormElement>(null);
    useFormKeyboardNav(formRef, {
        onSubmit: () => formRef.current?.requestSubmit(),
        onCancel: onClose,
    });

    const handleSubmit = (e: FormEvent) => {
        e.preventDefault();
        if (newPassword.length < 8) {
            showAlert('Mật khẩu mới phải có ít nhất 8 ký tự.');
            return;
        }
        if (newPassword !== confirmPassword) {
            showAlert('Mật khẩu nhập lại không khớp.');
            return;
        }
        if (newPassword === currentPassword) {
            showAlert('Mật khẩu mới phải khác mật khẩu hiện tại.');
            return;
        }
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
    const lbl = 'block text-sm font-medium text-gray-700 mb-1';

    return (
        <div className="fixed inset-0 z-[9999] flex items-center justify-center bg-black/40" onClick={onClose}>
            <div className="bg-white rounded-card shadow-lg w-full max-w-md mx-4" onClick={(e) => e.stopPropagation()}>
                <div className="flex items-center justify-between px-5 py-4 border-b border-gray-200">
                    <h2 className="text-lg font-semibold text-text-main">Đổi mật khẩu</h2>
                    <button onClick={onClose} className="p-1 rounded hover:bg-gray-100 text-gray-500"><FiX size={18} /></button>
                </div>
                <form ref={formRef} onSubmit={handleSubmit} className="px-5 py-4 space-y-3">
                    <div>
                        <label className={lbl}>Mật khẩu hiện tại <span className="text-danger">*</span></label>
                        <input
                            type="password"
                            className={inp}
                            required
                            autoComplete="current-password"
                            value={currentPassword}
                            onChange={(e) => setCurrentPassword(e.target.value)}
                        />
                    </div>
                    <div>
                        <label className={lbl}>Mật khẩu mới <span className="text-danger">*</span></label>
                        <input
                            type="password"
                            className={inp}
                            required
                            autoComplete="new-password"
                            value={newPassword}
                            onChange={(e) => setNewPassword(e.target.value)}
                        />
                        <p className="text-xs text-gray-400 mt-1">Tối thiểu 8 ký tự.</p>
                    </div>
                    <div>
                        <label className={lbl}>Nhập lại mật khẩu mới <span className="text-danger">*</span></label>
                        <input
                            type="password"
                            className={inp}
                            required
                            autoComplete="new-password"
                            value={confirmPassword}
                            onChange={(e) => setConfirmPassword(e.target.value)}
                        />
                    </div>
                    <ModalFooter onCancel={onClose} saving={isPending} saveLabel="Đổi mật khẩu" />
                </form>
            </div>
        </div>
    );
}
