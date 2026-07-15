import { useState, useEffect, type FormEvent } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { FiUser, FiLock, FiEye, FiEyeOff } from 'react-icons/fi';
import { useLogin } from '../hooks/useLogin';
import { GoogleLoginButton } from '../components/GoogleLoginButton';

/**
 * Trang đăng nhập — standalone, không dùng MainLayout.
 */
const LoginPage = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [showPassword, setShowPassword] = useState(false);
    const [gmailError, setGmailError] = useState<string | null>(null);

    // Reset form khi vào trang login (sau logout) — chặn autofill giữ lại email/mật khẩu cũ trên máy dùng chung
    useEffect(() => {
        setEmail('');
        setPassword('');
    }, []);

    const loginMutation = useLogin();
    const location = useLocation();
    const justActivated = (location.state as { activated?: boolean } | null)?.activated === true;

    const handleSubmit = (e: FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        setGmailError(null);
        if (!email.trim() || !password.trim()) return;
        if (!email.trim().toLowerCase().endsWith('@gmail.com')) {
            setGmailError('Chỉ chấp nhận địa chỉ @gmail.com');
            return;
        }
        loginMutation.mutate({ email: email.trim(), password });
    };

    const errorMessage = gmailError ?? (loginMutation.error
        ? (loginMutation.error as { response?: { data?: { message?: string } } })
              ?.response?.data?.message ?? 'Tên tài khoản hoặc mật khẩu không đúng.'
        : null);

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

                <form onSubmit={handleSubmit} noValidate autoComplete="off">

                    {/* Email */}
                    <div className="mb-4">
                        <label className="block text-md font-medium text-text-main mb-1">
                            Email
                        </label>
                        <div className="relative">
                            <span className="absolute inset-y-0 left-3 flex items-center text-gray-400 pointer-events-none">
                                <FiUser size={16} />
                            </span>
                            <input
                                type="email"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                placeholder="Nhập địa chỉ email"
                                autoComplete="off"
                                className="w-full pl-9 pr-3 py-2 border border-gray-300 rounded-btn text-md text-text-main placeholder-gray-400 focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary transition-colors"
                            />
                        </div>
                    </div>

                    {/* Password */}
                    <div className="mb-2">
                        <label className="block text-md font-medium text-text-main mb-1">
                            Mật khẩu
                        </label>
                        <div className="relative">
                            <span className="absolute inset-y-0 left-3 flex items-center text-gray-400 pointer-events-none">
                                <FiLock size={16} />
                            </span>
                            <input
                                type={showPassword ? 'text' : 'password'}
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                placeholder="Nhập mật khẩu"
                                autoComplete="new-password"
                                className="w-full pl-9 pr-10 py-2 border border-gray-300 rounded-btn text-md text-text-main placeholder-gray-400 focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary transition-colors"
                            />
                            <button
                                type="button"
                                onClick={() => setShowPassword((prev) => !prev)}
                                className="absolute inset-y-0 right-3 flex items-center text-gray-400 hover:text-gray-600 transition-colors"
                                tabIndex={-1}
                            >
                                {showPassword ? <FiEyeOff size={16} /> : <FiEye size={16} />}
                            </button>
                        </div>
                    </div>

                    {/* Quên mật khẩu */}
                    <div className="flex justify-end mb-6">
                        <Link
                            to="/forgot-password"
                            className="text-sm text-primary hover:underline"
                        >
                            Quên mật khẩu?
                        </Link>
                    </div>

                    {/* Error message */}
                    {errorMessage && (
                        <p className="text-sm text-danger mb-4 bg-red-50 border border-red-200 rounded-btn px-3 py-2">
                            {errorMessage}
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
