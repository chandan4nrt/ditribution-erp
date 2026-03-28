import React, { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { fetchGlobalInventory, fetchLowStockAlerts } from '../features/transactions/transactionSlice';
import { LayoutGrid, Package, AlertCircle, ShoppingCart, TrendingUp, Layers, ArrowRight, Bell } from 'lucide-react';
import { motion } from 'framer-motion';

const StatCard = ({ title, value, icon: Icon, color, trend }) => (
  <motion.div
    initial={{ opacity: 0, y: 20 }}
    animate={{ opacity: 1, y: 0 }}
    className="glass-card flex flex-col justify-between group cursor-pointer"
  >
    <div className="flex items-start justify-between">
      <div className={`p-3 rounded-2xl bg-${color}-500/10 text-${color}-400 group-hover:scale-110 transition-transform duration-300`}>
        <Icon size={24} />
      </div>
      {trend && (
        <span className={`text-xs font-medium px-2 py-1 rounded-full bg-emerald-500/10 text-emerald-400`}>
          +{trend}%
        </span>
      )}
    </div>
    <div className="mt-4">
      <p className="text-slate-400 text-sm font-medium uppercase tracking-wider">{title}</p>
      <h3 className="text-3xl font-bold mt-1 text-slate-50">{value}</h3>
    </div>
  </motion.div>
);

const Dashboard = () => {
  const dispatch = useDispatch();
  const { globalInventory, lowStockAlerts, loading } = useSelector((state) => state.transactions);

  useEffect(() => {
    dispatch(fetchGlobalInventory());
    dispatch(fetchLowStockAlerts());
  }, [dispatch]);

  const totalStock = globalInventory.reduce((acc, item) => acc + item.currentStock, 0);

  const container = {
    hidden: { opacity: 0 },
    show: {
      opacity: 1,
      transition: {
        staggerChildren: 0.1
      }
    }
  };

  const user = JSON.parse(localStorage.getItem('user') || '{}');

  return (
    <div className="min-h-screen p-8 bg-slate-950/50">
      <header className="flex items-center justify-between mb-10">
        <div>
          <h1 className="text-4xl font-extrabold tracking-tight text-white flex items-center gap-3">
            <LayoutGrid className="text-brand" size={36} />
            Command Center
          </h1>
          <p className="text-slate-400 mt-2 font-medium tracking-wide">Enterprise Resource Overview</p>
        </div>
        
        <div className="flex items-center gap-4">
          <button className="p-3 rounded-2xl glass hover:bg-slate-800 transition-colors relative">
            <Bell size={20} className="text-slate-300" />
            {lowStockAlerts.length > 0 && (
              <span className="absolute top-2 right-2 w-2.5 h-2.5 bg-rose-500 rounded-full border-2 border-slate-950 animate-pulse" />
            )}
          </button>
          <div className="h-10 w-px bg-slate-800 mx-2" />
          <div className="flex items-center gap-3 glass py-1.5 px-4 rounded-2xl">
            <div className="w-8 h-8 rounded-xl bg-brand/20 flex items-center justify-center text-brand font-bold">
              {user.username ? user.username.charAt(0).toUpperCase() : '?'}
            </div>
            <div className="flex flex-col">
              <span className="text-sm font-semibold text-slate-200">{user.username || 'User'}</span>
              <span className="text-[10px] font-bold text-slate-500 uppercase tracking-wider">{user.role || 'Guest'}</span>
            </div>
          </div>
        </div>
      </header>

      {loading ? (
        <div className="flex items-center justify-center h-64">
          <div className="relative">
            <div className="w-16 h-16 border-4 border-brand/20 border-t-brand rounded-full animate-spin" />
            <div className="absolute inset-0 flex items-center justify-center">
              <Package size={20} className="text-brand animate-pulse" />
            </div>
          </div>
        </div>
      ) : (
        <motion.div
          variants={container}
          initial="hidden"
          animate="show"
          className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-10"
        >
          <StatCard title="Total Products" value={globalInventory.length} icon={Package} color="blue" trend="12" />
          <StatCard title="Total Stock Items" value={totalStock.toLocaleString()} icon={Layers} color="emerald" trend="5" />
          <StatCard title="Active Sales" value="24" icon={TrendingUp} color="brand" trend="18" />
          <StatCard title="Low Stock Hazards" value={lowStockAlerts.length} icon={AlertCircle} color="rose" />
        </motion.div>
      )}

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Low Stock Alerts */}
        <div className="lg:col-span-2">
          <div className="glass-card h-full">
            <div className="flex items-center justify-between mb-8">
              <div>
                <h3 className="text-xl font-bold flex items-center gap-2">
                  Stock Alerts
                  <span className="text-xs font-bold px-2 py-0.5 rounded-md bg-rose-500/20 text-rose-400 border border-rose-500/30">CRITICAL</span>
                </h3>
                <p className="text-sm text-slate-400 mt-1">Immediate action required for items below reorder level</p>
              </div>
              <button className="text-xs font-bold text-brand uppercase tracking-widest flex items-center gap-2 group">
                View All <ArrowRight size={14} className="group-hover:translate-x-1 transition-transform" />
              </button>
            </div>

            <div className="overflow-x-auto">
              <table className="w-full text-left">
                <thead>
                  <tr className="border-b border-slate-800 text-slate-500 text-xs font-bold uppercase tracking-widest bg-slate-900/10">
                    <th className="pb-4 pt-2 pl-4">Item Details</th>
                    <th className="pb-4 pt-2 text-center">Current</th>
                    <th className="pb-4 pt-2 text-center">Minimum</th>
                    <th className="pb-4 pt-2 text-right pr-4">Action</th>
                  </tr>
                </thead>
                <tbody>
                  {lowStockAlerts.length === 0 ? (
                    <tr>
                      <td colSpan={4} className="py-20 text-center text-slate-500 italic">No critical stock levels detected</td>
                    </tr>
                  ) : (
                    lowStockAlerts.map((product) => (
                      <tr key={product.id} className="border-b border-slate-900/50 hover:bg-slate-800/20 transition-colors group">
                        <td className="py-4 pl-4">
                          <div className="flex items-center gap-3">
                            <div className="w-10 h-10 rounded-xl bg-slate-900 border border-slate-800 flex items-center justify-center text-slate-500">
                              {product.code.charAt(0)}
                            </div>
                            <div>
                              <p className="text-sm font-bold text-slate-100">{product.name}</p>
                              <p className="text-xs text-slate-500 font-mono">{product.code}</p>
                            </div>
                          </div>
                        </td>
                        <td className="py-4 text-center">
                          <div className="inline-flex items-center px-2.5 py-1 rounded-lg bg-rose-500/10 text-rose-500 font-bold text-sm">
                            {product.currentStock}
                          </div>
                        </td>
                        <td className="py-4 text-center text-slate-400 font-medium text-sm">
                          {product.reorderLevel}
                        </td>
                        <td className="py-4 text-right pr-4">
                          <button className="text-sm font-bold text-slate-300 hover:text-brand transition-colors bg-slate-800/50 hover:bg-brand/10 px-4 py-2 rounded-xl border border-slate-800 hover:border-brand/30">
                            Reorder
                          </button>
                        </td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>
          </div>
        </div>

        {/* Quick Actions / Recent Activity */}
        <div className="space-y-6">
          <div className="glass-card">
            <h3 className="text-lg font-bold mb-6">Operations Hub</h3>
            <div className="grid grid-cols-1 gap-3">
              {[
                { name: 'Purchase Entry', icon: ShoppingCart, color: 'blue' },
                { name: 'Stock Transfer', icon: Layers, color: 'emerald' },
                { name: 'Inventory Audit', icon: AlertCircle, color: 'amber' },
              ].map(action => (
                <button
                  key={action.name}
                  className="flex items-center gap-4 p-4 rounded-xl border border-slate-800/50 hover:border-brand/30 hover:bg-brand/5 transition-all duration-300 group text-left"
                >
                  <div className={`p-3 rounded-xl bg-${action.color}-500/10 text-${action.color}-400 group-hover:scale-110 transition-transform`}>
                    <action.icon size={20} />
                  </div>
                  <div>
                    <span className="block font-bold text-slate-200 group-hover:text-white transition-colors">{action.name}</span>
                    <span className="block text-xs text-slate-500 font-medium mt-0.5">Quick transaction launch</span>
                  </div>
                </button>
              ))}
            </div>
          </div>

          <div className="glass-card bg-brand relative overflow-hidden group">
            <div className="absolute top-0 right-0 p-8 text-brand-hover group-hover:scale-125 transition-transform duration-700 opacity-20">
              <TrendingUp size={120} />
            </div>
            <h4 className="text-white font-bold text-lg mb-2 relative z-10">Premium Insights</h4>
            <p className="text-slate-100/70 text-sm mb-6 relative z-10 font-medium">Unlock predictive stock modeling and advanced demand analytics dashboards.</p>
            <button className="bg-white text-brand font-extrabold text-xs uppercase tracking-widest px-6 py-3 rounded-xl relative z-10 hover:shadow-xl transition-shadow">
              Upgrade System
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
