import { useState } from 'react';
import type { PasswordEntry } from '../../types/entry';
import { entriesApi } from '../../api/entriesApi';
import editIcon from '../../assets/edit-pen-svgrepo-com.svg';
import deleteIcon from '../../assets/delete-svgrepo-com.svg';
import linkIcon from '../../assets/link-svgrepo-com.svg';
import '../../styles/entries/EntryCard.css';

const CLIPBOARD_CLEAR_MS = 20_000;

interface EntryCardProps {
  entry: PasswordEntry;
  onEdit: (entry: PasswordEntry) => void;
  onDelete: (entryId: number) => void;
}

function copyWithAutoClear(text: string) {
  navigator.clipboard.writeText(text);
  setTimeout(() => {
    navigator.clipboard.readText().then((current) => {
      if (current === text) {
        navigator.clipboard.writeText('');
      }
    });
  }, CLIPBOARD_CLEAR_MS);
}

export function EntryCard({ entry, onEdit, onDelete }: EntryCardProps) {
  const [revealedPassword, setRevealedPassword] = useState<string | null>(null);
  const [isRevealing, setIsRevealing] = useState(false);
  const [copiedField, setCopiedField] = useState<'username' | 'password' | null>(null);

  const handleReveal = async () => {
    setIsRevealing(true);
    try {
      const response = await entriesApi.reveal(entry.id);
      setRevealedPassword(response.data.password);
    } finally {
      setIsRevealing(false);
    }
  };

  const handleCopyUsername = () => {
    copyWithAutoClear(entry.username);
    setCopiedField('username');
    setTimeout(() => setCopiedField(null), 1500);
  };

  const handleCopyPassword = async () => {
    let passwordToCopy = revealedPassword;
    if (!passwordToCopy) {
      const response = await entriesApi.reveal(entry.id);
      passwordToCopy = response.data.password;
    }
    copyWithAutoClear(passwordToCopy);
    setCopiedField('password');
    setTimeout(() => setCopiedField(null), 1500);
    entriesApi.logCopy(entry.id).catch(() => {});
  };

  const handleOpenSite = () => {
    if (entry.url) {
      window.open(entry.url, '_blank');
    }
  };

  return (
    <div className="entry-card">
      <div className="entry-card-main">
        <div className="entry-card-title-row">
          <span className="entry-card-title">{entry.title}</span>
          {entry.url && (
            <button className="entry-card-link-btn" onClick={handleOpenSite} title="Otvori sajt">
              <img src={linkIcon} className="entry-card-icon" alt="" />
            </button>
          )}
        </div>
        <div className="entry-card-username-row">
          <span className="entry-card-username">{entry.username}</span>
          <button className="entry-card-mini-btn" onClick={handleCopyUsername}>
            {copiedField === 'username' ? 'Kopirano' : 'Kopiraj'}
          </button>
        </div>

        {revealedPassword ? (
          <div className="entry-card-password-row">
            <span className="entry-card-password">{revealedPassword}</span>
            <button className="entry-card-mini-btn" onClick={handleCopyPassword}>
              {copiedField === 'password' ? 'Kopirano' : 'Kopiraj'}
            </button>
            <button className="entry-card-mini-btn" onClick={() => setRevealedPassword(null)}>
              Sakrij
            </button>
          </div>
        ) : (
          <div className="entry-card-password-row">
            <span className="entry-card-password-dots">••••••••••</span>
            <button className="entry-card-mini-btn" onClick={handleReveal} disabled={isRevealing}>
              {isRevealing ? '...' : 'Prikaži'}
            </button>
            <button className="entry-card-mini-btn" onClick={handleCopyPassword}>
              {copiedField === 'password' ? 'Kopirano' : 'Kopiraj'}
            </button>
          </div>
        )}

        {entry.notes && <p className="entry-card-notes">{entry.notes}</p>}
      </div>

      <div className="entry-card-actions">
        <button className="entry-card-action-btn" onClick={() => onEdit(entry)}>
          <img src={editIcon} className="entry-card-icon" alt="" />
          Izmeni
        </button>
        <button className="entry-card-action-btn entry-card-action-btn-danger" onClick={() => onDelete(entry.id)}>
          <img src={deleteIcon} className="entry-card-icon" alt="" />
          Obriši
        </button>
      </div>
    </div>
  );
}