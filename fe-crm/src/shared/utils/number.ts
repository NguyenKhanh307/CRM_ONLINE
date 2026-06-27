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
