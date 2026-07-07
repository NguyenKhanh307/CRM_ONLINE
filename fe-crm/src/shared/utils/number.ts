/**
 * Tiện ích format số dùng chung — chuẩn locale Việt Nam (vi-VN).
 * Dùng ở mọi nơi thay cho `.toLocaleString()` tự phát (tránh lệch locale theo trình duyệt).
 */

/**
 * Format số nguyên/thập phân theo locale vi-VN: 1234567 → "1.234.567".
 * @param n - Giá trị số (null/undefined → "0")
 */
export function formatNumber(n: number | null | undefined): string {
    if (n == null) return '0';
    return n.toLocaleString('vi-VN');
}

/**
 * Format số tiền kèm đơn vị " đ": 1234567 → "1.234.567 đ".
 * @param n - Giá trị số (null/undefined → "0 đ")
 */
export function formatCurrency(n: number | null | undefined): string {
    return `${formatNumber(n)} đ`;
}

/** Đơn vị hiển thị tiền tệ trên dashboard. */
export type MoneyUnit = 'vnd' | 'million';

/**
 * Format số tiền theo đơn vị hiển thị: VND ("1.234.567 đ") hoặc Triệu đồng ("1,23 tr").
 * @param n - Giá trị số (null/undefined → 0)
 * @param unit - 'vnd' (mặc định) hoặc 'million' (chia 1.000.000)
 */
export function formatMoney(n: number | null | undefined, unit: MoneyUnit = 'vnd'): string {
    const v = n ?? 0;
    if (unit === 'million') {
        return `${(v / 1_000_000).toLocaleString('vi-VN', { maximumFractionDigits: 2 })} tr`;
    }
    return `${v.toLocaleString('vi-VN')} đ`;
}
