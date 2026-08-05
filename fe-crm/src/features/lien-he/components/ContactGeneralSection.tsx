import { useMemo, useState } from 'react';
import { SearchableSelect } from '@/shared/components/SearchableSelect';
import { FieldRow } from '@/shared/components/form/FieldRow';
import { PrefillHint } from '@/shared/components/form/PrefillHint';
import { fillEmpty, hasFilled } from '@/shared/utils/prefill';
import { inputCls } from '@/shared/components/form/formStyles';
import { useCustomerList } from '@/features/khach-hang/hooks/useCustomerList';
import { useActiveUsers } from '@/features/users/hooks/useActiveUsers';
import {
    SALUTATION_OPTIONS,
    SOURCE_OPTIONS,
    type ContactFormState,
} from './contactFormTypes';

interface Props {
    value: ContactFormState;
    onChange: (patch: Partial<ContactFormState>) => void;
    /** Map field→lỗi; ô có lỗi sẽ viền đỏ kèm dòng chữ đỏ bên dưới. */
    errors?: Record<string, string>;
}

/**
 * Section "Thông tin chung" của form thêm liên hệ.
 */
export const ContactGeneralSection = ({ value, onChange, errors = {} }: Props) => {
    const { data: customers = [] } = useCustomerList();
    const { data: users = [] } = useActiveUsers();

    const customerOptions = useMemo(
        () => customers.map((c) => ({ value: String(c.id), label: c.name })),
        [customers],
    );
    const userOptions = useMemo(
        () => users.map((u) => ({ value: String(u.id), label: u.fullName })),
        [users],
    );

    /** Tên tổ chức vừa kéo dữ liệu về — hiện dòng gợi ý dưới ô Tổ chức. */
    const [prefillFrom, setPrefillFrom] = useState<string | null>(null);

    /** Chọn tổ chức → tự điền địa chỉ, SĐT cơ quan, nguồn gốc (chỉ ô còn trống). */
    const onPickCustomer = (v: string) => {
        onChange({ customerId: v });
        setPrefillFrom(null);
        const customer = customers.find((c) => String(c.id) === v);
        if (!customer) return;
        const patch = fillEmpty(value, {
            officePhone: customer.phone ?? '',
            source: customer.source ?? '',
        });
        if (hasFilled(patch)) { onChange(patch); setPrefillFrom(`tổ chức «${customer.name}»`); }
    };

    return (
        <div className="grid grid-cols-2 gap-x-10 gap-y-4">
            <div className="space-y-4">
                <FieldRow label="Họ và đệm">
                    <input
                        type="text"
                        value={value.hoDem}
                        onChange={(e) => onChange({ hoDem: e.target.value })}
                        className={inputCls}
                    />
                </FieldRow>
                <FieldRow label="Họ và tên">
                    <input
                        type="text"
                        value={`${value.hoDem} ${value.ten}`.trim()}
                        readOnly
                        tabIndex={-1}
                        className={`${inputCls} bg-gray-50 cursor-default`}
                    />
                </FieldRow>
                <FieldRow label="Phòng ban">
                    <input
                        type="text"
                        value={value.department}
                        onChange={(e) => onChange({ department: e.target.value })}
                        className={inputCls}
                    />
                </FieldRow>
                <FieldRow label="Người phụ trách">
                    <SearchableSelect
                        value={value.assignedUserId}
                        onChange={(v) => onChange({ assignedUserId: v })}
                        options={userOptions}
                    />
                </FieldRow>
            </div>

            <div className="space-y-4">
                <FieldRow label="Xưng hô">
                    <SearchableSelect
                        value={value.salutation}
                        onChange={(v) => onChange({ salutation: v })}
                        options={SALUTATION_OPTIONS}
                    />
                </FieldRow>
                <FieldRow label="Tên" required error={errors.ten}>
                    <input
                        type="text"
                        value={value.ten}
                        onChange={(e) => onChange({ ten: e.target.value })}
                        className={inputCls}
                    />
                </FieldRow>
                <FieldRow label="Chức danh">
                    <input
                        type="text"
                        value={value.title}
                        onChange={(e) => onChange({ title: e.target.value })}
                        className={inputCls}
                    />
                </FieldRow>
                <FieldRow label="Tổ chức">
                    <SearchableSelect
                        value={value.customerId}
                        onChange={onPickCustomer}
                        options={customerOptions}
                    />
                    <PrefillHint source={prefillFrom} />
                </FieldRow>
                <FieldRow label="Nguồn gốc">
                    <SearchableSelect
                        value={value.source}
                        onChange={(v) => onChange({ source: v })}
                        options={SOURCE_OPTIONS}
                    />
                </FieldRow>
            </div>
        </div>
    );
};
