import { FieldRow } from '@/shared/components/form/FieldRow';
import { inputCls } from '@/shared/components/form/formStyles';
import type { ContactFormState } from './contactFormTypes';

interface Props {
    value: ContactFormState;
    onChange: (patch: Partial<ContactFormState>) => void;
}

/**
 * Section "Thông tin liên lạc" của form thêm liên hệ.
 */
export const ContactContactSection = ({ value, onChange }: Props) => (
    <div className="grid grid-cols-2 gap-x-10 gap-y-4">
        <div className="space-y-4">
            <FieldRow label="ĐT di động">
                <input
                    type="text"
                    value={value.mobilePhone}
                    onChange={(e) => onChange({ mobilePhone: e.target.value })}
                    className={inputCls}
                />
            </FieldRow>
            <FieldRow label="Email">
                <input
                    type="text"
                    value={value.email}
                    onChange={(e) => onChange({ email: e.target.value })}
                    className={inputCls}
                />
            </FieldRow>
            <FieldRow label="Email cá nhân">
                <input
                    type="text"
                    value={value.personalEmail}
                    onChange={(e) => onChange({ personalEmail: e.target.value })}
                    className={inputCls}
                />
            </FieldRow>
            <FieldRow label="Địa chỉ" alignTop>
                <textarea
                    rows={2}
                    value={value.address}
                    onChange={(e) => onChange({ address: e.target.value })}
                    className={`${inputCls} resize-none`}
                />
            </FieldRow>
            <label className="flex items-center gap-2 cursor-pointer w-fit ml-[160px]">
                <input
                    type="checkbox"
                    checked={value.doNotCall}
                    onChange={(e) => onChange({ doNotCall: e.target.checked })}
                    className="w-4 h-4 accent-primary"
                />
                <span className="text-md text-text-main">Không gọi điện</span>
            </label>
        </div>

        <div className="space-y-4">
            <FieldRow label="ĐT cơ quan">
                <input
                    type="text"
                    value={value.officePhone}
                    onChange={(e) => onChange({ officePhone: e.target.value })}
                    className={inputCls}
                />
            </FieldRow>
            <FieldRow label="Email cơ quan">
                <input
                    type="text"
                    value={value.workEmail}
                    onChange={(e) => onChange({ workEmail: e.target.value })}
                    className={inputCls}
                />
            </FieldRow>
            <FieldRow label="Zalo">
                <input
                    type="text"
                    value={value.zalo}
                    onChange={(e) => onChange({ zalo: e.target.value })}
                    className={inputCls}
                />
            </FieldRow>
            <div className="h-[60px]" />
            <label className="flex items-center gap-2 cursor-pointer w-fit ml-[160px]">
                <input
                    type="checkbox"
                    checked={value.doNotEmail}
                    onChange={(e) => onChange({ doNotEmail: e.target.checked })}
                    className="w-4 h-4 accent-primary"
                />
                <span className="text-md text-text-main">Không gửi Email</span>
            </label>
        </div>
    </div>
);
