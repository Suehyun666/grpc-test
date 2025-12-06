import React from 'react';
import Sidebar from './Sidebar';
import Header from './Header';
import { useTheme } from '../hooks/useTheme';

export default function Layout({ children }) {
  const { isDark, toggleTheme } = useTheme();

  return (
    <div className="flex h-screen overflow-hidden" style={{ backgroundColor: 'var(--bg-main)' }}>
      {/* 사이드바는 고정 */}
      <Sidebar isDark={isDark} toggleTheme={toggleTheme} />

      {/* 메인 영역: 헤더 + 콘텐츠 */}
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
        <Header />
        <main className="flex-1 overflow-y-auto p-6 scroll-smooth" style={{ backgroundColor: 'var(--bg-main)' }}>
          <div className="max-w-7xl mx-auto w-full">
            {children}
          </div>
        </main>
      </div>
    </div>
  );
}