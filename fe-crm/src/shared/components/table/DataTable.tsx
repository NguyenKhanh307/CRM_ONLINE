import { useState, useMemo, useEffect, useCallback, useRef } from 'react';
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
    QuickFilterDef,
    FilterCondition,
    ConditionalRule,
    FilterOperator,
    RowAction,
    ServerTableState,
} from '@/shared/types/table';
import { applyConditions, checkCondition } from './filterConditions.helpers';
import { RowContextMenu } from './RowContextMenu';
import { TableToolbar } from './TableToolbar';
import { FilterRecordsPanel } from './FilterRecordsPanel';
import { SortPanel } from './SortPanel';
import { ConditionalColoringPanel } from './ConditionalColoringPanel';
import { ColumnVisibilityPanel } from './ColumnVisibilityPanel';
import { TablePagination } from './TablePagination';
import { tableScrollMaxHeight } from './tableMetrics';

interface DataTableProps<T> {
    data: T[];
    columns: ColumnDef<T>[];
    isLoading?: boolean;
    emptyText?: string;
    quickFilters?: QuickFilterDef[];
    onSelectionChange?: (selectedRows: T[]) => void;
    /** ID bản ghi cần focus (highlight + cuộn tới, tự nhảy đúng trang). Dùng cho điều hướng từ thông báo. */
    focusId?: number | string | null;
    /** Các thao tác của một dòng — hiện trong menu chuột phải (thay cho cột "Thao tác"). */
    rowActions?: (row: T) => RowAction[];
    /** Hành động chính khi nhấp đúp vào dòng (thường là Chỉnh sửa hoặc mở trang chi tiết). */
    onRowDoubleClick?: (row: T) => void;
    /** Báo dòng đang được chọn (highlight) ra ngoài — null khi bỏ chọn. Dùng cho bố cục 2 bảng. */
    onRowSelect?: (row: T | null) => void;
    /** Giới hạn khung bảng còn đúng N dòng, phần dư cuộn trong khung. Bỏ trống → cao theo số dòng. */
    visibleRows?: number;
    /** Luôn giữ một dòng được chọn (mặc định dòng đầu trang) — không cho bỏ chọn. Dùng cho bố cục 2 bảng. */
    autoSelectFirstRow?: boolean;
    /**
     * Server-mode: phân trang + tìm kiếm + tag lọc nhanh xử lý ở server.
     * Không truyền → toàn bộ chạy client như cũ (bảng nhỏ: phân quyền, thùng rác...).
     */
    server?: ServerTableState;
}

type OpenPanel = 'filter' | 'sort' | 'coloring' | 'columns' | null;

/**
 * Bảng dữ liệu tái sử dụng kiểu Excel.
 * Hỗ trợ: chọn nhiều dòng, quick-filter tags, filter records,
 * sort đa cột, tô màu có điều kiện, ẩn/hiện cột, phân trang.
 */
