import { useState, useMemo } from 'react';
import { FiPlus, FiChevronDown, FiUpload, FiTrash2, FiInfo } from 'react-icons/fi';

interface QuoteItemRow {
    id: string;
    selected: boolean;
    maHangHoa: string;
    dienGiai: string;
    donViTinh: string;
    soLuong: number;
    donGia: number;
    tyLeChietKhau: number;
    thueSuat: number;
    ctkm: string;
    hangKm: boolean;
    thanhTien: number;
    tienChietKhau: number;
    tienThue: number;
    tongTien: number;
}

const computeRow = (row: QuoteItemRow): QuoteItemRow => {
    const thanhTien = row.soLuong * row.donGia;
    const tienChietKhau = (thanhTien * row.tyLeChietKhau) / 100;
    const tienThue = ((thanhTien - tienChietKhau) * row.thueSuat) / 100;
    return { ...row, thanhTien, tienChietKhau, tienThue, tongTien: thanhTien - tienChietKhau + tienThue };
};

const emptyRow = (): QuoteItemRow =>
    computeRow({
        id: crypto.randomUUID(),
        selected: false,
        maHangHoa: '', dienGiai: '', donViTinh: '',
        soLuong: 1, donGia: 0, tyLeChietKhau: 0, thueSuat: 0,
        ctkm: '', hangKm: false,
        thanhTien: 0, tienChietKhau: 0, tienThue: 0, tongTien: 0,
    });

const fmt = (n: number) => n === 0 ? '0' : n.toLocaleString('vi-VN');

// Sticky left offsets: checkbox(40) + STT(48) = 88
const stickyChk = 'sticky left-0 z-10';
const stickyStt = 'sticky left-[40px] z-10';
const stickyMa  = 'sticky left-[88px] z-10 shadow-[2px_0_5px_rgba(0,0,0,0.07)]';

const thBase = 'text-table font-semibold text-text-main border-b-2 border-gray-300 px-2 py-2 whitespace-nowrap bg-gray-100';
const tdBase = 'border-b border-gray-200 px-2 py-1';
const numIn  = 'w-full bg-transparent text-table text-text-main text-right focus:outline-none';
const txtIn  = 'w-full bg-transparent text-table text-text-main focus:outline-none';

/**
 * Bảng hàng hóa trong form báo giá.
 * Cột checkbox, STT và Mã hàng hóa được ghim (sticky) khi scroll ngang.
 */
