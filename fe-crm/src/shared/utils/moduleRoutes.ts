import type { RelatedModule } from '../types/related';

// route danh sách của từng phân hệ — nguồn sự thật duy nhất cho mọi link chéo giữa các module
export const MODULE_ROUTES: Record<RelatedModule, string> = {
    lead:        '/tiem-nang',
    contact:     '/lien-he',
    customer:    '/khach-hang',
    opportunity: '/co-hoi',
    quotation:   '/bao-gia',
    order:       '/don-hang',
    invoice:     '/hoa-don',
    ticket:      '/cham-soc',
    activity:    '/hoat-dong',
};

// phân hệ có trang chi tiết riêng — bấm vào dòng sẽ mở trang đó thay vì nhảy về danh sách
const HAS_DETAIL_PAGE: RelatedModule[] = ['lead', 'opportunity', 'ticket', 'contact', 'customer', 'quotation', 'order', 'invoice'];

// đường dẫn tới một bản ghi cụ thể — module có trang chi tiết -> `/co-hoi/12`, module chưa có -> `/bao-gia?focus=12`
// (prop focusId của DataTable sẽ tự nhảy đúng trang phân trang và highlight dòng)
export function recordPath(module: RelatedModule, id: number): string {
    const base = MODULE_ROUTES[module];
    return HAS_DETAIL_PAGE.includes(module) ? `${base}/${id}` : `${base}?focus=${id}`;
}
