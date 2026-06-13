/**
 * Class dùng chung cho form thêm mới / chỉnh sửa full-page.
 * Extract từ LeadAddPage / LeadGeneralSection để tránh lặp lại ở 9 module.
 */

/** Input/textarea chuẩn trong form. */
export const inputCls =
    'w-full border border-gray-300 rounded-btn px-3 py-1.5 text-md text-text-main bg-white ' +
    'focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary transition-colors';

/** Base class cho button header form. */
export const btnBase = 'px-4 py-1.5 rounded-btn text-md transition-colors';
