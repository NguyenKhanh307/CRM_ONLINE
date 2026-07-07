import type { DashboardPeriod } from '../types/dashboardTypes';
import type { MoneyUnit } from '@/shared/utils/number';

const SELECT_CLS = 'text-sm border border-gray-200 rounded-btn px-2.5 py-1.5 bg-white text-text-main focus:outline-none focus:border-primary';

/** Nhãn kỳ tiếng Việt. */
export const PERIOD_LABEL: Record<DashboardPeriod, string> = {
    month: 'Tháng này',
    quarter: 'Quý này',
    year: 'Năm nay',
};

interface PeriodProps {
    value: DashboardPeriod;
    onChange: (v: DashboardPeriod) => void;
}

/** Bộ chọn kỳ thời gian cấp trang. */
export const PeriodSelect = ({ value, onChange }: PeriodProps) => (
    <select className={SELECT_CLS} value={value} onChange={(e) => onChange(e.target.value as DashboardPeriod)}>
        <option value="month">Tháng này</option>
        <option value="quarter">Quý này</option>
        <option value="year">Năm nay</option>
    </select>
);

interface UnitProps {
    value: MoneyUnit;
    onChange: (v: MoneyUnit) => void;
}

/** Bộ chọn đơn vị hiển thị tiền tệ. */
export const UnitSelect = ({ value, onChange }: UnitProps) => (
    <select className={SELECT_CLS} value={value} onChange={(e) => onChange(e.target.value as MoneyUnit)}>
        <option value="vnd">VND</option>
        <option value="million">Triệu đồng</option>
    </select>
);
