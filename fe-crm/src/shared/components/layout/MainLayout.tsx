import { useState } from 'react';
import { Outlet } from 'react-router-dom';
import { Header } from './Header';
import { Sidebar } from './Sidebar';
import { CopilotWidget } from '@/shared/copilot/CopilotWidget';

export const MainLayout = () => {
    const [sidebarOpen, setSidebarOpen] = useState(true);
    const toggle = () => setSidebarOpen(v => !v);

    return (
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
    );
};
