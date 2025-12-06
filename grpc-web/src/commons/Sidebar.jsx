import React from 'react';
import { NavLink } from 'react-router-dom';
import { Zap, Settings, BarChart3, Moon, Sun, LogOut } from 'lucide-react';
import logo from '../assets/logo.svg';

export default function Sidebar({ isDark, toggleTheme }) {
  const menuItems = [
    { id: 'simulations', path: '/', icon: Zap, label: 'Simulations' },
    { id: 'test', path: '/test', icon: Settings, label: 'Configuration' },
    { id: 'dashboard', path: '/dashboard', icon: BarChart3, label: 'Reports' },
  ];

  return (
    <div className="w-64 bg-gradient-to-b from-slate-50 to-slate-100 dark:from-slate-950 dark:to-slate-900 text-slate-600 dark:text-slate-400 flex flex-col border-r border-slate-200 dark:border-slate-800 flex-shrink-0">
      {/* Brand Logo */}
      <div className="h-16 flex items-center px-6 border-b border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-950">
        <div className="flex items-center gap-3 w-full">
          {/* 로고 이미지 */}
          <img src={logo} alt="Logo" className="w-8 h-8" />
          <div className="font-bold text-lg text-slate-900 dark:text-white tracking-tight">
            gLoad <span className="bg-gradient-to-r from-blue-600 to-cyan-500 bg-clip-text text-transparent text-xs font-semibold align-top">PRO</span>
          </div>
        </div>
      </div>

      {/* Navigation */}
      <nav className="flex-1 py-6 px-3 space-y-1">
        <div className="text-xs font-semibold text-slate-500 dark:text-slate-600 uppercase tracking-wider mb-4 px-3">
          Menu
        </div>
        {menuItems.map(item => {
          const Icon = item.icon;
          return (
            <NavLink
              key={item.id}
              to={item.path}
              className={({ isActive }) =>
                `flex items-center gap-3 px-4 py-2.5 rounded-lg text-sm font-medium transition-all duration-200 ${
                  isActive
                    ? 'bg-gradient-to-r from-blue-500 to-cyan-500 text-white shadow-md'
                    : 'text-slate-600 dark:text-slate-400 hover:bg-slate-200 dark:hover:bg-slate-800 hover:text-slate-900 dark:hover:text-slate-200'
                }`
              }
            >
              <Icon className="w-4 h-4 flex-shrink-0" />
              <span>{item.label}</span>
            </NavLink>
          );
        })}
      </nav>

      {/* Footer / User Info */}
      <div className="p-4 border-t border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-950/50 space-y-3">
        <button
          onClick={toggleTheme}
          className="flex items-center gap-3 w-full px-4 py-2.5 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-800 transition-all duration-200 text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-slate-200"
        >
          {isDark ? (
            <Sun className="w-5 h-5 text-amber-500" />
          ) : (
            <Moon className="w-5 h-5 text-slate-500" />
          )}
          <span className="text-sm font-medium">
            {isDark ? 'Light Mode' : 'Dark Mode'}
          </span>
        </button>

        <button className="flex items-center gap-3 w-full px-4 py-2.5 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-800 transition-all duration-200 text-left group">
          <div className="w-8 h-8 rounded-full bg-gradient-to-br from-blue-500 to-cyan-500 flex items-center justify-center text-xs font-bold text-white flex-shrink-0">
            AD
          </div>
          <div className="flex-1 min-w-0">
            <p className="text-sm font-medium text-slate-900 dark:text-slate-100 truncate">Admin User</p>
            <p className="text-xs text-slate-500 dark:text-slate-600 truncate">admin@gload.com</p>
          </div>
          <LogOut className="w-4 h-4 text-slate-400 dark:text-slate-500 group-hover:text-slate-600 dark:group-hover:text-slate-300 flex-shrink-0" />
        </button>
      </div>
    </div>
  );
}