import { useEffect, useState, type FormEvent } from 'react';
import { FiUserPlus, FiMail, FiUser, FiPhone, FiLayers, FiShield } from 'react-icons/fi';
import { useRegisterEmployee } from '../hooks/useRegisterEmployee';
import { useRoleGroups } from '@/features/phan-quyen/hooks/useRoleGroups';

const inputCls =
    'w-full pl-9 pr-3 py-2 border border-gray-300 rounded-btn text-md text-text-main placeholder-gray-400 focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary transition-colors';

/**
 * Trang đăng ký tài khoản nhân viên mới — admin tạo tài khoản, hệ thống gửi email kích hoạt.
 */
const RegisterEmployeePage = () => {
    const [email, setEmail] = useState('');
    const [fullName, setFullName] = useState('');
    const [phone, setPhone] = useState('');
    const [roleId, setRoleId] = useState<number | ''>('');
    const [gmailError, setGmailError] = useState<string | null>(null);
    const [successMsg, setSuccessMsg] = useState<string | null>(null);

    const mutation = useRegisterEmployee();
    const { data: roles = [] } = useRoleGroups();

    // Chọn sẵn vai trò đầu tiên khi danh sách role tải xong (nếu chưa chọn).
    useEffect(() => {
        if (roleId === '' && roles.length > 0) setRoleId(roles[0].id);
    }, [roles, roleId]);

    const handleSubmit = (e: FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        setGmailError(null);
        setSuccessMsg(null);

        if (!email.trim().toLowerCase().endsWith('@gmail.com')) {
            setGmailError('Chỉ chấp nhận địa chỉ @gmail.com');
            return;
        }

        if (roleId === '') {
            setGmailError('Vui lòng chọn vai trò');
            return;
        }

        mutation.mutate(
            { email: email.trim(), fullName: fullName.trim(), phone: phone.trim() || undefined, roleId },
            {
                onSuccess: () => {
                    setSuccessMsg(`Đã gửi email kích hoạt tới ${email.trim()}`);
                    setEmail('');
                    setFullName('');
                    setPhone('');
                    setRoleId(roles.length > 0 ? roles[0].id : '');
                },
            }
        );
    };

    const apiError = mutation.error
        ? (mutation.error as { response?: { data?: { message?: string } } })
              ?.response?.data?.message ?? 'Đã xảy ra lỗi, vui lòng thử lại.'
        : null;

    const errorMessage = gmailError ?? apiError;

    return (
        <div className="p-6 bg-bg-main min-h-[calc(100vh-50px)]">
            {/* Header */}
            <div className="flex items-center gap-3 mb-6">
                <div className="flex items-center justify-center w-9 h-9 rounded-card bg-primary">
                    <FiUserPlus size={18} className="text-white" />
                </div>
                <div>
                    <h1 className="text-lg font-semibold text-text-main">Đăng ký tài khoản nhân viên</h1>
                    <p className="text-sm text-gray-500">Hệ thống sẽ gửi email kích hoạt tới nhân viên trong 24 giờ</p>
                </div>
            </div>

            <div className="bg-white rounded-card shadow-sm p-6 max-w-xl">
                <form onSubmit={handleSubmit} noValidate className="space-y-5">

                    {/* Email */}
                    <div>
                        <label className="block text-md font-medium text-text-main mb-1">
                            Email <span className="text-danger">*</span>
                        </label>
                        <div className="relative">
                            <span className="absolute inset-y-0 left-3 flex items-center text-gray-400 pointer-events-none">
                                <FiMail size={16} />
                            </span>
                            <input
                                type="email"
                                value={email}
                                onChange={(e) => { setEmail(e.target.value); setGmailError(null); }}
                                placeholder="nhanvien@gmail.com"
                                required
                                className={inputCls}
                            />
                        </div>
                        <p className="text-xs text-gray-400 mt-1">Chỉ chấp nhận địa chỉ @gmail.com</p>
                    </div>

                    {/* Họ tên */}
                    <div>
                        <label className="block text-md font-medium text-text-main mb-1">
                            Họ và tên <span className="text-danger">*</span>
                        </label>
                        <div className="relative">
                            <span className="absolute inset-y-0 left-3 flex items-center text-gray-400 pointer-events-none">
                                <FiUser size={16} />
                            </span>
                            <input
                                type="text"
                                value={fullName}
                                onChange={(e) => setFullName(e.target.value)}
                                placeholder="Nguyễn Văn A"
                                required
                                maxLength={30}
                                className={inputCls}
                            />
                        </div>
                    </div>

                    {/* Số điện thoại */}
                    <div>
                        <label className="block text-md font-medium text-text-main mb-1">
                            Số điện thoại
                        </label>
                        <div className="relative">
                            <span className="absolute inset-y-0 left-3 flex items-center text-gray-400 pointer-events-none">
                                <FiPhone size={16} />
                            </span>
                            <input
                                type="tel"
                                value={phone}
                                onChange={(e) => setPhone(e.target.value)}
                                placeholder="0901234567"
                                maxLength={11}
                                className={inputCls}
                            />
                        </div>
                    </div>

                    {/* Vai trò */}
                    <div>
                        <label className="block text-md font-medium text-text-main mb-1">
                            Vai trò <span className="text-danger">*</span>
                        </label>
                        <div className="relative">
                            <span className="absolute inset-y-0 left-3 flex items-center text-gray-400 pointer-events-none">
                                <FiShield size={16} />
                            </span>
                            <select
                                value={roleId}
                                onChange={(e) => setRoleId(Number(e.target.value))}
                                className="w-full pl-9 pr-3 py-2 border border-gray-300 rounded-btn text-md text-text-main focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary transition-colors bg-white"
                            >
                                {roles.length === 0 && <option value="">Đang tải vai trò…</option>}
                                {roles.map((r) => (
                                    <option key={r.id} value={r.id}>{r.name}</option>
                                ))}
                            </select>
                        </div>
                    </div>

                    {/* Thông báo lỗi */}
                    {errorMessage && (
                        <p className="text-sm text-danger bg-red-50 border border-red-200 rounded-btn px-3 py-2">
                            {errorMessage}
                        </p>
                    )}

                    {/* Thông báo thành công */}
                    {successMsg && (
                        <p className="text-sm text-green-700 bg-green-50 border border-green-200 rounded-btn px-3 py-2">
                            {successMsg}
                        </p>
                    )}

                    {/* Nút Đăng ký */}
                    <button
                        type="submit"
                        disabled={mutation.isPending || !email.trim() || !fullName.trim()}
                        className="w-full bg-primary text-white py-2 rounded-btn text-md font-medium hover:opacity-90 disabled:opacity-60 disabled:cursor-not-allowed transition-colors flex items-center justify-center gap-2"
                    >
                        {mutation.isPending && (
                            <span className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />
                        )}
                        <FiLayers size={16} />
                        Đăng ký và gửi email kích hoạt
                    </button>
                </form>
            </div>
        </div>
    );
};

export default RegisterEmployeePage;
