import type { ColumnDef } from '@tanstack/react-table';
import { formatISODate } from '@/shared/utils/date';
import type { TrashModuleConfig } from '@/features/thung-rac/types/thungRacTypes';

type Row = Record<string, unknown>;

const deletedAtCol: ColumnDef<Row> = {
    accessorKey: 'deletedAt',
    header: 'Ngày xóa',
    enableSorting: true,
    cell: ({ getValue }) => formatISODate(getValue<string>()),
};

const diTuyenColumns: ColumnDef<Row>[] = [
    { accessorKey: 'tenTuyen',    header: 'Tên tuyến',   enableSorting: true },
    { accessorKey: 'nhanVien',    header: 'Nhân viên',   enableSorting: true },
    { accessorKey: 'khuVuc',      header: 'Khu vực',     enableSorting: true },
    deletedAtCol,
];

const traLaiHangBanColumns: ColumnDef<Row>[] = [
    { accessorKey: 'maPhieu',     header: 'Mã phiếu',    enableSorting: true },
    { accessorKey: 'khachHang',   header: 'Khách hàng',  enableSorting: true },
    { accessorKey: 'tongTien',    header: 'Tổng tiền',   enableSorting: true },
    deletedAtCol,
];

const doiThuColumns: ColumnDef<Row>[] = [
    { accessorKey: 'tenDoiThu',   header: 'Tên đối thủ', enableSorting: true },
    { accessorKey: 'website',     header: 'Website',     enableSorting: true },
    { accessorKey: 'diemManh',    header: 'Điểm mạnh',   enableSorting: true },
    deletedAtCol,
];

const donHangNppColumns: ColumnDef<Row>[] = [
    { accessorKey: 'maDon',       header: 'Mã đơn',          enableSorting: true },
    { accessorKey: 'nhaPhanPhoi', header: 'Nhà phân phối',   enableSorting: true },
    { accessorKey: 'tongTien',    header: 'Tổng tiền',       enableSorting: true },
    deletedAtCol,
];

const traHangNppColumns: ColumnDef<Row>[] = [
    { accessorKey: 'maPhieu',     header: 'Mã phiếu',        enableSorting: true },
    { accessorKey: 'nhaPhanPhoi', header: 'Nhà phân phối',   enableSorting: true },
    { accessorKey: 'lyDo',        header: 'Lý do',           enableSorting: true },
    deletedAtCol,
];

const phieuBaoHanhColumns: ColumnDef<Row>[] = [
    { accessorKey: 'maPhieu',     header: 'Mã phiếu',    enableSorting: true },
    { accessorKey: 'sanPham',     header: 'Sản phẩm',    enableSorting: true },
    { accessorKey: 'khachHang',   header: 'Khách hàng',  enableSorting: true },
    deletedAtCol,
];

export const TRASH_MODULES: TrashModuleConfig[] = [
    {
        id: 'di-tuyen',
        label: 'Đi tuyến',
        columns: diTuyenColumns,
        mockData: [],
    },
    {
        id: 'tra-lai-hang-ban',
        label: 'Trả lại hàng bán',
        columns: traLaiHangBanColumns,
        mockData: [],
    },
    {
        id: 'doi-thu',
        label: 'Đối thủ',
        columns: doiThuColumns,
        mockData: [
            { tenDoiThu: 'Công ty TNHH ABC',  website: 'abc.com.vn',  diemManh: 'Giá thấp, phân phối rộng', deletedAt: '2025-04-10' },
            { tenDoiThu: 'Tập đoàn XYZ',      website: 'xyz.vn',      diemManh: 'Thương hiệu mạnh',          deletedAt: '2025-05-02' },
        ],
    },
    {
        id: 'don-hang-npp',
        label: 'Đơn hàng NPP',
        columns: donHangNppColumns,
        mockData: [],
    },
    {
        id: 'tra-hang-npp',
        label: 'Trả hàng NPP',
        columns: traHangNppColumns,
        mockData: [],
    },
    {
        id: 'phieu-bao-hanh',
        label: 'Phiếu bảo hành',
        columns: phieuBaoHanhColumns,
        mockData: [],
    },
];

export const DEFAULT_MODULE_ID = 'doi-thu';
