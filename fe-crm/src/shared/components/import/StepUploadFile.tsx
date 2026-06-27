import { useRef, useState, type DragEvent } from 'react';
import { FiUploadCloud, FiCheckCircle, FiDownload } from 'react-icons/fi';
import * as XLSX from 'xlsx';
import type { ImportField, ParsedFileData } from './importTypes';

interface Props {
    fields: ImportField[];
    onParsed: (data: ParsedFileData) => void;
}

const MAX_SIZE_MB = 20;
const MAX_ROWS = 5000;

function downloadTemplate(fields: ImportField[], fullVersion: boolean) {
    const headers = fullVersion ? fields.map(f => f.label) : fields.filter(f => f.required).map(f => f.label);
    const ws = XLSX.utils.aoa_to_sheet([headers]);
    const wb = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, ws, 'Sheet1');
    XLSX.writeFile(wb, fullVersion ? 'template_day_du.xlsx' : 'template_rut_gon.xlsx');
}

export const StepUploadFile = ({ fields, onParsed }: Props) => {
    const inputRef = useRef<HTMLInputElement>(null);
    const [dragging, setDragging] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [parsed, setParsed] = useState<ParsedFileData | null>(null);

    const processFile = (file: File) => {
        setError(null);
        const ext = file.name.split('.').pop()?.toLowerCase();
        if (!['xlsx', 'xls', 'csv'].includes(ext ?? '')) {
            setError('Chỉ hỗ trợ file .xlsx, .xls, .csv');
            return;
        }
        if (file.size > MAX_SIZE_MB * 1024 * 1024) {
            setError(`Dung lượng file tối đa ${MAX_SIZE_MB} MB`);
            return;
        }
        const reader = new FileReader();
        reader.onload = (e) => {
            try {
                const data = new Uint8Array(e.target!.result as ArrayBuffer);
                const wb = XLSX.read(data, { type: 'array', cellDates: true });
                const ws = wb.Sheets[wb.SheetNames[0]];
                const jsonRows = XLSX.utils.sheet_to_json<Record<string, string>>(ws, { defval: '' });
                if (jsonRows.length > MAX_ROWS) {
                    setError(`Tối đa ${MAX_ROWS} dòng dữ liệu`);
                    return;
                }
                const headers = jsonRows.length > 0 ? Object.keys(jsonRows[0]) : [];
                const result: ParsedFileData = { headers, rows: jsonRows, fileName: file.name };
                setParsed(result);
                onParsed(result);
            } catch {
                setError('Không thể đọc file. Vui lòng kiểm tra lại định dạng.');
            }
        };
        reader.readAsArrayBuffer(file);
    };

    const handleDrop = (e: DragEvent) => {
        e.preventDefault();
        setDragging(false);
        const file = e.dataTransfer.files[0];
        if (file) processFile(file);
    };

    return (
        <div className="max-w-2xl mx-auto space-y-6">
            {/* Dropzone */}
            {!parsed ? (
                <div
                    onClick={() => inputRef.current?.click()}
                    onDragOver={(e) => { e.preventDefault(); setDragging(true); }}
                    onDragLeave={() => setDragging(false)}
                    onDrop={handleDrop}
                    className={`border-2 border-dashed rounded-card p-12 flex flex-col items-center gap-3 cursor-pointer transition-colors ${
                        dragging ? 'border-primary bg-blue-50' : 'border-gray-300 bg-white hover:border-primary hover:bg-blue-50/40'
                    }`}
                >
                    <FiUploadCloud size={40} className="text-primary" />
                    <p className="text-md text-gray-600 text-center">
                        Kéo và thả tài liệu vào đây hoặc <span className="text-primary font-medium">Chọn file của bạn</span>
                    </p>
                    <p className="text-sm text-gray-400">Chúng tôi hỗ trợ các file .csv, .xls và .xlsx</p>
                    <input
                        ref={inputRef}
                        type="file"
                        accept=".xlsx,.xls,.csv"
                        className="hidden"
                        onChange={(e) => { const f = e.target.files?.[0]; if (f) processFile(f); }}
                    />
                </div>
            ) : (
                <div
                    onClick={() => { setParsed(null); if (inputRef.current) inputRef.current.value = ''; }}
                    className="border-2 border-success rounded-card p-12 flex flex-col items-center gap-3 cursor-pointer bg-white"
                    title="Click để chọn file khác"
                >
                    <FiCheckCircle size={40} className="text-success" />
                    <p className="text-md font-medium text-success">Tải file thành công</p>
                    <p className="text-md text-gray-600">{parsed.fileName}</p>
                    <p className="text-sm text-gray-400">{parsed.rows.length} dòng dữ liệu</p>
                    <input ref={inputRef} type="file" accept=".xlsx,.xls,.csv" className="hidden"
                        onChange={(e) => { const f = e.target.files?.[0]; if (f) processFile(f); }} />
                </div>
            )}

            {error && <p className="text-sm text-danger">{error}</p>}

            {/* Info */}
            <ul className="text-sm text-gray-500 space-y-1 list-disc list-inside">
                <li>Dung lượng tệp tối đa {MAX_SIZE_MB} MB</li>
                <li>Cho phép tối đa {MAX_ROWS.toLocaleString('vi-VN')} dòng dữ liệu</li>
            </ul>

            {/* Template downloads */}
            <div className="space-y-2">
                <button
                    type="button"
                    onClick={() => downloadTemplate(fields, false)}
                    className="w-full flex items-center justify-between px-4 py-3 rounded-card border border-gray-200 bg-white hover:bg-gray-50 text-md text-text-main"
                >
                    <div className="flex items-center gap-3">
                        <span className="w-6 h-6 bg-success rounded flex items-center justify-center text-white text-sm font-bold">X</span>
                        <div className="text-left">
                            <p className="font-medium">Tải tệp mẫu rút gọn</p>
                            <p className="text-sm text-gray-400">Bao gồm các trường bắt buộc và thông tin cơ bản khác</p>
                        </div>
                    </div>
                    <FiDownload size={16} className="text-gray-400" />
                </button>
                <button
                    type="button"
                    onClick={() => downloadTemplate(fields, true)}
                    className="w-full flex items-center justify-between px-4 py-3 rounded-card border border-gray-200 bg-white hover:bg-gray-50 text-md text-text-main"
                >
                    <div className="flex items-center gap-3">
                        <span className="w-6 h-6 bg-success rounded flex items-center justify-center text-white text-sm font-bold">X</span>
                        <div className="text-left">
                            <p className="font-medium">Tải tệp mẫu đầy đủ</p>
                            <p className="text-sm text-gray-400">Bao gồm đầy đủ các trường</p>
                        </div>
                    </div>
                    <FiDownload size={16} className="text-gray-400" />
                </button>
            </div>
        </div>
    );
};
