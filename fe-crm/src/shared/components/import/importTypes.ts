export interface ImportField {
    key: string;
    label: string;
    required?: boolean;
    type: 'text' | 'number' | 'date' | 'enum';
    enumValues?: string[];
    // nhãn tiếng Việt khớp đúng format() của *ExportColumns.ts cùng phân hệ — cho phép nhập lại
    // file vừa xuất ra mà không bị chặn "giá trị không hợp lệ"
    enumLabels?: Record<string, string>;
}

export interface ImportOptions {
    importType: 'CREATE' | 'UPDATE' | 'BOTH';
    ownerMode: 'SPECIFIC' | 'FROM_FILE';
    specificOwnerId?: number;
    ownerFileColumn?: string;
}

export interface ImportRowError {
    row: number;
    message: string;
}

export interface ImportBulkResult {
    successCount: number;
    failedCount: number;
    errors: ImportRowError[];
}

export interface ParsedFileData {
    headers: string[];
    rows: Record<string, string>[];
    fileName: string;
}

export type ColumnMapping = {
    fileColumn: string;
    fieldKey: string | null;
};
