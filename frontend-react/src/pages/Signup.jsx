import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import axios from 'axios';

const ROLES = [
  { value: 'PURCHASE_MANAGER', label: 'Purchase Manager' },
  { value: 'SALES_MANAGER',    label: 'Sales Manager' },
  { value: 'WAREHOUSE_MANAGER',label: 'Warehouse Manager' },
  { value: 'ACCOUNTANT',       label: 'Accountant' },
  { value: 'VIEWER',           label: 'Viewer (Read-only)' },
];

const Signup = () => {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    fullName: '',
    username: '',
    email: '',
    password: '',
    role: 'VIEWER',
  });
  const [isLoading, setIsLoading] = useState(false);
  const [success, setSuccess] = useState(false);
  const [error, setError] = useState('');

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSignup = async (e) => {
    e.preventDefault();
    setError('');
    setIsLoading(true);
    try {
      await axios.post('/api/auth/signup', formData);
      setSuccess(true);
    } catch (err) {
      setError(err.response?.data?.message || 'Signup failed. Please try again.');
    } finally {
      setIsLoading(false);
    }
  };

  if (success) {
    return (
      <div className="min-h-screen bg-slate-950 flex justify-center items-center relative overflow-hidden px-4">
        <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[600px] h-[600px] bg-emerald-500/10 rounded-full blur-[120px] pointer-events-none" />
        <div className="glass-card w-full max-w-md z-10 p-10 text-center border border-emerald-500/20">
          <div className="w-20 h-20 rounded-full bg-emerald-500/10 border border-emerald-500/30 flex items-center justify-center mx-auto mb-6">
            <svg className="w-10 h-10 text-emerald-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
            </svg>
          </div>
          <h2 className="text-2xl font-bold text-white mb-2">Request Submitted!</h2>
          <p className="text-slate-400 text-sm mb-6 leading-relaxed">
            Your account request has been received. An administrator will review your application and activate your account. You will be able to login once approved.
          </p>
          <div className="p-4 bg-amber-500/10 border border-amber-500/20 rounded-xl mb-8">
            <p className="text-amber-400 text-xs font-semibold uppercase tracking-widest mb-1">Status</p>
            <p className="text-amber-300 text-sm font-bold">⏳ Pending Admin Approval</p>
          </div>
          <Link to="/login" className="block w-full py-3 rounded-xl bg-brand text-white font-bold text-sm hover:bg-brand-hover transition-colors">
            Back to Login
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-slate-950 flex justify-center items-center relative overflow-hidden py-12 px-4">
      <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[700px] h-[700px] bg-indigo-500/10 rounded-full blur-[100px] pointer-events-none" />

      <div className="glass-card w-full max-w-md z-10 p-8 border border-slate-700/50">
        <div className="text-center mb-8">
          <div className="inline-flex items-center gap-2 px-3 py-1 bg-amber-500/10 border border-amber-500/20 rounded-full text-amber-400 text-xs font-semibold uppercase tracking-widest mb-4">
            <span className="w-1.5 h-1.5 rounded-full bg-amber-400 animate-pulse" />
            Requires Admin Approval
          </div>
          <h2 className="text-3xl font-bold text-white mb-2">Request Access</h2>
          <p className="text-slate-400 text-sm">Select your role and submit your access request</p>
        </div>

        <form onSubmit={handleSignup} className="flex flex-col gap-5">
          {error && (
            <div className="bg-red-500/10 border border-red-500/50 text-red-400 text-sm p-3 rounded-xl text-center">
              {error}
            </div>
          )}

          <div>
            <label className="block text-xs font-bold text-slate-500 uppercase tracking-widest mb-1.5" htmlFor="fullName">Full Name</label>
            <input
              id="fullName" name="fullName" type="text" required
              value={formData.fullName} onChange={handleChange}
              className="w-full bg-slate-900/50 border border-slate-700 rounded-xl px-4 py-3 text-slate-100 placeholder:text-slate-600 focus:outline-none focus:border-brand/50 focus:ring-1 focus:ring-brand/30 transition-all"
              placeholder="John Doe"
            />
          </div>

          <div>
            <label className="block text-xs font-bold text-slate-500 uppercase tracking-widest mb-1.5" htmlFor="username">Username</label>
            <input
              id="username" name="username" type="text" required
              value={formData.username} onChange={handleChange}
              className="w-full bg-slate-900/50 border border-slate-700 rounded-xl px-4 py-3 text-slate-100 placeholder:text-slate-600 focus:outline-none focus:border-brand/50 focus:ring-1 focus:ring-brand/30 transition-all"
              placeholder="johndoe"
            />
          </div>

          <div>
            <label className="block text-xs font-bold text-slate-500 uppercase tracking-widest mb-1.5" htmlFor="email">Email Address</label>
            <input
              id="email" name="email" type="email" required
              value={formData.email} onChange={handleChange}
              className="w-full bg-slate-900/50 border border-slate-700 rounded-xl px-4 py-3 text-slate-100 placeholder:text-slate-600 focus:outline-none focus:border-brand/50 focus:ring-1 focus:ring-brand/30 transition-all"
              placeholder="john@company.com"
            />
          </div>

          <div>
            <label className="block text-xs font-bold text-slate-500 uppercase tracking-widest mb-1.5" htmlFor="password">Password</label>
            <input
              id="password" name="password" type="password" required
              value={formData.password} onChange={handleChange}
              className="w-full bg-slate-900/50 border border-slate-700 rounded-xl px-4 py-3 text-slate-100 placeholder:text-slate-600 focus:outline-none focus:border-brand/50 focus:ring-1 focus:ring-brand/30 transition-all"
              placeholder="At least 6 characters"
            />
          </div>

          <div>
            <label className="block text-xs font-bold text-slate-500 uppercase tracking-widest mb-1.5" htmlFor="role">Requested Role</label>
            <select
              id="role" name="role" required
              value={formData.role} onChange={handleChange}
              className="w-full bg-slate-900/50 border border-slate-700 rounded-xl px-4 py-3 text-slate-100 focus:outline-none focus:border-brand/50 focus:ring-1 focus:ring-brand/30 transition-all appearance-none cursor-pointer"
            >
              {ROLES.map(r => (
                <option key={r.value} value={r.value} className="bg-slate-950">{r.label}</option>
              ))}
            </select>
          </div>

          <button
            type="submit"
            disabled={isLoading}
            className="w-full bg-brand hover:bg-brand-hover disabled:opacity-50 disabled:cursor-not-allowed text-white font-bold py-3 px-4 rounded-xl shadow-lg shadow-brand/20 transition-all active:scale-[0.98] mt-2 flex justify-center items-center gap-2"
          >
            {isLoading ? (
              <>
                <div className="w-5 h-5 border-2 border-white/30 border-t-white rounded-full animate-spin" />
                Submitting...
              </>
            ) : (
              'Submit Access Request'
            )}
          </button>
        </form>

        <p className="text-center text-sm text-slate-400 mt-8">
          Already have an account? <Link to="/login" className="text-brand font-semibold hover:underline">Log in</Link>
        </p>
      </div>
    </div>
  );
};

export default Signup;
