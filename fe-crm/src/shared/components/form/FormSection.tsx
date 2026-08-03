import type { ReactNode } from 'react';

// section có tiêu đề trong form full-page (vd "Thông tin chung", "Thông tin địa chỉ")
export const FormSection = ({ title, children }: { title: string; children: ReactNode }) => (
    <div>
        <h2 className="text-md font-semibold text-text-main mb-4 pb-2 border-b border-gray-200">
            {title}
        </h2>
        {children}
    </div>
);
