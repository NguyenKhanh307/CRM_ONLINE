import { useState, type ReactNode } from 'react';
import { useNavigate } from 'react-router-dom';
import { QuoteDetailSection } from '../components/QuoteDetailSection';
import { QuoteItemsTable } from '../components/QuoteItemsTable';

const Section = ({ title, children }: { title: string; children: ReactNode }) => (
    <div>
        <h2 className="text-md font-semibold text-text-main mb-4 pb-2 border-b border-gray-200">
            {title}
        </h2>
        {children}
    </div>
);

const btnBase = 'px-4 py-1.5 rounded-btn text-md transition-colors';

/**
 * Trang thêm báo giá mới — form 4 section, standalone trong MainLayout.
 */
const QuoteAddPage = () => {
    const navigate = useNavigate();
    const [description, setDescription] = useState('');
    const [isShared, setIsShared] = useState(false);

    return (
        <div className="p-6 bg-bg-main min-h-[calc(100vh-50px)]">
            {/* Header */}
            <div className="flex items-center justify-between mb-4">
                <div className="flex items-center gap-2">
                    <h1 className="text-lg font-semibold text-text-main">Thêm Báo giá</h1>
                </div>
                <div className="flex items-center gap-2">
                    <button
                        type="button"
                        onClick={() => navigate(-1)}
                        className={`${btnBase} border border-gray-300 text-gray-600 hover:bg-gray-50`}
                    >
                        Hủy
                    </button>
                    <button
                        type="button"
                        className={`${btnBase} border border-primary text-primary hover:bg-blue-50`}
                    >
                        Lưu và thêm
                    </button>
                    <button
                        type="button"
                        className={`${btnBase} bg-primary text-white hover:bg-blue-600`}
                    >
                        Lưu
                    </button>
                </div>
            </div>

            {/* Nội dung form */}
            <div className="bg-white rounded-card shadow-sm p-6 space-y-8">
                <Section title="Thông tin chi tiết">
                    <QuoteDetailSection />
                </Section>

                <Section title="Thông tin hàng hóa">
                    <QuoteItemsTable />
                </Section>

                <Section title="Thông tin mô tả">
                    <div className="flex items-start gap-3">
                        <span className="text-sm text-gray-600 w-[148px] flex-shrink-0 pt-1.5">
                            Mô tả
                        </span>
                        <textarea
                            rows={3}
                            value={description}
                            onChange={(e) => setDescription(e.target.value)}
                            className="flex-1 border border-gray-300 rounded-btn px-3 py-2 text-md text-text-main focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary resize-none transition-colors"
                        />
                    </div>
                </Section>

                <Section title="Thông tin hệ thống">
                    <label className="flex items-center gap-2 cursor-pointer w-fit">
                        <input
                            type="checkbox"
                            checked={isShared}
                            onChange={(e) => setIsShared(e.target.checked)}
                            className="w-4 h-4 accent-primary"
                        />
                        <span className="text-md text-text-main">Dùng chung</span>
                    </label>
                </Section>
            </div>
        </div>
    );
};

export default QuoteAddPage;
