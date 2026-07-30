import { useState, type FormEvent, type ChangeEvent } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { authApi } from '../api/authApi';
import { Button } from '../components/ui/button';
import { Input } from '../components/ui/input';
import { Label } from '../components/ui/label';
import { PasswordStrengthMeter } from '../components/ui/PasswordStrengthMeter';
import viewIcon from '../assets/view-svgrepo-com.svg';
import hideIcon from '../assets/hide-svgrepo-com.svg';
import '../styles/ui/PasswordStrengthMeter.css';
import '../styles/pages/AuthPages.css';

export function RegisterPage() {
  const navigate = useNavigate();

  const [email, setEmail] = useState('');
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();
    if (!email || !username || !password || !confirmPassword) {
      setError('Popuni sva polja.');
      return;
    }
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
      setError('Unesi ispravnu email adresu.');
      return;
    }
    setError(null);

    if (password !== confirmPassword) {
      setError('Lozinke se ne poklapaju.');
      return;
    }

    setIsLoading(true);

    try {
      if (password.length < 8) {
        setError('Lozinka mora imati bar 8 karaktera.');
        return;
      }
      await authApi.register({ email, username, password, notes: null });
      setSuccess(true);
      setTimeout(() => navigate('/login'), 1200);
    } catch {
      setError('Registracija nije uspela. Proveri da li korisničko ime već postoji.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="auth-page">
      <form className="auth-card" onSubmit={handleSubmit} noValidate>
        <h1 className="auth-title">Registracija</h1>

        <div className="brutalist-container">
          <Input
            id="email"
            type="email"
            className="brutalist-input"
            placeholder="unesite email"
            value={email}
            onChange={(e: ChangeEvent<HTMLInputElement>) => setEmail(e.target.value)}
            required
          />
          <Label htmlFor="email" className="brutalist-label">
            Email
          </Label>
        </div>

        <div className="brutalist-container">
          <Input
            id="username"
            className="brutalist-input"
            placeholder="unesite korisničko ime"
            value={username}
            onChange={(e: ChangeEvent<HTMLInputElement>) => setUsername(e.target.value)}
            required
          />
          <Label htmlFor="username" className="brutalist-label">
            Korisničko ime
          </Label>
        </div>

        <PasswordStrengthMeter password={password} />

        <div className="brutalist-container">
          <Input
            id="password"
            type={showPassword ? 'text' : 'password'}
            className="brutalist-input"
            placeholder="unesite master lozinku"
            value={password}
            onChange={(e: ChangeEvent<HTMLInputElement>) => setPassword(e.target.value)}
            required
          />
          <Label htmlFor="password" className="brutalist-label">
            Master lozinka
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

        <div className="brutalist-container">
          <Input
            id="confirmPassword"
            type={showConfirmPassword ? 'text' : 'password'}
            className="brutalist-input"
            placeholder="potvrdite lozinku"
            value={confirmPassword}
            onChange={(e: ChangeEvent<HTMLInputElement>) => setConfirmPassword(e.target.value)}
            required
          />
          <Label htmlFor="confirmPassword" className="brutalist-label">
            Potvrdite lozinku
          </Label>
          <button
            type="button"
            className="brutalist-toggle-visibility"
            onClick={() => setShowConfirmPassword((v) => !v)}
            tabIndex={-1}
          >
            <img src={showConfirmPassword ? hideIcon : viewIcon} alt="" />
          </button>
        </div>

        {error && <div className="auth-error">{error}</div>}
        {success && <div className="auth-success">Uspešno registrovano! </div>}

        <Button
          type="submit"
          disabled={isLoading}
          className="auth-submit border-2 border-border nb-shadow nb-shadow-hover"
        >
          {isLoading ? 'Učitavanje...' : 'Registruj se'}
        </Button>

        <p className="auth-footer-text">
          Već imate nalog? <Link to="/login">Prijavi se</Link>
        </p>
      </form>
    </div>
  );
}