import * as XLSX from 'xlsx';
import type { ExportColumn, ExportFormat } from './exportTypes';

/** Trả về hậu tố ngày dạng yyyymmdd cho tên file. */
function dateSuffix(): string {
    const d = new Date();
    const mm = String(d.getMonth() + 1).padStart(2, '0');
    const dd = String(d.getDate()).padStart(2, '0');
    return `${d.getFullYear()}${mm}${dd}`;
}

/** Lấy giá trị thuần của một ô từ cột (ưu tiên `format`, fallback `row[key]`). */
function cellValue<T>(col: ExportColumn<T>, row: T): string | number {
    const raw = col.format ? col.format(row) : (row as Record<string, unknown>)[col.key];
    if (raw == null) return '';
    return typeof raw === 'number' ? raw : String(raw);
}

/** Tải nội dung text (CSV) xuống dưới dạng file qua Blob + thẻ <a>. */
function downloadText(content: string, fileName: string): void {
    const blob = new Blob([content], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = fileName;
    a.click();
    URL.revokeObjectURL(url);
}

/**
 * Xuất danh sách bản ghi ra file Excel/CSV theo các cột đã chọn.
 *
 * @param rows         Các bản ghi cần xuất.
 * @param columns      Toàn bộ cột khả dụng của phân hệ.
 * @param selectedKeys Khóa các cột người dùng chọn xuất (giữ đúng thứ tự `columns`).
 * @param format       'xlsx' hoặc 'csv'.
 * @param fileName     Tên file (không kèm phần mở rộng); hậu tố ngày được tự thêm.
 */
export function exportRows<T>(
    rows: T[],
    columns: ExportColumn<T>[],
    selectedKeys: string[],
    format: ExportFormat,
    fileName: string,
): void {
    const cols = columns.filter(c => selectedKeys.includes(c.key));
    const headers = cols.map(c => c.label);
    const aoa: (string | number)[][] = [headers, ...rows.map(row => cols.map(c => cellValue(c, row)))];

    const ws = XLSX.utils.aoa_to_sheet(aoa);
    const name = `${fileName}_${dateSuffix()}`;

    if (format === 'csv') {
        // Prepend BOM để Excel mở CSV UTF-8 (tiếng Việt) không bị lỗi font.
        const csv = '﻿' + XLSX.utils.sheet_to_csv(ws);
        downloadText(csv, `${name}.csv`);
        return;
    }

    const wb = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, ws, 'Sheet1');
    XLSX.writeFile(wb, `${name}.xlsx`);
}
