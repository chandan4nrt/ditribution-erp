import React, { useState, useEffect } from 'react';
import { Routes, Route, Outlet, useLocation } from 'react-router-dom';
import axios from 'axios';
import Sidebar from './components/Sidebar';
import Dashboard from './pages/Dashboard';
import MenuBuilder from './pages/MenuBuilder';
import AdminPanel from './pages/AdminPanel';
import Landing from './pages/Landing';
import Login from './pages/Login';
import Signup from './pages/Signup';

const MENU_MASTER_URL = 'http://localhost:8080/api/menus'; 

const DashboardLayout = ({ menus, isSidebarCollapsed, setSidebarCollapsed }) => {
  return (
    <div className="flex h-screen bg-slate-950 overflow-hidden font-sans selection:bg-brand selection:text-white">
      {/* Dynamic Sidebar */}
      <Sidebar 
        menus={menus} 
        isCollapsed={isSidebarCollapsed} 
        toggleSidebar={() => setSidebarCollapsed(!isSidebarCollapsed)} 
      />

      {/* Main Content Area */}
      <main className="flex-1 overflow-y-auto relative custom-scrollbar z-0">
        {/* Simple Top Nav for Mobile/Controls */}
        <header className="sticky top-0 z-40 h-16 glass-card border-x-0 border-t-0 rounded-none px-8 flex items-center justify-between">
          <div className="flex items-center gap-4">
             <button 
                onClick={() => setSidebarCollapsed(!isSidebarCollapsed)}
                className="p-2 rounded-xl glass hover:bg-slate-800 transition-colors hidden md:block"
             >
                <div className={`w-5 h-0.5 bg-slate-300 mb-1 transition-all ${isSidebarCollapsed ? 'rotate-45 translate-y-1.5' : ''}`} />
                <div className={`w-5 h-0.5 bg-slate-300 mb-1 transition-opacity ${isSidebarCollapsed ? 'opacity-0' : 'opacity-100'}`} />
                <div className={`w-5 h-0.5 bg-slate-300 transition-all ${isSidebarCollapsed ? '-rotate-45 -translate-y-1.5' : ''}`} />
             </button>
             <div className="h-6 w-px bg-slate-800 hidden md:block" />
             <div className="text-sm font-bold text-slate-400">
                <span className="text-brand">Home</span> / Dashboard
             </div>
          </div>

          <div className="flex items-center gap-4">
            <div className="hidden sm:flex items-center bg-slate-900 border border-slate-800 rounded-xl px-4 py-1.5">
              <input 
                type="text" 
                placeholder="Ctrl + K to search..." 
                className="bg-transparent text-sm text-slate-200 outline-none w-48 placeholder:text-slate-600 font-medium"
              />
            </div>
          </div>
        </header>

        <section className="p-8 pb-20 relative z-10">
          <Outlet />
        </section>

        {/* Global Footer Background Decoration */}
        <div className="fixed bottom-0 left-0 w-full h-32 bg-gradient-to-t from-brand/5 to-transparent pointer-events-none -z-10" />
      </main>
    </div>
  );
};

function App() {
  const [menus, setMenus] = useState([]);
  const [isSidebarCollapsed, setSidebarCollapsed] = useState(false);
  const [loading, setLoading] = useState(true);

  const fetchMenus = async () => {
    const token = localStorage.getItem('token');
    
    // Prevent calling the API on public pages (Login, Signup, Landing)
    if (!token) {
      setLoading(false);
      return;
    }

    setLoading(true);
    try {
      const response = await axios.get(MENU_MASTER_URL, {
        headers: {
          Authorization: `Bearer ${token}`
        }
      });
      setMenus(response.data);
    } catch (error) {
      console.error('Error fetching menus:', error);
    } finally {
      setLoading(false);
    }
  };

  const location = useLocation();

  useEffect(() => {
    // Re-fetch only if entering dashboard or on mount
    if (location.pathname.startsWith('/dashboard')) {
       fetchMenus();
    }
  }, [location.pathname]);

  return (
    <Routes>
      <Route path="/" element={<Landing />} />
      <Route path="/login" element={<Login />} />
      <Route path="/signup" element={<Signup />} />
      <Route path="/dashboard" element={<DashboardLayout menus={menus} isSidebarCollapsed={isSidebarCollapsed} setSidebarCollapsed={setSidebarCollapsed} />}>
        <Route index element={<Dashboard />} />
        <Route path="menu-builder" element={<MenuBuilder onMenuAdded={fetchMenus} existingMenus={menus} />} />
        <Route path="admin" element={<AdminPanel />} />
      </Route>
    </Routes>
  );
}

export default App;
