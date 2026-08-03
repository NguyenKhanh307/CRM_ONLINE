import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { FiArrowLeft } from 'react-icons/fi';
import { useConfirm } from '@/shared/confirm/useConfirm';
import { parseVNDate } from '@/shared/utils/date';
import type { ImportField, ImportOptions, ImportBulkResult, ImportRowError, ParsedFileData, ColumnMapping } from './importTypes';
import { StepUploadFile } from './StepUploadFile';
import { StepMapColumns, buildInitialMappings } from './StepMapColumns';
import { StepOptions } from './StepOptions';
import { StepResult } from './StepResult';

interface Props {
    title: string;
    fields: ImportField[];
    onImport: (rows: Record<string, unknown>[], options: ImportOptions) => Promise<ImportBulkResult>;
    backPath: string;
}

const STEPS = [
    { num: 1, label: 'Tải file lên',       desc: 'Tải lên file của bạn, lưu ý bạn có thể sử dụng file mẫu của phần mềm để hạn chế lỗi xảy ra' },
    { num: 2, label: 'Ghép dữ liệu',       desc: 'Ghép các cột từ file của bạn với trường dữ liệu trên CRM' },
    { num: 3, label: 'Thiết lập tùy chọn', desc: 'Chọn loại nhập file, chủ sở hữu và gắn thẻ đối với các bản ghi' },
    { num: 4, label: 'Hoàn tất',           desc: '' },
];

// ô ngày sau khi đọc bằng `cellDates:true` có thể là Date object (Excel) hoặc chuỗi dd/mm/yyyy (CSV/gõ tay)
function dateCellToVNString(raw: unknown): string {
    if (raw instanceof Date) {
        const pad = (n: number) => String(n).padStart(2, '0');
        return `${pad(raw.getDate())}/${pad(raw.getMonth() + 1)}/${raw.getFullYear()}`;
    }
    return String(raw ?? '');
}

// ghép dữ liệu file theo mapping cột -> field. Ngày được parse dd/mm/yyyy -> ISO để gửi BE;
// ngày sai định dạng bị bỏ qua (null) và ghi vào `dateErrors` để cảnh báo người dùng thay vì âm thầm mất dữ liệu
function buildRows(
    fileRows: Record<string, string>[],
    mappings: ColumnMapping[],
    fields: ImportField[],
    options: ImportOptions,
): { rows: Record<string, unknown>[]; dateErrors: ImportRowError[] } {
    const dateErrors: ImportRowError[] = [];
    const rows = fileRows.map((row, idx) => {
        const rowNum = idx + 2; // dòng 1 là header trong file Excel
        const out: Record<string, unknown> = {};
        for (const m of mappings) {
            if (!m.fieldKey) continue;
            const field = fields.find(f => f.key === m.fieldKey);
            const raw = row[m.fileColumn] ?? '';
            if (field?.type === 'number') {
                out[m.fieldKey] = raw !== '' ? Number(raw) : null;
            } else if (field?.type === 'date') {
                const text = dateCellToVNString(raw);
                if (text === '') {
                    out[m.fieldKey] = null;
                } else {
                    const iso = parseVNDate(text);
                    if (iso === null) {
                        dateErrors.push({ row: rowNum, message: `Trường "${field.label}" sai định dạng ngày (cần dd/mm/yyyy): "${text}"` });
                        out[m.fieldKey] = null;
                    } else {
                        out[m.fieldKey] = iso;
                    }
                }
            } else {
                out[m.fieldKey] = raw !== '' ? raw : null;
            }
        }
        if (options.ownerMode === 'FROM_FILE' && options.ownerFileColumn) {
            out['ownerEmail'] = row[options.ownerFileColumn] ?? null;
        }
        return out;
    });
    return { rows, dateErrors };
}

