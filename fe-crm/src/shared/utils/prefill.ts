// tự điền form thêm mới từ một bản ghi đã chọn (khách hàng, cơ hội...)
// quy tắc: CHỈ điền ô đang trống — người dùng đã gõ gì thì giữ nguyên, kể cả khi họ đổi lựa chọn
// ở ô nguồn, vì tự điền là tiện ích, không được cướp dữ liệu đang nhập

// ô được coi là trống khi chưa có giá trị (form state dùng chuỗi rỗng cho ô chưa nhập)
const isEmpty = (v: unknown): boolean => v === '' || v === null || v === undefined;

// lọc patch xuống còn những khóa thực sự điền được (rỗng nghĩa là không có gì để điền)
export function fillEmpty<T extends object>(current: T, patch: Partial<T>): Partial<T> {
    const out: Partial<T> = {};
    (Object.keys(patch) as (keyof T)[]).forEach((k) => {
        if (!isEmpty(patch[k]) && isEmpty(current[k])) out[k] = patch[k];
    });
    return out;
}

// true nếu patch có ít nhất một ô được điền — dùng để quyết định hiện dòng gợi ý
export const hasFilled = (patch: object): boolean => Object.keys(patch).length > 0;

// liên hệ đại diện của một khách hàng: ưu tiên liên hệ chính, không có thì lấy liên hệ đầu tiên
// nhận kiểu generic để shared/ không phụ thuộc ngược vào type của feature
export function primaryContactOf<T extends { customerId: number | null; isPrimary?: boolean }>(
    contacts: T[],
    customerId: number,
): T | undefined {
    const own = contacts.filter((c) => c.customerId === customerId);
    return own.find((c) => c.isPrimary) ?? own[0];
}
