import { useState, type ChangeEvent, type ReactNode } from 'react';
import { SearchableSelect } from '@/shared/components/SearchableSelect';

interface OrgState {
    taiKhoanNganHang: string;
    moTaiNganHang: string;
    ngayThanhLap: string;
    loaiHinh: string;
    linhVuc: string;
    nganhNghe: string;
    doanhThu: string;
}

const LOAI_HINH_OPTIONS = [
    { value: 'tnhh',          label: 'Công ty TNHH' },
    { value: 'co-phan',       label: 'Công ty Cổ phần' },
    { value: 'tu-nhan',       label: 'Doanh nghiệp tư nhân' },
    { value: 'ho-kinh-doanh', label: 'Hộ kinh doanh' },
];

const DOANH_THU_OPTIONS = [
    { value: 'duoi-1ty',   label: 'Dưới 1 tỷ' },
    { value: '1-10ty',     label: '1 - 10 tỷ' },
    { value: '10-50ty',    label: '10 - 50 tỷ' },
    { value: '50-100ty',   label: '50 - 100 tỷ' },
    { value: 'tren-100ty', label: 'Trên 100 tỷ' },
];

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
 * Section "Thông tin tổ chức" trong form thêm tiềm năng.
 */
export const LeadOrgSection = () => {
    const [data, setData] = useState<OrgState>({
        taiKhoanNganHang: '',
        moTaiNganHang: '',
        ngayThanhLap: '',
        loaiHinh: '',
        linhVuc: '',
        nganhNghe: '',
        doanhThu: '',
    });

    const set =
        (field: keyof OrgState) =>
        (e: ChangeEvent<HTMLInputElement>) =>
            setData((prev) => ({ ...prev, [field]: e.target.value }));

    const setVal = (field: keyof OrgState) => (v: string) =>
        setData((prev) => ({ ...prev, [field]: v }));

    return (
        <div className="grid grid-cols-2 gap-x-10 gap-y-4">
            {/* Cột trái */}
            <div className="space-y-4">
                <FieldRow label="Tài khoản ngân hàng">
                    <input
                        type="text"
                        value={data.taiKhoanNganHang}
                        onChange={set('taiKhoanNganHang')}
                        className={inputCls}
                    />
                </FieldRow>
                <FieldRow label="Ngày thành lập">
                    <input
                        type="text"
                        value={data.ngayThanhLap}
                        onChange={set('ngayThanhLap')}
                        placeholder="dd/mm/yyyy"
                        className={inputCls}
                    />
                </FieldRow>
                <FieldRow label="Lĩnh vực">
                    <SearchableSelect
                        value={data.linhVuc}
                        onChange={setVal('linhVuc')}
                        options={[]}
                    />
                </FieldRow>
                <FieldRow label="Doanh thu">
                    <SearchableSelect
                        value={data.doanhThu}
                        onChange={setVal('doanhThu')}
                        options={DOANH_THU_OPTIONS}
                    />
                </FieldRow>
            </div>

            {/* Cột phải */}
            <div className="space-y-4">
                <FieldRow label="Mở tại ngân hàng">
                    <input
                        type="text"
                        value={data.moTaiNganHang}
                        onChange={set('moTaiNganHang')}
                        className={inputCls}
                    />
                </FieldRow>
                <FieldRow label="Loại hình">
                    <SearchableSelect
                        value={data.loaiHinh}
                        onChange={setVal('loaiHinh')}
                        options={LOAI_HINH_OPTIONS}
                    />
                </FieldRow>
                <FieldRow label="Ngành nghề">
                    <SearchableSelect
                        value={data.nganhNghe}
                        onChange={setVal('nganhNghe')}
                        options={[]}
                    />
                </FieldRow>
                <div /> {/* ô trống tương ứng với Doanh thu bên trái */}
            </div>
        </div>
    );
};