// wizard nhập file 4 bước dùng chung cho mọi phân hệ: tải file -> ghép cột -> tùy chọn -> kết quả
export const ImportWizard = ({ title, fields, onImport, backPath }: Props) => {
    const navigate = useNavigate();
    const { confirm } = useConfirm();
    const [step, setStep] = useState(1);
    const [fileData, setFileData] = useState<ParsedFileData | null>(null);
    const [mappings, setMappings] = useState<ColumnMapping[]>([]);
    const [options, setOptions] = useState<ImportOptions>({ importType: 'CREATE', ownerMode: 'SPECIFIC' });
    const [result, setResult] = useState<ImportBulkResult | null>(null);
    const [dateWarnings, setDateWarnings] = useState<ImportRowError[]>([]);
    const [isImporting, setIsImporting] = useState(false);
    const [importError, setImportError] = useState<string | null>(null);

    const handleFileParsed = (data: ParsedFileData) => {
        setFileData(data);
        setMappings(buildInitialMappings(data, fields));
    };

    // bước xác nhận rồi gọi api nhập file — ghi hàng loạt bản ghi nên phải hỏi trước, nêu rõ số dòng và phân hệ
    const handleImport = async () => {
        if (!fileData) return;

        const ok = await confirm({
            message: `Nhập ${fileData.rows.length} dòng vào phân hệ ${title}?`,
            confirmLabel: 'Bắt đầu nhập',
        });
        if (!ok) return;

        setIsImporting(true);
        setImportError(null);
        try {
            const { rows, dateErrors } = buildRows(fileData.rows, mappings, fields, options);
            const res = await onImport(rows, options);
            setResult(res);
            setDateWarnings(dateErrors);
            setStep(4);
        } catch {
            setImportError('Có lỗi xảy ra khi nhập dữ liệu. Vui lòng thử lại.');
        } finally {
            setIsImporting(false);
        }
    };

    const reset = () => {
        setStep(1);
        setFileData(null);
        setMappings([]);
        setOptions({ importType: 'CREATE', ownerMode: 'SPECIFIC' });
        setResult(null);
        setDateWarnings([]);
        setImportError(null);
    };

    const canNext =
        (step === 1 && fileData !== null) ||
        (step === 2 && mappings.some(m => m.fieldKey !== null)) ||
        step === 3;

    return (
        <div className="min-h-screen bg-bg-main">
            {/* Header */}
            <div className="bg-white border-b border-gray-200 px-6 py-4 flex items-center gap-3">
                <button
                    type="button"
                    onClick={() => navigate(backPath)}
                    className="text-gray-400 hover:text-text-main transition-colors"
                >
                    <FiArrowLeft size={18} />
                </button>
                <h1 className="text-lg font-semibold text-text-main">Nhập file — {title}</h1>
            </div>

            <div className="flex">
                {/* Left: Step list */}
                <aside className="w-72 shrink-0 bg-white border-r border-gray-200 min-h-[calc(100vh-57px)] p-6">
                    <h2 className="text-md font-semibold text-text-main mb-4">Nhập file</h2>
                    <ol className="space-y-0">
                        {STEPS.map((s, idx) => {
                            const isActive = step === s.num;
                            const isDone = step > s.num;
                            return (
                                <li key={s.num} className="flex gap-3">
                                    <div className="flex flex-col items-center">
                                        <div className={`w-7 h-7 rounded-full flex items-center justify-center text-sm font-semibold shrink-0 ${
                                            isActive ? 'bg-primary text-white' :
                                            isDone   ? 'bg-green-100 text-success border border-success' :
                                                       'border-2 border-gray-300 text-gray-400'
                                        }`}>
                                            {s.num}
                                        </div>
                                        {idx < STEPS.length - 1 && (
                                            <div className={`w-px flex-1 my-1 ${isDone ? 'bg-success' : 'bg-gray-200'}`} style={{ minHeight: 32 }} />
                                        )}
                                    </div>
                                    <div className="pb-6">
                                        <p className={`text-md font-medium ${isActive ? 'text-primary' : isDone ? 'text-gray-500' : 'text-gray-400'}`}>
                                            {s.label}
                                        </p>
                                        {isActive && s.desc && (
                                            <p className="text-sm text-gray-400 mt-1 leading-relaxed">{s.desc}</p>
                                        )}
                                    </div>
                                </li>
                            );
                        })}
                    </ol>
                </aside>

                {/* Right: Step content */}
                <main className="flex-1 p-8">
                    {step === 1 && (
                        <StepUploadFile fields={fields} onParsed={handleFileParsed} />
                    )}
                    {step === 2 && fileData && (
                        <StepMapColumns
                            fileData={fileData}
                            fields={fields}
                            mappings={mappings}
                            onMappingsChange={setMappings}
                        />
                    )}
                    {step === 3 && fileData && (
                        <StepOptions
                            options={options}
                            fileColumns={fileData.headers}
                            mappings={mappings}
                            onChange={setOptions}
                        />
                    )}
                    {step === 4 && result && (
                        <StepResult result={result} dateWarnings={dateWarnings} backPath={backPath} onReset={reset} />
                    )}

                    {importError && (
                        <p className="mt-4 text-sm text-danger">{importError}</p>
                    )}

                    {/* Navigation buttons */}
                    {step < 4 && (
                        <div className="flex gap-3 mt-8">
                            {step > 1 && (
                                <button
                                    type="button"
                                    onClick={() => setStep(s => s - 1)}
                                    className="px-5 py-2 rounded-btn border border-gray-300 text-md text-gray-600 hover:bg-gray-50"
                                >
                                    Quay lại
                                </button>
                            )}
                            {step < 3 && (
                                <button
                                    type="button"
                                    onClick={() => setStep(s => s + 1)}
                                    disabled={!canNext}
                                    className="px-5 py-2 rounded-btn bg-primary text-white text-md hover:opacity-90 disabled:opacity-50 disabled:cursor-not-allowed"
                                >
                                    Tiếp theo
                                </button>
                            )}
                            {step === 3 && (
                                <button
                                    type="button"
                                    onClick={handleImport}
                                    disabled={isImporting}
                                    className="px-5 py-2 rounded-btn bg-primary text-white text-md hover:opacity-90 disabled:opacity-50 disabled:cursor-not-allowed"
                                >
                                    {isImporting ? 'Đang nhập...' : 'Bắt đầu nhập'}
                                </button>
                            )}
                        </div>
                    )}
                </main>
            </div>
        </div>
    );
};
