import { useEffect, useRef, useState } from 'react';
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

    // Vào bằng link quảng cáo kèm ?utm_campaign=<mã>: tự so khớp chiến dịch và mở phiên luôn.
    useEffect(() => {
        if (restoring || lead || autoStartedRef.current || !utmCode || campaigns.length === 0) return;
        const match = campaigns.find((c) => c.code.toLowerCase() === utmCode.toLowerCase());
        if (!match) return;
        autoStartedRef.current = true;
        void start(match.id);
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

                {!lead ? (
                    <section className="rounded-xl border border-dashed border-gray-300 bg-white p-6 text-center">
                        <p className="mb-3 text-sm text-gray-500">
                            {restoring
                                ? 'Đang kiểm tra trình duyệt xem đã có mã theo dõi chưa...'
                                : 'Bấm để xem danh sách sản phẩm và gửi yêu cầu báo giá.'}
                        </p>
                        <button
                            type="button"
                            disabled={busy || restoring}
                            onClick={() => void start(null)}
                            className="rounded-lg bg-blue-600 px-5 py-2.5 text-sm font-semibold text-white hover:bg-blue-700 disabled:opacity-50"
                        >
                            Xem sản phẩm & nhận báo giá
                        </button>
                    </section>
                ) : (
                    <>
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
                            <LeadContactForm busy={busy} itemCount={cart.count} onSubmit={handleSubmit} />
                        </div>
                    </>
                )}
            </div>
        </div>
    );
};

export default TrackingDemoPage;
