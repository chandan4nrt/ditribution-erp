import React, { useState } from 'react';
import { NavLink } from 'react-router-dom';
import * as Icons from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';

const DynamicIcon = ({ name, className }) => {
  const IconComponent = Icons[name] || Icons.Circle;
  return <IconComponent size={20} className={className} />;
};

const NavItem = ({ item, isCollapsed }) => {
  const [isOpen, setIsOpen] = useState(false);
  const hasChildren = item.subMenus && item.subMenus.length > 0;

  return (
    <div className="mb-1">
      <div
        className={`flex items-center justify-between p-3 rounded-xl cursor-pointer transition-all duration-200 group
          ${isOpen ? 'bg-brand/10 text-brand' : 'text-slate-400 hover:bg-slate-800/50 hover:text-slate-100'}`}
        onClick={() => hasChildren && setIsOpen(!isOpen)}
      >
        <div className="flex items-center gap-3">
          <DynamicIcon name={item.icon} className={isOpen ? 'text-brand' : 'group-hover:text-slate-100'} />
          {!isCollapsed && (
            <span className="text-sm font-semibold tracking-wide">
              {item.menuName}
            </span>
          )}
        </div>
        {!isCollapsed && hasChildren && (
          <Icons.ChevronDown 
            size={16} 
            className={`transition-transform duration-300 ${isOpen ? 'rotate-180' : ''}`} 
          />
        )}
      </div>
      
      <AnimatePresence>
        {isOpen && hasChildren && !isCollapsed && (
          <motion.div
            initial={{ height: 0, opacity: 0 }}
            animate={{ height: 'auto', opacity: 1 }}
            exit={{ height: 0, opacity: 0 }}
            className="overflow-hidden ml-4 pl-4 border-l border-slate-800 mt-1 space-y-1"
          >
            {item.subMenus.map(child => (
              <NavLink
                key={child.id}
                to={`/${child.pageName}`}
                className={({ isActive }) => 
                  `flex items-center gap-3 p-2 rounded-lg text-sm transition-colors
                  ${isActive ? 'text-brand font-bold' : 'text-slate-500 hover:text-slate-200'}`
                }
              >
                <div className="w-1.5 h-1.5 rounded-full bg-current" />
                {child.menuName}
              </NavLink>
            ))}
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
};

const Sidebar = ({ menus, isCollapsed, toggleSidebar }) => {
  const user = JSON.parse(localStorage.getItem('user') || '{}');
  const isAdmin = user.role === 'ADMIN';

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  };

  return (
    <aside 
      className={`glass border-r border-slate-800 transition-all duration-500 ease-in-out z-50 flex flex-col
        ${isCollapsed ? 'w-20' : 'w-72'}`}
    >
      <div className="p-6 flex items-center justify-between">
        <div className="flex items-center gap-3 overflow-hidden">
          <div className="w-10 h-10 rounded-2xl bg-brand flex items-center justify-center shadow-lg shadow-brand/20 shrink-0">
            <Icons.ShieldCheck className="text-white" size={24} />
          </div>
          {!isCollapsed && (
            <span className="text-xl font-black tracking-tighter text-white whitespace-nowrap">
              NRT<span className="text-brand">ERP</span>
            </span>
          )}
        </div>
      </div>

      <nav className="flex-1 px-4 overflow-y-auto pt-4 custom-scrollbar">
        <div className={`mb-4 px-2 text-[10px] font-bold text-slate-500 uppercase tracking-[0.2em] transition-opacity duration-300 ${isCollapsed ? 'opacity-0' : 'opacity-100'}`}>
          Management
        </div>
        
        <NavLink
            to="/dashboard"
            end
            className={({ isActive }) => 
              `flex items-center gap-3 p-3 rounded-xl mb-1 transition-all duration-200
              ${isActive ? 'bg-brand text-white shadow-lg shadow-brand/20' : 'text-slate-400 hover:bg-slate-800/50 hover:text-slate-100'}`}
          >
            <Icons.LayoutGrid size={20} />
            {!isCollapsed && <span className="text-sm font-semibold tracking-wide">Dashboard</span>}
        </NavLink>

        <div className="h-px bg-slate-800/50 my-4 mx-2" />

        {menus.map(menu => (
          <NavItem key={menu.id} item={menu} isCollapsed={isCollapsed} />
        ))}
        
        <NavLink
            to="/dashboard/menu-builder"
            className={({ isActive }) => 
              `flex items-center gap-3 p-3 rounded-xl mb-1 transition-all duration-200
              ${isActive ? 'bg-brand text-white shadow-lg shadow-brand/20' : 'text-slate-400 hover:bg-slate-800/50 hover:text-slate-100'}`}
          >
            <Icons.Settings size={20} />
            {!isCollapsed && <span className="text-sm font-semibold tracking-wide">Menu Builder</span>}
        </NavLink>

        {isAdmin && (
          <NavLink
              to="/dashboard/admin"
              className={({ isActive }) => 
                `flex items-center gap-3 p-3 rounded-xl mb-1 transition-all duration-200
                ${isActive ? 'bg-rose-600 text-white shadow-lg shadow-rose-600/20' : 'text-slate-400 hover:bg-slate-800/50 hover:text-slate-100'}`}
            >
              <Icons.Users size={20} />
              {!isCollapsed && <span className="text-sm font-semibold tracking-wide">User Management</span>}
          </NavLink>
        )}
      </nav>

      <div className="p-4">
        <div className={`glass p-4 rounded-2xl transition-all duration-300 ${isCollapsed ? 'px-2' : ''}`}>
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-xl bg-gradient-to-br from-brand to-indigo-600 flex items-center justify-center text-white font-bold shrink-0 shadow-inner">
              {user.username ? user.username.substring(0, 2).toUpperCase() : '??'}
            </div>
            {!isCollapsed && (
              <div className="overflow-hidden">
                <p className="text-sm font-bold text-white truncate">{user.fullName || 'User'}</p>
                <p className="text-[10px] font-medium text-slate-500 uppercase tracking-wider">{user.role || 'Guest'}</p>
              </div>
            )}
          </div>
          {!isCollapsed && (
            <NavLink 
              to="/login"
              onClick={handleLogout}
              className="w-full mt-4 py-2 rounded-xl bg-slate-800 hover:bg-slate-700 text-slate-300 text-xs font-bold transition-colors flex items-center justify-center gap-2"
            >
              <Icons.LogOut size={14} />
              Sign Out
            </NavLink>
          )}
        </div>
      </div>
    </aside>
  );
};

export default Sidebar;
