import { useState, useMemo, useEffect, useCallback } from 'react';
import {
    useReactTable,
    getCoreRowModel,
    getFilteredRowModel,
    getSortedRowModel,
    getPaginationRowModel,
    flexRender,
    type ColumnDef,
    type SortingState,
    type VisibilityState,
    type RowSelectionState,
} from '@tanstack/react-table';
import type {
    ColumnMeta,
    QuickFilter,
    FilterCondition,
    ConditionalRule,
    FilterOperator,
} from '@/shared/types/table';
import { applyConditions, checkCondition } from './filterConditions.helpers';
import { TableToolbar } from './TableToolbar';
import { FilterRecordsPanel } from './FilterRecordsPanel';
import { SortPanel } from './SortPanel';
import { ConditionalColoringPanel } from './ConditionalColoringPanel';
import { ColumnVisibilityPanel } from './ColumnVisibilityPanel';
import { TablePagination } from './TablePagination';

interface DataTableProps<T> {
    data: T[];
    columns: ColumnDef<T>[];
    isLoading?: boolean;
    emptyText?: string;
    quickFilters?: QuickFilter[];
    onSelectionChange?: (selectedRows: T[]) => void;
}

type OpenPanel = 'filter' | 'sort' | 'coloring' | 'columns' | null;

/**
 * Bảng dữ liệu tái sử dụng kiểu Excel.
 * Hỗ trợ: chọn nhiều dòng, quick-filter tags, filter records,
 * sort đa cột, tô màu có điều kiện, ẩn/hiện cột, phân trang.
 */
