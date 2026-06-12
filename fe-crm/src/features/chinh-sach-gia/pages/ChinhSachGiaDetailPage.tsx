import { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { FiArrowLeft, FiEdit2 } from 'react-icons/fi';
import { usePricePolicyDetail } from '../hooks/usePricePolicyDetail';
import { PricePolicyFormModal } from '../components/PricePolicyFormModal';
import { PolicyProductsTab } from '../components/tabs/PolicyProductsTab';
import { PolicyCustomersTab } from '../components/tabs/PolicyCustomersTab';
import { PolicyCustomerCategoriesTab } from '../components/tabs/PolicyCustomerCategoriesTab';
import { PolicyProductTypesTab } from '../components/tabs/PolicyProductTypesTab';
import { PolicyEmployeesTab } from '../components/tabs/PolicyEmployeesTab';
import type { PricePolicyStatus } from '../types/pricingTypes';

const STATUS_LABEL: Record<PricePolicyStatus, string> = {
    active: 'Đang áp dụng',
    inactive: 'Ngừng áp dụng',
    expired: 'Hết hạn',
};
const STATUS_CLASS: Record<PricePolicyStatus, string> = {
    active: 'bg-green-100 text-green-700',
    inactive: 'bg-gray-100 text-gray-600',
    expired: 'bg-red-100 text-red-600',
};

const TABS = [
    { key: 'products',            label: 'Sản phẩm' },
    { key: 'customers',           label: 'Khách hàng' },
    { key: 'customer-categories', label: 'Nhóm KH' },
    { key: 'product-types',       label: 'Loại sản phẩm' },
    { key: 'employees',           label: 'Nhân viên' },
] as const;

type TabKey = typeof TABS[number]['key'];

const ChinhSachGiaDetailPage = () => {
    const { id } = useParams<{ id: string }>();
    const policyId = Number(id);
    const navigate = useNavigate();
    const { data: policy, isLoading } = usePricePolicyDetail(policyId);
    const [activeTab, setActiveTab] = useState<TabKey>('products');
    const [editOpen, setEditOpen] = useState(false);

    if (isLoading) {
        return <div className="p-6 text-gray-400">Đang tải...</div>;
    }
    if (!policy) {
        return <div className="p-6 text-gray-500">Không tìm thấy chính sách giá.</div>;
    }

    return (
        <div className="p-6 bg-bg-main min-h-screen">
            <button
                onClick={() => navigate('/chinh-sach-gia')}
                className="flex items-center gap-1.5 text-sm text-gray-500 hover:text-primary mb-4"
            >
                <FiArrowLeft size={14} /> Quay lại danh sách
            </button>

            <div className="bg-white rounded-card shadow-sm p-5 mb-4">
                <div className="flex items-start justify-between">
                    <div className="space-y-1">
                        <div className="flex items-center gap-3">
                            <h1 className="text-xl font-semibold text-text-main">{policy.name}</h1>
                            <span className={`inline-block px-2 py-0.5 rounded text-sm font-medium ${STATUS_CLASS[policy.status]}`}>
                                {STATUS_LABEL[policy.status]}
                            </span>
                        </div>
                        <div className="text-sm text-gray-500 flex flex-wrap gap-4">
                            <span>Mã: <strong className="text-text-main">{policy.code}</strong></span>
                            {policy.type && <span>Loại: <strong className="text-text-main">{policy.type}</strong></span>}
                            {policy.priority != null && <span>Ưu tiên: <strong className="text-text-main">{policy.priority}</strong></span>}
                            {policy.startDate && <span>Từ: <strong className="text-text-main">{policy.startDate}</strong></span>}
                            {policy.endDate && <span>Đến: <strong className="text-text-main">{policy.endDate}</strong></span>}
                        </div>
                    </div>
                    <button
                        onClick={() => setEditOpen(true)}
                        className="flex items-center gap-1.5 px-3 py-1.5 rounded-btn border border-gray-300 text-sm text-gray-600 hover:bg-gray-50"
                    >
                        <FiEdit2 size={13} /> Chỉnh sửa
                    </button>
                </div>
            </div>

            <div className="bg-white rounded-card shadow-sm">
                <div className="flex border-b border-gray-200 px-4">
                    {TABS.map(tab => (
                        <button
                            key={tab.key}
                            onClick={() => setActiveTab(tab.key)}
                            className={`px-4 py-3 text-sm font-medium border-b-2 -mb-px transition-colors ${
                                activeTab === tab.key
                                    ? 'border-primary text-primary'
                                    : 'border-transparent text-gray-500 hover:text-text-main'
                            }`}
                        >
                            {tab.label}
                        </button>
                    ))}
                </div>
                <div className="p-4">
                    {activeTab === 'products'            && <PolicyProductsTab policyId={policyId} />}
                    {activeTab === 'customers'           && <PolicyCustomersTab policyId={policyId} />}
                    {activeTab === 'customer-categories' && <PolicyCustomerCategoriesTab policyId={policyId} />}
                    {activeTab === 'product-types'       && <PolicyProductTypesTab policyId={policyId} />}
                    {activeTab === 'employees'           && <PolicyEmployeesTab policyId={policyId} />}
                </div>
            </div>

            <PricePolicyFormModal item={policy} open={editOpen} onClose={() => setEditOpen(false)} />
        </div>
    );
};

export default ChinhSachGiaDetailPage;