export const DataTable = <T extends object>({
    data,
    columns,
    isLoading = false,
    emptyText = 'Không có dữ liệu',
    quickFilters,
    onSelectionChange,
    focusId,
    rowActions,
    onRowDoubleClick,
    onRowSelect,
    visibleRows,
    autoSelectFirstRow = false,
    server,
}: DataTableProps<T>) => {
    const scrollRef = useRef<HTMLDivElement>(null);
    const isServer = !!server;
    // Callback server đổi ref mỗi render cha (object inline) — neo vào ref để effect không phải gắn lại
    const serverRef = useRef(server);
    serverRef.current = server;
    const [menu, setMenu] = useState<{ x: number; y: number; actions: RowAction[] } | null>(null);
    const [sorting, setSorting] = useState<SortingState>([]);
    const [columnVisibility, setColumnVisibility] = useState<VisibilityState>({});
    const [globalFilter, setGlobalFilter] = useState('');
    const [rowSelection, setRowSelection] = useState<RowSelectionState>({});
    const [selectedRowId, setSelectedRowId] = useState<string | null>(null);
    const [filterConditions, setFilterConditions] = useState<FilterCondition[]>([]);
    const [conditionalRules, setConditionalRules] = useState<ConditionalRule[]>([]);
    const [openPanel, setOpenPanel] = useState<OpenPanel>(null);
    const [activeQuickFilter, setActiveQuickFilter] = useState<string | null>(null);

    // Server-mode: ô search hiển thị tức thời, debounce 350ms rồi mới báo ra ngoài (gọi API)
    const [searchInput, setSearchInput] = useState(server?.searchValue ?? '');
    const serverSearchValue = server?.searchValue;
    useEffect(() => {
        if (isServer && serverSearchValue !== undefined) setSearchInput(serverSearchValue);
    }, [isServer, serverSearchValue]);
    useEffect(() => {
        if (!isServer) return;
        const t = setTimeout(() => {
            const srv = serverRef.current;
            if (srv && searchInput !== srv.searchValue) srv.onSearchChange(searchInput);
        }, 350);
        return () => clearTimeout(t);
    }, [isServer, searchInput]);

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

    /**
     * Lọc tiếp theo tag lọc nhanh đang chọn (so khớp String(row[field]) === value).
     * Memo theo field/value primitive thay vì identity mảng `quickFilters` — trang khai báo
     * mảng inline sẽ tạo ref mới mỗi render, nếu đưa vào deps thì `displayData` đổi ref liên tục
     * khi filter bật → các effect key theo `displayData` chạy lại → vòng lặp render (treo tab).
     */
    const activeTag = quickFilters?.find((q) => q.id === activeQuickFilter);
    const activeTagField = activeTag?.field;
    const activeTagValue = activeTag?.value;
    const displayData = useMemo(() => {
        // Server-mode: tag lọc nhanh đã áp ở server (param status) — không lọc lại client
        if (isServer || !activeQuickFilter || activeTagField == null) return preFilteredData;
        return preFilteredData.filter(
            (row) => String((row as Record<string, unknown>)[activeTagField] ?? '') === activeTagValue
        );
    }, [isServer, preFilteredData, activeQuickFilter, activeTagField, activeTagValue]);

    /** Map khai báo QuickFilterDef → QuickFilter (gắn isActive + onToggle) cho toolbar. */
    const toolbarQuickFilters = useMemo<QuickFilter[]>(
        () =>
            (quickFilters ?? []).map((q) => ({
                id: q.id,
                label: q.label,
                isActive: q.id === activeQuickFilter,
                onToggle: () => {
                    const next = activeQuickFilter === q.id ? null : q.id;
                    setActiveQuickFilter(next);
                    // Server-mode: báo giá trị tag ra ngoài để page gọi API với param status
                    if (isServer) serverRef.current?.onQuickFilterChange?.(next ? q.value : null);
                },
            })),
        [quickFilters, activeQuickFilter, isServer]
    );

    const table = useReactTable({
        data: displayData,
        columns: columnsWithSelection,
        // Server-mode: search do server lọc (không đưa globalFilter vào TanStack)
        state: { sorting, columnVisibility, globalFilter: isServer ? '' : globalFilter, rowSelection },
        getRowId: (row) => String((row as { id?: unknown }).id),
        onSortingChange: setSorting,
        onColumnVisibilityChange: setColumnVisibility,
        onGlobalFilterChange: setGlobalFilter,
        onRowSelectionChange: setRowSelection,
        enableRowSelection: true,
        // Server-mode: data đã là đúng một trang — TanStack không phân trang lại
        manualPagination: isServer,
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

    /**
     * Focus bản ghi theo `focusId`: tìm dòng trong danh sách đã lọc/sắp xếp,
     * tự nhảy đúng trang phân trang, highlight + cuộn tới. Dùng khi bấm thông báo.
     * Chỉ focus MỘT LẦN cho mỗi `focusId` (khi đã tìm thấy dòng) — tránh cuộn giật lại
     * mỗi lần danh sách refetch trong khi `?focus=` vẫn còn trên URL.
     */
    const focusedRef = useRef<string | null>(null);
    useEffect(() => {
        if (focusId == null) { focusedRef.current = null; return; }
        const target = String(focusId);
        if (focusedRef.current === target) return;
        const rows = table.getSortedRowModel().rows;
        const idx = rows.findIndex((r) => r.id === target);
        if (idx < 0) return;                       // dữ liệu chưa tải xong → thử lại ở lần render sau
        focusedRef.current = target;
        // Server-mode: bản ghi đã nằm trong trang hiện tại (page tự lo nạp đúng trang) — không nhảy trang client
        if (!isServer) table.setPageIndex(Math.floor(idx / table.getState().pagination.pageSize));
        setSelectedRowId(target);
        const raf = requestAnimationFrame(() => {
            scrollRef.current
                ?.querySelector(`[data-rowid="${CSS.escape(target)}"]`)
                ?.scrollIntoView({ behavior: 'smooth', block: 'center' });
        });
        return () => cancelAnimationFrame(raf);
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [focusId, displayData]);

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

    // Số liệu phân trang hiển thị: server-mode lấy từ prop, client-mode từ TanStack
    const shownPageIndex = server ? server.pageIndex : pageIndex;
    const shownPageSize = server ? server.pageSize : pageSize;
    const shownTotalRows = server ? server.totalRows : totalRows;
    const shownPageCount = server
        ? Math.max(1, Math.ceil(server.totalRows / server.pageSize))
        : table.getPageCount();

    /**
     * Giữ luôn có một dòng được chọn trên trang hiện tại: chọn lại dòng đầu khi tải xong dữ liệu,
     * đổi trang, đổi bộ lọc, hoặc dòng đang chọn biến mất. Không lặp vô hạn vì lần chạy kế tiếp
     * `selectedRowId` đã hợp lệ nên effect thoát sớm. `reportedRowIdRef` chặn gọi `onRowSelect`
     * trùng id — cắt đứt vòng setState cha ↔ effect kể cả khi effect chạy lại thừa.
     */
    const reportedRowIdRef = useRef<string | null>(null);
    useEffect(() => {
        if (!autoSelectFirstRow) return;
        const rows = table.getRowModel().rows;
        if (rows.length === 0) {
            setSelectedRowId(null);
            if (reportedRowIdRef.current !== null) {
                reportedRowIdRef.current = null;
                onRowSelect?.(null);
            }
            return;
        }
        if (selectedRowId && rows.some((r) => r.id === selectedRowId)) return;
        setSelectedRowId(rows[0].id);
        if (reportedRowIdRef.current !== rows[0].id) {
            reportedRowIdRef.current = rows[0].id;
            onRowSelect?.(rows[0].original);
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [autoSelectFirstRow, selectedRowId, displayData, pageIndex, pageSize]);

    return (
        <div className="flex flex-col w-full">
            {/* Toolbar + panels dropdown (relative anchor) */}
            <div className="relative">
                <TableToolbar
                    globalFilter={isServer ? searchInput : globalFilter}
                    onGlobalFilterChange={isServer ? setSearchInput : setGlobalFilter}
                    quickFilters={toolbarQuickFilters}
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
            <div
                ref={scrollRef}
                className="border border-gray-300 rounded-section overflow-auto"
                style={visibleRows ? { maxHeight: tableScrollMaxHeight(visibleRows) } : undefined}
            >
                <table className="w-full border-collapse table-auto">
                    <thead>
                        {table.getHeaderGroups().map((hg) => (
                            <tr key={hg.id}>
                                {hg.headers.map((header) => (
                                    <th
                                        key={header.id}
                                        style={header.column.columnDef.size ? { width: header.column.columnDef.size } : undefined}
                                        // shadow-inset thay viền dưới: viền của th dính bị border-collapse vẽ theo bảng nên trôi mất khi cuộn
                                        className="sticky top-0 z-10 text-title font-semibold text-left text-text-main bg-gray-100 shadow-[inset_0_-2px_0_0_#d1d5db] px-3 py-2 whitespace-nowrap select-none"
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
                                        data-rowid={row.id}
                                        onClick={() => {
                                            const next = autoSelectFirstRow ? row.id : isHighlighted ? null : row.id;
                                            setSelectedRowId(next);
                                            reportedRowIdRef.current = next;
                                            onRowSelect?.(next ? row.original : null);
                                        }}
                                        onDoubleClick={() => onRowDoubleClick?.(row.original)}
                                        onContextMenu={(e) => {
                                            if (!rowActions) return;
                                            const actions = rowActions(row.original);
                                            if (actions.length === 0) return;
                                            e.preventDefault();
                                            setSelectedRowId(row.id);   // chuột phải: set highlight, không toggle
                                            reportedRowIdRef.current = row.id;
                                            onRowSelect?.(row.original);
                                            setMenu({ x: e.clientX, y: e.clientY, actions });
                                        }}
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

            {rowActions && (
                <p className="text-xs text-gray-400 mt-1">
                    Chuột phải vào dòng để xem thao tác · Nhấp đúp để mở nhanh
                </p>
            )}

            <TablePagination
                pageIndex={shownPageIndex}
                pageCount={shownPageCount}
                pageSize={shownPageSize}
                totalRows={shownTotalRows}
                canPreviousPage={server ? server.pageIndex > 0 : table.getCanPreviousPage()}
                canNextPage={server ? server.pageIndex < shownPageCount - 1 : table.getCanNextPage()}
                onPageChange={server ? server.onPageChange : table.setPageIndex}
                onPageSizeChange={
                    server
                        ? (size) => { server.onPageSizeChange(size); server.onPageChange(0); }
                        : table.setPageSize
                }
            />

            {menu && (
                <RowContextMenu
                    x={menu.x}
                    y={menu.y}
                    actions={menu.actions}
                    onClose={() => setMenu(null)}
                />
            )}
        </div>
    );
};
