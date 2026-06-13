import { SearchableSelect } from '@/shared/components/SearchableSelect';
import { FieldRow } from '@/shared/components/form/FieldRow';
import { inputCls } from '@/shared/components/form/formStyles';
import { GENDER_OPTIONS, type ContactFormState } from './contactFormTypes';

interface Props {
    value: ContactFormState;
    onChange: (patch: Partial<ContactFormState>) => void;
}

/**
 * Section "Thông tin khác" của form thêm liên hệ.
 */
export const ContactOtherSection = ({ value, onChange }: Props) => (
    <div className="grid grid-cols-2 gap-x-10 gap-y-4">
        <div className="space-y-4">
            <FieldRow label="Giới tính">
                <SearchableSelect
                    value={value.gender}
                    onChange={(v) => onChange({ gender: v })}
                    options={GENDER_OPTIONS}
                />
            </FieldRow>
            <label className="flex items-center gap-2 cursor-pointer w-fit ml-[160px]">
                <input
                    type="checkbox"
                    checked={value.isPrimary}
                    onChange={(e) => onChange({ isPrimary: e.target.checked })}
                    className="w-4 h-4 accent-primary"
                />
                <span className="text-md text-text-main">Liên hệ chính</span>
            </label>
        </div>

        <div className="space-y-4">
            <FieldRow label="Ngày sinh">
                <input
                    type="date"
                    value={value.dateOfBirth}
                    onChange={(e) => onChange({ dateOfBirth: e.target.value })}
                    className={inputCls}
                />
            </FieldRow>
        </div>
    </div>
);
