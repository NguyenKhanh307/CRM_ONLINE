import { useState, useMemo } from 'react';
import type { ImportField, ColumnMapping, ParsedFileData } from './importTypes';

interface Props {
    fileData: ParsedFileData;
    fields: ImportField[];
    mappings: ColumnMapping[];
    onMappingsChange: (m: ColumnMapping[]) => void;
}

type TabKey = 'all' | 'mapped' | 'unmapped';

// bỏ dấu tiếng Việt để so khớp tên cột không phân biệt dấu
function stripDiacritics(s: string) {
    return s.normalize('NFD').replace(/[̀-ͯ]/g, '').toLowerCase().trim();
}

// tự ghép cột file với field CRM theo nhãn (bỏ dấu, không phân biệt hoa thường)
function autoMap(headers: string[], fields: ImportField[]): ColumnMapping[] {
    return headers.map(h => {
        const match = fields.find(f => stripDiacritics(f.label) === stripDiacritics(h));
        return { fileColumn: h, fieldKey: match?.key ?? null };
    });
}

// dựng mapping ban đầu khi vừa đọc xong file (bước 1 xong -> chuyển bước 2)
export function buildInitialMappings(fileData: ParsedFileData, fields: ImportField[]): ColumnMapping[] {
    return autoMap(fileData.headers, fields);
}

// bước 2 của wizard nhập file: ghép cột file với field CRM, có preview 3 dòng đầu
export const StepMapColumns = ({ fileData, fields, mappings, onMappingsChange }: Props) => {
    const [tab, setTab] = useState<TabKey>('all');

    const mapped = mappings.filter(m => m.fieldKey !== null);
    const unmapped = mappings.filter(m => m.fieldKey === null);
    const displayed = tab === 'mapped' ? mapped : tab === 'unmapped' ? unmapped : mappings;

    const previewRows = useMemo(() => fileData.rows.slice(0, 3), [fileData.rows]);

    const handleChange = (fileColumn: string, fieldKey: string | null) => {
        onMappingsChange(mappings.map(m =>
            m.fileColumn === fileColumn ? { ...m, fieldKey } : m
        ));
    };

    const tabs: { key: TabKey; label: string; count: number }[] = [
        { key: 'all',     label: 'Tất cả',    count: mappings.length },
        { key: 'mapped',  label: 'Đã ghép',   count: mapped.length },
        { key: 'unmapped',label: 'Chưa ghép', count: unmapped.length },
    ];

    return (
        <div className="space-y-4">
            {/* Layout selector (UI only) */}
            <div className="flex items-center gap-3">
                <span className="text-md text-gray-600">Chọn bố cục</span>
                <select className="border border-gray-300 rounded-btn px-3 py-1.5 text-md text-text-main bg-white">
                    <option>Mẫu tiêu chuẩn</option>
                </select>
            </div>

            {/* Tabs */}
            <div className="flex gap-0 border-b border-gray-200">
                {tabs.map(t => (
                    <button
                        key={t.key}
                        type="button"
                        onClick={() => setTab(t.key)}
                        className={`px-4 py-2 text-md font-medium border-b-2 -mb-px transition-colors ${
                            tab === t.key
                                ? t.key === 'unmapped'
                                    ? 'border-danger text-danger'
                                    : 'border-primary text-primary'
                                : 'border-transparent text-gray-500 hover:text-text-main'
                        }`}
                    >
                        {t.label} ({t.count})
                    </button>
                ))}
            </div>

            {/* Table */}
            <div className="bg-white rounded-card border border-gray-200 overflow-hidden">
                <table className="w-full text-md">
                    <thead className="bg-gray-50 border-b border-gray-200">
                        <tr>
                            <th className="px-4 py-3 text-left font-medium text-gray-600 w-1/3">Tiêu đề cột từ file</th>
                            <th className="px-4 py-3 text-left font-medium text-gray-600 w-1/3">Trường dữ liệu trên CRM</th>
                            <th className="px-4 py-3 text-left font-medium text-gray-600 w-1/3">Nội dung cột từ file</th>
                        </tr>
                    </thead>
                    <tbody>
                        {displayed.map((m, i) => (
                            <tr key={m.fileColumn} className={i % 2 === 0 ? '' : 'bg-gray-50/50'}>
                                <td className="px-4 py-3 text-text-main font-medium">{m.fileColumn}</td>
                                <td className="px-4 py-3">
                                    <select
                                        value={m.fieldKey ?? ''}
                                        onChange={(e) => handleChange(m.fileColumn, e.target.value || null)}
                                        className="w-full border border-gray-300 rounded-btn px-2 py-1.5 text-md bg-white text-text-main"
                                    >
                                        <option value="">— Bỏ qua —</option>
                                        {fields.map(f => (
                                            <option key={f.key} value={f.key}>
                                                {f.label}{f.required ? ' *' : ''}
                                            </option>
                                        ))}
                                    </select>
                                </td>
                                <td className="px-4 py-3 text-sm text-gray-500 space-y-0.5">
                                    {previewRows.map((row, ri) => (
                                        <div key={ri} className="truncate max-w-[200px]">
                                            {String(row[m.fileColumn] ?? '')}
                                        </div>
                                    ))}
                                </td>
                            </tr>
                        ))}
                        {displayed.length === 0 && (
                            <tr>
                                <td colSpan={3} className="px-4 py-8 text-center text-gray-400 text-md">
                                    Không có cột nào
                                </td>
                            </tr>
                        )}
                    </tbody>
                </table>
            </div>
        </div>
    );
};
