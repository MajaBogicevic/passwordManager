import { useState, type FormEvent, type ReactNode, type ChangeEvent } from 'react';
import { vaultApi } from '../../api/vaultApi';
import { useVaultStore } from '../../store/vaultStore';
import { Button } from '../ui/button';
import { Input } from '../ui/input';
import { Label } from '../ui/label';
import '../../styles/vault/UnlockGate.css';

interface UnlockGateProps {
  children: ReactNode;
}

export function UnlockGate({ children }: UnlockGateProps) {
  const isUnlocked = useVaultStore((state) => state.isUnlocked);
  const setUnlocked = useVaultStore((state) => state.setUnlocked);

  const [masterPassword, setMasterPassword] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);

  if (isUnlocked) {
    return <>{children}</>;
  }

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();
    setError(null);

    if (!masterPassword) {
      setError('Unesite master lozinku.');
      return;
    }

    setIsLoading(true);

    try {
      await vaultApi.unlock({ masterPassword });
      setUnlocked(true);
    } catch {
      setError('Pogrešna master lozinka.');
    } finally {
      setIsLoading(false);
      setMasterPassword('');
    }
  };

  return (
    <div className="unlock-gate">
      <form className="unlock-gate-card" onSubmit={handleSubmit} noValidate>
        <h2 className="unlock-gate-title">Sef je zaključan</h2>
        <p className="unlock-gate-subtitle">Unesite master lozinku da pristupite sačuvanim lozinkama</p>

        <div className="brutalist-container" style={{ width: '100%' }}>
          <Input
            id="unlockGateMasterPassword"
            type="password"
            className="brutalist-input"
            placeholder="unesite master lozinku"
            value={masterPassword}
            onChange={(e: ChangeEvent<HTMLInputElement>) => setMasterPassword(e.target.value)}
            autoFocus
            required
          />
          <Label htmlFor="unlockGateMasterPassword" className="brutalist-label">
            Master lozinka
          </Label>
        </div>

        {error && <div className="unlock-gate-error">{error}</div>}

        <Button
          type="submit"
          disabled={isLoading}
          className="unlock-gate-submit border-2 border-border nb-shadow nb-shadow-hover"
        >
          {isLoading ? 'Učitavanje...' : 'Otključaj'}
        </Button>
      </form>
    </div>
  );
}