// tiện ích resolve ID khóa ngoại sang tên hiển thị trên bảng danh sách
// dùng chung cho mọi module: page gọi lookup hook (vd useActiveUsers, useCustomerList), dựng
// Map<id, name> bằng toIdNameMap, rồi truyền vào factory column config; cell render gọi lookupName(map, row.fkId)

// dựng Map<number, string> từ danh sách entity để tra cứu nhanh id -> tên
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

// trả tên hiển thị cho một id khóa ngoại — null/undefined hoặc chưa có trong map đều trả '—'
// (không lộ mã "#id"; tên chuẩn nay do BE resolve sẵn qua INameResolver)
export function lookupName(map: Map<number, string>, id: number | null | undefined): string {
    if (id == null) return '—';
    return map.get(id) ?? '—';
}
