import { FiArrowRight } from 'react-icons/fi';
import { QUALIFY_THRESHOLD } from '../config/trackingDemoConfig';

interface Props {
    // có chiến dịch nguồn gắn vào tiềm năng hay không — đổi lời giải thích phần attribution
    hasCampaign: boolean;
    // tiềm năng đã vượt ngưỡng chưa
    qualified: boolean;
}

// một bước trong chuỗi hệ quả bên CRM
const Step = ({ done, title, detail }: { done: boolean; title: string; detail: string }) => (
    <li className="flex gap-3">
        <span className={`mt-0.5 flex h-5 w-5 shrink-0 items-center justify-center rounded-full text-xs font-bold ${
            done ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-400'
        }`}>
            {done ? '✓' : '·'}
        </span>
        <div>
            <div className={`text-sm font-medium ${done ? 'text-gray-800' : 'text-gray-500'}`}>{title}</div>
            <div className="text-xs text-gray-500">{detail}</div>
        </div>
    </li>
);

// bảng giải thích: mỗi thao tác trên landing page tạo ra hệ quả gì bên trong CRM
// giúp người xem demo nối được landing page với dữ liệu thật
export const CrmEffectPanel = ({ hasCampaign, qualified }: Props) => (
    <section className="rounded-xl border border-blue-200 bg-blue-50/60 p-5">
        <h2 className="mb-3 font-semibold text-gray-800">Điều gì đang xảy ra bên trong CRM</h2>
        <ul className="space-y-3">
            <Step
                done
                title="Tiềm năng ẩn danh được tạo"
                detail="Xuất hiện ngay ở Tiềm năng (/tiem-nang) với mã TNW…, nguồn “web”, điểm 0."
            />
            <Step
                done={hasCampaign}
                title="Quy nguồn về chiến dịch"
                detail={hasCampaign
                    ? 'Tiềm năng đã gắn campaign_id — sẽ hiện ở tab “Tiềm năng” của trang chi tiết Chiến dịch và được tính vào ROI.'
                    : 'Chưa chọn chiến dịch nên tiềm năng không quy về đâu cả — báo cáo ROI sẽ không đếm khách này.'}
            />
            <Step
                done
                title="Mỗi hành vi ghi một dòng lịch sử"
                detail="Bảng lead_tracking_events lưu hành động + điểm; điểm cộng dồn vào leads.score."
            />
            <Step
                done={qualified}
                title={`Vượt ${QUALIFY_THRESHOLD} điểm → tự chuyển “Đủ điều kiện”`}
                detail="Backend đổi trạng thái và tạo thông báo cho người phụ trách + quản lý trực tiếp (chuông trên header)."
            />
            <Step
                done={false}
                title="Sale chuyển đổi tiềm năng"
                detail="Nút “Chuyển đổi” trong CRM sinh Khách hàng + Liên hệ + Cơ hội; cơ hội kế thừa luôn chiến dịch nguồn."
            />
        </ul>

        <p className="mt-4 flex items-center gap-1.5 text-xs text-gray-500">
            <FiArrowRight size={12} />
            Chiến dịch → Tiềm năng → Cơ hội → Báo giá → Đơn hàng → Hóa đơn: <code>campaign_id</code> đi hết chuỗi để tính doanh thu quy về.
        </p>
    </section>
);
