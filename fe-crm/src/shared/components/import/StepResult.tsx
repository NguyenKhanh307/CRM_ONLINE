import { FiCheckCircle, FiXCircle, FiArrowLeft, FiRefreshCw } from 'react-icons/fi';
import type { ImportBulkResult } from './importTypes';

interface Props {
    result: ImportBulkResult;
    backPath: string;
    onReset: () => void;
}

export const StepResult = ({ result, backPath, onReset }: Props) => (
    <div className="max-w-2xl mx-auto space-y-6">
        {/* Summary */}
        <div className="grid grid-cols-2 gap-4">
            <div className="bg-green-50 border border-green-200 rounded-card p-5 flex items-center gap-4">
                <FiCheckCircle size={32} className="text-success shrink-0" />
                <div>
                    <p className="text-xl font-bold text-success">{result.successCount}</p>
                    <p className="text-md text-gray-600">Dòng nhập thành công</p>
                </div>
            </div>
            <div className="bg-red-50 border border-red-200 rounded-card p-5 flex items-center gap-4">
                <FiXCircle size={32} className="text-danger shrink-0" />
                <div>
                    <p className="text-xl font-bold text-danger">{result.failedCount}</p>
                    <p className="text-md text-gray-600">Dòng nhập thất bại</p>
                </div>
            </div>
        </div>

        {/* Error list */}
        {result.errors.length > 0 && (
            <div className="bg-white rounded-card border border-gray-200 overflow-hidden">
                <div className="px-4 py-3 border-b border-gray-200 bg-gray-50">
                    <p className="text-md font-medium text-text-main">Chi tiết lỗi</p>
                </div>
                <div className="divide-y divide-gray-100 max-h-64 overflow-y-auto">
                    {result.errors.map((err, i) => (
                        <div key={i} className="px-4 py-3 flex gap-3 items-start">
                            <span className="text-sm text-gray-400 w-16 shrink-0">Dòng {err.row}</span>
                            <span className="text-sm text-danger">{err.message}</span>
                        </div>
                    ))}
                </div>
            </div>
        )}

        {/* Actions */}
        <div className="flex gap-3">
            <a
                href={backPath}
                className="flex items-center gap-2 px-4 py-2 rounded-btn bg-primary text-white text-md hover:bg-blue-600 transition-colors"
            >
                <FiArrowLeft size={14} />
                Quay về danh sách
            </a>
            <button
                type="button"
                onClick={onReset}
                className="flex items-center gap-2 px-4 py-2 rounded-btn border border-gray-300 text-md text-gray-600 hover:bg-gray-50 transition-colors"
            >
                <FiRefreshCw size={14} />
                Nhập thêm
            </button>
        </div>
    </div>
);
