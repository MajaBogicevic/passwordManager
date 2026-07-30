import { useEffect, useState, type FormEvent, type ChangeEvent } from 'react';
import { useNavigate } from 'react-router-dom';
import { usersApi } from '../api/usersApi';
import { authApi } from '../api/authApi';
import { folderApi } from '../api/folderApi';
import { entriesApi } from '../api/entriesApi';
import { useAuthStore } from '../store/authStore';
import { useVaultStore } from '../store/vaultStore';
import { Button } from '../components/ui/button';
import { Input } from '../components/ui/input';
import { Label } from '../components/ui/label';
import { PasswordStrengthMeter } from '../components/ui/PasswordStrengthMeter';
import type { UserProfile } from '../types/user';
import editIcon from '../assets/edit-pen-svgrepo-com.svg';
import viewIcon from '../assets/view-svgrepo-com.svg';
import hideIcon from '../assets/hide-svgrepo-com.svg';
import '../styles/pages/ProfilePage.css';
import '../styles/ui/PasswordStrengthMeter.css';

export function ProfilePage() {
  const navigate = useNavigate();
  const clearSession = useAuthStore((state) => state.clearSession);
  const isUnlocked = useVaultStore((state) => state.isUnlocked);
  const setUnlocked = useVaultStore((state) => state.setUnlocked);

  const [profile, setProfile] = useState<UserProfile | null>(null);
  const [isLoadingProfile, setIsLoadingProfile] = useState(true);

  const [folderCount, setFolderCount] = useState<number | null>(null);
  const [entryCount, setEntryCount] = useState<number | null>(null);

  const [isChangePasswordOpen, setIsChangePasswordOpen] = useState(false);
  const [oldPassword, setOldPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmNewPassword, setConfirmNewPassword] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [showOldPassword, setShowOldPassword] = useState(false);
  const [showNewPassword, setShowNewPassword] = useState(false);
  const [showConfirmNewPassword, setShowConfirmNewPassword] = useState(false);

  useEffect(() => {
    usersApi
      .getMe()
      .then((response) => setProfile(response.data))
      .finally(() => setIsLoadingProfile(false));
  }, []);

  useEffect(() => {
    if (!isUnlocked) {
      setFolderCount(null);
      setEntryCount(null);
      return;
    }
    folderApi.getAll().then((response) => setFolderCount(response.data.length));
    entriesApi.getAll().then((response) => setEntryCount(response.data.length));
  }, [isUnlocked]);

  const handleToggleChangePassword = () => {
    setIsChangePasswordOpen((open) => !open);
    setError(null);
    setSuccess(null);
    setOldPassword('');
    setNewPassword('');
    setConfirmNewPassword('');
  };

  const handleChangePassword = async (event: FormEvent) => {
    event.preventDefault();
    setError(null);
    setSuccess(null);

    if (!oldPassword || !newPassword || !confirmNewPassword) {
      setError('Popuni sva polja.');
      return;
    }

    if (newPassword.length < 8) {
      setError('Nova lozinka mora imati bar 8 karaktera.');
      return;
    }

    if (newPassword !== confirmNewPassword) {
      setError('Nova lozinka i potvrda se ne poklapaju.');
      return;
    }

    setIsSubmitting(true);
    try {
      await authApi.changePassword({ oldPassword, newPassword });
      setSuccess('Lozinka je promenjena. Bićeš preusmerena na prijavu...');
      setTimeout(() => {
        clearSession();
        setUnlocked(false);
        navigate('/login');
      }, 1500);
    } catch (err: any) {
      setError(err?.apiError?.message ?? 'Greška prilikom promene lozinke. Proveri staru lozinku.');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="profile-page">
      <h1 className="profile-title">Profil</h1>

      {isLoadingProfile && <p className="profile-page-empty">Učitavanje...</p>}

      {profile && (
        <div className="profile-info-card">
          <div className="profile-info-row">
            <span className="profile-info-label">Korisničko ime</span>
            <span className="profile-info-value">{profile.username}</span>
          </div>
          <div className="profile-info-row">
            <span className="profile-info-label">Email</span>
            <span className="profile-info-value">{profile.email}</span>
          </div>
          <div className="profile-info-row">
            <span className="profile-info-label">Nalog kreiran</span>
            <span className="profile-info-value">
              {new Date(profile.createdAt).toLocaleDateString('sr-RS')}
            </span>
          </div>

          {isUnlocked && folderCount !== null && entryCount !== null && (
            <div className="profile-counts-row">
              <div className="profile-count-badge">
                <span className="profile-count-number">{folderCount}</span>
                <span className="profile-count-label">Foldera</span>
              </div>
              <div className="profile-count-badge">
                <span className="profile-count-number">{entryCount}</span>
                <span className="profile-count-label">Sačuvanih lozinki</span>
              </div>
            </div>
          )}
        </div>
      )}

      <Button
        variant="secondary"
        className="profile-change-password-toggle border-2 border-border"
        onClick={handleToggleChangePassword}
      >
        {!isChangePasswordOpen && <img src={editIcon} className="profile-icon" alt="" />}
        {isChangePasswordOpen ? 'x Otkaži' : 'Promeni lozinku'}
      </Button>

      <div className={`profile-password-section ${isChangePasswordOpen ? 'open' : ''}`}>
        <form onSubmit={handleChangePassword} className="profile-password-form" noValidate>
          <div className="brutalist-container">
            <Input
              id="oldPassword"
              type={showOldPassword ? 'text' : 'password'}
              className="brutalist-input"
              placeholder="unesite trenutnu lozinku"
              value={oldPassword}
              onChange={(e: ChangeEvent<HTMLInputElement>) => setOldPassword(e.target.value)}
              required
            />
            <Label htmlFor="oldPassword" className="brutalist-label">
              Trenutna lozinka
            </Label>
            <button
              type="button"
              className="brutalist-toggle-visibility"
              onClick={() => setShowOldPassword((v) => !v)}
              tabIndex={-1}
            >
              <img src={showOldPassword ? hideIcon : viewIcon} alt="" />
            </button>
          </div>

          <div className="brutalist-container">
            <Input
              id="newPassword"
              type={showNewPassword ? 'text' : 'password'}
              className="brutalist-input"
              placeholder="unesite novu lozinku"
              value={newPassword}
              onChange={(e: ChangeEvent<HTMLInputElement>) => setNewPassword(e.target.value)}
              required
            />
            <Label htmlFor="newPassword" className="brutalist-label">
              Nova lozinka
            </Label>
            <button
              type="button"
              className="brutalist-toggle-visibility"
              onClick={() => setShowNewPassword((v) => !v)}
              tabIndex={-1}
            >
              <img src={showNewPassword ? hideIcon : viewIcon} alt="" />
            </button>
          </div>
          <PasswordStrengthMeter password={newPassword} />

          <div className="brutalist-container">
            <Input
              id="confirmNewPassword"
              type={showConfirmNewPassword ? 'text' : 'password'}
              className="brutalist-input"
              placeholder="potvrdite novu lozinku"
              value={confirmNewPassword}
              onChange={(e: ChangeEvent<HTMLInputElement>) => setConfirmNewPassword(e.target.value)}
              required
            />
            <Label htmlFor="confirmNewPassword" className="brutalist-label">
              Potvrdite novu lozinku
            </Label>
            <button
              type="button"
              className="brutalist-toggle-visibility"
              onClick={() => setShowConfirmNewPassword((v) => !v)}
              tabIndex={-1}
            >
              <img src={showConfirmNewPassword ? hideIcon : viewIcon} alt="" />
            </button>
          </div>

          {error && <div className="profile-form-error">{error}</div>}
          {success && <div className="profile-form-success">{success}</div>}

          <Button
            type="submit"
            disabled={isSubmitting}
            className="profile-form-submit border-2 border-border nb-shadow nb-shadow-hover"
          >
            {isSubmitting ? 'Učitavanje...' : 'Sačuvaj novu lozinku'}
          </Button>
        </form>
      </div>
    </div>
  );
}