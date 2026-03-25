import React, { useState } from 'react';
import axios from 'axios';
import * as Icons from 'lucide-react';
import { motion } from 'framer-motion';

const MenuForm = ({ onMenuAdded, existingMenus }) => {
  const [formData, setFormData] = useState({
    menuName: '',
    icon: 'Folder',
    sequence: 1,
    pageName: '',
    roleCode: 'USER',
    parentId: ''
  });
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const flattenMenus = (menus, level = 0) => {
    let flat = [];
    menus.forEach(m => {
      flat.push({ id: m.id, name: `${'- '.repeat(level)}${m.menuName}` });
      if (m.subMenus) flat = [...flat, ...flattenMenus(m.subMenus, level + 1)];
    });
    return flat;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsSubmitting(true);
    try {
      const payload = { ...formData, parentId: formData.parentId || null };
      
      let response;
      if (formData.parentId) {
        response = await axios.post(`/api/menus/${formData.parentId}/submenus`, payload);
      } else {
        response = await axios.post('/api/menus', payload);
      }

      onMenuAdded(response.data);
      setFormData({ menuName: '', icon: 'Folder', sequence: 1, pageName: '', roleCode: 'USER', parentId: '' });
      // Toast notification would be better
      alert('Menu created successfully!');
    } catch (error) {
      console.error('Error adding menu:', error);
      alert('Failed to add menu');
    } finally {
      setIsSubmitting(false);
    }
  };

  const menuIcons = ['Home', 'Settings', 'Package', 'ShoppingCart', 'Users', 'Layers', 'LayoutGrid', 'Folder', 'FileText', 'PieChart', 'Activity', 'ShieldCheck'];

  return (
    <div className="glass-card max-w-2xl mx-auto border-t-4 border-t-brand">
      <div className="flex items-center gap-4 mb-8">
        <div className="p-3 rounded-2xl bg-brand/10 text-brand">
          <Icons.LayoutGrid size={24} />
        </div>
        <div>
          <h2 className="text-2xl font-bold text-white">Interface Configuration</h2>
          <p className="text-slate-400 font-medium">Define dynamic navigation & menu hierarchies</p>
        </div>
      </div>

      <form onSubmit={handleSubmit} className="space-y-6">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div className="space-y-2">
            <label className="text-xs font-bold text-slate-500 uppercase tracking-widest pl-1">Menu Title</label>
            <input
              name="menuName"
              value={formData.menuName}
              onChange={handleChange}
              required
              placeholder="e.g. Inventory Management"
              className="w-full bg-slate-900/50 border border-slate-800 rounded-xl px-4 py-3 text-slate-100 placeholder:text-slate-700 outline-none focus:border-brand/40 focus:ring-4 focus:ring-brand/10 transition-all"
            />
          </div>
          
          <div className="space-y-2">
            <label className="text-xs font-bold text-slate-500 uppercase tracking-widest pl-1">Visual Icon</label>
            <div className="relative">
              <select 
                name="icon" 
                value={formData.icon} 
                onChange={handleChange}
                className="w-full bg-slate-900/50 border border-slate-800 rounded-xl px-4 py-3 text-slate-100 outline-none focus:border-brand/40 transition-all appearance-none cursor-pointer"
              >
                {menuIcons.map(icon => (
                  <option key={icon} value={icon} className="bg-slate-950">{icon}</option>
                ))}
              </select>
              <div className="absolute right-4 top-1/2 -translate-y-1/2 pointer-events-none text-slate-500">
                <Icons.ChevronDown size={16} />
              </div>
            </div>
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div className="space-y-2">
            <label className="text-xs font-bold text-slate-500 uppercase tracking-widest pl-1">Display Sequence</label>
            <input
              type="number"
              name="sequence"
              value={formData.sequence}
              onChange={handleChange}
              className="w-full bg-slate-900/50 border border-slate-800 rounded-xl px-4 py-3 text-slate-100 outline-none focus:border-brand/40 transition-all"
            />
          </div>
          <div className="space-y-2">
            <label className="text-xs font-bold text-slate-500 uppercase tracking-widest pl-1">Security Role</label>
            <select 
              name="roleCode" 
              value={formData.roleCode} 
              onChange={handleChange}
              className="w-full bg-slate-900/50 border border-slate-800 rounded-xl px-4 py-3 text-slate-100 outline-none focus:border-brand/40 transition-all appearance-none"
            >
              <option value="USER" className="bg-slate-950">Standard User</option>
              <option value="VENDOR" className="bg-slate-950">External Vendor</option>
              <option value="ADMIN" className="bg-slate-950">System Administrator</option>
            </select>
          </div>
        </div>

        <div className="space-y-2">
          <label className="text-xs font-bold text-slate-500 uppercase tracking-widest pl-1">Navigation Slug (Page Name)</label>
          <input
            name="pageName"
            value={formData.pageName}
            onChange={handleChange}
            placeholder="e.g. inventory-dashboard"
            className="w-full bg-slate-900/50 border border-slate-800 rounded-xl px-4 py-3 text-slate-100 placeholder:text-slate-700 outline-none focus:border-brand/40 transition-all"
          />
        </div>

        <div className="space-y-2">
          <label className="text-xs font-bold text-slate-500 uppercase tracking-widest pl-1">Parent Hierarchy</label>
          <select
            name="parentId"
            value={formData.parentId}
            onChange={handleChange}
            className="w-full bg-slate-900/50 border border-slate-800 rounded-xl px-4 py-3 text-slate-100 outline-none focus:border-brand/40 transition-all"
          >
            <option value="" className="bg-slate-950">None (Top-Level Category)</option>
            {flattenMenus(existingMenus).map(m => (
              <option key={m.id} value={m.id} className="bg-slate-950">{m.name}</option>
            ))}
          </select>
        </div>

        <button 
          type="submit" 
          disabled={isSubmitting}
          className="w-full bg-brand hover:bg-brand-hover text-white font-black uppercase tracking-widest text-sm py-4 rounded-xl shadow-lg shadow-brand/20 transition-all active:scale-95 disabled:opacity-50 disabled:cursor-not-allowed mt-2"
        >
          {isSubmitting ? 'Processing Request...' : 'Publish to Navigation'}
        </button>
      </form>
    </div>
  );
};