export const QuoteItemsTable = () => {
    const [rows, setRows] = useState<QuoteItemRow[]>([emptyRow()]);
    const [autoIncrease, setAutoIncrease] = useState(false);

    const allSelected = rows.length > 0 && rows.every((r) => r.selected);
    const totals = useMemo(
        () =>
            rows.reduce(
                (acc, r) => ({
                    soLuong:       acc.soLuong       + r.soLuong,
                    thanhTien:     acc.thanhTien     + r.thanhTien,
                    tienChietKhau: acc.tienChietKhau + r.tienChietKhau,
                    tienThue:      acc.tienThue      + r.tienThue,
                    tongTien:      acc.tongTien      + r.tongTien,
                }),
                { soLuong: 0, thanhTien: 0, tienChietKhau: 0, tienThue: 0, tongTien: 0 }
            ),
        [rows]
    );

    const selectAll  = (v: boolean) => setRows((p) => p.map((r) => ({ ...r, selected: v })));
    const selectRow  = (id: string, v: boolean) => setRows((p) => p.map((r) => r.id === id ? { ...r, selected: v } : r));
    const changeRow  = (id: string, field: keyof QuoteItemRow, val: string | number | boolean) =>
        setRows((p) => p.map((r) => r.id === id ? computeRow({ ...r, [field]: val } as QuoteItemRow) : r));
    const addRow     = () => setRows((p) => [...p, emptyRow()]);
    const clearRows  = () => setRows([emptyRow()]);

    return (
        <div className="space-y-3">
            {/* Toolbar */}
            <div className="flex items-center justify-between flex-wrap gap-2">
                <div className="flex items-center gap-3 flex-wrap">
                    <button type="button" className="border border-primary text-primary rounded-btn px-3 py-1.5 text-sm hover:bg-blue-50 transition-colors flex items-center gap-1">
                        <FiPlus size={14} /> Thêm, sửa nhanh hàng hóa
                    </button>
                    <label className="flex items-center gap-2 cursor-pointer select-none">
                        <button
                            type="button"
                            onClick={() => setAutoIncrease((v) => !v)}
                            className={`relative w-9 h-5 rounded-full flex-shrink-0 transition-colors ${autoIncrease ? 'bg-primary' : 'bg-gray-300'}`}
                        >
                            <span className={`absolute top-0.5 w-4 h-4 bg-white rounded-full shadow transition-transform ${autoIncrease ? 'translate-x-4' : 'translate-x-0.5'}`} />
                        </button>
                        <span className="text-sm text-gray-600">Tự động tăng SL khi chọn trùng</span>
                    </label>
                </div>
                <div className="flex items-center gap-3">
                    <button type="button" className="text-sm text-primary flex items-center gap-1 hover:underline">
                        <FiUpload size={13} /> Nhập khẩu hàng hóa
                    </button>
                    <button type="button" onClick={clearRows} className="text-sm text-danger flex items-center gap-1 hover:underline">
                        <FiTrash2 size={13} /> Xóa tất cả
                    </button>
                </div>
            </div>

            {/* Bảng */}
            <div className="overflow-x-auto border border-gray-300 rounded-section">
                <table className="border-collapse" style={{ minWidth: 1480, width: '100%' }}>
                    <thead>
                        <tr>
                            <th className={`${stickyChk} z-20 ${thBase} text-center`} style={{ width: 40, minWidth: 40 }}>
                                <input type="checkbox" checked={allSelected} onChange={(e) => selectAll(e.target.checked)} className="w-4 h-4 accent-primary" />
                            </th>
                            <th className={`${stickyStt} z-20 ${thBase} text-center`} style={{ width: 48, minWidth: 48 }}>STT</th>
                            <th className={`${stickyMa} z-20 ${thBase} text-left`} style={{ width: 144, minWidth: 144 }}>Mã hàng hóa</th>
                            <th className={`${thBase} text-left`}  style={{ width: 200 }}>Diễn giải</th>
                            <th className={`${thBase} text-left`}  style={{ width: 88 }}>Đơn vị tính</th>
                            <th className={`${thBase} text-right`} style={{ width: 72 }}>Số lượng</th>
                            <th className={`${thBase} text-right`} style={{ width: 96 }}>Đơn giá</th>
                            <th className={`${thBase} text-right`} style={{ width: 96 }}>Thành tiền</th>
                            <th className={`${thBase} text-right`} style={{ width: 72 }}>Tỷ lệ CK%</th>
                            <th className={`${thBase} text-right`} style={{ width: 104 }}>Tiền chiết khấu</th>
                            <th className={`${thBase} text-right`} style={{ width: 72 }}>Thuế suất%</th>
                            <th className={`${thBase} text-right`} style={{ width: 96 }}>Tiền thuế</th>
                            <th className={`${thBase} text-right`} style={{ width: 96 }}>Tổng tiền</th>
                            <th className={`${thBase} text-left`}  style={{ width: 80 }}>
                                <span className="flex items-center gap-1">CTKM <FiInfo size={11} className="text-gray-400" /></span>
                            </th>
                            <th className={`${thBase} text-center`} style={{ width: 72 }}>
                                <span className="flex items-center justify-center gap-1">Hàng KM <FiInfo size={11} className="text-gray-400" /></span>
                            </th>
                        </tr>
                    </thead>
                    <tbody>
                        {rows.map((row, idx) => {
                            const bg = idx % 2 === 0 ? '#ffffff' : '#f9fafb';
                            return (
                                <tr key={row.id}>
                                    <td style={{ backgroundColor: bg }} className={`${stickyChk} ${tdBase} text-center`}>
                                        <input type="checkbox" checked={row.selected} onChange={(e) => selectRow(row.id, e.target.checked)} className="w-4 h-4 accent-primary" />
                                    </td>
                                    <td style={{ backgroundColor: bg }} className={`${stickyStt} ${tdBase} text-center text-table text-gray-500`}>{idx + 1}</td>
                                    <td style={{ backgroundColor: bg }} className={`${stickyMa} ${tdBase}`}>
                                        <input type="text" value={row.maHangHoa} onChange={(e) => changeRow(row.id, 'maHangHoa', e.target.value)} className={txtIn} />
                                    </td>
                                    <td className={tdBase}>
                                        <input type="text" value={row.dienGiai} onChange={(e) => changeRow(row.id, 'dienGiai', e.target.value)} className={txtIn} />
                                    </td>
                                    <td className={tdBase}>
                                        <input type="text" value={row.donViTinh} onChange={(e) => changeRow(row.id, 'donViTinh', e.target.value)} className={txtIn} />
                                    </td>
                                    <td className={tdBase}>
                                        <input type="number" min={0} value={row.soLuong} onChange={(e) => changeRow(row.id, 'soLuong', parseFloat(e.target.value) || 0)} className={numIn} />
                                    </td>
                                    <td className={tdBase}>
                                        <input type="number" min={0} value={row.donGia} onChange={(e) => changeRow(row.id, 'donGia', parseFloat(e.target.value) || 0)} className={numIn} />
                                    </td>
                                    <td className={`${tdBase} text-right text-table text-text-main`}>{fmt(row.thanhTien)}</td>
                                    <td className={tdBase}>
                                        <input type="number" min={0} max={100} value={row.tyLeChietKhau} onChange={(e) => changeRow(row.id, 'tyLeChietKhau', parseFloat(e.target.value) || 0)} className={numIn} />
                                    </td>
                                    <td className={`${tdBase} text-right text-table text-text-main`}>{fmt(row.tienChietKhau)}</td>
                                    <td className={tdBase}>
                                        <input type="number" min={0} max={100} value={row.thueSuat} onChange={(e) => changeRow(row.id, 'thueSuat', parseFloat(e.target.value) || 0)} className={numIn} />
                                    </td>
                                    <td className={`${tdBase} text-right text-table text-text-main`}>{fmt(row.tienThue)}</td>
                                    <td className={`${tdBase} text-right text-table font-medium text-text-main`}>{fmt(row.tongTien)}</td>
                                    <td className={tdBase}>
                                        <input type="text" value={row.ctkm} onChange={(e) => changeRow(row.id, 'ctkm', e.target.value)} className={txtIn} />
                                    </td>
                                    <td className={`${tdBase} text-center`}>
                                        <input type="checkbox" checked={row.hangKm} onChange={(e) => changeRow(row.id, 'hangKm', e.target.checked)} className="w-4 h-4 accent-primary" />
                                    </td>
                                </tr>
                            );
                        })}
                        {/* Tổng cộng */}
                        <tr>
                            <td colSpan={2} style={{ backgroundColor: '#f3f4f6' }} className={`${stickyChk} border-t-2 border-gray-300 bg-gray-100`} />
                            <td style={{ backgroundColor: '#f3f4f6' }} className={`${stickyMa} border-t-2 border-gray-300 bg-gray-100 px-3 py-2 text-table font-semibold text-text-main`}>Tổng cộng</td>
                            <td colSpan={2} className="border-t-2 border-gray-300 bg-gray-100" />
                            <td className="border-t-2 border-gray-300 bg-gray-100 px-2 py-2 text-table text-right font-medium">{fmt(totals.soLuong)}</td>
                            <td className="border-t-2 border-gray-300 bg-gray-100" />
                            <td className="border-t-2 border-gray-300 bg-gray-100 px-2 py-2 text-table text-right font-medium">{fmt(totals.thanhTien)}</td>
                            <td className="border-t-2 border-gray-300 bg-gray-100" />
                            <td className="border-t-2 border-gray-300 bg-gray-100 px-2 py-2 text-table text-right font-medium">{fmt(totals.tienChietKhau)}</td>
                            <td className="border-t-2 border-gray-300 bg-gray-100" />
                            <td className="border-t-2 border-gray-300 bg-gray-100 px-2 py-2 text-table text-right font-medium">{fmt(totals.tienThue)}</td>
                            <td className="border-t-2 border-gray-300 bg-gray-100 px-2 py-2 text-table text-right font-medium">{fmt(totals.tongTien)}</td>
                            <td colSpan={2} className="border-t-2 border-gray-300 bg-gray-100" />
                        </tr>
                    </tbody>
                </table>
            </div>

            {/* Tóm tắt */}
            <div className="flex items-center justify-between text-sm text-gray-500 px-1">
                <span>Tổng số: {rows.length}</span>
                <span>Số dòng/trang: 100 &nbsp; 1 – {rows.length}</span>
            </div>

            {/* Nút hành động */}
            <div className="flex items-center gap-2 flex-wrap">
                <button type="button" className="border border-gray-300 text-text-main rounded-btn px-3 py-1.5 text-sm hover:bg-gray-50 transition-colors">
                    + Chọn hàng hóa
                </button>
                <div className="flex">
                    <button type="button" onClick={addRow} className="border border-gray-300 border-r-0 text-text-main rounded-btn rounded-r-none px-3 py-1.5 text-sm hover:bg-gray-50 transition-colors flex items-center gap-1">
                        <FiPlus size={13} /> Thêm dòng
                    </button>
                    <button type="button" className="border border-gray-300 text-text-main rounded-btn rounded-l-none px-2 py-1.5 text-sm hover:bg-gray-50 transition-colors">
                        <FiChevronDown size={13} />
                    </button>
                </div>
                <button type="button" className="border border-gray-300 text-text-main rounded-btn px-3 py-1.5 text-sm hover:bg-gray-50 transition-colors">
                    Áp dụng khuyến mại
                </button>
                <button type="button" className="border border-gray-300 text-text-main rounded-btn px-3 py-1.5 text-sm hover:bg-gray-50 transition-colors">
                    Chiết khấu đơn hàng
                </button>
            </div>
        </div>
    );
};
