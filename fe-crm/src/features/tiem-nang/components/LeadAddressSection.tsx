import { useState, type ChangeEvent, type ReactNode } from 'react';
import { SearchableSelect, type SelectOption } from '@/shared/components/SearchableSelect';

interface AddressState {
    quocGia: string;
    tinhThanh: string;
    quanHuyen: string;
    phuongXa: string;
    soNhaDuong: string;
    maVung: string;
    diaChi: string;
}

const QUOC_GIA_OPTIONS: SelectOption[] = [
    { value: 'viet-nam', label: 'Việt Nam' },
    { value: 'my',       label: 'Mỹ' },
    { value: 'nhat',     label: 'Nhật Bản' },
    { value: 'han-quoc', label: 'Hàn Quốc' },
    { value: 'trung-quoc', label: 'Trung Quốc' },
];

const PROVINCES: SelectOption[] = [
    { value: 'ha-noi',     label: 'Hà Nội' },
    { value: 'ho-chi-minh', label: 'Hồ Chí Minh' },
    { value: 'da-nang',    label: 'Đà Nẵng' },
    { value: 'hai-phong',  label: 'Hải Phòng' },
    { value: 'can-tho',    label: 'Cần Thơ' },
    { value: 'an-giang',   label: 'An Giang' },
    { value: 'ba-ria-vung-tau', label: 'Bà Rịa - Vũng Tàu' },
    { value: 'binh-duong', label: 'Bình Dương' },
    { value: 'dong-nai',   label: 'Đồng Nai' },
    { value: 'khanh-hoa',  label: 'Khánh Hòa' },
];

const DISTRICTS: Record<string, SelectOption[]> = {
    'ha-noi': [
        { value: 'ba-dinh',   label: 'Ba Đình' },
        { value: 'hoan-kiem', label: 'Hoàn Kiếm' },
        { value: 'dong-da',   label: 'Đống Đa' },
        { value: 'hai-ba-trung', label: 'Hai Bà Trưng' },
        { value: 'cau-giay',  label: 'Cầu Giấy' },
        { value: 'tay-ho',    label: 'Tây Hồ' },
    ],
    'ho-chi-minh': [
        { value: 'q1',  label: 'Quận 1' },
        { value: 'q3',  label: 'Quận 3' },
        { value: 'q5',  label: 'Quận 5' },
        { value: 'q7',  label: 'Quận 7' },
        { value: 'binh-thanh', label: 'Bình Thạnh' },
        { value: 'go-vap',     label: 'Gò Vấp' },
    ],
    'da-nang': [
        { value: 'hai-chau',  label: 'Hải Châu' },
        { value: 'thanh-khe', label: 'Thanh Khê' },
        { value: 'son-tra',   label: 'Sơn Trà' },
        { value: 'ngu-hanh-son', label: 'Ngũ Hành Sơn' },
    ],
};

const WARDS: Record<string, SelectOption[]> = {
    'ba-dinh': [
        { value: 'phuc-xa',    label: 'Phúc Xá' },
        { value: 'truc-bach',  label: 'Trúc Bạch' },
        { value: 'vinh-phuc',  label: 'Vĩnh Phúc' },
        { value: 'co-nhuong',  label: 'Cống Vị' },
    ],
    'hoan-kiem': [
        { value: 'hang-bac',   label: 'Hàng Bạc' },
        { value: 'hang-bo',    label: 'Hàng Bồ' },
        { value: 'hoan-kiem-p', label: 'Hoàn Kiếm' },
        { value: 'ly-thai-to', label: 'Lý Thái Tổ' },
    ],
    'q1': [
        { value: 'ben-nghe',  label: 'Bến Nghé' },
        { value: 'ben-thanh', label: 'Bến Thành' },
        { value: 'co-giang',  label: 'Cô Giang' },
        { value: 'nguyen-thai-binh', label: 'Nguyễn Thái Bình' },
    ],
};

const inputCls =
    'w-full border border-gray-300 rounded-btn px-3 py-1.5 text-md text-text-main bg-white ' +
    'focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary transition-colors';

const FieldRow = ({ label, children }: { label: string; children: ReactNode }) => (
    <div className="flex items-center gap-3">
        <span className="text-sm text-gray-600 flex-shrink-0 w-[148px]">{label}</span>
        <div className="flex-1 min-w-0">{children}</div>
    </div>
);

/**
 * Section "Thông tin địa chỉ" trong form thêm tiềm năng.
 * Hỗ trợ cascade: Tỉnh → Quận/Huyện → Phường/Xã (dữ liệu tĩnh).
 */
export const LeadAddressSection = () => {
    const [data, setData] = useState<AddressState>({
        quocGia: 'viet-nam',
        tinhThanh: '',
        quanHuyen: '',
        phuongXa: '',
        soNhaDuong: '',
        maVung: '',
        diaChi: '',
    });

    const set =
        (field: keyof AddressState) =>
        (e: ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) =>
            setData((prev) => ({ ...prev, [field]: e.target.value }));

    const setProvince = (v: string) =>
        setData((prev) => ({ ...prev, tinhThanh: v, quanHuyen: '', phuongXa: '' }));

    const setDistrict = (v: string) =>
        setData((prev) => ({ ...prev, quanHuyen: v, phuongXa: '' }));

    return (
        <div className="grid grid-cols-2 gap-x-10 gap-y-4">
            {/* Cột trái */}
            <div className="space-y-4">
                <FieldRow label="Quốc gia">
                    <SearchableSelect
                        value={data.quocGia}
                        onChange={(v) => setData((prev) => ({ ...prev, quocGia: v }))}
                        options={QUOC_GIA_OPTIONS}
                    />
                </FieldRow>
                <FieldRow label="Quận/Huyện">
                    <SearchableSelect
                        value={data.quanHuyen}
                        onChange={setDistrict}
                        options={DISTRICTS[data.tinhThanh] ?? []}
                    />
                </FieldRow>
                <FieldRow label="Số nhà, Đường phố">
                    <input
                        type="text"
                        value={data.soNhaDuong}
                        onChange={set('soNhaDuong')}
                        className={inputCls}
                    />
                </FieldRow>
            </div>

            {/* Cột phải */}
            <div className="space-y-4">
                <FieldRow label="Tỉnh/Thành phố">
                    <SearchableSelect
                        value={data.tinhThanh}
                        onChange={setProvince}
                        options={PROVINCES}
                    />
                </FieldRow>
                <FieldRow label="Phường/Xã">
                    <SearchableSelect
                        value={data.phuongXa}
                        onChange={(v) => setData((prev) => ({ ...prev, phuongXa: v }))}
                        options={WARDS[data.quanHuyen] ?? []}
                    />
                </FieldRow>
                <FieldRow label="Mã vùng">
                    <input
                        type="text"
                        value={data.maVung}
                        onChange={set('maVung')}
                        className={inputCls}
                    />
                </FieldRow>
            </div>

            {/* Full-width: Địa chỉ */}
            <div className="col-span-2">
                <FieldRow label="Địa chỉ">
                    <textarea
                        rows={3}
                        value={data.diaChi}
                        onChange={set('diaChi')}
                        className="w-full border border-gray-300 rounded-btn px-3 py-2 text-md text-text-main
                                   focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary
                                   resize-none transition-colors"
                    />
                </FieldRow>
            </div>

            {/* Full-width: Vị trí cắm mốc */}
            <div className="col-span-2">
                <FieldRow label="">
                    <a href="#" className="text-primary text-md hover:underline">
                        Vị trí cắm mốc
                    </a>
                </FieldRow>
            </div>
        </div>
    );
};
