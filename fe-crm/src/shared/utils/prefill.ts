/**
 * Tự điền form thêm mới từ một bản ghi đã chọn (khách hàng, cơ hội…).
 *
 * Quy tắc: **chỉ điền ô đang trống**. Người dùng đã gõ gì thì giữ nguyên, kể cả khi
 * họ đổi lựa chọn ở ô nguồn — tự điền là tiện ích, không được cướp dữ liệu đang nhập.
 */

/** Ô được coi là trống khi chưa có giá trị (form state dùng chuỗi rỗng cho ô chưa nhập). */
const isEmpty = (v: unknown): boolean => v === '' || v === null || v === undefined;

/**
 * Lọc patch xuống còn những khóa thực sự điền được.
 *
 * @param current State form hiện tại
 * @param patch   Giá trị muốn điền, lấy từ bản ghi nguồn
 * @returns Patch đã lọc — rỗng nghĩa là không có gì để điền
 *
 * @example
 * set(fillEmpty(form, { contactId: '12', taxCode: kh.taxCode ?? '' }));
 */
export function fillEmpty<T extends object>(current: T, patch: Partial<T>): Partial<T> {
    const out: Partial<T> = {};
    (Object.keys(patch) as (keyof T)[]).forEach((k) => {
        if (!isEmpty(patch[k]) && isEmpty(current[k])) out[k] = patch[k];
    });
    return out;
}

/** true nếu patch có ít nhất một ô được điền — dùng để quyết định hiện dòng gợi ý. */
export const hasFilled = (patch: object): boolean => Object.keys(patch).length > 0;

/**
 * Liên hệ đại diện của một khách hàng: ưu tiên liên hệ chính, không có thì lấy liên hệ đầu tiên.
 * Nhận kiểu generic để `shared/` không phụ thuộc ngược vào type của feature.
 *
 * @param contacts   Danh sách liên hệ đã nạp sẵn ở trang (hook `useContactList`, size 500)
 * @param customerId ID khách hàng vừa chọn
 */
export function primaryContactOf<T extends { customerId: number | null; isPrimary?: boolean }>(
    contacts: T[],
    customerId: number,
): T | undefined {
    const own = contacts.filter((c) => c.customerId === customerId);
    return own.find((c) => c.isPrimary) ?? own[0];
}
