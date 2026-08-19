// Mồ côi — 0 nơi import (urgentItems không còn hiển thị ở dashboard nào, StaffDashboardView cố ý
// bỏ "việc cần xử lý gấp"). Mở lại: bỏ comment component này + wiring vào view + bỏ comment field
// urgentItems ở SalesDashboardResult (BE) và hàm urgentItems() trong DashboardRepositoryImpl.
// import { Link } from 'react-router-dom';
// import { FiLifeBuoy, FiTag, FiFileText } from 'react-icons/fi';
// import { ScrollFrame } from '@/shared/components/table/ScrollFrame';
// import type { UrgentItem } from '../types/dashboardTypes';
//
// interface Props {
//     items: UrgentItem[];
//     /**
//      * Chiều cao tối đa khung cuộn (px). Mặc định ~6,5 mục — cố ý để lộ một phần mục kế tiếp
//      * làm tín hiệu còn nội dung bên dưới. Dòng cao không đều nên dùng px thay vì `visibleRows`.
//      */
//     maxHeight?: number;
// }
//
// /** Ánh xạ loại việc gấp → route + icon + màu. */
// const META: Record<UrgentItem['type'], { to: (id: number | null) => string; icon: typeof FiTag; color: string }> = {
//     ticket: { to: (id) => `/cham-soc/${id}`, icon: FiLifeBuoy, color: 'text-danger' },
//     quotation: { to: () => '/bao-gia', icon: FiTag, color: 'text-warning' },
//     invoice: { to: () => '/hoa-don', icon: FiFileText, color: 'text-primary' },
// };
//
// /**
//  * Danh sách việc gấp — mỗi mục link tới bản ghi liên quan.
//  * BE gộp 3 nguồn (phiếu quá hạn SLA / báo giá sắp hết hạn / hóa đơn quá hạn), mỗi nguồn tối đa 6
//  * ⇒ tới 18 mục, nên danh sách phải tự cuộn thay vì kéo dài thẻ dashboard.
//  */
// export const UrgentList = ({ items, maxHeight = 340 }: Props) => {
//     if (items.length === 0) return <p className="text-sm text-gray-400 py-8 text-center">Không có việc gấp 🎉</p>;
//     return (
//         <ScrollFrame maxHeight={maxHeight}>
//         <ul className="divide-y divide-gray-100">
//             {items.map((it, i) => {
//                 const m = META[it.type];
//                 const Icon = m.icon;
//                 return (
//                     <li key={i}>
//                         <Link to={m.to(it.refId)} className="flex items-center gap-3 py-2 hover:bg-gray-50 rounded px-1">
//                             <Icon className={`${m.color} shrink-0`} size={16} />
//                             <div className="flex-1 min-w-0">
//                                 <div className="text-sm text-text-main truncate">
//                                     <span className="font-medium">{it.code}</span> — {it.label}
//                                 </div>
//                                 <div className="text-sm text-gray-400">{it.dueInfo}</div>
//                             </div>
//                         </Link>
//                     </li>
//                 );
//             })}
//         </ul>
//         </ScrollFrame>
//     );
// };
