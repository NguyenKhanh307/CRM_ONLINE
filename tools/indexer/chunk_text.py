# -*- coding: utf-8 -*-
"""Dung "the tom tat" tieng Viet cho moi ban ghi — day chinh la van ban duoc embed.

Chat luong truy hoi phu thuoc TRUC TIEP vao van ban nay: viet bang tu ngu nguoi dung
thuc su go vao o chat ("giao hang tre", "tra hang", "da chot") thay vi ma enum tieng Anh.
"""

# --- Tu dien nhan tieng Viet cho cac enum ------------------------------------
CUSTOMER_TYPE = {"company": "Doanh nghiệp", "individual": "Cá nhân"}
LEAD_STATUS = {"new": "Mới", "contacting": "Đang liên hệ", "qualified": "Đủ điều kiện",
               "converted": "Đã chuyển đổi", "lost": "Thất bại"}
OPP_STATUS = {"open": "Đang mở", "won": "Đã thắng (chốt thành công)", "lost": "Đã thua"}
QUO_STATUS = {"draft": "Nháp", "pending": "Chờ duyệt", "approved": "Đã duyệt",
              "rejected": "Bị từ chối", "sent": "Đã gửi khách", "accepted": "Khách đã chấp nhận",
              "expired": "Hết hạn"}
QUO_RESPONSE = {"accepted": "Khách chấp nhận", "adjust": "Khách đề nghị điều chỉnh",
                "rejected": "Khách từ chối"}
ORD_STATUS = {"draft": "Nháp", "confirmed": "Đã xác nhận", "processing": "Đang xử lý",
              "completed": "Đã hoàn tất", "cancelled": "Đã hủy"}
INV_STATUS = {"draft": "Nháp", "sent": "Đã phát hành", "partially_paid": "Thanh toán một phần",
              "paid": "Đã thanh toán", "cancelled": "Đã hủy"}
PAY_STATUS = {"unpaid": "Chưa thanh toán", "partial": "Thanh toán một phần",
              "paid": "Đã thanh toán đủ"}
TK_TYPE = {"support": "Hỗ trợ", "return": "Trả hàng", "exchange": "Đổi hàng",
           "complaint": "Khiếu nại"}
TK_STATUS = {"new": "Mới tiếp nhận", "assigned": "Đã phân công", "in_progress": "Đang xử lý",
             "approved": "Đã duyệt", "rejected": "Từ chối", "received": "Đã nhận hàng về",
             "inspected": "Đã kiểm hàng", "resolved": "Đã giải quyết", "closed": "Đã đóng",
             "reopened": "Mở lại"}
TK_REASON = {"defective": "Hàng lỗi/hỏng", "wrong_item": "Giao sai hàng",
             "not_as_described": "Không đúng mô tả", "changed_mind": "Khách đổi ý",
             "late_delivery": "Giao hàng trễ", "other": "Lý do khác"}
TK_RESOLUTION = {"refund": "Hoàn tiền", "replacement": "Đổi hàng mới", "repair": "Sửa chữa",
                 "store_credit": "Ghi có công nợ", "answered": "Đã giải đáp",
                 "rejected": "Từ chối xử lý"}
PRIORITY = {"low": "Thấp", "medium": "Trung bình", "high": "Cao", "urgent": "Khẩn cấp"}
CHANNEL = {"phone": "Điện thoại", "email": "Email", "web": "Website", "zalo": "Zalo",
           "other": "Khác"}
CAMP_TYPE = {"email": "Email marketing", "event": "Sự kiện", "ads": "Quảng cáo",
             "social": "Mạng xã hội", "seo": "SEO", "webinar": "Webinar", "other": "Khác"}
CAMP_STATUS = {"draft": "Nháp", "scheduled": "Đã lên lịch", "running": "Đang chạy",
               "paused": "Tạm dừng", "completed": "Đã kết thúc", "cancelled": "Đã hủy"}
ACT_TYPE = {"call": "Cuộc gọi", "meeting": "Cuộc họp", "email": "Email", "task": "Công việc",
            "note": "Ghi chú"}
