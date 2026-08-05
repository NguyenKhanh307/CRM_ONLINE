import { useMemo, useRef, useState } from 'react';
import { useConfirm } from '@/shared/confirm/useConfirm';
import { collectErrors } from '@/shared/utils/validators';
import { useNavigate } from 'react-router-dom';
import { useFormKeyboardNav } from '@/shared/keyboard/useFormKeyboardNav';
import { SearchableSelect } from '@/shared/components/SearchableSelect';
import { FormPageHeader } from '@/shared/components/form/FormPageHeader';
import { FormSection } from '@/shared/components/form/FormSection';
import { FieldRow } from '@/shared/components/form/FieldRow';
import { DerivedContextBox } from '@/shared/components/form/DerivedContextBox';
import { RecordPicker } from '@/shared/components/form/RecordPicker';
import { orderService } from '@/features/don-hang/services/orderService';
import { quotationService } from '@/features/bao-gia/services/quotationService';
import type { QuotationResult } from '@/features/bao-gia/types/quotationTypes';
import { inputCls } from '@/shared/components/form/formStyles';
import { useAlert } from '@/shared/alert/useAlert';
import { useAuth } from '@/core/auth/useAuth';
import { useActiveUsers } from '@/features/users/hooks/useActiveUsers';
import { useCreateTicket } from '../hooks/useCreateTicket';
import { ReturnItemsTable, type ReturnRow, emptyReturnRow, toReturnItemPayloads } from '../components/ReturnItemsTable';
import { TYPE_OPTIONS, CHANNEL_OPTIONS, PRIORITY_OPTIONS, REASON_OPTIONS } from '../config/ticketEnums';
import type { CreateTicketPayload, TicketType, TicketChannel, TicketPriority, ReturnReason } from '../types/ticketTypes';

interface HeaderState {
    code: string; type: string; subject: string; priority: string; channel: string; reason: string;
    orderId: string; assignedUserId: string; description: string;
}

const initialState = (assignedUserId: string): HeaderState => ({
    code: '', type: 'support', subject: '', priority: 'medium', channel: 'web', reason: '',
    orderId: '', assignedUserId, description: '',
});

