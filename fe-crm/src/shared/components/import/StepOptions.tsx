import type { ImportOptions, ColumnMapping } from './importTypes';

interface Props {
    options: ImportOptions;
    fileColumns: string[];
    mappings: ColumnMapping[];
    onChange: (o: ImportOptions) => void;
}

// bước 3 của wizard nhập file: chọn loại nhập (thêm mới/cập nhật/cả hai) + cách gán chủ sở hữu
export const StepOptions = ({ options, fileColumns, mappings, onChange }: Props) => {
    const set = <K extends keyof ImportOptions>(k: K, v: ImportOptions[K]) =>
        onChange({ ...options, [k]: v });

    const unmappedColumns = fileColumns.filter(
        col => !mappings.find(m => m.fileColumn === col && m.fieldKey !== null)
    );
    const allColumns = fileColumns;

    return (
        <div className="max-w-2xl mx-auto space-y-8">
            {/* Import type */}
            <div>
                <h3 className="text-md font-semibold text-text-main mb-3">Chọn loại nhập file đối với các bản ghi</h3>
                <div className="grid grid-cols-3 gap-3">
                    {([
                        { value: 'CREATE', label: 'Thêm mới',   desc: 'Chỉ thêm mới, không cập nhật bản ghi đã có' },
                        { value: 'UPDATE', label: 'Cập nhật',   desc: 'Chỉ cập nhật bản ghi, không thêm mới' },
                        { value: 'BOTH',   label: 'Cả hai',     desc: 'Thêm mới và cập nhật bản ghi' },
                    ] as const).map(opt => (
                        <label
                            key={opt.value}
                            className={`flex items-start gap-2.5 p-4 border rounded-card cursor-pointer transition-colors ${
                                options.importType === opt.value
                                    ? 'border-primary bg-blue-50/60'
                                    : 'border-gray-200 bg-white hover:border-gray-300'
                            }`}
                        >
                            <input
                                type="radio"
                                name="importType"
                                value={opt.value}
                                checked={options.importType === opt.value}
                                onChange={() => set('importType', opt.value)}
                                className="mt-0.5 accent-primary"
                            />
                            <div>
                                <p className="text-md font-medium text-text-main">{opt.label}</p>
                                <p className="text-sm text-gray-400 mt-0.5">{opt.desc}</p>
                            </div>
                        </label>
                    ))}
                </div>
                {options.importType !== 'CREATE' && (
                    <p className="mt-2 text-sm text-gray-500">
                        Hệ thống dựa trên trường <strong>điện thoại</strong> hoặc <strong>email</strong> để xác định bản ghi đã tồn tại.
                    </p>
                )}
            </div>

            {/* Owner */}
            <div>
                <h3 className="text-md font-semibold text-text-main mb-3">Bàn giao chủ sở hữu</h3>
                <div className="grid grid-cols-2 gap-3">
                    {([
                        { value: 'SPECIFIC', label: 'Cho người dùng cụ thể' },
                        { value: 'FROM_FILE', label: 'Từ file ở cột' },
                    ] as const).map(opt => (
                        <label
                            key={opt.value}
                            className={`flex items-center gap-2.5 p-4 border rounded-card cursor-pointer transition-colors ${
                                options.ownerMode === opt.value
                                    ? 'border-primary bg-blue-50/60'
                                    : 'border-gray-200 bg-white hover:border-gray-300'
                            }`}
                        >
                            <input
                                type="radio"
                                name="ownerMode"
                                value={opt.value}
                                checked={options.ownerMode === opt.value}
                                onChange={() => set('ownerMode', opt.value)}
                                className="accent-primary"
                            />
                            <span className="text-md text-text-main">{opt.label}</span>
                        </label>
                    ))}
                </div>

                {options.ownerMode === 'FROM_FILE' && (
                    <div className="mt-3">
                        <label className="text-sm text-gray-600 mb-1 block">Chọn cột chứa email chủ sở hữu</label>
                        <select
                            value={options.ownerFileColumn ?? ''}
                            onChange={(e) => set('ownerFileColumn', e.target.value || undefined)}
                            className="border border-gray-300 rounded-btn px-3 py-1.5 text-md bg-white text-text-main w-full max-w-xs"
                        >
                            <option value="">— Chọn cột —</option>
                            {allColumns.map(c => (
                                <option key={c} value={c}>{c}</option>
                            ))}
                        </select>
                    </div>
                )}

                {options.ownerMode === 'SPECIFIC' && (
                    <p className="mt-2 text-sm text-gray-500">
                        Chủ sở hữu sẽ được set theo người dùng đang đăng nhập.
                    </p>
                )}
            </div>

            {unmappedColumns.length > 0 && (
                <div className="bg-yellow-50 border border-yellow-200 rounded-card p-4">
                    <p className="text-sm text-yellow-700">
                        <strong>{unmappedColumns.length} cột chưa được ghép</strong> sẽ bị bỏ qua khi nhập.
                    </p>
                </div>
            )}
        </div>
    );
};