ACT_STATUS = {"planned": "Đã lên kế hoạch", "in_progress": "Đang thực hiện",
              "done": "Đã hoàn thành", "cancelled": "Đã hủy"}
ACT_TARGET = {"lead": "Tiềm năng", "customer": "Khách hàng", "opportunity": "Cơ hội"}


def _money(v):
    """1234567 -> '1.234.567 đ' (None/0 -> None de bo dong khoi the)."""
    if v in (None, "", 0):
        return None
    return f"{int(round(float(v))):,}".replace(",", ".") + " đ"


def _date(v):
    return v.strftime("%d/%m/%Y") if v else None


def _line(parts):
    """Ghep cac manh khong rong bang ' · ' (bo dong neu rong het)."""
    kept = [p for p in parts if p]
    return " · ".join(kept) if kept else None


def _kv(label, value, mapping=None):
    if value in (None, ""):
        return None
    if mapping:
        value = mapping.get(value, value)
    return f"{label}: {value}"


def _join(title, lines):
    body = "\n".join(l for l in lines if l)
    return title, (title + "\n" + body if body else title)


# --- Mot ham dung the cho moi phan he ---------------------------------------
def _customer(r):
    title = f"[Khách hàng {r['code']}] {r['name']}"
    return _join(title, [
        _line([_kv("Loại", r["type"], CUSTOMER_TYPE), _kv("Tên viết tắt", r["short_name"]),
               _kv("Ngành", r["industry"]), _kv("Xếp hạng", r["rating"])]),
        _line([_kv("Mã số thuế", r["tax_code"]), _kv("Điện thoại", r["phone"]),
               _kv("Email", r["email"]), _kv("Website", r["website"])]),
        _kv("Địa chỉ", r["address"]),
        _line([_kv("Quy mô nhân sự", r["employee_size"]),
               _kv("Doanh thu năm", _money(r["annual_revenue"]))]),
        _line([_kv("Người phụ trách", r["owner_name"]), _kv("Ngày tạo", _date(r["created_at"]))]),
    ])


def _contact(r):
    title = f"[Liên hệ] {r['salutation'] or ''} {r['full_name']}".strip()
    return _join(title, [
        _line([_kv("Công ty", r["customer_name"]),
               "Là liên hệ chính của công ty" if r["is_primary"] else None]),
        _line([_kv("Chức danh", r["title"]), _kv("Phòng ban", r["department"]),
               _kv("Vị trí", r["position"])]),
        _line([_kv("Điện thoại", r["phones"]), _kv("Email", r["email"]),
               _kv("Email công việc", r["work_email"]), _kv("Zalo", r["zalo"])]),
        _kv("Địa chỉ", r["address"]),
        _line([_kv("Nguồn", r["source"]), _kv("Người phụ trách", r["owner_name"])]),
    ])


def _lead(r):
    title = f"[Tiềm năng {r['code']}] {r['name']}"
    return _join(title, [
        _line([_kv("Công ty", r["company_name"]), _kv("Ngành", r["industry"]),
               _kv("Trạng thái", r["status"], LEAD_STATUS), _kv("Điểm", r["score"])]),
        _line([_kv("Chức danh", r["title"]), _kv("Phòng ban", r["department"]),
               _kv("Mã số thuế", r["tax_code"]),
               _kv("Giá trị ước tính", _money(r["estimated_value"]))]),
        _line([_kv("Điện thoại", r["phone"]), _kv("Email", r["email"]),
               _kv("Website", r["website"])]),
        _line([_kv("Nguồn", r["source"]), _kv("Chiến dịch", r["campaign_name"]),
               _kv("Người phụ trách", r["owner_name"]), _kv("Ngày tạo", _date(r["created_at"]))]),
        _kv("Ghi chú", r["note"]),
    ])


