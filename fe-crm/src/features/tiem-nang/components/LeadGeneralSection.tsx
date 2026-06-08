import { useState, type ChangeEvent, type ReactNode } from 'react';
import { FiInfo } from 'react-icons/fi';
import { SearchableSelect } from '@/shared/components/SearchableSelect';

interface GeneralState {
    xuHo: string;
    hovaDem: string;
    ten: string;
    phongBan: string;
    chucDanh: string;
    dtDiDong: string;
    dtCoQuan: string;
    nguonGoc: string;
    loaiTiemNang: string;
    zalo: string;
    emailCaNhan: string;
    emailCoQuan: string;
    toChuc: string;
    maSoThue: string;
}

const XU_HO_OPTIONS = [
    { value: 'anh',           label: 'Anh' },
    { value: 'chi',           label: 'Chị' },
    { value: 'ong',           label: 'Ông' },
    { value: 'ba',            label: 'Bà' },
    { value: 'khong-xac-dinh', label: 'Không xác định' },
];

const NGUON_GOC_OPTIONS = [
    { value: 'website',    label: 'Website' },
    { value: 'gioi-thieu', label: 'Giới thiệu' },
    { value: 'dien-thoai', label: 'Điện thoại' },
    { value: 'email',      label: 'Email' },
    { value: 'mxh',        label: 'Mạng xã hội' },
    { value: 'khac',       label: 'Khác' },
];

const LOAI_TIEM_NANG_OPTIONS = [
    { value: 'ca-nhan',       label: 'Cá nhân' },
    { value: 'doanh-nghiep',  label: 'Doanh nghiệp' },
    { value: 'ho-kinh-doanh', label: 'Hộ kinh doanh' },
];

const inputCls =
    'w-full border border-gray-300 rounded-btn px-3 py-1.5 text-md text-text-main bg-white ' +
    'focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary transition-colors';

const FieldRow = ({
    label,
    required,
    children,
}: {
    label: string;
    required?: boolean;
    children: ReactNode;
}) => (
    <div className="flex items-center gap-3">
        <span className="text-sm text-gray-600 flex-shrink-0 w-[148px]">
            {label}
            {required && <span className="text-danger ml-0.5">*</span>}
        </span>
        <div className="flex-1 min-w-0">{children}</div>
    </div>
);

/**
 * Section "Thông tin chung" trong form thêm tiềm năng.
 */
export const LeadGeneralSection = () => {
    const [data, setData] = useState<GeneralState>({
        xuHo: '',
        hovaDem: '',
        ten: '',
        phongBan: '',
        chucDanh: '',
        dtDiDong: '',
        dtCoQuan: '',
        nguonGoc: '',
        loaiTiemNang: '',
        zalo: '',
        emailCaNhan: '',
        emailCoQuan: '',
        toChuc: '',
        maSoThue: '',
    });

    const set =
        (field: keyof GeneralState) =>
        (e: ChangeEvent<HTMLInputElement>) =>
            setData((prev) => ({ ...prev, [field]: e.target.value }));

    const setVal = (field: keyof GeneralState) => (v: string) =>
        setData((prev) => ({ ...prev, [field]: v }));

    const PhoneField = ({
        label,
        field,
    }: {
        label: string;
        field: 'dtDiDong' | 'dtCoQuan';
    }) => (
        <FieldRow label={label}>
            <div className="relative">
                <input
                    type="text"
                    value={data[field]}
                    onChange={set(field)}
                    className={`${inputCls} pr-8`}
                />
                <span className="absolute inset-y-0 right-3 flex items-center text-gray-400 pointer-events-none">
                    <FiInfo size={14} />
                </span>
            </div>
        </FieldRow>
    );

    return (
        <div className="grid grid-cols-2 gap-x-10 gap-y-4">
            {/* Cột trái */}
            <div className="space-y-4">
                <FieldRow label="Xưng hô">
                    <SearchableSelect
                        value={data.xuHo}
                        onChange={setVal('xuHo')}
                        options={XU_HO_OPTIONS}
                    />
                </FieldRow>
                <FieldRow label="Tên" required>
                    <input
                        type="text"
                        value={data.ten}
                        onChange={set('ten')}
                        className={inputCls}
                    />
                </FieldRow>
                <FieldRow label="Phòng ban">
                    <SearchableSelect
                        value={data.phongBan}
                        onChange={setVal('phongBan')}
                        options={[]}
                    />
                </FieldRow>
                <PhoneField label="ĐT di động" field="dtDiDong" />
                <FieldRow label="Nguồn gốc">
                    <SearchableSelect
                        value={data.nguonGoc}
                        onChange={setVal('nguonGoc')}
                        options={NGUON_GOC_OPTIONS}
                    />
                </FieldRow>
                <FieldRow label="Zalo">
                    <input
                        type="text"
                        value={data.zalo}
                        onChange={set('zalo')}
                        className={inputCls}
                    />
                </FieldRow>
                <FieldRow label="Email cơ quan">
                    <input
                        type="text"
                        value={data.emailCoQuan}
                        onChange={set('emailCoQuan')}
                        className={inputCls}
                    />
                </FieldRow>
                <FieldRow label="Mã số thuế">
                    <input
                        type="text"
                        value={data.maSoThue}
                        onChange={set('maSoThue')}
                        className={inputCls}
                    />
                </FieldRow>
            </div>

            {/* Cột phải */}
            <div className="space-y-4">
                <FieldRow label="Họ và đệm">
                    <input
                        type="text"
                        value={data.hovaDem}
                        onChange={set('hovaDem')}
                        className={inputCls}
                    />
                </FieldRow>
                <FieldRow label="Họ và tên">
                    <input
                        type="text"
                        value={`${data.hovaDem} ${data.ten}`.trim()}
                        readOnly
                        className={`${inputCls} bg-gray-50 cursor-default`}
                        tabIndex={-1}
                    />
                </FieldRow>
                <FieldRow label="Chức danh">
                    <SearchableSelect
                        value={data.chucDanh}
                        onChange={setVal('chucDanh')}
                        options={[]}
                    />
                </FieldRow>
                <PhoneField label="ĐT cơ quan" field="dtCoQuan" />
                <FieldRow label="Loại tiềm năng">
                    <SearchableSelect
                        value={data.loaiTiemNang}
                        onChange={setVal('loaiTiemNang')}
                        options={LOAI_TIEM_NANG_OPTIONS}
                    />
                </FieldRow>
                <FieldRow label="Email cá nhân">
                    <input
                        type="text"
                        value={data.emailCaNhan}
                        onChange={set('emailCaNhan')}
                        className={inputCls}
                    />
                </FieldRow>
                <FieldRow label="Tổ chức">
                    <input
                        type="text"
                        value={data.toChuc}
                        onChange={set('toChuc')}
                        className={inputCls}
                    />
                </FieldRow>
                <div /> {/* ô trống tương ứng với Mã số thuế bên trái */}
            </div>
        </div>
    );
};
