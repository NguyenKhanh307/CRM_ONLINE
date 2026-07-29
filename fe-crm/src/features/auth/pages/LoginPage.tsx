import { useEffect, useRef, useState, type FormEvent } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { FiUser, FiLock, FiEye, FiEyeOff } from 'react-icons/fi';
import { FormField } from '@/shared/components/form/FormField';
import { useFormKeyboardNav } from '@/shared/keyboard/useFormKeyboardNav';
import { collectErrors, emailError } from '@/shared/utils/validators';
import { useLogin } from '../hooks/useLogin';
import { GoogleLoginButton } from '../components/GoogleLoginButton';

const inputCls =
    'w-full pl-9 pr-3 py-2 border border-gray-300 rounded-btn text-md text-text-main placeholder-gray-400 focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary transition-colors';

/**
 * Trang đăng nhập — standalone, không dùng MainLayout.
 * Lỗi nhập liệu hiện đỏ ngay dưới ô tương ứng; cố ý KHÔNG có popup xác nhận (không phải
 * thao tác thêm/sửa/xóa bản ghi). Lỗi trả về từ API là lỗi cấp form nên vẫn hiện banner.
 */
const LoginPage = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [showPassword, setShowPassword] = useState(false);
    const [errors, setErrors] = useState<Record<string, string>>({});

    // Reset form khi vào trang login (sau logout) — chặn autofill giữ lại email/mật khẩu cũ trên máy dùng chung
    useEffect(() => {
        setEmail('');
        setPassword('');
    }, []);

    const loginMutation = useLogin();
    const location = useLocation();
    const justActivated = (location.state as { activated?: boolean } | null)?.activated === true;

    const formRef = useRef<HTMLFormElement>(null);
    // Không truyền onCancel: Esc không được xóa dữ liệu người dùng đang nhập.
    useFormKeyboardNav(formRef, { onSubmit: () => formRef.current?.requestSubmit() });

    /** Xóa lỗi của một ô ngay khi người dùng gõ lại. */
    const clearError = (key: string) =>
        setErrors((prev) => (prev[key] ? { ...prev, [key]: '' } : prev));

    const handleSubmit = (e: FormEvent<HTMLFormElement>) => {
        e.preventDefault();

        const found = collectErrors({
            email: !email.trim() ? 'Vui lòng nhập email' : emailError(email),
            password: !password.trim() ? 'Vui lòng nhập mật khẩu' : null,
        });
        setErrors(found);
        if (Object.keys(found).length > 0) return;

        loginMutation.mutate({ email: email.trim(), password });
    };

    const apiError = loginMutation.error
        ? (loginMutation.error as { response?: { data?: { message?: string } } })
              ?.response?.data?.message ?? 'Tên tài khoản hoặc mật khẩu không đúng.'
        : null;

    return (
        <div className="min-h-screen bg-blue-200 flex items-center justify-center px-4">
            <div className="bg-white rounded-card shadow-sm w-full max-w-md p-10">

                {/* Logo / Tiêu đề */}
                <div className="text-center mb-8">
                    <div className="inline-flex items-center justify-center w-12 h-12 rounded-card bg-primary mb-4">
                        <span className="text-white font-bold text-lg">C</span>
                    </div>
                    <h1 className="text-xl font-semibold text-text-main">Đăng nhập CRM</h1>
                    <p className="text-sm text-gray-500 mt-1">Chào mừng bạn quay trở lại</p>
                </div>

                {justActivated && (
                    <p className="text-sm text-green-700 mb-6 bg-green-50 border border-green-200 rounded-btn px-3 py-2">
                        Tài khoản đã được kích hoạt thành công. Hãy đăng nhập.
                    </p>
                )}

                <form ref={formRef} onSubmit={handleSubmit} noValidate autoComplete="off" className="space-y-4">

                    <FormField label="Email" icon={FiUser} error={errors.email}>
                        <input
                            type="email"
                            value={email}
                            onChange={(e) => { setEmail(e.target.value); clearError('email'); }}
                            placeholder="Nhập địa chỉ email"
                            autoComplete="off"
                            className={inputCls}
                        />
                    </FormField>

                    <FormField label="Mật khẩu" icon={FiLock} error={errors.password}>
                        <input
                            type={showPassword ? 'text' : 'password'}
                            value={password}
                            onChange={(e) => { setPassword(e.target.value); clearError('password'); }}
                            placeholder="Nhập mật khẩu"
                            autoComplete="new-password"
                            className={`${inputCls} pr-10`}
                        />
                        <button
                            type="button"
                            onClick={() => setShowPassword((prev) => !prev)}
                            className="absolute inset-y-0 right-3 flex items-center text-gray-400 hover:text-gray-600 transition-colors"
                            tabIndex={-1}
                        >
                            {showPassword ? <FiEyeOff size={16} /> : <FiEye size={16} />}
                        </button>
                    </FormField>

                    {/* Quên mật khẩu */}
                    <div className="flex justify-end">
                        <Link
                            to="/forgot-password"
                            className="text-sm text-primary hover:underline"
                        >
                            Quên mật khẩu?
                        </Link>
                    </div>

                    {/* Lỗi cấp form từ API (sai tài khoản / mật khẩu) */}
                    {apiError && (
                        <p className="text-sm text-danger bg-red-50 border border-red-200 rounded-btn px-3 py-2">
                            {apiError}
                        </p>
                    )}

                    {/* Nút Đăng nhập */}
                    <button
                        type="submit"
                        disabled={loginMutation.isPending}
                        className="w-full bg-primary text-white py-2 rounded-btn text-md font-medium hover:opacity-90 disabled:opacity-60 disabled:cursor-not-allowed transition-colors flex items-center justify-center gap-2"
                    >
                        {loginMutation.isPending && (
                            <span className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />
                        )}
                        Đăng nhập
                    </button>
                </form>

                {/* Divider */}
                <div className="flex items-center gap-3 my-6">
                    <div className="flex-1 h-px bg-gray-200" />
                    <span className="text-sm text-gray-400">hoặc</span>
                    <div className="flex-1 h-px bg-gray-200" />
                </div>

                {/* Đăng nhập bằng Google */}
                <GoogleLoginButton />
            </div>
        </div>
    );
};

export default LoginPage;
