import React, { useState, useEffect, useCallback } from 'react';
import axios from 'axios';

const AUTH_URL = 'http://localhost:8080/api/auth';

const ROLES = ['ADMIN', 'PURCHASE_MANAGER', 'SALES_MANAGER', 'WAREHOUSE_MANAGER', 'ACCOUNTANT', 'VIEWER'];

const ROLE_COLORS = {
  ADMIN: 'bg-rose-500/10 text-rose-400 border-rose-500/20',
  PURCHASE_MANAGER: 'bg-indigo-500/10 text-indigo-400 border-indigo-500/20',
  SALES_MANAGER: 'bg-brand/10 text-brand border-brand/20',
  WAREHOUSE_MANAGER: 'bg-amber-500/10 text-amber-400 border-amber-500/20',
  ACCOUNTANT: 'bg-teal-500/10 text-teal-400 border-teal-500/20',
  VIEWER: 'bg-slate-500/10 text-slate-400 border-slate-500/20',
};

const Badge = ({ label, colorClass }) => (
  <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-bold border uppercase tracking-wide ${colorClass}`}>
    {label}
  </span>
);

const authHeaders = () => ({
  headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
});

export default function AdminPanel() {
  const [tab, setTab] = useState('waitlist');
  const [waitlist, setWaitlist] = useState([]);
  const [allUsers, setAllUsers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [actionLoading, setActionLoading] = useState(null); // userId being acted on
  const [toast, setToast] = useState(null);
  const [roleEdit, setRoleEdit] = useState({}); // { userId: selectedRole }

  const showToast = (msg, type = 'success') => {
    setToast({ msg, type });
    setTimeout(() => setToast(null), 3500);
  };

  const fetchWaitlist = useCallback(async () => {
    setLoading(true);
    try {
      const res = await axios.get(`${AUTH_URL}/users/pending`, authHeaders());
      setWaitlist(res.data);
    } catch {
      showToast('Failed to load waitlist', 'error');
    } finally {
      setLoading(false);
    }
  }, []);

  const fetchAllUsers = useCallback(async () => {
    setLoading(true);
    try {
      const res = await axios.get(`${AUTH_URL}/users`, authHeaders());
      setAllUsers(res.data);
    } catch {
      showToast('Failed to load users', 'error');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    if (tab === 'waitlist') fetchWaitlist();
    else fetchAllUsers();
  }, [tab, fetchWaitlist, fetchAllUsers]);

  const handleApprove = async (userId) => {
    setActionLoading(userId);
    try {
      await axios.post(`${AUTH_URL}/users/${userId}/approve`, {}, authHeaders());
      showToast('User approved successfully!');
      fetchWaitlist();
      // refresh all users list too
      fetchAllUsers();
    } catch {
      showToast('Failed to approve user', 'error');
    } finally {
      setActionLoading(null);
    }
  };

  const handleToggleStatus = async (userId, currentlyActive) => {
    setActionLoading(userId);
    try {
      await axios.patch(`${AUTH_URL}/users/${userId}/toggle-status`, {}, authHeaders());
      showToast(currentlyActive ? 'User blocked.' : 'User unblocked.');
      fetchAllUsers();
    } catch {
      showToast('Failed to update status', 'error');
    } finally {
      setActionLoading(null);
    }
  };

  const handleRoleChange = async (userId) => {
    const newRole = roleEdit[userId];
    if (!newRole) return;
    setActionLoading(userId);
    try {
      await axios.patch(`${AUTH_URL}/users/${userId}/role`, { role: newRole }, authHeaders());
      showToast('Role updated successfully!');
      setRoleEdit(prev => ({ ...prev, [userId]: undefined }));
      fetchAllUsers();
    } catch {
      showToast('Failed to change role', 'error');
    } finally {
      setActionLoading(null);
    }
  };

  return (
    <div className="max-w-6xl mx-auto">
      {/* Toast */}
      {toast && (
        <div className={`fixed top-6 right-6 z-50 px-5 py-3 rounded-xl shadow-2xl text-sm font-bold transition-all
          ${toast.type === 'error'
            ? 'bg-red-500/90 text-white'
            : 'bg-emerald-500/90 text-white'}`}>
          {toast.type === 'error' ? '✗ ' : '✓ '}{toast.msg}
        </div>
      )}

      {/* Header */}
      <header className="mb-10">
        <h1 className="text-4xl font-extrabold text-white tracking-tight">User Management</h1>
        <p className="text-slate-400 mt-2 font-medium">Approve requests, manage roles, and control access across the ERP network.</p>
      </header>

      {/* Tab Switcher */}
      <div className="flex gap-2 mb-8 border-b border-slate-800 pb-0">
        {[
          { key: 'waitlist', label: 'Approval Waitlist', icon: '⏳', count: waitlist.length },
          { key: 'users',    label: 'All Users',         icon: '👥', count: allUsers.filter(u => u.approved).length },
        ].map(t => (
          <button
            key={t.key}
            onClick={() => setTab(t.key)}
            className={`flex items-center gap-2 px-6 py-3 font-bold text-sm border-b-2 transition-all
              ${tab === t.key
                ? 'border-brand text-brand'
                : 'border-transparent text-slate-500 hover:text-slate-300'}`}
          >
            <span>{t.icon}</span>
            {t.label}
            {t.key === 'waitlist' && waitlist.length > 0 && (
              <span className="ml-1 px-2 py-0.5 bg-amber-500 text-black text-xs font-black rounded-full">
                {waitlist.length}
              </span>
            )}
          </button>
        ))}
      </div>

      {/* WAITLIST TAB */}
      {tab === 'waitlist' && (
        <div>
          {loading ? (
            <Skeleton />
          ) : waitlist.length === 0 ? (
            <EmptyState icon="🎉" title="No Pending Requests" desc="All signup requests have been reviewed." />
          ) : (
            <div className="space-y-4">
              {waitlist.map(user => (
                <div key={user.id} className="glass-card flex flex-col sm:flex-row sm:items-center justify-between gap-4 p-5 border border-amber-500/10 hover:border-amber-500/25 transition-colors">
                  <div className="flex items-center gap-4">
                    <div className="w-11 h-11 rounded-2xl bg-amber-500/10 border border-amber-500/20 flex items-center justify-center text-amber-400 font-black text-lg select-none">
                      {user.fullName?.[0]?.toUpperCase() ?? '?'}
                    </div>
                    <div>
                      <p className="text-white font-bold">{user.fullName}</p>
                      <p className="text-slate-500 text-sm">@{user.username} · {user.email}</p>
                      <p className="text-slate-600 text-xs mt-0.5">{new Date(user.createdAt).toLocaleDateString('en-IN', { day: 'numeric', month: 'short', year: 'numeric' })}</p>
                    </div>
                  </div>

                  <div className="flex items-center gap-3">
                    <Badge label={user.role?.replace('_', ' ')} colorClass={ROLE_COLORS[user.role] ?? 'bg-slate-700 text-slate-400 border-slate-600'} />
                    <button
                      onClick={() => handleApprove(user.id)}
                      disabled={actionLoading === user.id}
                      className="px-5 py-2 bg-emerald-600 hover:bg-emerald-500 disabled:opacity-50 text-white text-sm font-bold rounded-xl transition-all active:scale-95 flex items-center gap-2"
                    >
                      {actionLoading === user.id ? <Spinner /> : '✓ Approve'}
                    </button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {/* ALL USERS TAB */}
      {tab === 'users' && (
        <div>
          {loading ? (
            <Skeleton />
          ) : allUsers.length === 0 ? (
            <EmptyState icon="👤" title="No Users Found" desc="No users registered in the system yet." />
          ) : (
            <div className="glass-card overflow-hidden p-0">
              <table className="w-full text-sm">
                <thead>
                  <tr className="border-b border-slate-800">
                    {['User', 'Email', 'Role', 'Status', 'Actions'].map(h => (
                      <th key={h} className="text-left px-5 py-4 text-xs font-bold text-slate-500 uppercase tracking-widest">{h}</th>
                    ))}
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-800/60">
                  {allUsers.map(user => (
                    <tr key={user.id} className="hover:bg-slate-800/30 transition-colors">
                      <td className="px-5 py-4">
                        <div className="flex items-center gap-3">
                          <div className="w-9 h-9 rounded-xl bg-brand/10 border border-brand/20 flex items-center justify-center text-brand font-black text-sm select-none">
                            {user.fullName?.[0]?.toUpperCase() ?? '?'}
                          </div>
                          <div>
                            <p className="text-white font-semibold">{user.fullName}</p>
                            <p className="text-slate-500 text-xs">@{user.username}</p>
                          </div>
                        </div>
                      </td>

                      <td className="px-5 py-4 text-slate-400 text-xs">{user.email}</td>

                      {/* Role Cell with inline dropdown */}
                      <td className="px-5 py-4">
                        <div className="flex items-center gap-2">
                          <select
                            value={roleEdit[user.id] ?? user.role}
                            onChange={e => setRoleEdit(prev => ({ ...prev, [user.id]: e.target.value }))}
                            className="bg-slate-900 border border-slate-700 rounded-lg px-2 py-1 text-xs text-slate-200 focus:outline-none focus:border-brand/40 transition-all"
                          >
                            {ROLES.map(r => (
                              <option key={r} value={r} className="bg-slate-950">{r.replace(/_/g, ' ')}</option>
                            ))}
                          </select>
                          {roleEdit[user.id] && roleEdit[user.id] !== user.role && (
                            <button
                              onClick={() => handleRoleChange(user.id)}
                              disabled={actionLoading === user.id}
                              className="px-2.5 py-1 bg-brand hover:bg-brand-hover text-white text-xs font-bold rounded-lg transition-all disabled:opacity-50"
                            >
                              {actionLoading === user.id ? <Spinner /> : 'Save'}
                            </button>
                          )}
                        </div>
                      </td>

                      {/* Status badges */}
                      <td className="px-5 py-4">
                        <div className="flex flex-col gap-1">
                          {!user.approved ? (
                            <Badge label="Pending" colorClass="bg-amber-500/10 text-amber-400 border-amber-500/20" />
                          ) : user.active ? (
                            <Badge label="Active" colorClass="bg-emerald-500/10 text-emerald-400 border-emerald-500/20" />
                          ) : (
                            <Badge label="Blocked" colorClass="bg-red-500/10 text-red-400 border-red-500/20" />
                          )}
                        </div>
                      </td>

                      {/* Actions */}
                      <td className="px-5 py-4">
                        <div className="flex items-center gap-2">
                          {!user.approved && (
                            <button
                              onClick={() => handleApprove(user.id)}
                              disabled={actionLoading === user.id}
                              className="px-3 py-1.5 bg-emerald-600/80 hover:bg-emerald-600 text-white text-xs font-bold rounded-lg transition-all disabled:opacity-50"
                            >
                              {actionLoading === user.id ? <Spinner /> : '✓ Approve'}
                            </button>
                          )}
                          {user.approved && (
                            <button
                              onClick={() => handleToggleStatus(user.id, user.active)}
                              disabled={actionLoading === user.id}
                              className={`px-3 py-1.5 text-white text-xs font-bold rounded-lg transition-all disabled:opacity-50
                                ${user.active
                                  ? 'bg-red-600/80 hover:bg-red-600'
                                  : 'bg-slate-600 hover:bg-slate-500'}`}
                            >
                              {actionLoading === user.id ? <Spinner /> : user.active ? '⛔ Block' : '✓ Unblock'}
                            </button>
                          )}
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      )}
    </div>
  );
}

// ── Sub-components ─────────────────────────────────────────────────────────
const Spinner = () => (
  <div className="w-3.5 h-3.5 border-2 border-white/30 border-t-white rounded-full animate-spin inline-block" />
);

const Skeleton = () => (
  <div className="space-y-3 animate-pulse">
    {[1, 2, 3].map(i => (
      <div key={i} className="glass-card h-20 bg-slate-800/50" />
    ))}
  </div>
);

const EmptyState = ({ icon, title, desc }) => (
  <div className="glass-card py-16 text-center">
    <div className="text-5xl mb-4">{icon}</div>
    <p className="text-white font-bold text-lg">{title}</p>
    <p className="text-slate-500 text-sm mt-1">{desc}</p>
  </div>
);
