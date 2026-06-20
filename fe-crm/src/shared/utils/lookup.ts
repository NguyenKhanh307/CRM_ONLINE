/**
 * Tiện ích resolve ID khóa ngoại sang tên hiển thị trên bảng danh sách.
 *
 * Dùng chung cho mọi module: page gọi lookup hook (vd `useActiveUsers`,
 * `useCustomerList`), dựng `Map<id, name>` bằng `toIdNameMap`, rồi truyền vào
 * factory column config; cell render gọi `lookupName(map, row.fkId)`.
 */

/**
 * Dựng `Map<number, string>` từ danh sách entity để tra cứu nhanh ID → tên.
 * @param items  - Mảng entity (có thể undefined khi query chưa load)
 * @param idKey  - Tên field khóa chính (vd 'id')
 * @param nameKey- Tên field tên hiển thị (vd 'fullName', 'name', 'code')
 */
export function toIdNameMap<T>(
    items: T[] | undefined,
    idKey: keyof T,
    nameKey: keyof T
): Map<number, string> {
    const map = new Map<number, string>();
    if (!items) return map;
    for (const item of items) {
        const id = item[idKey];
        const name = item[nameKey];
        if (typeof id === 'number' && name != null) {
            map.set(id, String(name));
        }
    }
    return map;
}

/**
 * Trả tên hiển thị cho một ID khóa ngoại.
 * - `null`/`undefined` → '—'
 * - chưa có trong map (vd nằm ngoài trang đầu của lookup list) → '#<id>'
 */
export function lookupName(map: Map<number, string>, id: number | null | undefined): string {
    if (id == null) return '—';
    return map.get(id) ?? `#${id}`;
}
