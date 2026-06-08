import type { ColumnDef } from '@tanstack/react-table';

export interface TrashModuleConfig {
    id: string;
    label: string;
    columns: ColumnDef<Record<string, unknown>>[];
    mockData: Record<string, unknown>[];
}