def _opportunity(r):
    title = f"[Cơ hội {r['code']}] {r['name']}"
    return _join(title, [
        _line([_kv("Khách hàng", r["customer_name"]), _kv("Liên hệ", r["contact_name"])]),
        _line([_kv("Giai đoạn", r["stage_name"]), _kv("Trạng thái", r["status"], OPP_STATUS),
               _kv("Xác suất", f"{r['probability']}%" if r["probability"] is not None else None)]),
        _line([_kv("Giá trị", _money(r["amount"])),
               _kv("Doanh thu kỳ vọng", _money(r["expected_revenue"])),
               _kv("Dự kiến chốt", _date(r["expected_close_date"]))]),
        _line([_kv("Loại", r["opportunity_type"]), _kv("Nguồn", r["source"]),
               _kv("Chiến dịch", r["campaign_name"]), _kv("Người phụ trách", r["owner_name"])]),
        _kv("Lý do thắng/thua", r["win_loss_reason"]),
        _kv("Mô tả", r["description"]),
    ])


def _quotation(r):
    title = f"[Báo giá {r['code']}] {r['customer_name'] or ''}".strip()
    return _join(title, [
        _line([_kv("Khách hàng", r["customer_name"]), _kv("Liên hệ", r["contact_name"]),
               _kv("Cơ hội", r["opportunity_name"])]),
        _line([_kv("Trạng thái", r["status"], QUO_STATUS),
               "Báo giá chính của cơ hội" if r["is_primary"] else None,
               "Đã khóa (đã chuyển thành đơn hàng)" if r["is_locked"] else None]),
        _line([_kv("Ngày báo giá", _date(r["quote_date"])),
               _kv("Hiệu lực đến", _date(r["valid_until"]))]),
        _line([_kv("Tạm tính", _money(r["subtotal"])), _kv("Chiết khấu", _money(r["discount"])),
               _kv("Thuế", _money(r["tax"])), _kv("Tổng cộng", _money(r["total"]))]),
        _line([_kv("Phản hồi của khách", r["customer_response"], QUO_RESPONSE),
               _kv("Chiến dịch", r["campaign_name"]),
               _kv("Người phụ trách", r["owner_name"])]),
        _kv("Ý kiến khách hàng", r["customer_response_note"]),
        _kv("Ghi chú", r["note"]),
    ])


def _order(r):
    title = f"[Đơn hàng {r['code']}] {r['customer_name'] or ''}".strip()
    return _join(title, [
        _line([_kv("Khách hàng", r["customer_name"]), _kv("Liên hệ", r["contact_name"])]),
        _line([_kv("Trạng thái", r["status"], ORD_STATUS),
               _kv("Ngày đặt", _date(r["order_date"])),
               _kv("Ngày giao", _date(r["delivery_date"]))]),
        _line([_kv("Tạm tính", _money(r["subtotal"])), _kv("Chiết khấu", _money(r["discount"])),
               _kv("Thuế", _money(r["tax"])), _kv("Tổng cộng", _money(r["total"]))]),
        _line([_kv("Từ báo giá", r["quotation_code"]), _kv("Cơ hội", r["opportunity_name"]),
               _kv("Chiến dịch", r["campaign_name"]),
               _kv("Người phụ trách", r["owner_name"])]),
        _kv("Địa chỉ xuất hóa đơn", r["billing_address"]),
        _kv("Ghi chú", r["note"]),
    ])


def _invoice(r):
    title = f"[Hóa đơn {r['code']}] {r['customer_name'] or ''}".strip()
    return _join(title, [
        _line([_kv("Khách hàng", r["customer_name"]), _kv("Liên hệ", r["contact_name"])]),
        _line([_kv("Trạng thái", r["status"], INV_STATUS),
               _kv("Tình trạng thanh toán", r["payment_status"], PAY_STATUS)]),
        _line([_kv("Ngày hóa đơn", _date(r["invoice_date"])),
               _kv("Hạn thanh toán", _date(r["due_date"]))]),
        _line([_kv("Tạm tính", _money(r["subtotal"])), _kv("Chiết khấu", _money(r["discount"])),
               _kv("Thuế", _money(r["tax"])), _kv("Tổng cộng", _money(r["total"]))]),
        _line([_kv("Từ đơn hàng", r["order_code"]), _kv("Chiến dịch", r["campaign_name"]),
               _kv("Người phụ trách", r["owner_name"])]),
        _kv("Ghi chú", r["note"]),
    ])


