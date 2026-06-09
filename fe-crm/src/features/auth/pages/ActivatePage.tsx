import { useState, type FormEvent } from 'react';
import { useSearchParams } from 'react-router-dom';
import { FiLock, FiEye, FiEyeOff } from 'react-icons/fi';
import { useActivateAccount } from '../hooks/useActivateAccount';

/**
 * Trang kích hoạt tài khoản — standalone, không dùng MainLayout.
 * Nhân viên nhấn link từ email, đặt mật khẩu lần đầu.
 */
const ActivatePage = () => {
    const [searchParams] = useSearchParams();
    const token = searchParams.get('token') ?? '';

    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [showPassword, setShowPassword] = useState(false);
    const [showConfirm, setShowConfirm] = useState(false);
    const [validationError, setValidationError] = useState<string | null>(null);

    const mutation = useActivateAccount();

    const handleSubmit = (e: FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        setValidationError(null);

        if (!token) {
            setValidationError('Link kích hoạt không hợp lệ. Vui lòng kiểm tra lại email.');
            return;
        }
        if (password.length < 8) {
            setValidationError('Mật khẩu phải có ít nhất 8 ký tự.');
            return;
        }
        if (password !== confirmPassword) {
            setValidationError('Mật khẩu xác nhận không khớp.');
            return;
        }

        mutation.mutate({ token, newPassword: password });
    };

    const apiError = mutation.error
        ? (mutation.error as { response?: { data?: { message?: string } } })
              ?.response?.data?.message ?? 'Đã xảy ra lỗi, vui lòng thử lại.'
        : null;

    const errorMessage = validationError ?? apiError;

    const inputCls =
        'w-full pl-9 pr-10 py-2 border border-gray-300 rounded-btn text-md text-text-main placeholder-gray-400 focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary transition-colors';

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

                <form onSubmit={handleSubmit} noValidate>

                    {/* Mật khẩu mới */}
                    <div className="mb-4">
                        <label className="block text-md font-medium text-text-main mb-1">
                            Mật khẩu mới
                        </label>
                        <div className="relative">
                            <span className="absolute inset-y-0 left-3 flex items-center text-gray-400 pointer-events-none">
                                <FiLock size={16} />
                            </span>
                            <input
                                type={showPassword ? 'text' : 'password'}
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
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
                        </div>
                    </div>

                    {/* Xác nhận mật khẩu */}
                    <div className="mb-6">
                        <label className="block text-md font-medium text-text-main mb-1">
                            Xác nhận mật khẩu
                        </label>
                        <div className="relative">
                            <span className="absolute inset-y-0 left-3 flex items-center text-gray-400 pointer-events-none">
                                <FiLock size={16} />
                            </span>
                            <input
                                type={showConfirm ? 'text' : 'password'}
                                value={confirmPassword}
                                onChange={(e) => setConfirmPassword(e.target.value)}
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
                        </div>
                    </div>

                    {/* Error */}
                    {errorMessage && (
                        <p className="text-sm text-danger mb-4 bg-red-50 border border-red-200 rounded-btn px-3 py-2">
                            {errorMessage}
                        </p>
                    )}

                    {/* Nút kích hoạt */}
                    <button
                        type="submit"
                        disabled={mutation.isPending || !token}
                        className="w-full bg-primary text-white py-2 rounded-btn text-md font-medium hover:bg-blue-600 disabled:opacity-60 disabled:cursor-not-allowed transition-colors flex items-center justify-center gap-2"
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
