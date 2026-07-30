import { useState, type FormEvent } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { authApi } from '../api/authApi';
import { useAuthStore } from '../store/authStore';
import { Button } from '../components/ui/button';
import { Input } from '../components/ui/input';
import { Label } from '../components/ui/label';
import viewIcon from '../assets/view-svgrepo-com.svg';
import hideIcon from '../assets/hide-svgrepo-com.svg';
import '../styles/pages/AuthPages.css';

export function LoginPage() {
  const navigate = useNavigate();
  const setTokens = useAuthStore((state) => state.setTokens);
  const setUsername = useAuthStore((state) => state.setUsername);
 
  const [username, setUsernameField] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
 
  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();
    setError(null);
    if (!username || !password) {
      setError('Popuni sva polja.');
      return;
    }
    setIsLoading(true);
 
    try {
      const response = await authApi.login({ username, password });
      setTokens(response.data.accessToken, response.data.refreshToken);
      setUsername(username);
      navigate('/profile');
    } catch {
      setError('Pogrešno korisničko ime ili lozinka.');
    } finally {
      setIsLoading(false);
    }
  };
 
  return (
    <div className="auth-page">
      <form className="auth-card" onSubmit={handleSubmit} noValidate>
        <h1 className="auth-title">Prijava</h1>
 
        <div className="brutalist-container">
          <Input
            id="username"
            className="brutalist-input"
            placeholder="unesite korisničko ime"
            value={username}
            onChange={(e) => setUsernameField(e.target.value)}
            required
          />
          <Label htmlFor="username" className="brutalist-label">
            Korisničko ime
          </Label>
        </div>
 
        <div className="brutalist-container">
          <Input
            id="password"
            type={showPassword ? 'text' : 'password'}
            className="brutalist-input"
            placeholder="unesite lozinku"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
          <Label htmlFor="password" className="brutalist-label">
            Lozinka
          </Label>
          <button
            type="button"
            className="brutalist-toggle-visibility"
            onClick={() => setShowPassword((v) => !v)}
            tabIndex={-1}
          >
            <img src={showPassword ? hideIcon : viewIcon} alt="" />
          </button>
        </div>
 
        {error && <div className="auth-error">{error}</div>}
 
        <Button
          type="submit"
          disabled={isLoading}
          className="auth-submit border-2 border-border nb-shadow nb-shadow-hover"
        >
          {isLoading ? 'Učitavanje...' : 'Prijavi se'}
        </Button>
 
        <p className="auth-footer-text">
          Nemate nalog? <Link to="/register">Registruj se</Link>
        </p>
      </form>
    </div>
  );
}