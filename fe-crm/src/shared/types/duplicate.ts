// phân hệ chứa bản ghi nghi trùng
export type DuplicateModule = 'lead' | 'customer' | 'contact';

// một bản ghi bị nghi trùng — GET /api/duplicates/check, chỉ để cảnh báo, không chặn lưu
export interface DuplicateMatch {
    module: DuplicateModule;
    id: number;
    // liên hệ không có mã -> null
    code: string | null;
    name: string | null;
    // trường bị trùng: email | phone | taxCode
    matchedField: string;
    matchedValue: string | null;
}

// tham số dò trùng
export interface DuplicateCheckParams {
    email?: string | null;
    phone?: string | null;
    taxCode?: string | null;
    // bỏ chính bản ghi đang sửa ra khỏi kết quả
    excludeModule?: DuplicateModule;
    excludeId?: number;
}
