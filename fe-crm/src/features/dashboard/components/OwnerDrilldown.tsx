import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useLiveQuery } from '@/core/data/useLiveQuery';
import { opportunityService } from '@/features/co-hoi/services/opportunityService';
import { recordPath } from '@/shared/utils/moduleRoutes';
import { formatCurrency } from '@/shared/utils/number';
import { ScrollFrame } from '@/shared/components/table/ScrollFrame';
import { SearchableSelect } from '@/shared/components/SearchableSelect';
import type { EmployeeWinRateRow } from '@/features/dashboard/types/dashboardTypes';

type Tab = 'open' | 'lost';

interface OwnerDrilldownProps {
    privileged: boolean;
    // dùng chính user đang đăng nhập khi không phải quản lý (BE tự ép, FE không cần truyền ownerId)
    employees: EmployeeWinRateRow[];
    employeesLoading: boolean;
}

/**
 * E25 — chọn 1 nhân viên (chỉ quản lý) → xem danh sách cơ hội Đang mở/Đã thua kèm giai đoạn + lý do.
 * Với sale: ẩn ô chọn, luôn xem của chính họ (BE tự ép ownerId về chính họ bất kể truyền gì).
 */
export const OwnerDrilldown = ({ privileged, employees, employeesLoading }: OwnerDrilldownProps) => {
    const navigate = useNavigate();
    const [ownerId, setOwnerId] = useState<string>('');
    const [tab, setTab] = useState<Tab>('open');

    // chọn sẵn nhân viên đầu danh sách khi dữ liệu xếp hạng vừa tải xong
    useEffect(() => {
        if (privileged && !ownerId && employees.length > 0) {
            setOwnerId(String(employees[0].userId));
        }
    }, [privileged, employees, ownerId]);

    const enabled = privileged ? ownerId !== '' : true;
    // phạm vi đang xem — dùng làm phần biến đổi của key để tự tải lại khi đổi nhân viên/tab
    const scope = privileged ? ownerId : 'self';
    const { data, isLoading } = useLiveQuery(
        `dashboard-owner-drilldown:${scope}:${tab}`,
        () =>
            opportunityService
                .getList({
                    ownerId: privileged && ownerId ? Number(ownerId) : undefined,
                    status: tab,
                    page: 0,
                    size: 20,
                    sortBy: 'updatedAt',
                    sortDir: 'desc',
                })
                .then((r) => r.data.data.items),
        enabled,
    );

    return (
        <div>
            {privileged && (
                <div className="mb-3 max-w-xs">
                    <SearchableSelect
                        options={employees.map((e) => ({ value: String(e.userId), label: e.fullName }))}
                        value={ownerId}
                        onChange={setOwnerId}
                        placeholder={employeesLoading ? 'Đang tải…' : 'Chọn nhân viên'}
                    />
                </div>
            )}

            <div className="flex gap-1 mb-3 border-b border-gray-100">
                {(['open', 'lost'] as Tab[]).map((t) => (
                    <button
                        key={t}
                        type="button"
                        onClick={() => setTab(t)}
                        className={`px-3 py-1.5 text-sm border-b-2 -mb-px ${
                            tab === t ? 'border-primary text-primary font-medium' : 'border-transparent text-gray-500 hover:text-text-main'
                        }`}
                    >
                        {t === 'open' ? 'Đang mở' : 'Đã thua'}
                    </button>
                ))}
            </div>

            {isLoading && <p className="text-sm text-gray-400 py-8 text-center">Đang tải…</p>}
            {!isLoading && (!data || data.length === 0) && (
                <p className="text-sm text-gray-400 py-8 text-center">Không có cơ hội nào</p>
            )}
            {!isLoading && data && data.length > 0 && (
                <ScrollFrame visibleRows={8}>
                    <table className="w-full text-table">
                        <thead>
                            <tr className="text-left text-sm text-gray-500">
                                <th className="py-2 px-3 font-medium">Tên cơ hội</th>
                                <th className="py-2 px-3 font-medium">Giai đoạn</th>
                                {tab === 'lost' && <th className="py-2 px-3 font-medium">Lý do thua</th>}
                                <th className="py-2 px-3 font-medium text-right">Giá trị</th>
                            </tr>
                        </thead>
                        <tbody>
                            {data.map((row) => (
                                <tr
                                    key={row.id}
                                    onClick={() => navigate(recordPath('opportunity', row.id))}
                                    className="border-b border-gray-100 last:border-0 hover:bg-blue-50 cursor-pointer"
                                >
                                    <td className="py-2 px-3 text-text-main">{row.name}</td>
                                    <td className="py-2 px-3 text-text-main">{row.stageName ?? '—'}</td>
                                    {tab === 'lost' && <td className="py-2 px-3 text-text-main">{row.winLossReason ?? '—'}</td>}
                                    <td className="py-2 px-3 text-text-main text-right">{formatCurrency(row.amount ?? 0)}</td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </ScrollFrame>
            )}
        </div>
    );
};

