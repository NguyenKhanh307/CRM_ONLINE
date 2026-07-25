import { useRef, useState, type FormEvent } from 'react';
import { useSearchParams } from 'react-router-dom';
import { FiLock, FiEye, FiEyeOff } from 'react-icons/fi';
import { FormField } from '@/shared/components/form/FormField';
import { useFormKeyboardNav } from '@/shared/keyboard/useFormKeyboardNav';
import { collectErrors } from '@/shared/utils/validators';
import { useActivateAccount } from '../hooks/useActivateAccount';

const inputCls =
    'w-full pl-9 pr-10 py-2 border border-gray-300 rounded-btn text-md text-text-main placeholder-gray-400 focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary transition-colors';

/**
 * Trang kích hoạt tài khoản — standalone, không dùng MainLayout.
 * Nhân viên nhấn link từ email, đặt mật khẩu lần đầu.
 * Lỗi nhập liệu hiện đỏ dưới đúng ô; không dùng popup.
 */
const ActivatePage = () => {
    const [searchParams] = useSearchParams();
    const token = searchParams.get('token') ?? '';

    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [showPassword, setShowPassword] = useState(false);
    const [showConfirm, setShowConfirm] = useState(false);
    const [errors, setErrors] = useState<Record<string, string>>({});

    const mutation = useActivateAccount();

    const formRef = useRef<HTMLFormElement>(null);
    useFormKeyboardNav(formRef, { onSubmit: () => formRef.current?.requestSubmit() });

    const clearError = (key: string) =>
        setErrors((prev) => (prev[key] ? { ...prev, [key]: '' } : prev));

    const handleSubmit = (e: FormEvent<HTMLFormElement>) => {
        e.preventDefault();

        const found = collectErrors({
            password: !password
                ? 'Vui lòng nhập mật khẩu mới'
                : password.length < 8 ? 'Mật khẩu phải có ít nhất 8 ký tự' : null,
            confirmPassword: !confirmPassword
                ? 'Vui lòng nhập lại mật khẩu'
                : password !== confirmPassword ? 'Mật khẩu xác nhận không khớp' : null,
        });
        setErrors(found);
        if (Object.keys(found).length > 0) return;

        mutation.mutate({ token, newPassword: password });
    };

    const apiError = mutation.error
        ? (mutation.error as { response?: { data?: { message?: string } } })
              ?.response?.data?.message ?? 'Đã xảy ra lỗi, vui lòng thử lại.'
        : null;

    return (
        <div className="min-h-screen bg-blue-200 flex items-center justify-center px-4">
            <div className="bg-white rounded-card shadow-sm w-full max-w-md p-10">

                {/* Logo / Tiêu đề */}
                <div className="text-center mb-8">
                    <div className="inline-flex items-center justify-center w-12 h-12 rounded-card bg-primary mb-4">
                        <FiLock size={20} className="text-white" />
                    </div>
                    <h1 className="text-xl font-semibold text-text-main">Kích hoạt tài khoản CRM</h1>
                    <p className="text-sm text-gray-500 mt-1">Đặt mật khẩu để hoàn tất kích hoạt</p>
                </div>

                {!token && (
                    <p className="text-sm text-danger bg-red-50 border border-red-200 rounded-btn px-3 py-2 mb-4">
                        Link kích hoạt không hợp lệ. Vui lòng kiểm tra lại email.
                    </p>
                )}

                <form ref={formRef} onSubmit={handleSubmit} noValidate className="space-y-4">

                    <FormField label="Mật khẩu mới" icon={FiLock} error={errors.password}>
                        <input
                            type={showPassword ? 'text' : 'password'}
                            value={password}
                            onChange={(e) => { setPassword(e.target.value); clearError('password'); }}
                            placeholder="Ít nhất 8 ký tự"
                            autoComplete="new-password"
                            className={inputCls}
                        />
                        <button
                            type="button"
                            onClick={() => setShowPassword((p) => !p)}
                            className="absolute inset-y-0 right-3 flex items-center text-gray-400 hover:text-gray-600 transition-colors"
                            tabIndex={-1}
                        >
                            {showPassword ? <FiEyeOff size={16} /> : <FiEye size={16} />}
                        </button>
                    </FormField>

                    <FormField label="Xác nhận mật khẩu" icon={FiLock} error={errors.confirmPassword}>
                        <input
                            type={showConfirm ? 'text' : 'password'}
                            value={confirmPassword}
                            onChange={(e) => { setConfirmPassword(e.target.value); clearError('confirmPassword'); }}
                            placeholder="Nhập lại mật khẩu"
                            autoComplete="new-password"
                            className={inputCls}
                        />
                        <button
                            type="button"
                            onClick={() => setShowConfirm((p) => !p)}
                            className="absolute inset-y-0 right-3 flex items-center text-gray-400 hover:text-gray-600 transition-colors"
                            tabIndex={-1}
                        >
                            {showConfirm ? <FiEyeOff size={16} /> : <FiEye size={16} />}
                        </button>
                    </FormField>

                    {/* Lỗi cấp form từ API */}
                    {apiError && (
                        <p className="text-sm text-danger bg-red-50 border border-red-200 rounded-btn px-3 py-2">
                            {apiError}
                        </p>
                    )}

                    {/* Nút kích hoạt */}
                    <button
                        type="submit"
                        disabled={mutation.isPending || !token}
                        className="w-full bg-primary text-white py-2 rounded-btn text-md font-medium hover:opacity-90 disabled:opacity-60 disabled:cursor-not-allowed transition-colors flex items-center justify-center gap-2"
                    >
                        {mutation.isPending && (
                            <span className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />
                        )}
                        Kích hoạt tài khoản
                    </button>
                </form>
            </div>
        </div>
    );
};

export default ActivatePage;
