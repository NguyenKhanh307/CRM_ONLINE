import { useEffect, useRef, useState, type FormEvent } from 'react';
import { FiUser, FiUpload, FiArrowLeft } from 'react-icons/fi';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '@/core/auth/useAuth';
import { useAlert } from '@/shared/alert/useAlert';
import { useConfirm } from '@/shared/confirm/useConfirm';
import { phoneError } from '@/shared/utils/validators';
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
    const fileInputRef = useRef<HTMLInputElement>(null);

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
        if (!fullName.trim()) {
            showAlert('Họ và tên không được để trống.');
            return;
        }
        const vErr = phoneError(phone);
        if (vErr) {
            showAlert(vErr);
            return;
        }
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
        <div className="p-6 bg-bg-main min-h-[calc(100vh-50px)]">
            {/* Header */}
            <div className="flex items-center gap-3 mb-6">
                <button
                    onClick={() => navigate(-1)}
                    className="p-2 rounded hover:bg-gray-100 text-gray-500"
                    title="Quay lại"
                >
                    <FiArrowLeft size={18} />
                </button>
                <div className="flex items-center justify-center w-9 h-9 rounded-card bg-primary">
                    <FiUser size={18} className="text-white" />
                </div>
                <div>
                    <h1 className="text-lg font-semibold text-text-main">Thiết lập tài khoản</h1>
                    <p className="text-sm text-gray-500">Chỉnh sửa thông tin hồ sơ cá nhân của bạn</p>
                </div>
            </div>

            <div className="bg-white rounded-card shadow-sm p-6 max-w-xl">
                {isLoading ? (
                    <p className="text-gray-400">Đang tải hồ sơ…</p>
                ) : (
                    <form onSubmit={handleSubmit} noValidate className="space-y-5">
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
                        <div>
                            <label className="block text-md font-medium text-text-main mb-1">Email</label>
                            <input
                                type="email"
                                value={profile?.email ?? ''}
                                readOnly
                                tabIndex={-1}
                                className={`${inputCls} bg-gray-50 cursor-default`}
                            />
                            <p className="text-xs text-gray-400 mt-1">Email đăng nhập không thể thay đổi.</p>
                        </div>

                        {/* Họ tên */}
                        <div>
                            <label className="block text-md font-medium text-text-main mb-1">
                                Họ và tên <span className="text-danger">*</span>
                            </label>
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

                        {/* Số điện thoại */}
                        <div>
                            <label className="block text-md font-medium text-text-main mb-1">Số điện thoại</label>
                            <input
                                type="tel"
                                value={phone}
                                onChange={(e) => setPhone(e.target.value)}
                                placeholder="0901234567"
                                maxLength={11}
                                className={inputCls}
                            />
                        </div>

                        {/* Nút lưu */}
                        <div className="flex justify-end gap-2 pt-2">
                            <button
                                type="button"
                                onClick={() => navigate(-1)}
                                className="border border-gray-300 text-text-main py-2 px-4 rounded-btn text-md font-medium hover:bg-gray-50 transition-colors"
                            >
                                Hủy
                            </button>
                            <button
                                type="submit"
                                disabled={isPending || uploading}
                                className="bg-primary text-white py-2 px-5 rounded-btn text-md font-medium hover:opacity-90 disabled:opacity-60 disabled:cursor-not-allowed transition-colors flex items-center justify-center gap-2"
                            >
                                {isPending && (
                                    <span className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />
                                )}
                                Lưu thay đổi
                            </button>
                        </div>
                    </form>
                )}
            </div>
        </div>
    );
};

export default AccountSettingsPage;
