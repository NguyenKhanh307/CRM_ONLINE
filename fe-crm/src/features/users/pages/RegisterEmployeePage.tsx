import { useEffect, useRef, useState, type FormEvent } from 'react';
import { FiUserPlus, FiMail, FiUser, FiPhone, FiLayers, FiShield } from 'react-icons/fi';
import { CenteredFormCard } from '@/shared/components/form/CenteredFormCard';
import { FormField } from '@/shared/components/form/FormField';
import { useConfirm } from '@/shared/confirm/useConfirm';
import { useFormKeyboardNav } from '@/shared/keyboard/useFormKeyboardNav';
import { collectErrors, emailError, phoneError } from '@/shared/utils/validators';
import { useRegisterEmployee } from '../hooks/useRegisterEmployee';
import { useRoleGroups } from '@/features/phan-quyen/hooks/useRoleGroups';

const inputCls =
    'w-full pl-9 pr-3 py-2 border border-gray-300 rounded-btn text-md text-text-main placeholder-gray-400 focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary transition-colors';

// trang đăng ký tài khoản nhân viên mới — admin tạo tài khoản, hệ thống gửi email kích hoạt
// lỗi nhập liệu hiện đỏ dưới từng ô; tạo tài khoản là hành động ghi nên phải qua popup xác nhận
const RegisterEmployeePage = () => {
    const [email, setEmail] = useState('');
    const [fullName, setFullName] = useState('');
    const [phone, setPhone] = useState('');
    const [roleId, setRoleId] = useState<number | ''>('');
    const [errors, setErrors] = useState<Record<string, string>>({});
    const [successMsg, setSuccessMsg] = useState<string | null>(null);

    const mutation = useRegisterEmployee();
    const { data: roles = [] } = useRoleGroups();
    const { confirmCreate } = useConfirm();

    const formRef = useRef<HTMLFormElement>(null);
    useFormKeyboardNav(formRef, { onSubmit: () => formRef.current?.requestSubmit() });

    // Chọn sẵn vai trò đầu tiên khi danh sách role tải xong (nếu chưa chọn).
    useEffect(() => {
        if (roleId === '' && roles.length > 0) setRoleId(roles[0].id);
    }, [roles, roleId]);

    const clearError = (key: string) =>
        setErrors((prev) => (prev[key] ? { ...prev, [key]: '' } : prev));

    const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        setSuccessMsg(null);

        const found = collectErrors({
            email: !email.trim()
                ? 'Vui lòng nhập email'
                : emailError(email) ?? (email.trim().toLowerCase().endsWith('@gmail.com')
                    ? null
                    : 'Chỉ chấp nhận địa chỉ @gmail.com'),
            fullName: !fullName.trim() ? 'Vui lòng nhập họ và tên' : null,
            phone: phoneError(phone),
            roleId: roleId === '' ? 'Vui lòng chọn vai trò' : null,
        });
        setErrors(found);
        if (Object.keys(found).length > 0) return;

        if (!(await confirmCreate('tài khoản nhân viên'))) return;

        mutation.mutate(
            { email: email.trim(), fullName: fullName.trim(), phone: phone.trim() || undefined, roleId: roleId as number },
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

    return (
        <CenteredFormCard
            icon={FiUserPlus}
            title="Đăng ký tài khoản nhân viên"
            subtitle="Hệ thống sẽ gửi email kích hoạt tới nhân viên trong 24 giờ"
        >
            <form ref={formRef} onSubmit={handleSubmit} noValidate className="space-y-5">

                <FormField
                    label="Email"
                    required
                    icon={FiMail}
                    hint="Chỉ chấp nhận địa chỉ @gmail.com"
                    error={errors.email}
                >
                    <input
                        type="email"
                        value={email}
                        onChange={(e) => { setEmail(e.target.value); clearError('email'); }}
                        placeholder="nhanvien@gmail.com"
                        className={inputCls}
                    />
                </FormField>

                <FormField label="Họ và tên" required icon={FiUser} error={errors.fullName}>
                    <input
                        type="text"
                        value={fullName}
                        onChange={(e) => { setFullName(e.target.value); clearError('fullName'); }}
                        placeholder="Nguyễn Văn A"
                        maxLength={30}
                        className={inputCls}
                    />
                </FormField>

                <FormField label="Số điện thoại" icon={FiPhone} error={errors.phone}>
                    <input
                        type="tel"
                        value={phone}
                        onChange={(e) => { setPhone(e.target.value); clearError('phone'); }}
                        placeholder="0901234567"
                        maxLength={11}
                        className={inputCls}
                    />
                </FormField>

                <FormField label="Vai trò" required icon={FiShield} error={errors.roleId}>
                    <select
                        value={roleId}
                        onChange={(e) => { setRoleId(Number(e.target.value)); clearError('roleId'); }}
                        className="w-full pl-9 pr-3 py-2 border border-gray-300 rounded-btn text-md text-text-main focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary transition-colors bg-white"
                    >
                        {roles.length === 0 && <option value="">Đang tải vai trò…</option>}
                        {roles.map((r) => (
                            <option key={r.id} value={r.id}>{r.name}</option>
                        ))}
                    </select>
                </FormField>

                {/* Lỗi cấp form từ API */}
                {apiError && (
                    <p className="text-sm text-danger bg-red-50 border border-red-200 rounded-btn px-3 py-2">
                        {apiError}
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
                    disabled={mutation.isPending}
                    className="w-full bg-primary text-white py-2 rounded-btn text-md font-medium hover:opacity-90 disabled:opacity-60 disabled:cursor-not-allowed transition-colors flex items-center justify-center gap-2"
                >
                    {mutation.isPending && (
                        <span className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />
                    )}
                    <FiLayers size={16} />
                    Đăng ký và gửi email kích hoạt
                </button>
            </form>
        </CenteredFormCard>
    );
};

export default RegisterEmployeePage;
