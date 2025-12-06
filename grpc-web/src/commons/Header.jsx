import React from 'react';
import { useLocation } from 'react-router-dom';
import { Bell, Search, HelpCircle } from 'lucide-react';
import { useTheme } from '../hooks/useTheme';

export default function Header() {
  const location = useLocation();
  const { isDark } = useTheme();

  // 경로에 따른 Breadcrumb 텍스트 매핑
  const getPageTitle = (pathname) => {
    if (pathname === '/') return 'Simulations';
    if (pathname === '/test') return 'Configuration';
    if (pathname === '/dashboard') return 'Live Dashboard';
    return 'Simulations';
  };

  return (
    <header className="h-16 bg-white dark:bg-slate-900 border-b border-slate-200 dark:border-slate-700 px-6 flex items-center justify-between sticky top-0 z-10 shadow-sm">
      {/* Left: Breadcrumb / Title */}
      <div className="flex items-center gap-4">
        <h2 className="text-lg font-semibold text-slate-800 dark:text-slate-100 flex items-center gap-2">
          {getPageTitle(location.pathname)}
        </h2>
        {/* 상태 표시 뱃지 예시 */}
        {location.pathname === '/dashboard' && (
           <span className="flex h-2.5 w-2.5 relative">
             <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-green-400 opacity-75"></span>
             <span className="relative inline-flex rounded-full h-2.5 w-2.5 bg-green-500"></span>
           </span>
        )}
      </div>

      {/* Right: Actions */}
      <div className="flex items-center gap-4">
        {/* 검색바 (장식용) */}
        <div className="hidden md:flex items-center bg-slate-100 dark:bg-slate-800 rounded-md px-3 py-1.5 border border-transparent dark:border-slate-700 focus-within:border-orange-300 focus-within:bg-white dark:focus-within:bg-slate-700 transition-all">
          <Search className="w-4 h-4 text-slate-400 dark:text-slate-500 mr-2" />
          <input 
            type="text" 
            placeholder="Search simulations..." 
            className="bg-transparent border-none text-sm focus:outline-none w-48 text-slate-600 dark:text-slate-300 placeholder:text-slate-400 dark:placeholder:text-slate-500"
          />
        </div>

        <div className="h-6 w-px bg-slate-200 dark:bg-slate-700 mx-1"></div>

        <button className="text-slate-500 dark:text-slate-400 hover:text-slate-700 dark:hover:text-slate-200 transition-colors relative">
          <Bell className="w-5 h-5" />
          <span className="absolute top-0 right-0 w-2 h-2 bg-red-500 rounded-full border-2 border-white dark:border-slate-900"></span>
        </button>
        
        <button className="text-slate-500 dark:text-slate-400 hover:text-slate-700 dark:hover:text-slate-200 transition-colors">
          <HelpCircle className="w-5 h-5" />
        </button>
      </div>
    </header>
  );
}