// tải ảnh lên Cloudinary bằng unsigned upload preset — không lộ secret, không cần backend
// cấu hình qua 2 biến env: VITE_CLOUDINARY_CLOUD_NAME, VITE_CLOUDINARY_UPLOAD_PRESET

const CLOUD_NAME = import.meta.env.VITE_CLOUDINARY_CLOUD_NAME as string | undefined;
const UPLOAD_PRESET = import.meta.env.VITE_CLOUDINARY_UPLOAD_PRESET as string | undefined;

// giới hạn kích thước ảnh tải lên (5MB)
export const MAX_AVATAR_SIZE = 5 * 1024 * 1024;

// upload một file ảnh lên Cloudinary và trả về secure_url
// dùng fetch thô (KHÔNG dùng axiosInstance) vì axiosInstance prepend baseURL + Bearer token
// của backend nội bộ — sẽ hỏng khi gọi thẳng API Cloudinary
export const uploadImage = async (file: File): Promise<string> => {
    if (!CLOUD_NAME || !UPLOAD_PRESET) {
        throw new Error('Chưa cấu hình Cloudinary (VITE_CLOUDINARY_CLOUD_NAME / VITE_CLOUDINARY_UPLOAD_PRESET).');
    }
    if (!file.type.startsWith('image/')) {
        throw new Error('Vui lòng chọn tệp ảnh.');
    }
    if (file.size > MAX_AVATAR_SIZE) {
        throw new Error('Ảnh vượt quá 5MB, vui lòng chọn ảnh nhỏ hơn.');
    }

    const formData = new FormData();
    formData.append('file', file);
    formData.append('upload_preset', UPLOAD_PRESET);

    const res = await fetch(`https://api.cloudinary.com/v1_1/${CLOUD_NAME}/image/upload`, {
        method: 'POST',
        body: formData,
    });

    if (!res.ok) {
        throw new Error('Tải ảnh lên Cloudinary thất bại. Vui lòng thử lại.');
    }

    const data = (await res.json()) as { secure_url?: string };
    if (!data.secure_url) {
        throw new Error('Cloudinary không trả về đường dẫn ảnh.');
    }
    return data.secure_url;
};
