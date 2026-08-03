import { useMemo, useState } from 'react';
import { PageHeaderSlot } from '@/shared/components/layout/PageHeaderSlot';
import { DataTable } from '@/shared/components/table/DataTable';
import { useAuditLogList } from '../hooks/useAuditLogList';
import { auditLogColumns } from '../config/auditLogColumns';
import { AUDIT_SOURCE_TABS } from '../types/auditLogTypes';

// trang admin xem nhật ký sự kiện thao tác người dùng — gộp từ các bảng đã có sẵn trong DB
// (duyệt báo giá, bàn giao tiềm năng, nhật ký phiếu chăm sóc, thông báo hệ thống, tạo/sửa/xóa
// bản ghi); chỉ đọc, không thêm bảng/field DB nào
const NhatKyHeThongPage = () => {
    const [activeSource, setActiveSource] = useState<string | null>(null);
    const [search, setSearch] = useState('');
    const [page, setPage] = useState(0);
    const [pageSize, setPageSize] = useState(20);

    const { data: pageData, isLoading } = useAuditLogList({
        source: activeSource, q: search, page, size: pageSize,
    });

    // DataTable cần trường id ổn định cho mỗi dòng — nhật ký không có khóa riêng nên đánh số theo trang.
    const data = useMemo(
        () => (pageData?.items ?? []).map((e, i) => ({ ...e, id: page * pageSize + i })),
        [pageData, page, pageSize],
    );
    const total = pageData?.total ?? 0;

    return (
        <div className="p-6 bg-bg-main">
            <PageHeaderSlot>
                <h1 className="text-lg font-semibold text-text-main truncate">Nhật ký hệ thống</h1>
            </PageHeaderSlot>

            <div className="flex items-center gap-1 mb-4 border-b border-gray-200">
                {AUDIT_SOURCE_TABS.map(t => (
                    <button
                        key={t.id ?? 'all'}
                        onClick={() => { setActiveSource(t.id); setPage(0); }}
                        className={`px-4 py-2 text-md font-medium transition-colors border-b-2 -mb-px ${
                            activeSource === t.id
                                ? 'border-primary text-primary'
                                : 'border-transparent text-gray-500 hover:text-gray-700'
                        }`}
                    >
                        {t.label}
                    </button>
                ))}
            </div>

            <div className="bg-white rounded-card p-4 shadow-sm">
                <DataTable
                    data={data}
                    columns={auditLogColumns}
                    isLoading={isLoading}
                    emptyText="Chưa có sự kiện nào"
                    server={{
                        totalRows: total,
                        pageIndex: page,
                        pageSize,
                        onPageChange: setPage,
                        onPageSizeChange: setPageSize,
                        searchValue: search,
                        onSearchChange: (v) => { setSearch(v); setPage(0); },
                    }}
                />
            </div>
        </div>
    );
};

export default NhatKyHeThongPage;
