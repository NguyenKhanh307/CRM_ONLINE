import { useState, type ChangeEvent, type ReactNode } from 'react';
import { FiUser, FiTarget, FiGrid, FiChevronDown } from 'react-icons/fi';
import { SearchableSelect } from '@/shared/components/SearchableSelect';
import { formatDate } from '@/shared/utils/date';

interface DetailState {
    soBaoGia: string;
    hieuLucDenNgay: string;
    khachHang: string;
    diaChi: string;
    coHoi: string;
    ngayBaoGia: string;
    tinhTrang: string;
    maSoThue: string;
    lienHe: string;
    duAnBanHang: string;
}

const today = formatDate(new Date());

const inputCls =
    'w-full border border-gray-300 rounded-btn px-3 py-1.5 text-md text-text-main bg-white ' +
    'focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary transition-colors';

const FieldRow = ({ label, children }: { label: string; children: ReactNode }) => (
    <div className="flex items-center gap-3">
        <span className="text-sm text-gray-600 flex-shrink-0 w-[148px]">{label}</span>
        <div className="flex-1 min-w-0">{children}</div>
    </div>
);

const SelectWithIcon = ({
    icon,
    value,
    onChange,
}: {
    icon: ReactNode;
    value: string;
    onChange: (v: string) => void;
}) => (
    <div className="relative">
        <span className="absolute inset-y-0 left-3 flex items-center text-gray-400 pointer-events-none z-10">
            {icon}
        </span>
        <select
            value={value}
            onChange={(e) => onChange(e.target.value)}
            className={`${inputCls} pl-9 pr-8 appearance-none`}
        >
            <option value="">— Không chọn —</option>
        </select>
        <span className="absolute inset-y-0 right-3 flex items-center text-gray-400 pointer-events-none">
            <FiChevronDown size={14} />
        </span>
    </div>
);

/**
 * Section "Thông tin chi tiết" trong form thêm báo giá.
 */
export const QuoteDetailSection = () => {
    const [data, setData] = useState<DetailState>({
        soBaoGia: '',
        hieuLucDenNgay: '',
        khachHang: '',
        diaChi: '',
        coHoi: '',
        ngayBaoGia: today,
        tinhTrang: 'ban-thao',
        maSoThue: '',
        lienHe: '',
        duAnBanHang: '',
    });

    const set =
        (field: keyof DetailState) =>
        (e: ChangeEvent<HTMLInputElement | HTMLSelectElement>) =>
            setData((prev) => ({ ...prev, [field]: e.target.value }));

    const setVal = (field: keyof DetailState) => (v: string) =>
        setData((prev) => ({ ...prev, [field]: v }));

    return (
        <div className="grid grid-cols-2 gap-x-10 gap-y-4">
            {/* Cột trái */}
            <div className="space-y-4">
                <FieldRow label="Số báo giá">
                    <input
                        type="text"
                        value={data.soBaoGia}
                        onChange={set('soBaoGia')}
                        placeholder="Mã tự sinh"
                        className={inputCls}
                    />
                </FieldRow>
                <FieldRow label="Hiệu lực đến ngày">
                    <input
                        type="text"
                        value={data.hieuLucDenNgay}
                        onChange={set('hieuLucDenNgay')}
                        placeholder="dd/mm/yyyy"
                        className={inputCls}
                    />
                </FieldRow>
                <FieldRow label="Khách hàng">
                    <SelectWithIcon
                        icon={<FiUser size={14} />}
                        value={data.khachHang}
                        onChange={setVal('khachHang')}
                    />
                </FieldRow>
                <FieldRow label="Địa chỉ">
                    <input
                        type="text"
                        value={data.diaChi}
                        onChange={set('diaChi')}
                        className={inputCls}
                    />
                </FieldRow>
                <FieldRow label="Cơ hội">
                    <SelectWithIcon
                        icon={<FiTarget size={14} />}
                        value={data.coHoi}
                        onChange={setVal('coHoi')}
                    />
                </FieldRow>
            </div>

            {/* Cột phải */}
            <div className="space-y-4">
                <FieldRow label="Ngày báo giá">
                    <input
                        type="text"
                        value={data.ngayBaoGia}
                        onChange={set('ngayBaoGia')}
                        placeholder="dd/mm/yyyy"
                        className={inputCls}
                    />
                </FieldRow>
                <FieldRow label="Tình trạng">
                    <SearchableSelect
                        value={data.tinhTrang}
                        onChange={setVal('tinhTrang')}
                        options={[
                            { value: 'ban-thao',      label: 'Bản thảo' },
                            { value: 'dam-phan',      label: 'Đàm phán' },
                            { value: 'da-gui',        label: 'Đã gửi' },
                            { value: 'cho-xac-nhan',  label: 'Chờ xác nhận' },
                            { value: 'da-xac-nhan',   label: 'Đã xác nhận' },
                            { value: 'dong-y',        label: 'Đồng ý' },
                        ]}
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
                <FieldRow label="Liên hệ">
                    <SelectWithIcon
                        icon={<FiUser size={14} />}
                        value={data.lienHe}
                        onChange={setVal('lienHe')}
                    />
                </FieldRow>
                <FieldRow label="Dự án bán hàng">
                    <SelectWithIcon
                        icon={<FiGrid size={14} />}
                        value={data.duAnBanHang}
                        onChange={setVal('duAnBanHang')}
                    />
                </FieldRow>
            </div>
        </div>
    );
};
