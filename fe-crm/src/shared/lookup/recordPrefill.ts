import { contactService } from '@/features/lien-he/services/contactService';

/**
 * ID liên hệ đại diện của một khách hàng (ưu tiên liên hệ chính) — **hỏi thẳng server**.
 *
 * Trước đây tìm trong danh sách liên hệ nạp sẵn ở trang, nhưng bảng liên hệ có hàng chục nghìn dòng
 * nên trang chỉ nạp được một phần → khách hàng vừa chọn thường không có liên hệ nào trong đó và
 * ô Liên hệ lặng lẽ không được điền. Nay lọc theo `customerId` (tham số mới của `GET /api/contacts`).
 *
 * @param customerId ID khách hàng vừa chọn
 * @returns ID liên hệ dạng chuỗi, hoặc '' nếu khách chưa có liên hệ nào
 */
export async function fetchPrimaryContactId(customerId: number): Promise<string> {
    try {
        const items = (await contactService.getList({ customerId, page: 0, size: 20 })).data.data.items;
        const contact = items.find((c) => c.isPrimary) ?? items[0];
        return contact ? String(contact.id) : '';
    } catch {
        return '';   // lỗi mạng → bỏ qua phần tự điền, người dùng tự chọn
    }
}
