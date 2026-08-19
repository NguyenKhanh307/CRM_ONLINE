import { FieldRow } from '@/shared/components/form/FieldRow';
import { inputCls } from '@/shared/components/form/formStyles';
import type { ContactFormState } from './contactFormTypes';

interface Props {
    value: ContactFormState;
    onChange: (patch: Partial<ContactFormState>) => void;
    /** Map field→lỗi; ô có lỗi sẽ viền đỏ kèm dòng chữ đỏ bên dưới. */
    errors?: Record<string, string>;
}

/**
 * Section "Thông tin liên lạc" của form thêm liên hệ.
 */
export const ContactContactSection = ({ value, onChange, errors = {} }: Props) => (
    <div className="grid grid-cols-2 gap-x-10 gap-y-4">
        <div className="space-y-4">
            <FieldRow label="Số điện thoại" error={errors.phone}>
                <input
                    type="text"
                    value={value.phone}
                    onChange={(e) => onChange({ phone: e.target.value })}
                    className={inputCls}
                />
            </FieldRow>
            <FieldRow label="Email" error={errors.email}>
                <input
                    type="text"
                    value={value.email}
                    onChange={(e) => onChange({ email: e.target.value })}
                    className={inputCls}
                />
            </FieldRow>
        </div>

        <div className="space-y-4">
            <FieldRow label="Zalo">
                <input
                    type="text"
                    value={value.zalo}
                    onChange={(e) => onChange({ zalo: e.target.value })}
                    className={inputCls}
                />
            </FieldRow>
        </div>
    </div>
);
