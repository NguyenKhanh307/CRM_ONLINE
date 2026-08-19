import { useEffect, useRef } from 'react';
import { useSearchParams } from 'react-router-dom';
import { HERO_CONTENT, UTM_PARAM } from '../config/trackingDemoConfig';
import { useTrackingCampaigns } from '../hooks/useTrackingCampaigns';
import { useTrackingProducts } from '../hooks/useTrackingProducts';
import { useTrackingSession } from '../hooks/useTrackingSession';
import { useQuoteCart } from '../hooks/useQuoteCart';
import { ProductCatalog } from '../components/ProductCatalog';
import { QuoteCart } from '../components/QuoteCart';
import { LeadContactForm } from '../components/LeadContactForm';
import type { LeadFormState } from '../types/trackingTypes';

// trang landing page sản phẩm có gắn web tracking (route công khai `/tracking-demo`)
// kịch bản: vào trang (kèm ?utm_campaign=<mã> nếu đến từ quảng cáo) -> mở phiên -> duyệt sản
// phẩm -> yêu cầu báo giá, kèm bảng giải thích hệ quả tương ứng bên trong CRM
const TrackingDemoPage = () => {
    const [searchParams] = useSearchParams();
    const { data: campaigns = [] } = useTrackingCampaigns();
    const { data: products = [], isLoading: productsLoading } = useTrackingProducts();
    const { lead, busy, message, restoring, start, requestQuote, trackView } = useTrackingSession();
    const cart = useQuoteCart();

    const formRef = useRef<HTMLDivElement>(null);
    // chặn tự mở phiên nhiều lần khi vào bằng link có sẵn utm_campaign
    const autoStartedRef = useRef(false);

    // mã chiến dịch trên url — chiến dịch chỉ đính qua URL, không còn ô chọn tay trên trang
    const utmCode = searchParams.get(UTM_PARAM);

    // Tự mở phiên ngay khi vào trang — không bắt khách bấm nút mới xem được sản phẩm.
    // Có ?utm_campaign=<mã> khớp thì gắn chiến dịch nguồn, không thì mở phiên trơn.
    useEffect(() => {
        if (restoring || lead || autoStartedRef.current) return;
        // có utm nhưng danh sách chiến dịch chưa tải xong thì đợi, để gắn đúng nguồn
        if (utmCode && campaigns.length === 0) return;
        autoStartedRef.current = true;
        const match = utmCode ? campaigns.find((c) => c.code.toLowerCase() === utmCode.toLowerCase()) : undefined;
        void start(match ? match.id : null);
    }, [restoring, lead, utmCode, campaigns, start]);

    const handleSubmit = (form: LeadFormState) => {
        void requestQuote(form, cart.items).then(() => cart.clear());
    };

    return (
        <div className="min-h-screen bg-gray-50 px-4 py-10">
            <div className="mx-auto max-w-3xl space-y-5">
                <header className="rounded-xl border border-gray-200 bg-white p-6 text-center shadow-sm">
                    <h1 className="text-2xl font-bold text-gray-800">{HERO_CONTENT.title}</h1>
                    <p className="mx-auto mt-1 max-w-xl text-sm text-gray-500">{HERO_CONTENT.subtitle}</p>
                </header>

                {message && (
                    <div className="rounded-lg border border-blue-200 bg-blue-50 px-4 py-3 text-sm text-blue-800">{message}</div>
                )}

                <ProductCatalog
                    products={products}
                    isLoading={productsLoading}
                    quantities={cart.quantities}
                    onToggle={cart.toggle}
                    onQuantityChange={cart.setQuantity}
                    onView={trackView}
                />
                <QuoteCart
                    products={products}
                    quantities={cart.quantities}
                    onRemove={cart.remove}
                    onRequestQuote={() => formRef.current?.scrollIntoView({ behavior: 'smooth', block: 'start' })}
                />
                <div ref={formRef}>
                    {/* phiên tracking (lead) có thể chưa kịp mở ngay lúc trang vừa tải — khóa nút gửi
                        tới khi có phiên, tránh requestQuote() no-op im lặng nếu khách bấm quá nhanh */}
                    <LeadContactForm busy={busy || !lead} itemCount={cart.count} onSubmit={handleSubmit} />
                </div>
            </div>
        </div>
    );
};

export default TrackingDemoPage;
