// mô tả một cột có thể xuất ra file của một phân hệ
export interface ExportColumn<T> {
    // định danh cột (duy nhất trong cùng một phân hệ)
    key: string;
    // tiêu đề hiển thị trong modal và làm header trong file xuất ra
    label: string;
    // hàm lấy giá trị thuần để ghi vào ô; mặc định (nếu không truyền) lấy `row[key]`
    format?: (row: T) => string | number | null | undefined;
}
