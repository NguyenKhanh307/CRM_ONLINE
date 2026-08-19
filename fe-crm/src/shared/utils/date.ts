// chuyển chuỗi ISO yyyy-mm-dd (từ API/DB) thành dd/mm/yyyy để hiển thị, không đúng định dạng thì trả nguyên bản
export function formatISODate(iso: string): string {
    if (!iso) return '';
    const parts = iso.split('T')[0].split('-');   // bỏ phần giờ nếu có
    if (parts.length !== 3) return iso;
    const [yyyy, mm, dd] = parts;
    return `${dd}/${mm}/${yyyy}`;
}

// chuyển chuỗi dd/mm/yyyy (người dùng gõ) thành ISO yyyy-mm-dd để lưu/gửi API
// kiểm tra ngày có thật (loại 31/02, 32/01...), trả về null nếu không hợp lệ
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

// ghép chuỗi số thành mặt nạ dd/mm/yyyy khi người dùng gõ (tự chèn dấu '/')
export function maskDate(raw: string): string {
    // chỉ lấy chữ số, tối đa 8 ký tự (ddmmyyyy)
    const digits = raw.replace(/\D/g, '').slice(0, 8);
    const dd = digits.slice(0, 2);
    const mm = digits.slice(2, 4);
    const yyyy = digits.slice(4, 8);
    let out = dd;
    if (digits.length >= 3) out += '/' + mm;
    if (digits.length >= 5) out += '/' + yyyy;
    return out;
}
