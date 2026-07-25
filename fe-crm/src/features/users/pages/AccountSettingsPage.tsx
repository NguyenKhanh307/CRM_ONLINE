import { useEffect, useRef, useState, type FormEvent } from 'react';
import { FiUser, FiUpload } from 'react-icons/fi';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '@/core/auth/useAuth';
import { useAlert } from '@/shared/alert/useAlert';
import { useConfirm } from '@/shared/confirm/useConfirm';
import { CenteredFormCard } from '@/shared/components/form/CenteredFormCard';
import { FormField } from '@/shared/components/form/FormField';
import { useFormKeyboardNav } from '@/shared/keyboard/useFormKeyboardNav';
import { ActionButton } from '@/shared/components/ActionButton';
import { SHORTCUTS } from '@/shared/keyboard/shortcuts';
import { collectErrors, phoneError } from '@/shared/utils/validators';
import { uploadImage } from '@/shared/utils/cloudinary';
import { UserAvatar } from '@/shared/components/UserAvatar';
import { useMyProfile } from '../hooks/useMyProfile';
import { useUpdateProfile } from '../hooks/useUpdateProfile';

const inputCls =
    'w-full px-3 py-2 border border-gray-300 rounded-btn text-md text-text-main placeholder-gray-400 focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary transition-colors';

/**
 * Trang thiết lập tài khoản — người dùng tự sửa họ tên, số điện thoại và ảnh đại diện.
 * Email hiển thị chỉ-đọc (là định danh đăng nhập).
 */
const AccountSettingsPage = () => {
    const navigate = useNavigate();
    const { user, updateUser } = useAuth();
    const { showAlert } = useAlert();
    const { confirmSave } = useConfirm();

    const { data: profile, isLoading } = useMyProfile();
    const { mutate, isPending } = useUpdateProfile();

    const [fullName, setFullName] = useState('');
    const [phone, setPhone] = useState('');
    const [avatarUrl, setAvatarUrl] = useState('');
    const [uploading, setUploading] = useState(false);
    const [errors, setErrors] = useState<Record<string, string>>({});
    const fileInputRef = useRef<HTMLInputElement>(null);

    const formRef = useRef<HTMLFormElement>(null);
    // Không truyền onCancel: Esc không được xóa dữ liệu đang nhập.
    // enabled: !isLoading vì form chỉ render sau khi hồ sơ tải xong.
    useFormKeyboardNav(formRef, {
        onSubmit: () => formRef.current?.requestSubmit(),
        enabled: !isLoading,
    });

    const clearError = (key: string) =>
        setErrors((prev) => (prev[key] ? { ...prev, [key]: '' } : prev));

    // Nạp dữ liệu form khi hồ sơ tải xong.
    useEffect(() => {
        if (!profile) return;
        setFullName(profile.fullName ?? '');
        setPhone(profile.phone ?? '');
        setAvatarUrl(profile.avatarUrl ?? '');
    }, [profile]);

    const handlePickFile = () => fileInputRef.current?.click();

    const handleFileChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
        const file = e.target.files?.[0];
        e.target.value = ''; // cho phép chọn lại cùng file
        if (!file) return;
        setUploading(true);
        try {
            const url = await uploadImage(file);
            setAvatarUrl(url);
        } catch (err) {
            showAlert(err instanceof Error ? err.message : 'Tải ảnh lên thất bại.');
        } finally {
            setUploading(false);
        }
    };

    const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
        e.preventDefault();

        const found = collectErrors({
            fullName: !fullName.trim() ? 'Vui lòng nhập họ và tên' : null,
            phone: phoneError(phone),
        });
        setErrors(found);
        if (Object.keys(found).length > 0) return;

        if (!(await confirmSave('thông tin tài khoản'))) return;
        mutate(
            {
                fullName: fullName.trim(),
                phone: phone.trim() || undefined,
                avatarUrl: avatarUrl.trim() || undefined,
            },
            {
                onSuccess: (updated) => {
                    updateUser({ fullName: updated.fullName, avatarUrl: updated.avatarUrl });
                    showAlert('Đã cập nhật thông tin tài khoản.');
                },
            },
        );
    };

    return (
        <CenteredFormCard
            icon={FiUser}
            title="Thiết lập tài khoản"
            subtitle="Chỉnh sửa thông tin hồ sơ cá nhân của bạn"
            onBack={() => navigate(-1)}
        >
            {isLoading ? (
                <p className="text-gray-400">Đang tải hồ sơ…</p>
            ) : (
                <form ref={formRef} onSubmit={handleSubmit} noValidate className="space-y-5">
                        {/* Ảnh đại diện */}
                        <div className="flex items-center gap-4">
                            <UserAvatar fullName={fullName || user?.fullName} avatarUrl={avatarUrl} size={80} />
                            <div>
                                <button
                                    type="button"
                                    onClick={handlePickFile}
                                    disabled={uploading}
                                    className="inline-flex items-center gap-2 border border-gray-300 text-text-main py-1.5 px-3 rounded-btn text-md font-medium hover:bg-gray-50 disabled:opacity-60 transition-colors"
                                >
                                    {uploading ? (
                                        <span className="w-4 h-4 border-2 border-gray-400 border-t-transparent rounded-full animate-spin" />
                                    ) : (
                                        <FiUpload size={15} />
                                    )}
                                    {uploading ? 'Đang tải…' : 'Tải ảnh lên'}
                                </button>
                                <p className="text-xs text-gray-400 mt-1">Ảnh JPG/PNG, tối đa 5MB.</p>
                            </div>
                            <input
                                ref={fileInputRef}
                                type="file"
                                accept="image/*"
                                className="hidden"
                                onChange={handleFileChange}
                            />
                        </div>

                        {/* Email — chỉ đọc */}
                        <FormField label="Email" hint="Email đăng nhập không thể thay đổi.">
                            <input
                                type="email"
                                value={profile?.email ?? ''}
                                readOnly
                                tabIndex={-1}
                                className={`${inputCls} bg-gray-50 cursor-default`}
                            />
                        </FormField>

                        <FormField label="Họ và tên" required error={errors.fullName}>
                            <input
                                type="text"
                                value={fullName}
                                onChange={(e) => { setFullName(e.target.value); clearError('fullName'); }}
                                placeholder="Nguyễn Văn A"
                                maxLength={30}
                                className={inputCls}
                            />
                        </FormField>

                        <FormField label="Số điện thoại" error={errors.phone}>
                            <input
                                type="tel"
                                value={phone}
                                onChange={(e) => { setPhone(e.target.value); clearError('phone'); }}
                                placeholder="0901234567"
                                maxLength={11}
                                className={inputCls}
                            />
                        </FormField>

                        {/* Nút lưu */}
                        <div className="flex justify-end gap-1.5 pt-2">
                            <ActionButton variant="secondary" onClick={() => navigate(-1)}>
                                Hủy
                            </ActionButton>
                            <ActionButton
                                variant="primary"
                                type="submit"
                                disabled={isPending || uploading}
                                shortcut={SHORTCUTS.SAVE.keys}
                            >
                                {isPending ? 'Đang lưu…' : 'Lưu thay đổi'}
                            </ActionButton>
                        </div>
                    </form>
                )}
        </CenteredFormCard>
    );
};

export default AccountSettingsPage;
