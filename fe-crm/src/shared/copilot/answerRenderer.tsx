import React from 'react';

/**
 * Render nhẹ câu trả lời của trợ lý (không dùng thư viện markdown):
 * - `**...**` → in đậm (nhãn), bỏ dấu `*`.
 * - dòng bắt đầu `* ` / `- ` / `• ` → gạch đầu dòng.
 * - cụm "tăng X%"/"+X%" → xanh (text-success); "giảm X%"/"-X%" → đỏ (text-danger); số thường → đen.
 */

/** Regex cụm tăng/giảm phần trăm để tô màu. */
const CHANGE_RE = /((?:tăng|giảm)\s*[\d.,]+\s*%|[+\-]\s*[\d.,]+\s*%)/gi;

/** Chọn class màu theo cụm tăng/giảm. */
function changeColor(token: string): string {
    const t = token.toLowerCase().trim();
    if (t.includes('giảm') || t.startsWith('-')) return 'text-danger';
    if (t.includes('tăng') || t.startsWith('+')) return 'text-success';
    return '';
}

/** Tô màu các cụm tăng/giảm % trong một đoạn text. */
function renderColored(text: string, keyBase: string): React.ReactNode[] {
    const parts: React.ReactNode[] = [];
    let last = 0;
    let m: RegExpExecArray | null;
    let i = 0;
    CHANGE_RE.lastIndex = 0;
    while ((m = CHANGE_RE.exec(text)) !== null) {
        if (m.index > last) parts.push(text.slice(last, m.index));
        parts.push(
            <span key={`${keyBase}-c${i++}`} className={`${changeColor(m[0])} font-medium`}>{m[0]}</span>,
        );
        last = m.index + m[0].length;
    }
    if (last < text.length) parts.push(text.slice(last));
    return parts;
}

/** Bắt cả **in đậm** lẫn *in đậm* (một hoặc hai dấu sao) — ưu tiên hai dấu. */
const BOLD_RE = /\*\*(.+?)\*\*|\*(.+?)\*/g;

/** Render nội dung một dòng: in đậm nhãn + tô màu tăng/giảm; xóa dấu `*` thừa. */
function renderInline(text: string, keyBase: string): React.ReactNode[] {
    const nodes: React.ReactNode[] = [];
    let last = 0;
    let m: RegExpExecArray | null;
    let i = 0;
    BOLD_RE.lastIndex = 0;
    while ((m = BOLD_RE.exec(text)) !== null) {
        if (m.index > last) {
            const plain = text.slice(last, m.index).replace(/\*+/g, '');
            nodes.push(<React.Fragment key={`${keyBase}-t${i}`}>{renderColored(plain, `${keyBase}-t${i}`)}</React.Fragment>);
        }
        const inner = m[1] ?? m[2] ?? '';
        nodes.push(
            <strong key={`${keyBase}-b${i}`} className="font-semibold">
                {renderColored(inner, `${keyBase}-b${i}`)}
            </strong>,
        );
        last = m.index + m[0].length;
        i++;
    }
    if (last < text.length) {
        const plain = text.slice(last).replace(/\*+/g, '');
        nodes.push(<React.Fragment key={`${keyBase}-t${i}`}>{renderColored(plain, `${keyBase}-t${i}`)}</React.Fragment>);
    }
    return nodes;
}

/** Chuyển câu trả lời (text) thành cây React: gạch đầu dòng + in đậm + tô màu. */
export function renderAnswer(text: string): React.ReactNode {
    const lines = text.split('\n');
    const blocks: React.ReactNode[] = [];
    let bullets: React.ReactNode[] = [];
    const flush = (k: string) => {
        if (bullets.length) {
            blocks.push(<ul key={k} className="list-disc pl-4 space-y-1">{bullets}</ul>);
            bullets = [];
        }
    };
    lines.forEach((line, i) => {
        const raw = line.trim();
        if (!raw) {
            flush(`ul${i}`);
            return;
        }
        const bullet = raw.match(/^[*\-•]\s+(.*)$/);
        if (bullet) {
            bullets.push(<li key={`li${i}`}>{renderInline(bullet[1], `li${i}`)}</li>);
        } else {
            flush(`ul${i}`);
            blocks.push(<p key={`p${i}`}>{renderInline(raw, `p${i}`)}</p>);
        }
    });
    flush('ul-end');
    return <div className="space-y-1">{blocks}</div>;
}
