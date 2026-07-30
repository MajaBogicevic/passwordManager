import { useEffect, useState, type FormEvent, type ChangeEvent } from 'react';
import { folderApi } from '../../api/folderApi';
import type { Folder } from '../../types/folder';
import type { PasswordEntry } from '../../types/entry';
import { Button } from '../ui/button';
import { Input } from '../ui/input';
import { Label } from '../ui/label';
import { PasswordStrengthMeter } from '../ui/PasswordStrengthMeter';
import '../../styles/ui/PasswordStrengthMeter.css';
import '../../styles/entries/EntryForm.css';
import { entriesApi } from '../../api/entriesApi';
import viewIcon from '../../assets/view-svgrepo-com.svg';
import hideIcon from '../../assets/hide-svgrepo-com.svg';

export interface EntryFormValues {
  title: string;
  url: string;
  username: string;
  plainPassword: string;
  notes: string;
  folderId: number;
}

interface EntryFormProps {
  initialEntry?: PasswordEntry;
  onSubmit: (values: EntryFormValues) => Promise<void>;
  submitLabel: string;
  defaultFolderId?: number;
}

export function EntryForm({ initialEntry, onSubmit, submitLabel, defaultFolderId }: EntryFormProps) {
  const [folders, setFolders] = useState<Folder[]>([]);
  const [title, setTitle] = useState(initialEntry?.title ?? '');
  const [url, setUrl] = useState(initialEntry?.url ?? '');
  const [username, setUsername] = useState(initialEntry?.username ?? '');
  const [plainPassword, setPlainPassword] = useState('');
  const [notes, setNotes] = useState(initialEntry?.notes ?? '');
  const [folderId, setFolderId] = useState<number>(initialEntry?.folderId ?? defaultFolderId ?? 0);
  const [error, setError] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isRevealingPassword, setIsRevealingPassword] = useState(false);
  const [showPassword, setShowPassword] = useState(false);

  useEffect(() => {
    folderApi.getAll().then((response) => {
      setFolders(response.data);
    });
  }, []);

  useEffect(() => {
    if (initialEntry) {
      handleRevealCurrentPassword();
    }
  }, [initialEntry]);

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();
    setError(null);

    setIsSubmitting(true);
    try {
      await onSubmit({ title, url, username, plainPassword, notes, folderId });
    } catch {
      setError('Došlo je do greške. Proverite unete podatke.');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleRevealCurrentPassword = async () => {
    if (!initialEntry) return;
    setIsRevealingPassword(true);
    try {
      const response = await entriesApi.reveal(initialEntry.id);
      setPlainPassword(response.data.password);
    } catch {
      setError('Nije uspelo učitavanje trenutne lozinke.');
    } finally {
      setIsRevealingPassword(false);
    }
  };

  return (
    <form className="entry-form" onSubmit={handleSubmit} noValidate>
      <div className="brutalist-container">
        <Input
          id="entryTitle"
          className="brutalist-input"
          placeholder="npr. Gmail"
          value={title}
          onChange={(e: ChangeEvent<HTMLInputElement>) => setTitle(e.target.value)}
          required
        />
        <Label htmlFor="entryTitle" className="brutalist-label">
          Naziv
        </Label>
      </div>

      <div className="brutalist-container">
        <Input
          id="entryUrl"
          className="brutalist-input"
          placeholder="https://example.com"
          value={url}
          onChange={(e: ChangeEvent<HTMLInputElement>) => setUrl(e.target.value)}
        />
        <Label htmlFor="entryUrl" className="brutalist-label">
          URL sajta
        </Label>
      </div>

      <div className="brutalist-container">
        <Input
          id="entryUsername"
          className="brutalist-input"
          placeholder="unesite korisničko ime ili email"
          value={username}
          onChange={(e: ChangeEvent<HTMLInputElement>) => setUsername(e.target.value)}
          required
        />
        <Label htmlFor="entryUsername" className="brutalist-label">
          Korisničko ime / email
        </Label>
      </div>

      <div className="brutalist-container">
        <Input
          id="entryPassword"
          type={showPassword ? 'text' : 'password'}
          className="brutalist-input"
          placeholder={initialEntry ? '' : 'unesi lozinku'}
          value={plainPassword}
          onChange={(e: ChangeEvent<HTMLInputElement>) => setPlainPassword(e.target.value)}
          required={!initialEntry}
        />
        <Label htmlFor="entryPassword" className="brutalist-label">
          {initialEntry ? 'Nova lozinka' : 'Lozinka'}
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

      <PasswordStrengthMeter password={plainPassword} />

      <div className="entry-form-field">
        <label className="entry-form-label" htmlFor="entryFolder">
          Folder
        </label>
        <select
          id="entryFolder"
          className="entry-form-select"
          value={folderId}
          onChange={(e) => setFolderId(Number(e.target.value))}
        >
          <option value={0}>Bez foldera</option>
          {folders.map((folder) => (
            <option key={folder.id} value={folder.id}>
              {folder.name}
            </option>
          ))}
        </select>
        {folders.length === 0 && (
          <span className="entry-form-hint">Nemate još nijedan folder.</span>
        )}
      </div>

      <div className="entry-form-field">
        <label className="entry-form-label" htmlFor="entryNotes">
          Beleške
        </label>
        <textarea
          id="entryNotes"
          className="entry-form-textarea"
          rows={3}
          value={notes}
          onChange={(e) => setNotes(e.target.value)}
        />
      </div>

      {error && <div className="entry-form-error">{error}</div>}

      <Button
        type="submit"
        disabled={isSubmitting}
        className="entry-form-submit border-2 border-border nb-shadow nb-shadow-hover"
      >
        {isSubmitting ? 'Učitavanje...' : submitLabel}
      </Button>
    </form>
  );
}