export const DataTable = <T extends Record<string, unknown>>({
    data,
    columns,
    isLoading = false,
    emptyText = 'Không có dữ liệu',
    quickFilters,
    onSelectionChange,
}: DataTableProps<T>) => {
    const [sorting, setSorting] = useState<SortingState>([]);
    const [columnVisibility, setColumnVisibility] = useState<VisibilityState>({});
    const [globalFilter, setGlobalFilter] = useState('');
    const [rowSelection, setRowSelection] = useState<RowSelectionState>({});
    const [selectedRowId, setSelectedRowId] = useState<string | null>(null);
    const [filterConditions, setFilterConditions] = useState<FilterCondition[]>([]);
    const [conditionalRules, setConditionalRules] = useState<ConditionalRule[]>([]);
    const [openPanel, setOpenPanel] = useState<OpenPanel>(null);

    const togglePanel = (panel: OpenPanel) =>
        setOpenPanel((cur) => (cur === panel ? null : panel));

    /** Cột checkbox chọn dòng — luôn đứng đầu bảng. */
    const selectionColumn = useMemo<ColumnDef<T>>(
        () => ({
            id: '__select__',
            size: 40,
            enableSorting: false,
            header: ({ table }) => (
                <input
                    type="checkbox"
                    checked={table.getIsAllPageRowsSelected()}
                    onChange={table.getToggleAllPageRowsSelectedHandler()}
                    className="w-4 h-4 accent-primary cursor-pointer"
                    title="Chọn / bỏ chọn tất cả trang này"
                />
            ),
            cell: ({ row }) => (
                <input
                    type="checkbox"
                    checked={row.getIsSelected()}
                    onChange={row.getToggleSelectedHandler()}
                    onClick={(e) => e.stopPropagation()}
                    className="w-4 h-4 accent-primary cursor-pointer"
                />
            ),
        }),
        []
    );

    const columnsWithSelection = useMemo<ColumnDef<T>[]>(
        () => [selectionColumn, ...columns],
        [selectionColumn, columns]
    );

    /** Pre-filter data bằng filterConditions trước khi vào TanStack. */
    const preFilteredData = useMemo(
        () => applyConditions(data as Record<string, unknown>[], filterConditions) as T[],
        [data, filterConditions]
    );

    const table = useReactTable({
        data: preFilteredData,
        columns: columnsWithSelection,
        state: { sorting, columnVisibility, globalFilter, rowSelection },
        onSortingChange: setSorting,
        onColumnVisibilityChange: setColumnVisibility,
        onGlobalFilterChange: setGlobalFilter,
        onRowSelectionChange: setRowSelection,
        enableRowSelection: true,
        getCoreRowModel: getCoreRowModel(),
        getSortedRowModel: getSortedRowModel(),
        getFilteredRowModel: getFilteredRowModel(),
        getPaginationRowModel: getPaginationRowModel(),
        globalFilterFn: 'includesString',
    });

    useEffect(() => {
        if (!onSelectionChange) return;
        const selected = table.getSelectedRowModel().rows.map((r) => r.original);
        onSelectionChange(selected);
    }, [rowSelection, onSelectionChange, table]);

    const columnMeta = useMemo<ColumnMeta[]>(
        () =>
            table.getAllColumns().map((col) => ({
                id: col.id,
                header: typeof col.columnDef.header === 'string' ? col.columnDef.header : col.id,
                isVisible: col.getIsVisible(),
                canSort: col.getCanSort(),
                toggleVisibility: () => col.toggleVisibility(),
            })),
        // eslint-disable-next-line react-hooks/exhaustive-deps
        [table, columnVisibility]
    );

    const handleClearAll = useCallback(() => {
        setSorting([]);
        setColumnVisibility({});
        setFilterConditions([]);
        setConditionalRules([]);
    }, []);

    /** Tính màu cho một row từ conditionalRules (scope=row). */
    const getRowColor = useCallback(
        (row: T): string | undefined => {
            for (const rule of conditionalRules) {
                if (rule.scope !== 'row' || !rule.fieldId) continue;
                const cellVal = String((row as Record<string, unknown>)[rule.fieldId] ?? '');
                if (checkCondition(cellVal, rule.operator as FilterOperator, rule.value)) {
                    return rule.color;
                }
            }
            return undefined;
        },
        [conditionalRules]
    );

    /** Tính màu cho một cell từ conditionalRules (scope=cell). */
    const getCellColor = useCallback(
        (colId: string, row: T): string | undefined => {
            for (const rule of conditionalRules) {
                if (rule.scope !== 'cell' || rule.fieldId !== colId) continue;
                const cellVal = String((row as Record<string, unknown>)[colId] ?? '');
                if (checkCondition(cellVal, rule.operator as FilterOperator, rule.value)) {
                    return rule.color;
                }
            }
            return undefined;
        },
        [conditionalRules]
    );

    const selectedCount = Object.keys(rowSelection).length;
    const { pageIndex, pageSize } = table.getState().pagination;
    const totalRows = table.getFilteredRowModel().rows.length;

    return (
        <div className="flex flex-col w-full">
            {/* Toolbar + panels dropdown (relative anchor) */}
            <div className="relative">
                <TableToolbar
                    globalFilter={globalFilter}
                    onGlobalFilterChange={setGlobalFilter}
                    quickFilters={quickFilters}
                    selectedCount={selectedCount}
                    onClearSelection={() => setRowSelection({})}
                    isFilterOpen={openPanel === 'filter'}
                    onToggleFilter={() => togglePanel('filter')}
                    activeFilterCount={filterConditions.filter(
                        (c) => c.fieldId !== '' && (c.operator === 'is_empty' || c.operator === 'is_not_empty' || c.value !== '')
                    ).length}
                    isSortOpen={openPanel === 'sort'}
                    onToggleSort={() => togglePanel('sort')}
                    activeSortCount={sorting.length}
                    isColoringOpen={openPanel === 'coloring'}
                    onToggleColoring={() => togglePanel('coloring')}
                    activeColoringCount={conditionalRules.length}
                    isColumnsOpen={openPanel === 'columns'}
                    onToggleColumns={() => togglePanel('columns')}
                />

                {openPanel !== null && (
                    <div
                        className="fixed inset-0 z-10"
                        onClick={() => setOpenPanel(null)}
                    />
                )}

                {openPanel === 'filter' && (
                    <FilterRecordsPanel
                        columns={columnMeta}
                        conditions={filterConditions}
                        onChange={setFilterConditions}
                        onClose={() => setOpenPanel(null)}
                    />
                )}
                {openPanel === 'sort' && (
                    <SortPanel
                        columns={columnMeta}
                        sorting={sorting}
                        onApply={setSorting}
                        onClose={() => setOpenPanel(null)}
                    />
                )}
                {openPanel === 'coloring' && (
                    <ConditionalColoringPanel
                        columns={columnMeta}
                        rules={conditionalRules}
                        onChange={setConditionalRules}
                        onClose={() => setOpenPanel(null)}
                    />
                )}
                {openPanel === 'columns' && (
                    <ColumnVisibilityPanel
                        columns={columnMeta}
                        onClose={() => setOpenPanel(null)}
                    />
                )}
            </div>

            {/* Bảng */}
            <div className="border border-gray-300 rounded-section overflow-auto">
                <table className="w-full border-collapse table-auto">
                    <thead>
                        {table.getHeaderGroups().map((hg) => (
                            <tr key={hg.id}>
                                {hg.headers.map((header) => (
                                    <th
                                        key={header.id}
                                        style={header.column.columnDef.size ? { width: header.column.columnDef.size } : undefined}
                                        className="text-title font-semibold text-left text-text-main bg-gray-100 border-b-2 border-gray-300 px-3 py-2 whitespace-nowrap select-none"
                                    >
                                        {flexRender(header.column.columnDef.header, header.getContext())}
                                    </th>
                                ))}
                            </tr>
                        ))}
                    </thead>

                    <tbody>
                        {isLoading ? (
                            <tr>
                                <td colSpan={columnsWithSelection.length} className="text-table text-center py-8 text-gray-400">
                                    Đang tải...
                                </td>
                            </tr>
                        ) : table.getRowModel().rows.length === 0 ? (
                            <tr>
                                <td colSpan={columnsWithSelection.length} className="text-table text-center py-8 text-gray-400">
                                    {emptyText}
                                </td>
                            </tr>
                        ) : (
                            table.getRowModel().rows.map((row, rowIndex) => {
                                const isHighlighted = selectedRowId === row.id;
                                const isEven = rowIndex % 2 === 0;
                                const rowColor = getRowColor(row.original);

                                return (
                                    <tr
                                        key={row.id}
                                        onClick={() => setSelectedRowId(isHighlighted ? null : row.id)}
                                        style={rowColor && !isHighlighted ? { backgroundColor: rowColor } : undefined}
                                        className={[
                                            'cursor-pointer transition-colors border-l-2',
                                            isHighlighted
                                                ? 'bg-blue-50 border-l-primary'
                                                : rowColor
                                                  ? 'border-l-transparent hover:brightness-95'
                                                  : isEven
                                                    ? 'bg-white border-l-transparent hover:bg-blue-50'
                                                    : 'bg-gray-50 border-l-transparent hover:bg-blue-50',
                                        ].join(' ')}
                                    >
                                        {row.getVisibleCells().map((cell) => {
                                            const cellColor = !rowColor
                                                ? getCellColor(cell.column.id, row.original)
                                                : undefined;
                                            return (
                                                <td
                                                    key={cell.id}
                                                    style={cellColor ? { backgroundColor: cellColor } : undefined}
                                                    className="text-table text-text-main px-3 py-2 border-b border-gray-200 whitespace-nowrap"
                                                >
                                                    {flexRender(cell.column.columnDef.cell, cell.getContext())}
                                                </td>
                                            );
                                        })}
                                    </tr>
                                );
                            })
                        )}
                    </tbody>
                </table>
            </div>

            <TablePagination
                pageIndex={pageIndex}
                pageCount={table.getPageCount()}
                pageSize={pageSize}
                totalRows={totalRows}
                canPreviousPage={table.getCanPreviousPage()}
                canNextPage={table.getCanNextPage()}
                onPageChange={table.setPageIndex}
                onPageSizeChange={table.setPageSize}
            />
        </div>
    );
};
