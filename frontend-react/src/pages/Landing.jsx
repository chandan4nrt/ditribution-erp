import React from 'react';
import { Link } from 'react-router-dom';

const Landing = () => {
  return (
    <div className="min-h-screen bg-slate-950 text-slate-50 flex flex-col justify-center items-center relative overflow-hidden">
      {/* Background decorations */}
      <div className="absolute top-[-10%] left-[-10%] w-96 h-96 bg-brand/20 rounded-full blur-[120px] pointer-events-none" />
      <div className="absolute bottom-[-10%] right-[-10%] w-96 h-96 bg-indigo-500/20 rounded-full blur-[120px] pointer-events-none" />
      
      <div className="z-10 text-center max-w-4xl px-6">
        <h1 className="text-5xl md:text-7xl font-bold tracking-tight mb-6">
          The Next-Gen <span className="text-transparent bg-clip-text bg-gradient-to-r from-brand to-indigo-400">Distribution ERP</span>
        </h1>
        <p className="text-lg md:text-xl text-slate-400 mb-10 max-w-2xl mx-auto">
          Streamline your inventory, sales, and supply chain with our completely modern, 
          real-time, and lightning-fast platform designed for scale.
        </p>

        <div className="flex flex-col sm:flex-row items-center justify-center gap-4">
          <Link 
            to="/signup" 
            className="px-8 py-4 rounded-xl bg-brand font-semibold text-white hover:bg-brand-hover hover:scale-105 transition-all shadow-lg shadow-brand/25 w-full sm:w-auto"
          >
            Get Started
          </Link>
          <Link 
            to="/login" 
            className="px-8 py-4 rounded-xl glass font-semibold hover:bg-slate-800 hover:scale-105 transition-all w-full sm:w-auto text-center"
          >
            Sign In
          </Link>
        </div>
      </div>
    </div>
  );
};

export default Landing;
