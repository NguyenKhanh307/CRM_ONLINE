interface Row {
    label: string;
    value: string | null | undefined;
}

interface Props {
    rows: Row[];
}

// khối hiển thị thông tin chỉ đọc suy ra từ bản ghi cha (vd chọn Báo giá -> hiện Khách hàng/Liên hệ
// của báo giá đó) — dùng ở form Đơn hàng/Hóa đơn/Phiếu chăm sóc sau khi các phân hệ này rút gọn
export const DerivedContextBox = ({ rows }: Props) => {
    // chỉ hiển thị các dòng có value, nếu không có dòng nào -> null
    const visible = rows.filter(r => r.value);
    if (visible.length === 0) return null;
    return (
        <div className="rounded-btn border border-gray-200 bg-gray-50 px-3 py-2 text-md space-y-0.5">
            {visible.map(r => (
                <div key={r.label} className="flex gap-2">
                    <span className="text-gray-500 shrink-0">{r.label}:</span>
                    <span className="text-text-main">{r.value}</span>
                </div>
            ))}
        </div>
    );
};
