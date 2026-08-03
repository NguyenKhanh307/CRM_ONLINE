// giá trị rỗng được coi là hợp lệ ở mọi hàm kiểm tra khác — dùng kèm requiredError riêng
export const isEmpty = (v: string | number | null | undefined): boolean =>
    v === null || v === undefined || String(v).trim() === '';

// gom các lỗi thành map field->message, bỏ các mục null
export const collectErrors = (
    entries: Record<string, string | null>,
): Record<string, string> =>
    Object.fromEntries(Object.entries(entries).filter(([, v]) => v !== null)) as Record<string, string>;
