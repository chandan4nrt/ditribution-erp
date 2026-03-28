import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import axios from 'axios';

const Login = () => {
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const handleLogin = async (e) => {
    e.preventDefault();
    setError('');
    setIsLoading(true);
    
    try {
      const response = await axios.post('/api/auth/login', {
        username: email,
        password: password
      });
      
      const { token, ...userMetadata } = response.data;
      if (token) {
        localStorage.setItem('token', token);
        localStorage.setItem('user', JSON.stringify(userMetadata));
        navigate('/dashboard');
      } else {
        setError('No token received from backend.');
      }
    } catch (err) {
      console.error('Login error:', err);
      const msg = err.response?.data?.message || '';
      if (msg.toLowerCase().includes('pending') || msg.toLowerCase().includes('approval')) {
        setError('⏳ Your account is pending admin approval. Please wait for an administrator to review your request.');
      } else if (msg.toLowerCase().includes('blocked') || msg.toLowerCase().includes('disabled')) {
        setError('⛔ Your account has been blocked. Please contact an administrator.');
      } else {
        setError('Invalid username or password. Please try again.');
      }
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-slate-950 flex justify-center items-center relative overflow-hidden py-12 px-4 shadow-sm">
      {/* Dynamic Background */}
      <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[600px] h-[600px] bg-brand/10 rounded-full blur-[100px] pointer-events-none" />

      <div className="glass-card w-full max-w-md z-10 p-8">
        <div className="text-center mb-10">
          <h2 className="text-3xl font-bold bg-clip-text text-transparent bg-gradient-to-r from-white to-slate-400 mb-2">Welcome Back</h2>
          <p className="text-slate-400 text-sm">Sign in to access your dashboard</p>
        </div>

        <form onSubmit={handleLogin} className="flex flex-col gap-5">
          {error && (
            <div className="bg-red-500/10 border border-red-500/50 text-red-500 text-sm p-3 rounded-lg text-center">
              {error}
            </div>
          )}
          
          <div>
            <label className="block text-sm font-medium text-slate-300 mb-1.5" htmlFor="email">Email or Username</label>
            <input 
              id="email"
              type="text" 
              required
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className="w-full bg-slate-900/50 border border-slate-700 rounded-xl px-4 py-3 text-slate-100 placeholder:text-slate-600 focus:outline-none focus:border-brand/50 focus:ring-1 focus:ring-brand/50 transition-all font-sans"
              placeholder="you@company.com"
            />
          </div>

          <div>
             <div className="flex justify-between items-center mb-1.5">
               <label className="block text-sm font-medium text-slate-300" htmlFor="password">Password</label>
               <a href="#" className="text-xs text-brand hover:underline font-medium">Forgot password?</a>
             </div>
            <input 
              id="password"
              type="password" 
              required
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="w-full bg-slate-900/50 border border-slate-700 rounded-xl px-4 py-3 text-slate-100 placeholder:text-slate-600 focus:outline-none focus:border-brand/50 focus:ring-1 focus:ring-brand/50 transition-all font-sans"
              placeholder="••••••••"
            />
          </div>

          <button 
            type="submit" 
            disabled={isLoading}
            className="w-full bg-brand hover:bg-brand-hover disabled:bg-brand/50 disabled:cursor-not-allowed text-white font-semibold py-3 px-4 rounded-xl shadow-lg shadow-brand/20 transition-all active:scale-[0.98] mt-2 flex justify-center items-center gap-2"
          >
            {isLoading ? (
              <>
                <div className="w-5 h-5 border-2 border-white/30 border-t-white rounded-full animate-spin" />
                Signing in...
              </>
            ) : (
              'Sign In'
            )}
          </button>
        </form>

        <p className="text-center text-sm text-slate-400 mt-8">
          Don't have an account? <Link to="/signup" className="text-brand font-semibold hover:underline">Sign up</Link>
        </p>
      </div>
    </div>
  );
};

export default Login;