export default function MenuBuilder({ onMenuAdded, existingMenus }) {
  return (
    <div className="max-w-6xl mx-auto">
      <header className="mb-12">
        <h1 className="text-4xl font-extrabold text-white tracking-tight">Menu Architect</h1>
        <p className="text-slate-400 mt-2 font-medium">Design and deploy recursive navigation structures across the ERP network.</p>
      </header>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8 items-start">
        <div className="lg:col-span-2">
          <MenuForm onMenuAdded={onMenuAdded} existingMenus={existingMenus} />
        </div>

        <div className="space-y-6">
          <div className="glass-card border-l-4 border-l-amber-500">
             <div className="flex items-center gap-3 mb-4 text-amber-500">
                <Icons.Zap size={20} />
                <h4 className="font-bold uppercase tracking-widest text-xs">Architect Pro Tip</h4>
             </div>
             <p className="text-sm text-slate-300 leading-relaxed font-medium">
                Changes made here are <span className="text-amber-500 font-bold underline">Recursive</span>. Deleting a parent item will automatically restructure or prune its children in the global registry.
             </p>
          </div>

          <div className="glass-card">
              <h4 className="font-bold text-slate-100 mb-6 flex items-center gap-2">
                 <Icons.Activity size={18} className="text-brand" />
                 Registry Statistics
              </h4>
              <div className="space-y-4">
                 <div className="flex justify-between items-center p-3 rounded-xl bg-slate-900/50 border border-slate-800/50">
                    <span className="text-sm font-medium text-slate-500">Total Records</span>
                    <span className="text-lg font-black text-brand">{existingMenus.length}</span>
                 </div>
                 <div className="flex justify-between items-center p-3 rounded-xl bg-slate-900/50 border border-slate-800/50">
                    <span className="text-sm font-medium text-slate-500">Live Services</span>
                    <span className="text-lg font-black text-emerald-500">Active</span>
                 </div>
              </div>
          </div>

          <div className="p-1 rounded-3xl bg-gradient-to-br from-brand/50 to-indigo-600/50 shadow-2xl">
              <div className="bg-slate-950 rounded-[inherit] p-6">
                 <h4 className="font-bold text-white mb-2">Need Custom UI?</h4>
                 <p className="text-xs text-slate-400 font-medium mb-6">Contact the Platform Eng team for custom icon requests or unique routing logic.</p>
                 <button className="w-full py-2.5 rounded-xl border border-slate-800 text-xs font-bold text-slate-300 hover:bg-slate-800 transition-colors">
                    View Docs
                 </button>
              </div>
          </div>
        </div>
      </div>
    </div>
  );
}
