// xlsx-js-style là fork tương thích API của SheetJS (xlsx) nhưng ghi được style ô (cell.s)
// khi xuất .xlsx — bản xlsx community gốc bỏ qua style lúc ghi. Chỉ đổi ở file này (nơi cần
// style tiêu đề); wizard nhập file vẫn dùng `xlsx` gốc, không cần style.
import * as XLSX from 'xlsx-js-style';
import type { ExportColumn } from './exportTypes';

// style tiêu đề khi xuất .xlsx: chữ đậm + nền xanh lá nhạt
const HEADER_STYLE = {
    font: { bold: true },
    fill: { fgColor: { rgb: 'E2EFDA' } },
    alignment: { vertical: 'center' },
} as const;

// trả về hậu tố ngày dạng yyyymmdd cho tên file
function dateSuffix(): string {
    const d = new Date();
    // padStart(2, '0') để tháng/ngày luôn 2 chữ số (01, 02, …, 10, 11, 12)
    const mm = String(d.getMonth() + 1).padStart(2, '0');
    const dd = String(d.getDate()).padStart(2, '0');
    return `${d.getFullYear()}${mm}${dd}`;
}

// lấy giá trị thuần của một ô từ cột (ưu tiên `format`, fallback `row[key]`)
function cellValue<T>(col: ExportColumn<T>, row: T): string | number {
    // nếu `format` trả về null/undefined, coi như ô rỗng -> ghi chuỗi rỗng
    const raw = col.format ? col.format(row) : (row as Record<string, unknown>)[col.key];
    if (raw == null) return '';
    // nếu là number thì giữ nguyên, còn lại convert sang string (để tránh ghi object/array)
    return typeof raw === 'number' ? raw : String(raw);
}

// xuất danh sách bản ghi ra file Excel theo các cột đã chọn
// selectedKeys giữ đúng thứ tự `columns`; fileName không kèm phần mở rộng, hậu tố ngày được tự thêm
export function exportRows<T>(
    rows: T[],
    columns: ExportColumn<T>[],
    selectedKeys: string[],
    fileName: string,
): void {
    const cols = columns.filter(c => selectedKeys.includes(c.key));
    const headers = cols.map(c => c.label);
    const aoa: (string | number)[][] = [headers, ...rows.map(row => cols.map(c => cellValue(c, row)))];

    const ws = XLSX.utils.aoa_to_sheet(aoa);
    const name = `${fileName}_${dateSuffix()}`;

    // tiêu đề in đậm + nền xanh lá nhạt
    cols.forEach((_, i) => {
        const cell = ws[XLSX.utils.encode_cell({ r: 0, c: i })];
        if (cell) cell.s = HEADER_STYLE;
    });
    // độ rộng cột theo độ dài tiêu đề (tối thiểu 10 ký tự) — dễ đọc hơn mặc định
    ws['!cols'] = cols.map((c) => ({ wch: Math.max(10, c.label.length + 2) }));

    const wb = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, ws, 'Sheet1');
    XLSX.writeFile(wb, `${name}.xlsx`);
}
