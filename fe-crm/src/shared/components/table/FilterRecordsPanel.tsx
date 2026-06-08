import { FiX, FiHelpCircle, FiPlus } from 'react-icons/fi';
import type { ColumnMeta, FilterCondition, FilterOperator } from '@/shared/types/table';

interface FilterRecordsPanelProps {
    columns: ColumnMeta[];
    conditions: FilterCondition[];
    onChange: (conditions: FilterCondition[]) => void;
    onClose: () => void;
}

const OPERATORS: { value: FilterOperator; label: string }[] = [
    { value: 'is',               label: 'is' },
    { value: 'is_not',           label: 'is not' },
    { value: 'contains',         label: 'contains' },
    { value: 'does_not_contain', label: 'does not contain' },
    { value: 'is_empty',         label: 'is empty' },
    { value: 'is_not_empty',     label: 'is not empty' },
];

const HIDE_VALUE_OPS: FilterOperator[] = ['is_empty', 'is_not_empty'];

/**
 * Panel lọc bản ghi theo điều kiện từng cột (AND logic).
 */
export const FilterRecordsPanel = ({
    columns,
    conditions,
    onChange,
    onClose,
}: FilterRecordsPanelProps) => {
    const dataCols = columns.filter((c) => c.id !== '__select__');

    const addCondition = () => {
        const newCond: FilterCondition = {
            id: crypto.randomUUID(),
            fieldId: dataCols[0]?.id ?? '',
            operator: 'contains',
            value: '',
        };
        onChange([...conditions, newCond]);
    };

    const updateCondition = (id: string, patch: Partial<FilterCondition>) => {
        onChange(conditions.map((c) => (c.id === id ? { ...c, ...patch } : c)));
    };

    const removeCondition = (id: string) => {
        onChange(conditions.filter((c) => c.id !== id));
    };

    return (
        <div className="absolute right-0 top-full mt-1 z-20 w-[480px] bg-white rounded-section border border-gray-200 shadow-lg">
            {/* Header */}
            <div className="flex items-center justify-between px-4 py-2.5 border-b border-gray-200">
                <span className="flex items-center gap-1.5 text-title font-semibold text-text-main">
                    Filter records
                    <FiHelpCircle size={13} className="text-gray-400" />
                </span>
                <button onClick={onClose} className="text-gray-400 hover:text-gray-600 transition-colors">
                    <FiX size={15} />
                </button>
            </div>

            {/* Conditions list */}
            <div className="px-4 py-3 space-y-2 max-h-72 overflow-y-auto">
                {conditions.length === 0 && (
                    <p className="text-table text-gray-400 text-center py-2">
                        Chưa có điều kiện nào. Nhấn "+ Add Condition" để thêm.
                    </p>
                )}
                {conditions.map((cond) => (
                    <div key={cond.id} className="flex items-center gap-2">
                        {/* Field selector */}
                        <select
                            value={cond.fieldId}
                            onChange={(e) => updateCondition(cond.id, { fieldId: e.target.value })}
                            className="text-table border border-gray-300 rounded-btn px-2 py-1.5 focus:outline-none focus:border-primary min-w-0 flex-shrink-0 w-36"
                        >
                            {dataCols.map((col) => (
                                <option key={col.id} value={col.id}>{col.header}</option>
                            ))}
                        </select>

                        {/* Operator selector */}
                        <select
                            value={cond.operator}
                            onChange={(e) => updateCondition(cond.id, { operator: e.target.value as FilterOperator, value: '' })}
                            className="text-table border border-gray-300 rounded-btn px-2 py-1.5 focus:outline-none focus:border-primary flex-shrink-0 w-36"
                        >
                            {OPERATORS.map((op) => (
                                <option key={op.value} value={op.value}>{op.label}</option>
                            ))}
                        </select>

                        {/* Value input */}
                        {!HIDE_VALUE_OPS.includes(cond.operator) && (
                            <input
                                type="text"
                                value={cond.value}
                                onChange={(e) => updateCondition(cond.id, { value: e.target.value })}
                                placeholder="Enter here"
                                className="text-table border border-gray-300 rounded-btn px-2.5 py-1.5 flex-1 min-w-0 focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary"
                            />
                        )}
                        {HIDE_VALUE_OPS.includes(cond.operator) && (
                            <div className="flex-1" />
                        )}

                        {/* Remove */}
                        <button
                            onClick={() => removeCondition(cond.id)}
                            className="text-gray-400 hover:text-danger flex-shrink-0 transition-colors"
                        >
                            <FiX size={15} />
                        </button>
                    </div>
                ))}
            </div>

            {/* Footer */}
            <div className="px-4 py-2.5 border-t border-gray-100">
                <button
                    onClick={addCondition}
                    className="flex items-center gap-1 text-table text-primary hover:underline"
                >
                    <FiPlus size={13} />
                    Add Condition
                </button>
            </div>
        </div>
    );
};
