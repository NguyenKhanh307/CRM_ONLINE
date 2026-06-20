/** Định dạng file xuất ra: Excel (.xlsx) hoặc CSV (.csv). */
export type ExportFormat = 'xlsx' | 'csv';

/**
 * Mô tả một cột có thể xuất ra file của một phân hệ.
 * Tương tự `ImportField` nhưng dùng cho chiều xuất dữ liệu.
 */
export interface ExportColumn<T> {
    /** Định danh cột (duy nhất trong cùng một phân hệ). */
    key: string;
    /** Tiêu đề hiển thị trong modal và làm header trong file xuất ra. */
    label: string;
    /**
     * Hàm lấy giá trị thuần để ghi vào ô.
     * Mặc định (nếu không truyền) lấy `row[key]`.
     */
    format?: (row: T) => string | number | null | undefined;
}
