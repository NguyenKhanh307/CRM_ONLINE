import { FiX, FiHelpCircle, FiPlus } from 'react-icons/fi';
import type { ColumnMeta, ConditionalRule, FilterOperator } from '@/shared/types/table';
import { OPERATOR_OPTIONS, HIDE_VALUE_OPERATORS } from './filterConditions.helpers';

interface ConditionalColoringPanelProps {
    columns: ColumnMeta[];
    rules: ConditionalRule[];
    onChange: (rules: ConditionalRule[]) => void;
    onClose: () => void;
}

const DEFAULT_COLORS = ['#fadb14', '#ff7875', '#95de64', '#69b1ff', '#b37feb', '#ff9c6e'];

/**
 * Panel tô màu có điều kiện cho cell hoặc row.
 * Thay đổi áp dụng ngay.
 */
export const ConditionalColoringPanel = ({
    columns,
    rules,
    onChange,
    onClose,
}: ConditionalColoringPanelProps) => {
    const dataCols = columns.filter((c) => c.id !== '__select__');

    const addRule = () => {
        const colorIdx = rules.length % DEFAULT_COLORS.length;
        const newRule: ConditionalRule = {
            id: crypto.randomUUID(),
            color: DEFAULT_COLORS[colorIdx],
            scope: 'cell',
            fieldId: dataCols[0]?.id ?? '',
            operator: 'contains',
            value: '',
        };
        onChange([...rules, newRule]);
    };

    const updateRule = (id: string, patch: Partial<ConditionalRule>) => {
        onChange(rules.map((r) => (r.id === id ? { ...r, ...patch } : r)));
    };

    const removeRule = (id: string) => {
        onChange(rules.filter((r) => r.id !== id));
    };

    return (
        <div className="absolute right-0 top-full mt-1 z-20 w-[600px] bg-white rounded-section border border-gray-200 shadow-lg">
            {/* Header */}
            <div className="flex items-center justify-between px-4 py-2.5 border-b border-gray-200">
                <span className="flex items-center gap-1.5 text-title font-semibold text-text-main">
                    Tô màu có điều kiện
                    <FiHelpCircle size={13} className="text-gray-400" />
                </span>
                <div className="flex items-center gap-3">
                    <span className="flex items-center gap-1 text-sm text-gray-400 cursor-default select-none">
                        ✎ Tô màu thông minh cho tất cả
                        <FiHelpCircle size={12} className="text-gray-400" />
                    </span>
                    <button onClick={onClose} className="text-gray-400 hover:text-gray-600 transition-colors">
                        <FiX size={15} />
                    </button>
                </div>
            </div>

            {/* Rules list */}
            <div className="px-4 py-3 space-y-2 max-h-72 overflow-y-auto">
                {rules.length === 0 && (
                    <p className="text-table text-gray-400 text-center py-2">
                        Chưa có quy tắc nào. Nhấn "+ Thêm quy tắc" để thêm.
                    </p>
                )}
                {rules.map((rule) => (
                    <div key={rule.id} className="flex items-center gap-2">
                        <span className="text-gray-300 text-sm select-none">⠿</span>

                        {/* Color picker */}
                        <label
                            className="w-7 h-7 rounded border border-gray-300 cursor-pointer flex-shrink-0 overflow-hidden"
                            style={{ backgroundColor: rule.color }}
                            title="Chọn màu"
                        >
                            <input
                                type="color"
                                value={rule.color}
                                onChange={(e) => updateRule(rule.id, { color: e.target.value })}
                                className="opacity-0 w-0 h-0"
                            />
                        </label>

                        {/* Cell / Row scope */}
                        <select
                            value={rule.scope}
                            onChange={(e) => updateRule(rule.id, { scope: e.target.value as 'cell' | 'row' })}
                            className="text-table border border-gray-300 rounded-btn px-2 py-1.5 focus:outline-none focus:border-primary flex-shrink-0 w-20"
                        >
                            <option value="cell">Ô</option>
                            <option value="row">Hàng</option>
                        </select>

                        {/* Field selector */}
                        <select
                            value={rule.fieldId}
                            onChange={(e) => updateRule(rule.id, { fieldId: e.target.value })}
                            className="text-table border border-gray-300 rounded-btn px-2 py-1.5 focus:outline-none focus:border-primary flex-shrink-0 w-32"
                        >
                            {dataCols.map((col) => (
                                <option key={col.id} value={col.id}>{col.header}</option>
                            ))}
                        </select>

                        {/* Operator selector */}
                        <select
                            value={rule.operator}
                            onChange={(e) => updateRule(rule.id, { operator: e.target.value as FilterOperator, value: '' })}
                            className="text-table border border-gray-300 rounded-btn px-2 py-1.5 focus:outline-none focus:border-primary flex-shrink-0 w-36"
                        >
                            {OPERATOR_OPTIONS.map((op) => (
                                <option key={op.value} value={op.value}>{op.label}</option>
                            ))}
                        </select>

                        {/* Value input */}
                        {!HIDE_VALUE_OPERATORS.includes(rule.operator) ? (
                            <input
                                type="text"
                                value={rule.value}
                                onChange={(e) => updateRule(rule.id, { value: e.target.value })}
                                placeholder="Nhập giá trị"
                                className="text-table border border-gray-300 rounded-btn px-2.5 py-1.5 flex-1 min-w-0 focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary"
                            />
                        ) : (
                            <div className="flex-1" />
                        )}

                        {/* Remove */}
                        <button
                            onClick={() => removeRule(rule.id)}
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
                    onClick={addRule}
                    className="flex items-center gap-1 text-table text-primary hover:underline"
                >
                    <FiPlus size={13} />
                    Thêm quy tắc
                </button>
            </div>
        </div>
    );
};
