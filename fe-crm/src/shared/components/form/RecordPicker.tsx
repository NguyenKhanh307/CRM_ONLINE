import { useState } from 'react';
import { SearchableSelect } from '@/shared/components/SearchableSelect';
import { useRecordSearch, searchHintOf, type RecordModule } from '@/shared/lookup/useRecordSearch';

interface Props {
    /** Phân hệ của bản ghi cần chọn. Rỗng = chưa xác định (vd chưa chọn loại đối tượng) → ô bị khóa. */
    module: RecordModule | '';
    /** ID bản ghi đang chọn, dạng chuỗi ('' = chưa chọn). */
    value: string;
    onChange: (value: string) => void;
    /**
     * Tên bản ghi đang chọn do backend trả sẵn (`*Name`/`*Code`).
     * **Bắt buộc khi sửa bản ghi cũ**: kết quả tìm kiếm chỉ có 20 dòng nên bản ghi đang gắn thường
     * không nằm trong đó, thiếu nhãn dự phòng là ô hiện y như chưa chọn.
     */
    fallbackLabel?: string | null;
    /** Thu hẹp phạm vi tìm, vd `{ customerId }` để chỉ hiện liên hệ của khách đang chọn. */
    customerId?: number;
    placeholder?: string;
}

/**
 * Ô chọn một bản ghi khóa ngoại, **tìm kiếm phía server**.
 *
 * Dùng cho mọi phân hệ có bảng lớn (liên hệ, tiềm năng, cơ hội, báo giá, đơn hàng, hóa đơn) —
 * xem `useRecordSearch` để biết vì sao không dùng hook `use*List` nạp sẵn.
 */
export const RecordPicker = ({
    module,
    value,
    onChange,
    fallbackLabel,
    customerId,
    placeholder,
}: Props) => {
    const [q, setQ] = useState('');
    const { options, loading } = useRecordSearch(module, q, { customerId });

    return (
        <SearchableSelect
            value={value}
            onChange={onChange}
            options={options}
            onSearchChange={setQ}
            loading={loading}
            fallbackLabel={fallbackLabel}
            placeholder={module ? placeholder : '— Chọn loại trước —'}
            searchPlaceholder={module ? searchHintOf(module) : 'Tìm kiếm'}
        />
    );
};
