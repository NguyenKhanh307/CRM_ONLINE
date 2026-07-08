import { Editor } from '@tinymce/tinymce-react';

// Self-host TinyMCE (bản GPL) — bundle qua Vite, KHÔNG dùng API key / CDN.
import 'tinymce/tinymce';
import 'tinymce/models/dom/model';
import 'tinymce/themes/silver/theme';
import 'tinymce/icons/default/icons';
import 'tinymce/plugins/lists/plugin';
import 'tinymce/plugins/link/plugin';
// Skin cho thanh công cụ (nạp vào <head> của trang).
import 'tinymce/skins/ui/oxide/skin.min.css';
// Skin cho vùng nội dung (nạp vào iframe qua content_style).
import contentUiCss from 'tinymce/skins/ui/oxide/content.min.css?raw';
import contentCss from 'tinymce/skins/content/default/content.min.css?raw';

interface Props {
    /** Nội dung HTML hiện tại. */
    value: string;
    /** Callback khi nội dung thay đổi (trả về HTML). */
    onChange: (html: string) => void;
    /** Chiều cao editor (px). */
    height?: number;
}

/**
 * Trình soạn thảo WYSIWYG dùng chung (TinyMCE self-host).
 * Toolbar gọn cho soạn email; xuất ra HTML.
 */
export function RichTextEditor({ value, onChange, height = 320 }: Props) {
    return (
        <Editor
            licenseKey="gpl"
            value={value}
            onEditorChange={(html) => onChange(html)}
            init={{
                menubar: false,
                branding: false,
                statusbar: false,
                height,
                plugins: 'lists link',
                toolbar: 'bold italic underline | bullist numlist | link | removeformat',
                skin: false,
                content_css: false,
                content_style: [contentUiCss, contentCss].join('\n'),
            }}
        />
    );
}
