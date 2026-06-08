import type { FilterCondition, FilterOperator } from '@/shared/types/table';

/**
 * Kiểm tra một giá trị cell có thoả điều kiện operator không.
 */
export function checkCondition(
    cellValue: string,
    operator: FilterOperator,
    value: string
): boolean {
    const cell = cellValue.toLowerCase();
    const val = value.toLowerCase();
    switch (operator) {
        case 'is':               return cell === val;
        case 'is_not':           return cell !== val;
        case 'contains':         return cell.includes(val);
        case 'does_not_contain': return !cell.includes(val);
        case 'is_empty':         return cell.trim() === '';
        case 'is_not_empty':     return cell.trim() !== '';
        default:                 return true;
    }
}

/**
 * Áp dụng danh sách FilterCondition lên data, trả về mảng đã lọc (AND logic).
 * Chỉ áp dụng các condition có fieldId hợp lệ.
 */
export function applyConditions<T extends Record<string, unknown>>(
    data: T[],
    conditions: FilterCondition[]
): T[] {
    const active = conditions.filter(
        (c) => c.fieldId !== '' && (c.operator === 'is_empty' || c.operator === 'is_not_empty' || c.value !== '')
    );
    if (active.length === 0) return data;

    return data.filter((row) =>
        active.every((cond) => {
            const raw = row[cond.fieldId];
            const cellValue = raw == null ? '' : String(raw);
            return checkCondition(cellValue, cond.operator, cond.value);
        })
    );
}
