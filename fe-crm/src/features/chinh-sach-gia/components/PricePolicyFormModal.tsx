import { useState, useRef, type FormEvent, useEffect } from 'react';
import { FiX } from 'react-icons/fi';
import type { PricePolicyResult, CreatePricePolicyPayload, UpdatePricePolicyPayload, PricePolicyStatus } from '../types/pricingTypes';
import { useCreatePricePolicy } from '../hooks/useCreatePricePolicy';
import { useUpdatePricePolicy } from '../hooks/useUpdatePricePolicy';
import { DateInput } from '@/shared/components/form/DateInput';
import { FormField } from '@/shared/components/form/FormField';
import { ModalFooter } from '@/shared/components/ModalFooter';
import { useConfirm } from '@/shared/confirm/useConfirm';
import { useFormKeyboardNav } from '@/shared/keyboard/useFormKeyboardNav';
import { collectErrors, dateRangeError, nonNegativeError } from '@/shared/utils/validators';

interface Props {
    item: PricePolicyResult | null;
    open: boolean;
    onClose: () => void;
}

const EMPTY: CreatePricePolicyPayload = {
    code: '', name: '', type: null, priority: null,
    startDate: null, endDate: null, status: 'active',
};

export function PricePolicyFormModal({ item, open, onClose }: Props) {
    const { mutate: createFn, isPending: isCreating } = useCreatePricePolicy();
    const { mutate: updateFn, isPending: isUpdating } = useUpdatePricePolicy();
    const isPending = isCreating || isUpdating;

    const [form, setForm] = useState<CreatePricePolicyPayload>(EMPTY);
    const [errors, setErrors] = useState<Record<string, string>>({});

    const { confirmCreate, confirmSave } = useConfirm();

    const formRef = useRef<HTMLFormElement>(null);
    // enabled: open — modal render rỗng khi đóng, thiếu cờ này thì listener không bao giờ gắn.
    useFormKeyboardNav(formRef, {
        onSubmit: () => formRef.current?.requestSubmit(),
        onCancel: onClose,
        enabled: open,
    });

    useEffect(() => {
        if (!open) return;
        setErrors({});
        if (item) {
            setForm({
                code: item.code,
                name: item.name,
                type: item.type,
                priority: item.priority,
                startDate: item.startDate,
                endDate: item.endDate,
                status: item.status,
            });
        } else {
            setForm(EMPTY);
        }
    }, [open, item]);

    if (!open) return null;

    const clearError = (key: string) =>
        setErrors((prev) => (prev[key] ? { ...prev, [key]: '' } : prev));

    const handleSubmit = async (e: FormEvent) => {
        e.preventDefault();

        const found = collectErrors({
            code: !item && !form.code.trim() ? 'Vui lòng nhập mã chính sách' : null,
            name: !form.name.trim() ? 'Vui lòng nhập tên chính sách' : null,
            priority: nonNegativeError(form.priority, 'Độ ưu tiên'),
            endDate: dateRangeError(form.startDate, form.endDate),
        });
        setErrors(found);
        if (Object.keys(found).length > 0) return;

        const ok = item ? await confirmSave('chính sách giá') : await confirmCreate('chính sách giá');
        if (!ok) return;

        if (item) {
            const payload: UpdatePricePolicyPayload = {
                name: form.name, type: form.type, priority: form.priority,
                startDate: form.startDate, endDate: form.endDate, status: form.status,
            };
            updateFn({ id: item.id, payload }, { onSuccess: onClose });
        } else {
            createFn(form, { onSuccess: onClose });
        }
    };

    const inp = 'w-full border border-gray-300 rounded-btn px-3 py-1.5 text-md text-text-main focus:outline-none focus:border-primary';

    return (
        <div className="fixed inset-0 z-[9999] flex items-center justify-center bg-black/40" onClick={onClose}>
            <div className="bg-white rounded-card shadow-lg w-full max-w-lg mx-4 max-h-[90vh] overflow-y-auto" onClick={e => e.stopPropagation()}>
                <div className="flex items-center justify-between px-5 py-4 border-b border-gray-200">
                    <h2 className="text-lg font-semibold text-text-main">
                        {item ? 'Chỉnh sửa chính sách giá' : 'Tạo chính sách giá'}
                    </h2>
                    <button onClick={onClose} className="p-1 rounded hover:bg-gray-100 text-gray-500"><FiX size={18} /></button>
                </div>
                <form ref={formRef} onSubmit={handleSubmit} noValidate className="px-5 py-4 space-y-3">
                    <FormField label="Mã chính sách" required error={errors.code}>
                        <input
                            className={inp} maxLength={20}
                            value={form.code}
                            onChange={e => { setForm(f => ({ ...f, code: e.target.value })); clearError('code'); }}
                            disabled={!!item}
                        />
                    </FormField>
                    <FormField label="Tên chính sách" required error={errors.name}>
                        <input
                            className={inp} maxLength={40}
                            value={form.name}
                            onChange={e => { setForm(f => ({ ...f, name: e.target.value })); clearError('name'); }}
                        />
                    </FormField>
                    <div className="grid grid-cols-2 gap-3">
                        <FormField label="Loại">
                            <input
                                className={inp} maxLength={20}
                                value={form.type ?? ''}
                                onChange={e => setForm(f => ({ ...f, type: e.target.value || null }))}
                            />
                        </FormField>
                        <FormField label="Độ ưu tiên" error={errors.priority}>
                            <input
                                className={inp} type="number" min={0}
                                value={form.priority ?? ''}
                                onChange={e => {
                                    setForm(f => ({ ...f, priority: e.target.value ? Number(e.target.value) : null }));
                                    clearError('priority');
                                }}
                            />
                        </FormField>
                    </div>
                    <div className="grid grid-cols-2 gap-3">
                        <FormField label="Ngày bắt đầu">
                            <DateInput
                                value={form.startDate ?? ''}
                                onChange={v => { setForm(f => ({ ...f, startDate: v || null })); clearError('endDate'); }}
                            />
                        </FormField>
                        <FormField label="Ngày kết thúc" error={errors.endDate}>
                            <DateInput
                                value={form.endDate ?? ''}
                                onChange={v => { setForm(f => ({ ...f, endDate: v || null })); clearError('endDate'); }}
                            />
                        </FormField>
                    </div>
                    <FormField label="Trạng thái">
                        <select
                            className={inp}
                            value={form.status}
                            onChange={e => setForm(f => ({ ...f, status: e.target.value as PricePolicyStatus }))}
                        >
                            <option value="active">Đang áp dụng</option>
                            <option value="inactive">Ngừng áp dụng</option>
                            <option value="expired">Hết hạn</option>
                        </select>
                    </FormField>
                    <ModalFooter onCancel={onClose} saving={isPending} />
                </form>
            </div>
        </div>
    );
}
