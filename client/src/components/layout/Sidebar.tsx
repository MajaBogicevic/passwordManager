import { NavLink } from 'react-router-dom';
import { useAuthStore } from '../../store/authStore';
import { useVaultStore } from '../../store/vaultStore';
import { vaultApi } from '../../api/vaultApi';
import avatarIcon from '../../assets/avatar-svgrepo-com.svg';
import logoutIcon from '../../assets/logout-svgrepo-com.svg';
import keyIcon from '../../assets/key-svgrepo-com.svg';
import '../../styles/layout/Sidebar.css';

const navItems = [
  { to: '/vault', label: 'Sef' },
  { to: '/folders', label: 'Folderi' },
  { to: '/generator', label: 'Generator lozinki' },
  { to: '/activity', label: 'Bezbednosna aktivnost' },
  { to: '/profile', label: 'Profil' },
];

export function Sidebar() {
  const username = useAuthStore((state) => state.username);
  const clearSession = useAuthStore((state) => state.clearSession);
  const setUnlocked = useVaultStore((state) => state.setUnlocked);

  const handleLockVault = async () => {
    try {
      await vaultApi.lock();
    } finally {
      setUnlocked(false);
    }
  };

  const handleLogout = async () => {
    clearSession();
    setUnlocked(false);
  };

  return (
    <aside className="sidebar">
      <div className="sidebar-header">
        <span className="sidebar-logo">PasswordMan</span>
      </div>

      <nav className="sidebar-nav">
        {navItems.map((item) => (
          <NavLink
            key={item.to}
            to={item.to}
            className={({ isActive }) => `sidebar-nav-item ${isActive ? 'sidebar-nav-item-active' : ''}`}
          >
            <span>{item.label}</span>
          </NavLink>
        ))}
      </nav>

      <div className="sidebar-footer">
        {username && (
          <div className="sidebar-username">
            <img src={avatarIcon} className="sidebar-icon" alt="" />
            {username}
          </div>
        )}
        <button className="sidebar-action-btn" onClick={handleLockVault}>
          <img src={keyIcon} className="sidebar-icon" alt="" />
          Zaključaj sef
        </button>
        <button className="sidebar-action-btn sidebar-action-btn-danger" onClick={handleLogout}>
          <img src={logoutIcon} className="sidebar-icon" alt="" />
          Odjavi se
        </button>
      </div>
    </aside>
  );
}