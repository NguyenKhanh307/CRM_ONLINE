import type { CellContext } from '@tanstack/react-table';
import { formatISODate } from '@/shared/utils/date';
import { lookupName } from '@/shared/utils/lookup';

/**
 * Cell render dùng chung cho DataTable — giảm lặp code ở các config cột.
 * Tất cả đều generic theo kiểu row `T` để gán trực tiếp vào `ColumnDef<T>['cell']`.
 */

/** Số tiền: "1.234.567 đ", null → "—". */
export function currencyCell<T>({ getValue }: CellContext<T, unknown>) {
    const v = getValue() as number | null;
    return v != null ? v.toLocaleString('vi-VN') + ' đ' : '—';
}

/** Số thường (vd số lượng, %): toLocaleString, null → "—". */
export function numberCell<T>({ getValue }: CellContext<T, unknown>) {
    const v = getValue() as number | null;
    return v != null ? v.toLocaleString('vi-VN') : '—';
}

/** Ngày ISO → dd/mm/yyyy, rỗng → "—". */
export function dateCell<T>({ getValue }: CellContext<T, unknown>) {
    const v = getValue() as string | null;
    return v ? formatISODate(v) : '—';
}

/** Text thường, null/rỗng → "—". */
export function textCell<T>({ getValue }: CellContext<T, unknown>) {
    const v = getValue() as string | null;
    return v != null && v !== '' ? v : '—';
}

/** Badge boolean xanh/đỏ với nhãn tùy biến. */
export function boolBadge(trueLabel: string, falseLabel: string) {
    return function BoolBadgeCell<T>({ getValue }: CellContext<T, unknown>) {
        const v = getValue() as boolean;
        return (
            <span className={`inline-block px-2 py-0.5 rounded text-sm font-medium ${v ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-600'}`}>
                {v ? trueLabel : falseLabel}
            </span>
        );
    };
}

/** Cờ Có/Không đơn giản (không badge). */
export function yesNoCell<T>({ getValue }: CellContext<T, unknown>) {
    return (getValue() as boolean) ? 'Có' : 'Không';
}

/** Map giá trị enum → nhãn tiếng Việt; không có nhãn thì hiện giá trị thô. */
export function labelCell(labels: Record<string, string>) {
    return function LabelCell<T>({ getValue }: CellContext<T, unknown>) {
        const v = getValue() as string | null;
        if (v == null || v === '') return '—';
        return labels[v] ?? v;
    };
}

/** Badge enum có màu + nhãn tiếng Việt. */
export function badgeCell(labels: Record<string, string>, colors: Record<string, string>) {
    return function BadgeCell<T>({ getValue }: CellContext<T, unknown>) {
        const v = getValue() as string | null;
        if (v == null || v === '') return '—';
        return (
            <span className={`inline-block px-2 py-0.5 rounded text-sm font-medium ${colors[v] ?? 'bg-gray-100 text-gray-600'}`}>
                {labels[v] ?? v}
            </span>
        );
    };
}

/** Resolve ID khóa ngoại sang tên qua map; null → "—", chưa load → "#id". */
export function fkCell(map: Map<number, string>) {
    return function FkCell<T>({ getValue }: CellContext<T, unknown>) {
        return lookupName(map, getValue() as number | null);
    };
}
