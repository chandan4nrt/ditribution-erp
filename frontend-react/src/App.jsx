import React, { useState, useEffect } from 'react';
import axios from 'axios';
import * as Icons from 'lucide-react';
import './App.css';

const API_BASE_URL = 'http://localhost:8080/api/menus';
// const MENU_MASTER_URL = 'http://localhost:8080/api/v1/menumaster';
const MENU_MASTER_URL = 'http://192.168.10.25:9090/api/v1/menumaster';

const DynamicIcon = ({ name, className }) => {
  const IconComponent = Icons[name] || Icons.Circle;
  return <IconComponent size={18} className={className} />;
};

const SidebarItem = ({ item }) => {
  const [isOpen, setIsOpen] = useState(false);
  const hasChildren = item.subMenus && item.subMenus.length > 0;

  return (
    <div className="sidebar-group">
      <div
        className={`sidebar-item ${hasChildren ? 'expandable' : ''}`}
        onClick={() => hasChildren && setIsOpen(!isOpen)}
      >
        <div className="item-content">
          <DynamicIcon name={item.icon} className="icon" />
          <span className="label">{item.menuName}</span>
        </div>
        {hasChildren && (
          <Icons.ChevronDown size={14} className={`chevron ${isOpen ? 'open' : ''}`} />
        )}
      </div>
      {isOpen && hasChildren && (
        <div className="sub-menu">
          {item.subMenus.map(child => (
            <SidebarItem key={child.id} item={child} />
          ))}
        </div>
      )}
    </div>
  );
};

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
        response = await axios.post(`${API_BASE_URL}/${formData.parentId}/submenus`, payload);
      } else {
        response = await axios.post(API_BASE_URL, payload);
      }

      onMenuAdded(response.data);
      setFormData({ menuName: '', icon: 'Folder', sequence: 1, pageName: '', parentId: '' });
      alert('Menu created successfully!');
    } catch (error) {
      console.error('Error adding menu:', error);
      alert('Failed to add menu');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="form-card card">
      <h2>Create New Menu</h2>
      <p>Add dynamically to Sidebar or create Submenus</p>
      <form onSubmit={handleSubmit}>
        <div className="form-row">
          <div className="form-group">
            <label>Menu Name</label>
            <input
              name="menuName"
              value={formData.menuName}
              onChange={handleChange}
              required
              placeholder="e.g. Sales"
            />
          </div>
          <div className="form-group">
            <label>Icon (Lucide Name)</label>
            <select name="icon" value={formData.icon} onChange={handleChange}>
              <option value="Home">Home</option>
              <option value="Settings">Settings</option>
              <option value="BarChart">Stats</option>
              <option value="Users">Users</option>
              <option value="Layout">Dashboard</option>
              <option value="FileText">Reports</option>
              <option value="Folder">Folder</option>
              <option value="Layers">Layer</option>
            </select>
          </div>
        </div>

        <div className="form-row">
          <div className="form-group">
            <label>Sequence</label>
            <input
              type="number"
              name="sequence"
              value={formData.sequence}
              onChange={handleChange}
            />
          </div>
          <div className="form-group">
            <label>Role Code</label>
            <select name="roleCode" value={formData.roleCode} onChange={handleChange}>
              <option value="USER">USER</option>
              <option value="VENDOR">VENDOR</option>
            </select>
          </div>
        </div>

        <div className="form-group">
          <label>Page Name (Slug)</label>
          <input
            name="pageName"
            value={formData.pageName}
            onChange={handleChange}
            placeholder="e.g. sales-view"
          />
        </div>

        <div className="form-group">
          <label>Parent Menu (Optional)</label>
          <select
            name="parentId"
            value={formData.parentId}
            onChange={handleChange}
          >
            <option value="">None (Top Level)</option>
            {flattenMenus(existingMenus).map(m => (
              <option key={m.id} value={m.id}>{m.name}</option>
            ))}
          </select>
        </div>

        <button type="submit" disabled={isSubmitting}>
          {isSubmitting ? 'Creating...' : 'Create Dynamic Menu'}
        </button>
      </form>
    </div>
  );
};

function App() {
  const [menus, setMenus] = useState([]);
  const [currentRole, setCurrentRole] = useState('USER');
  const [loading, setLoading] = useState(true);

  const fetchMenus = async (role) => {
    setLoading(true);
    try {
      const response = await axios.get(`${MENU_MASTER_URL}/${role || currentRole}`);
      setMenus(response.data);
    } catch (error) {
      console.error('Error fetching menus:', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchMenus(currentRole);
  }, [currentRole]);

  const handleMenuAdded = () => {
    fetchMenus(); // Refresh the list
  };

  return (
    <div className="app-container">
      {/* Sidebar Section */}
      <aside className="sidebar">
        <div className="sidebar-header">
          <Icons.ShieldCheck className="brand-icon" />
          <span className="brand-name">NRT Console</span>
        </div>
        <nav className="nav-container">
          <div className="nav-label">Navigation</div>
          {loading ? (
            <div className="nav-loading">Loading menus...</div>
          ) : (
            menus.length === 0 ? <div className="nav-empty">No menus yet</div> :
              menus.map(item => <SidebarItem key={item.id} item={item} />)
          )}
        </nav>
        <div className="sidebar-footer">
          <div className="role-switcher">
            <label>View Mode:</label>
            <div className="role-btns">
              <button
                className={currentRole === 'USER' ? 'active' : ''}
                onClick={() => setCurrentRole('USER')}
              >USER</button>
              <button
                className={currentRole === 'VENDOR' ? 'active' : ''}
                onClick={() => setCurrentRole('VENDOR')}
              >VENDOR</button>
            </div>
          </div>
          <div className="user-badge">
            <div className="avatar">{currentRole === 'USER' ? 'JD' : 'VN'}</div>
            <div className="user-info">
              <span className="user-name">{currentRole === 'USER' ? 'John Doe' : 'Vendor One'}</span>
              <span className="user-role">{currentRole}</span>
            </div>
          </div>
        </div>
      </aside>

      {/* Main Content Content */}
      <main className="main-content">
        <header className="content-header">
          <h1>Dynamic Menu Management</h1>
          {/* <div className="header-actions">
            <button className="secondary-btn"><Icons.Bell size={18} /></button>
            <button className="primary-btn">Export Config</button>
          </div> */}
        </header>

        <section className="dashboard-grid">
          <div className="welcome-card card">
            <div className="hero-text">
              <h2>Welcome to the Menu Builder</h2>
              {/* <p>Configure your application navigation dynamically in real-time. Changes are immediately persisted to the backend.</p> */}
            </div>
            {/* <Icons.Zap size={60} className="glow-icon" /> */}
          </div>

          <MenuForm onMenuAdded={handleMenuAdded} existingMenus={menus} />

          <div className="data-card card">
            <h3>API Status</h3>
            <div className="status-item">
              <span className="dot active"></span>
              <span>Menu Endpoint Connected</span>
            </div>
            <div className="status-item">
              <span className="dot active"></span>
              <span>Recursive Persistence API active</span>
            </div>
            <div className="status-stats">
              <div className="stat">
                <span className="val">{menus.length}</span>
                <span className="label">Root Items</span>
              </div>
              <div className="stat">
                <span className="val">Active</span>
                <span className="label">System State</span>
              </div>
            </div>
          </div>
        </section>
      </main>
    </div>
  );
}

export default App;
