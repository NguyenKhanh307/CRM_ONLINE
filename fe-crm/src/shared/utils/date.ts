/** Regex khớp đúng định dạng dd/mm/yyyy — không chấp nhận ký tự thừa. */
const DATE_REGEX = /^\d{2}\/\d{2}\/\d{4}$/;

/** Năm hợp lệ trong ngữ cảnh CRM (không chấp nhận năm 0 hay năm quá xa). */
const YEAR_MIN = 1900;
const YEAR_MAX = 2100;

// ---------------------------------------------------------------------------
// Helpers nội bộ
// ---------------------------------------------------------------------------

function isLeapYear(year: number): boolean {
    return (year % 4 === 0 && year % 100 !== 0) || year % 400 === 0;
}

function getDaysInMonth(month: number, year: number): number {
    const days = [31, isLeapYear(year) ? 29 : 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];
    return days[month - 1];
}

// ---------------------------------------------------------------------------
// API công khai
// ---------------------------------------------------------------------------

/**
 * Kiểm tra nhanh xem chuỗi có phải ngày hợp lệ định dạng dd/mm/yyyy không.
 * @param value - Chuỗi cần kiểm tra, vd "31/12/2025"
 */
export function isValidDate(value: string): boolean {
    if (!DATE_REGEX.test(value)) return false;
    const [dd, mm, yyyy] = value.split('/').map(Number);
    if (yyyy < YEAR_MIN || yyyy > YEAR_MAX) return false;
    if (mm < 1 || mm > 12) return false;
    if (dd < 1 || dd > getDaysInMonth(mm, yyyy)) return false;
    return true;
}

/**
 * Trả về thông báo lỗi (string) nếu không hợp lệ, null nếu hợp lệ.
 * Dùng cho form validation để hiển thị lỗi chi tiết.
 * @param value - Chuỗi cần kiểm tra
 * @param fieldLabel - Tên trường hiển thị trong thông báo lỗi (mặc định "Ngày")
 */
export function validateDate(value: string, fieldLabel = 'Ngày'): string | null {
    if (!value || value.trim() === '') return `${fieldLabel} không được để trống`;
    if (!DATE_REGEX.test(value)) return `${fieldLabel} phải đúng định dạng dd/mm/yyyy`;

    const [dd, mm, yyyy] = value.split('/').map(Number);

    if (yyyy < YEAR_MIN || yyyy > YEAR_MAX) {
        return `${fieldLabel}: năm phải từ ${YEAR_MIN} đến ${YEAR_MAX}`;
    }
    if (mm < 1 || mm > 12) {
        return `${fieldLabel}: tháng phải từ 01 đến 12`;
    }
    const maxDay = getDaysInMonth(mm, yyyy);
    if (dd < 1 || dd > maxDay) {
        return `${fieldLabel}: ngày phải từ 01 đến ${String(maxDay).padStart(2, '0')} trong tháng ${String(mm).padStart(2, '0')}/${yyyy}`;
    }
    return null;
}

/**
 * Chuyển chuỗi dd/mm/yyyy thành Date object.
 * Trả về null nếu chuỗi không hợp lệ.
 */
export function parseDate(value: string): Date | null {
    if (!isValidDate(value)) return null;
    const [dd, mm, yyyy] = value.split('/').map(Number);
    return new Date(yyyy, mm - 1, dd);
}

/**
 * Chuyển Date object thành chuỗi dd/mm/yyyy.
 */
export function formatDate(date: Date): string {
    const dd = String(date.getDate()).padStart(2, '0');
    const mm = String(date.getMonth() + 1).padStart(2, '0');
    const yyyy = date.getFullYear();
    return `${dd}/${mm}/${yyyy}`;
}

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
 * Kiểm tra khoảng ngày hợp lệ: from <= to, cả hai phải hợp lệ.
 * @param from - Ngày bắt đầu dd/mm/yyyy
 * @param to   - Ngày kết thúc dd/mm/yyyy
 */
export function isValidDateRange(from: string, to: string): boolean {
    const fromDate = parseDate(from);
    const toDate = parseDate(to);
    if (!fromDate || !toDate) return false;
    return fromDate <= toDate;
}

/**
 * Trả về thông báo lỗi cho khoảng ngày, null nếu hợp lệ.
 */
export function validateDateRange(
    from: string,
    to: string,
    fromLabel = 'Từ ngày',
    toLabel = 'Đến ngày'
): string | null {
    const fromError = validateDate(from, fromLabel);
    if (fromError) return fromError;
    const toError = validateDate(to, toLabel);
    if (toError) return toError;
    if (!isValidDateRange(from, to)) {
        return `${fromLabel} phải nhỏ hơn hoặc bằng ${toLabel}`;
    }
    return null;
}
