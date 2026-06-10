import { useState, type FormEvent, useEffect } from 'react';
import { FiX } from 'react-icons/fi';
import type { OrderResult, UpdateOrderPayload } from '../types/orderTypes';
import { useUpdateOrder } from '../hooks/useUpdateOrder';

interface Props {
    item: OrderResult | null;
    onClose: () => void;
}

const ORDER_STATUSES = ['draft', 'confirmed', 'processing', 'shipped', 'delivered', 'cancelled'];
const ORDER_STATUS_LABELS: Record<string, string> = {
    draft: 'Nháp', confirmed: 'Đã xác nhận', processing: 'Đang xử lý',
    shipped: 'Đã giao', delivered: 'Hoàn thành', cancelled: 'Đã hủy',
};
const PAYMENT_STATUSES = ['unpaid', 'partial', 'paid'];
const PAYMENT_STATUS_LABELS: Record<string, string> = { unpaid: 'Chưa thanh toán', partial: 'Một phần', paid: 'Đã thanh toán' };
const ORDER_TYPES = ['sale', 'return'];
const ORDER_TYPE_LABELS: Record<string, string> = { sale: 'Bán hàng', return: 'Trả hàng' };

export function OrderEditModal({ item, onClose }: Props) {
    const { mutate, isPending } = useUpdateOrder();
    const [form, setForm] = useState<UpdateOrderPayload>({
        customerId: null, contactId: null, ownerId: null, warehouseId: null,
        orderType: 'sale', orderDate: null, status: 'draft', paymentStatus: 'unpaid',
        subtotal: null, discount: null, tax: null, total: null, note: null,
    });

    useEffect(() => {
        if (!item) return;
        setForm({
            customerId: item.customerId, contactId: item.contactId, ownerId: item.ownerId,
            warehouseId: item.warehouseId, orderType: item.orderType, orderDate: item.orderDate,
            status: item.status, paymentStatus: item.paymentStatus, subtotal: item.subtotal,
            discount: item.discount, tax: item.tax, total: item.total, note: item.note,
        });
    }, [item]);

    if (!item) return null;

    const handleSubmit = (e: FormEvent) => {
        e.preventDefault();
        mutate({ id: item.id, payload: form }, { onSuccess: onClose });
    };

    const inp = 'w-full border border-gray-300 rounded-btn px-3 py-1.5 text-md text-text-main focus:outline-none focus:border-primary';
    const lbl = 'block text-sm font-medium text-gray-700 mb-1';

    return (
        <div className="fixed inset-0 z-[9999] flex items-center justify-center bg-black/40" onClick={onClose}>
            <div className="bg-white rounded-card shadow-lg w-full max-w-lg mx-4 max-h-[90vh] overflow-y-auto" onClick={(e) => e.stopPropagation()}>
                <div className="flex items-center justify-between px-5 py-4 border-b border-gray-200">
                    <h2 className="text-lg font-semibold text-text-main">Chỉnh sửa đơn hàng</h2>
                    <button onClick={onClose} className="p-1 rounded hover:bg-gray-100 text-gray-500"><FiX size={18} /></button>
                </div>
                <form onSubmit={handleSubmit} className="px-5 py-4 space-y-3">
                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <label className={lbl}>Loại đơn</label>
                            <select className={inp} value={form.orderType} onChange={e => setForm(f => ({ ...f, orderType: e.target.value }))}>
                                {ORDER_TYPES.map(t => <option key={t} value={t}>{ORDER_TYPE_LABELS[t]}</option>)}
                            </select>
                        </div>
                        <div>
                            <label className={lbl}>Ngày đặt hàng</label>
                            <input type="date" className={inp} value={form.orderDate ?? ''} onChange={e => setForm(f => ({ ...f, orderDate: e.target.value || null }))} />
                        </div>
                    </div>
                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <label className={lbl}>Trạng thái đơn</label>
                            <select className={inp} value={form.status} onChange={e => setForm(f => ({ ...f, status: e.target.value }))}>
                                {ORDER_STATUSES.map(s => <option key={s} value={s}>{ORDER_STATUS_LABELS[s]}</option>)}
                            </select>
                        </div>
                        <div>
                            <label className={lbl}>Trạng thái thanh toán</label>
                            <select className={inp} value={form.paymentStatus} onChange={e => setForm(f => ({ ...f, paymentStatus: e.target.value }))}>
                                {PAYMENT_STATUSES.map(s => <option key={s} value={s}>{PAYMENT_STATUS_LABELS[s]}</option>)}
                            </select>
                        </div>
                    </div>
                    <div className="grid grid-cols-3 gap-3">
                        <div>
                            <label className={lbl}>Chiết khấu</label>
                            <input type="number" className={inp} value={form.discount ?? ''} onChange={e => setForm(f => ({ ...f, discount: e.target.value ? +e.target.value : null }))} />
                        </div>
                        <div>
                            <label className={lbl}>Thuế</label>
                            <input type="number" className={inp} value={form.tax ?? ''} onChange={e => setForm(f => ({ ...f, tax: e.target.value ? +e.target.value : null }))} />
                        </div>
                        <div>
                            <label className={lbl}>Tổng cộng</label>
                            <input type="number" className={inp} value={form.total ?? ''} onChange={e => setForm(f => ({ ...f, total: e.target.value ? +e.target.value : null }))} />
                        </div>
                    </div>
                    <div>
                        <label className={lbl}>Ghi chú</label>
                        <textarea className={inp} rows={2} value={form.note ?? ''} onChange={e => setForm(f => ({ ...f, note: e.target.value || null }))} />
                    </div>
                    <div className="flex justify-end gap-3 pt-2 border-t border-gray-100">
                        <button type="button" onClick={onClose} className="px-4 py-1.5 rounded-btn border border-gray-300 text-md text-text-main hover:bg-gray-50">Hủy</button>
                        <button type="submit" disabled={isPending} className="px-4 py-1.5 rounded-btn bg-primary text-white text-md hover:opacity-90 disabled:opacity-50">
                            {isPending ? 'Đang lưu...' : 'Lưu'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}
