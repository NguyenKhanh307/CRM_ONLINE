/** Phân hệ chứa bản ghi nghi trùng. */
export type DuplicateModule = 'lead' | 'customer' | 'contact';

/** Một bản ghi bị nghi trùng — GET /api/duplicates/check. Chỉ để cảnh báo, không chặn lưu. */
export interface DuplicateMatch {
    module: DuplicateModule;
    id: number;
    /** Liên hệ không có mã → null. */
    code: string | null;
    name: string | null;
    /** Trường bị trùng: email | phone | taxCode. */
    matchedField: string;
    matchedValue: string | null;
}

/** Tham số dò trùng. */
export interface DuplicateCheckParams {
    email?: string | null;
    phone?: string | null;
    taxCode?: string | null;
    /** Bỏ chính bản ghi đang sửa ra khỏi kết quả. */
    excludeModule?: DuplicateModule;
    excludeId?: number;
}
