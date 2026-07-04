/**
 * Chuyển chuỗi ISO yyyy-mm-dd (từ API/DB) thành dd/mm/yyyy để hiển thị.
 * Nếu chuỗi không đúng định dạng, trả về nguyên bản.
 * @param iso - Chuỗi ISO, vd "2024-01-05"
 */
export function formatISODate(iso: string): string {
    if (!iso) return '';
    const parts = iso.split('T')[0].split('-');   // bỏ phần giờ nếu có
    if (parts.length !== 3) return iso;
    const [yyyy, mm, dd] = parts;
    return `${dd}/${mm}/${yyyy}`;
}

/**
 * Chuyển chuỗi dd/mm/yyyy (người dùng gõ) thành ISO yyyy-mm-dd để lưu/gửi API.
 * Kiểm tra ngày có thật (loại 31/02, 32/01…). Trả về null nếu không hợp lệ.
 * @param str - Chuỗi dd/mm/yyyy, vd "05/01/2024"
 */
export function parseVNDate(str: string): string | null {
    if (!str) return null;
    const m = str.trim().match(/^(\d{1,2})\/(\d{1,2})\/(\d{4})$/);
    if (!m) return null;
    const dd = Number(m[1]);
    const mm = Number(m[2]);
    const yyyy = Number(m[3]);
    if (mm < 1 || mm > 12 || dd < 1 || dd > 31) return null;
    const d = new Date(yyyy, mm - 1, dd);
    // Loại ngày không có thật (vd 31/02 bị Date tự cuộn sang tháng khác)
    if (d.getFullYear() !== yyyy || d.getMonth() !== mm - 1 || d.getDate() !== dd) return null;
    const pad = (n: number) => String(n).padStart(2, '0');
    return `${yyyy}-${pad(mm)}-${pad(dd)}`;
}
