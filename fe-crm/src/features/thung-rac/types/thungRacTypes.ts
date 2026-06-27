export type TrashModule =
    | 'tiem-nang'
    | 'lien-he'
    | 'khach-hang'
    | 'co-hoi'
    | 'bao-gia'
    | 'hoa-don'
    | 'san-pham';

export interface DeletedItemRow {
    id: number;
    displayName: string;
    deletedAt: string;
    deletedByName: string | null;
}

export interface TrashModuleLabel {
    id: TrashModule;
    label: string;
}

export const TRASH_MODULE_LABELS: TrashModuleLabel[] = [
    { id: 'tiem-nang',  label: 'Tiềm năng' },
    { id: 'lien-he',    label: 'Liên hệ' },
    { id: 'khach-hang', label: 'Khách hàng' },
    { id: 'co-hoi',     label: 'Cơ hội' },
    { id: 'bao-gia',    label: 'Báo giá' },
    { id: 'hoa-don',    label: 'Hóa đơn' },
    { id: 'san-pham',   label: 'Sản phẩm' },
];
