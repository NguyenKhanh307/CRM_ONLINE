import { useState } from 'react';
import { Outlet } from 'react-router-dom';
import { Header } from './Header';
import { Sidebar } from './Sidebar';
import { CopilotWidget } from '@/shared/copilot/CopilotWidget';
import { PageShortcutsProvider } from '@/shared/keyboard/PageShortcutsProvider';

// layout khung chính (header + sidebar + main + copilot widget) cho mọi trang sau đăng nhập
// h-screen overflow-hidden: <main> là vùng cuộn duy nhất, trang con không được tự thêm min-h-screen
export const MainLayout = () => {
    const [sidebarOpen, setSidebarOpen] = useState(true);
    const toggle = () => setSidebarOpen(v => !v);

    return (
        <PageShortcutsProvider>
            <div className="flex flex-col h-screen bg-bg-main overflow-hidden">
                <Header onToggleSidebar={toggle} />

                <div className="flex flex-1 overflow-hidden">
                    <Sidebar isOpen={sidebarOpen} onToggle={toggle} />

                    <main className="flex-1 overflow-auto">
                        <Outlet />
                    </main>
                </div>

                <CopilotWidget />
            </div>
        </PageShortcutsProvider>
    );
};