def _ticket(r):
    title = f"[Phiếu chăm sóc {r['code']}] {r['subject']}"
    return _join(title, [
        _line([_kv("Loại yêu cầu", r["type"], TK_TYPE), _kv("Trạng thái", r["status"], TK_STATUS),
               _kv("Mức ưu tiên", r["priority"], PRIORITY),
               _kv("Kênh tiếp nhận", r["channel"], CHANNEL)]),
        _line([_kv("Khách hàng", r["customer_name"]), _kv("Liên hệ", r["contact_name"]),
               _kv("Hóa đơn liên quan", r["invoice_code"]),
               _kv("Sản phẩm", r["product_name"])]),
        _kv("Lý do", r["reason"], TK_REASON),
        _kv("Mô tả", r["description"]),
        _kv("Hình thức giải quyết", r["resolution_type"], TK_RESOLUTION),
        _kv("Ghi chú xử lý", r["resolution_note"]),
        _kv("Trao đổi nội bộ", r["notes"]),
        _line([_kv("Đánh giá hài lòng", f"{r['satisfaction_score']}/5"
                   if r["satisfaction_score"] else None),
               _kv("Nhân viên xử lý", r["owner_name"]),
               _kv("Ngày tạo", _date(r["created_at"]))]),
        _kv("Nhận xét của khách", r["satisfaction_comment"]),
    ])


def _campaign(r):
    title = f"[Chiến dịch {r['code']}] {r['name']}"
    return _join(title, [
        _line([_kv("Loại", r["type"], CAMP_TYPE), _kv("Trạng thái", r["status"], CAMP_STATUS),
               _kv("Kênh", r["channel"])]),
        _line([_kv("Từ ngày", _date(r["start_date"])), _kv("Đến ngày", _date(r["end_date"]))]),
        _line([_kv("Ngân sách", _money(r["budget"])),
               _kv("Chi phí thực tế", _money(r["actual_cost"])),
               _kv("Doanh thu kỳ vọng", _money(r["expected_revenue"])),
               _kv("Quy mô mục tiêu", r["target_size"])]),
        _line([_kv("Người phụ trách", r["owner_name"])]),
        _kv("Mô tả", r["description"]),
    ])


def _product(r):
    title = f"[Sản phẩm {r['sku']}] {r['name']}"
    return _join(title, [
        _line([_kv("Nhóm hàng", r["category_name"]), _kv("Loại", r["type"]),
               _kv("Đơn vị tính", r["unit"])]),
        _line([_kv("Giá bán", _money(r["base_price"])), _kv("Giá vốn", _money(r["cost_price"])),
               _kv("Thuế VAT", f"{r['vat_rate']}%" if r["vat_rate"] is not None else None)]),
        None if r["is_active"] else "Trạng thái: Ngừng kinh doanh",
        _kv("Mô tả", r["description"]),
    ])


def _activity(r):
    title = f"[{ACT_TYPE.get(r['type'], 'Hoạt động')}] {r['subject']}"
    return _join(title, [
        _line([_kv("Trạng thái", r["status"], ACT_STATUS),
               _kv("Mức ưu tiên", r["priority"], PRIORITY),
               _kv("Gắn với", r["target_type"], ACT_TARGET)]),
        _line([_kv("Hạn", _date(r["due_at"])), _kv("Hoàn thành", _date(r["completed_at"])),
               _kv("Địa điểm", r["location"])]),
        _line([_kv("Chiều gọi", {"in": "Khách gọi đến", "out": "Gọi cho khách"}.get(
            r["call_direction"])), _kv("Kết quả gọi", r["call_result"]),
               _kv("Thời lượng", f"{r['call_duration']} giây" if r["call_duration"] else None)]),
        _kv("Nội dung", r["content"]),
        _kv("Người thực hiện", r["owner_name"]),
    ])


BUILDERS = {
    "customer": _customer, "contact": _contact, "lead": _lead, "opportunity": _opportunity,
    "quotation": _quotation, "order": _order, "invoice": _invoice, "ticket": _ticket,
    "campaign": _campaign, "product": _product, "activity": _activity,
}


def build(module, row):
    """@return (title, content) — content la van ban se duoc embed."""
    return BUILDERS[module](row)
