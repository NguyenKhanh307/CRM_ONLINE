# -*- coding: utf-8 -*-
"""SQL lay ban ghi nguon cho tung phan he.

Nguyen tac:
  - LUON kem `deleted_at IS NULL` (ke ca bang JOIN) — khong duoc index ban ghi da xoa mem.
  - Ten khoa ngoai resolve bang LEFT JOIN ngay trong SQL -> khong N+1.
  - Cot `owner` la RANH GIOI PHAN QUYEN: owner_id voi lead/customer/opportunity/quotation/
    order/invoice/campaign; assigned_user_id voi contact/activity/ticket; NULL cho product.
"""

# {module: (sql, co_deleted_at)} — key cung la gia tri cot copilot_chunks.module
QUERIES = {
    "customer": """
        SELECT c.id, c.owner_id AS owner, c.code, c.name, c.short_name, c.type, c.tax_code,
               c.industry, c.phone, c.email, c.website, c.address, c.rating, c.employee_size,
               c.annual_revenue, c.status, c.created_at, u.full_name AS owner_name
        FROM customers c
        LEFT JOIN users u ON u.id = c.owner_id
        WHERE c.deleted_at IS NULL
    """,
    "contact": """
        SELECT ct.id, ct.assigned_user_id AS owner, ct.full_name, ct.salutation, ct.title,
               ct.department, ct.position, ct.email, ct.work_email, ct.zalo, ct.address,
               ct.source, ct.is_primary, ct.created_at,
               c.name AS customer_name, u.full_name AS owner_name,
               (SELECT GROUP_CONCAT(p.phone SEPARATOR ', ') FROM contact_phones p
                 WHERE p.contact_id = ct.id) AS phones
        FROM contacts ct
        LEFT JOIN customers c ON c.id = ct.customer_id AND c.deleted_at IS NULL
        LEFT JOIN users u ON u.id = ct.assigned_user_id
        WHERE ct.deleted_at IS NULL
    """,
    "lead": """
        SELECT l.id, l.owner_id AS owner, l.code, l.name, l.company_name, l.industry,
               l.title, l.department, l.tax_code, l.estimated_value,
               l.email, l.phone, l.website, l.source, l.status, l.score,
               l.note, l.created_at,
               ca.name AS campaign_name, u.full_name AS owner_name
        FROM leads l
        LEFT JOIN campaigns ca ON ca.id = l.campaign_id AND ca.deleted_at IS NULL
        LEFT JOIN users u ON u.id = l.owner_id
        WHERE l.deleted_at IS NULL
    """,
    "opportunity": """
        SELECT o.id, o.owner_id AS owner, o.code, o.name, o.opportunity_type, o.amount,
               o.expected_revenue, o.probability, o.expected_close_date, o.source,
               o.win_loss_reason, o.description, o.status, o.created_at,
               c.name AS customer_name, ct.full_name AS contact_name,
               st.name AS stage_name, ca.name AS campaign_name, u.full_name AS owner_name
        FROM opportunities o
        LEFT JOIN customers c ON c.id = o.customer_id AND c.deleted_at IS NULL
        LEFT JOIN contacts ct ON ct.id = o.contact_id AND ct.deleted_at IS NULL
        LEFT JOIN opportunity_stages st ON st.id = o.stage_id
        LEFT JOIN campaigns ca ON ca.id = o.campaign_id AND ca.deleted_at IS NULL
        LEFT JOIN users u ON u.id = o.owner_id
        WHERE o.deleted_at IS NULL
    """,
    "quotation": """
        SELECT q.id, q.owner_id AS owner, q.code, q.quote_date, q.valid_until, q.status,
               q.subtotal, q.discount, q.tax, q.total, q.note, q.is_primary, q.is_locked,
               q.customer_response, q.customer_response_note, q.created_at,
               c.name AS customer_name, ct.full_name AS contact_name,
               o.name AS opportunity_name, ca.name AS campaign_name, u.full_name AS owner_name
        FROM quotations q
        LEFT JOIN customers c ON c.id = q.customer_id AND c.deleted_at IS NULL
        LEFT JOIN contacts ct ON ct.id = q.contact_id AND ct.deleted_at IS NULL
        LEFT JOIN opportunities o ON o.id = q.opportunity_id AND o.deleted_at IS NULL
        LEFT JOIN campaigns ca ON ca.id = q.campaign_id AND ca.deleted_at IS NULL
        LEFT JOIN users u ON u.id = q.owner_id
        WHERE q.deleted_at IS NULL
    """,
    "order": """
        SELECT o.id, o.owner_id AS owner, o.code, o.order_date, o.delivery_date, o.status,
               o.subtotal, o.discount, o.tax, o.total, o.note, o.billing_address, o.tax_code,
               o.created_at,
               c.name AS customer_name, ct.full_name AS contact_name,
               q.code AS quotation_code, op.name AS opportunity_name,
               ca.name AS campaign_name, u.full_name AS owner_name
        FROM orders o
        LEFT JOIN customers c ON c.id = o.customer_id AND c.deleted_at IS NULL
        LEFT JOIN contacts ct ON ct.id = o.contact_id AND ct.deleted_at IS NULL
        LEFT JOIN quotations q ON q.id = o.quotation_id AND q.deleted_at IS NULL
        LEFT JOIN opportunities op ON op.id = o.opportunity_id AND op.deleted_at IS NULL
        LEFT JOIN campaigns ca ON ca.id = o.campaign_id AND ca.deleted_at IS NULL
        LEFT JOIN users u ON u.id = o.owner_id
        WHERE o.deleted_at IS NULL
    """,
    "invoice": """
        SELECT v.id, v.owner_id AS owner, v.code, v.invoice_date, v.due_date, v.status,
               v.payment_status, v.subtotal, v.discount, v.tax, v.total, v.note,
               v.billing_address, v.tax_code, v.created_at,
               c.name AS customer_name, ct.full_name AS contact_name,
               o.code AS order_code, ca.name AS campaign_name, u.full_name AS owner_name
        FROM invoices v
        LEFT JOIN customers c ON c.id = v.customer_id AND c.deleted_at IS NULL
        LEFT JOIN contacts ct ON ct.id = v.contact_id AND ct.deleted_at IS NULL
        LEFT JOIN orders o ON o.id = v.order_id AND o.deleted_at IS NULL
        LEFT JOIN campaigns ca ON ca.id = v.campaign_id AND ca.deleted_at IS NULL
        LEFT JOIN users u ON u.id = v.owner_id
        WHERE v.deleted_at IS NULL
    """,
    # Binh luan ticket duoc GOP vao chunk cua ticket cha (khong tach chunk rieng).
    # Loai type='system' vi do la audit log tu sinh, khong co gia tri ngu nghia.
    "ticket": """
        SELECT t.id, t.assigned_user_id AS owner, t.code, t.type, t.subject, t.description,
               t.channel, t.priority, t.status, t.reason, t.resolution_type, t.resolution_note,
               t.satisfaction_score, t.satisfaction_comment, t.sla_due_at, t.resolved_at,
               t.created_at,
               c.name AS customer_name, ct.full_name AS contact_name,
               v.code AS invoice_code, p.name AS product_name, u.full_name AS owner_name,
               (SELECT GROUP_CONCAT(tc.content ORDER BY tc.created_at SEPARATOR ' | ')
                  FROM ticket_comments tc
                 WHERE tc.ticket_id = t.id AND tc.type = 'note') AS notes
        FROM support_tickets t
        LEFT JOIN customers c ON c.id = t.customer_id AND c.deleted_at IS NULL
        LEFT JOIN contacts ct ON ct.id = t.contact_id AND ct.deleted_at IS NULL
        LEFT JOIN invoices v ON v.id = t.invoice_id AND v.deleted_at IS NULL
        LEFT JOIN products p ON p.id = t.product_id AND p.deleted_at IS NULL
        LEFT JOIN users u ON u.id = t.assigned_user_id
        WHERE t.deleted_at IS NULL
    """,
    "campaign": """
        SELECT ca.id, ca.owner_id AS owner, ca.code, ca.name, ca.type, ca.status, ca.channel,
               ca.start_date, ca.end_date, ca.budget, ca.actual_cost, ca.target_size,
               ca.expected_revenue, ca.description, ca.created_at, u.full_name AS owner_name
        FROM campaigns ca
        LEFT JOIN users u ON u.id = ca.owner_id
        WHERE ca.deleted_at IS NULL
    """,
    # San pham dung chung toan cong ty -> owner = NULL (ai cung tim duoc)
    "product": """
        SELECT p.id, NULL AS owner, p.sku, p.name, p.type, p.unit, p.base_price, p.cost_price,
               p.vat_rate, p.description, p.is_active, p.created_at,
               pc.name AS category_name
        FROM products p
        LEFT JOIN product_categories pc ON pc.id = p.category_id
        WHERE p.deleted_at IS NULL
    """,
    # activities KHONG co cot deleted_at (xem crm.sql)
    "activity": """
        SELECT a.id, a.assigned_user_id AS owner, a.type, a.subject, a.content, a.priority,
               a.target_type, a.target_id, a.location, a.call_direction, a.call_result,
               a.call_duration, a.status, a.due_at, a.completed_at, a.created_at,
               u.full_name AS owner_name
        FROM activities a
        LEFT JOIN users u ON u.id = a.assigned_user_id
    """,
}

MODULES = list(QUERIES.keys())


def fetch(conn, module, limit=None):
    """Doc ban ghi nguon cua mot phan he. @return list[dict]"""
    sql = QUERIES[module].strip()
    sql += " ORDER BY id" + (f" LIMIT {int(limit)}" if limit else "")
    with conn.cursor() as cur:
        cur.execute(sql)
        return cur.fetchall()