// trang thêm phiếu hỗ trợ mới — hiện bảng dòng hàng trả/đổi khi loại là trả/đổi. Phiếu chỉ còn
// 1 khóa ngoại chính (orderId) — khách hàng/liên hệ tra qua chuỗi đơn hàng -> báo giá, hiển thị read-only
const TicketAddPage = () => {
    const navigate = useNavigate();
    const { showAlert } = useAlert();
    const { confirmCreate } = useConfirm();
    const { user } = useAuth();
    const defaultUserId = user ? String(user.id) : '';
    const [form, setForm] = useState<HeaderState>(() => initialState(defaultUserId));
    const [returnRows, setReturnRows] = useState<ReturnRow[]>([emptyReturnRow()]);
    const { mutate, isPending } = useCreateTicket();
    const [quotation, setQuotation] = useState<QuotationResult | null>(null);

    const { data: users = [] } = useActiveUsers();

    const userOptions = useMemo(() => users.map((u) => ({ value: String(u.id), label: u.fullName })), [users]);

    const isReturn = form.type === 'return' || form.type === 'exchange';
    const [errors, setErrors] = useState<Record<string, string>>({});

    // hàm cập nhật form và xóa lỗi của đúng những field vừa gõ
    const set = (patch: Partial<HeaderState>) => {
        setForm((p) => ({ ...p, ...patch }));
        setErrors((e) => {
            const next = { ...e };
            Object.keys(patch).forEach((k) => delete next[k]);
            return next;
        });
    };

    // hàm chọn đơn hàng -> fetch chuỗi order -> quotation để hiển thị khách hàng/liên hệ read-only
    const onPickOrder = async (v: string) => {
        set({ orderId: v });
        setQuotation(null);
        if (!v) return;
        const order = (await orderService.getById(Number(v))).data.data;
        if (order.quotationId != null) {
            const q = await quotationService.getById(order.quotationId);
            setQuotation(q.data.data);
        }
    };

    // hàm kiểm tra bắt buộc + biên (khớp ràng buộc backend) — trả map field->lỗi
    const validate = (): Record<string, string> =>
        collectErrors({
            code: !form.code.trim() ? 'Mã phiếu không được để trống' : null,
            subject: !form.subject.trim() ? 'Tiêu đề không được để trống' : null,
        });

    // hàm lưu — lỗi hiện đỏ dưới ô, popup xác nhận chỉ mở khi dữ liệu đã hợp lệ
    const submit = async () => {
        // bước kiểm tra dữ liệu
        const errs = validate();
        setErrors(errs);
        if (Object.keys(errs).length > 0) return;

        const payload: CreateTicketPayload = {
            code: form.code.trim(),
            type: form.type as TicketType,
            subject: form.subject.trim(),
            description: form.description || null,
            orderId: form.orderId ? Number(form.orderId) : null,
            channel: form.channel as TicketChannel,
            priority: form.priority as TicketPriority,
            reason: (form.reason || null) as ReturnReason | null,
            assignedUserId: form.assignedUserId ? Number(form.assignedUserId) : null,
            returnItems: isReturn ? toReturnItemPayloads(returnRows) : [],
        };
        // bước hỏi xác nhận rồi mới gọi api lưu
        if (!(await confirmCreate('phiếu hỗ trợ'))) return;
        mutate(payload, {
            onSuccess: () => navigate('/cham-soc'),
            onError: (err: unknown) => showAlert((err as { response?: { data?: { message?: string } } })?.response?.data?.message ?? 'Có lỗi xảy ra khi lưu phiếu'),
        });
    };

    const formRef = useRef<HTMLDivElement>(null);
    useFormKeyboardNav(formRef, { onSubmit: () => submit() });

    return (
        <div ref={formRef} className="p-6 bg-bg-main min-h-[calc(100vh-50px)]">
            <FormPageHeader title="Thêm phiếu hỗ trợ" saving={isPending}
                onCancel={() => navigate(-1)} onSave={() => submit()} />

            <div className="bg-white rounded-card shadow-sm p-6 space-y-8">
                <FormSection title="Thông tin phiếu">
                    <div className="grid grid-cols-2 gap-x-10 gap-y-4">
                        <div className="space-y-4">
                            <FieldRow label="Mã phiếu" required error={errors.code}>
                                <input type="text" value={form.code} onChange={(e) => set({ code: e.target.value })} className={inputCls} />
                            </FieldRow>
                            <FieldRow label="Tiêu đề" required error={errors.subject}>
                                <input type="text" value={form.subject} onChange={(e) => set({ subject: e.target.value })} className={inputCls} />
                            </FieldRow>
                            <FieldRow label="Loại yêu cầu">
                                <SearchableSelect value={form.type} onChange={(v) => set({ type: v })} options={TYPE_OPTIONS} />
                            </FieldRow>
                            <FieldRow label="Độ ưu tiên">
                                <SearchableSelect value={form.priority} onChange={(v) => set({ priority: v })} options={PRIORITY_OPTIONS} />
                            </FieldRow>
                            <FieldRow label="Kênh tiếp nhận">
                                <SearchableSelect value={form.channel} onChange={(v) => set({ channel: v })} options={CHANNEL_OPTIONS} />
                            </FieldRow>
                            <FieldRow label="Lý do">
                                <SearchableSelect value={form.reason} onChange={(v) => set({ reason: v })} options={REASON_OPTIONS} />
                            </FieldRow>
                        </div>
                        <div className="space-y-4">
                            <FieldRow label="Đơn hàng">
                                <RecordPicker module="order" value={form.orderId} onChange={onPickOrder} />
                            </FieldRow>
                            <DerivedContextBox rows={[
                                { label: 'Khách hàng', value: quotation?.customerName },
                                { label: 'Liên hệ', value: quotation?.contactName },
                            ]} />
                            <FieldRow label="Người xử lý">
                                <SearchableSelect value={form.assignedUserId} onChange={(v) => set({ assignedUserId: v })} options={userOptions} />
                            </FieldRow>
                        </div>
                    </div>
                </FormSection>

                {isReturn && (
                    <FormSection title="Hàng trả / đổi">
                        <ReturnItemsTable rows={returnRows} onChange={setReturnRows} />
                    </FormSection>
                )}

                <FormSection title="Mô tả">
                    <FieldRow label="Nội dung" alignTop>
                        <textarea rows={3} value={form.description} onChange={(e) => set({ description: e.target.value })} className={`${inputCls} resize-none`} />
                    </FieldRow>
                </FormSection>
            </div>
        </div>
    );
};

export default TicketAddPage;
