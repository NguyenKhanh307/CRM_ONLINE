// giá trị rỗng được coi là hợp lệ ở mọi hàm kiểm tra khác — dùng kèm requiredError riêng
export const isEmpty = (v: string | number | null | undefined): boolean =>
    v === null || v === undefined || String(v).trim() === '';

// gom các lỗi thành map field->message, bỏ các mục null
export const collectErrors = (
    entries: Record<string, string | null>,
): Record<string, string> =>
    Object.fromEntries(Object.entries(entries).filter(([, v]) => v !== null)) as Record<string, string>;

// kiểm tra map lỗi từ collectErrors() — còn lỗi thì hiện popup cảnh báo chung (bổ sung cho chữ đỏ
// dưới từng ô, phòng khi ô lỗi nằm ngoài màn hình) rồi trả về false để chặn submit
export const validateOrWarn = (
    errs: Record<string, string>,
    showAlert: (message: string) => void,
): boolean => {
    if (Object.keys(errs).length === 0) return true;
    showAlert('Vui lòng kiểm tra lại thông tin đã nhập trước khi lưu.');
    return false;
};
