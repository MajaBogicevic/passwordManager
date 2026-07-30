import { useEffect, useState, type FormEvent, type ChangeEvent } from 'react';
import { Link } from 'react-router-dom';
import { UnlockGate } from '../components/vault/UnlockGate';
import { Modal } from '../components/ui/Modal';
import { Button } from '../components/ui/button';
import { Input } from '../components/ui/input';
import { Label } from '../components/ui/label';
import { folderApi } from '../api/folderApi';
import deleteIcon from '../assets/delete-svgrepo-com.svg';
import { entriesApi } from '../api/entriesApi';
import type { PasswordEntry } from '../types/entry';
import type { CreateFolderRequest, Folder } from '../types/folder';
import '../styles/pages/FoldersPage.css';

function CreateFolderForm({ onCreated, onClose }: { onCreated: () => void; onClose: () => void }) {
  const [folderName, setFolderName] = useState('');
  const [entries, setEntries] = useState<PasswordEntry[]>([]);
  const [selectedEntryIds, setSelectedEntryIds] = useState<Set<number>>(new Set());
  const [isLoadingEntries, setIsLoadingEntries] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    entriesApi
      .getAll()
      .then((response) => setEntries(response.data))
      .finally(() => setIsLoadingEntries(false));
  }, []);

  const toggleEntry = (entryId: number) => {
    setSelectedEntryIds((prev) => {
      const next = new Set(prev);
      if (next.has(entryId)) {
        next.delete(entryId);
      } else {
        next.add(entryId);
      }
      return next;
    });
  };

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();
    setError(null);

    if (!folderName) {
      setError('Unesite naziv foldera.');
      return;
    }

    setIsSubmitting(true);
    try {
      const createResponse = await folderApi.create({ folderName });
      const newFolderId = createResponse.data.folderId;

      const entriesToMove = entries.filter((entry) => selectedEntryIds.has(entry.id));

      await Promise.all(
        entriesToMove.map((entry) =>
          entriesApi.update({
            entryId: entry.id,
            title: entry.title,
            url: entry.url,
            username: entry.username,
            plainPassword: '',
            notes: entry.notes,
            folderId: newFolderId,
          })
        )
      );

      onCreated();
      onClose();
    } catch {
      setError('Greška prilikom kreiranja foldera.');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <form className="folder-form" onSubmit={handleSubmit} noValidate>
      <div className="brutalist-container">
        <Input
          id="folderName"
          className="brutalist-input"
          placeholder="npr. Posao, Lično, Društvene mreže"
          value={folderName}
          onChange={(e: ChangeEvent<HTMLInputElement>) => setFolderName(e.target.value)}
          autoFocus
          required
        />
        <Label htmlFor="folderName" className="brutalist-label">
          Naziv foldera
        </Label>
      </div>

      <div className="folder-form-entry-picker">
        <span className="folder-form-entry-picker-title">Ubaci postojeće lozinke (opciono)</span>

        {isLoadingEntries && <p className="folder-form-entry-picker-empty">Učitavanje...</p>}

        {!isLoadingEntries && entries.length === 0 && (
          <p className="folder-form-entry-picker-empty">Nemaš još nijednu sačuvanu lozinku.</p>
        )}

        {!isLoadingEntries && entries.length > 0 && (
          <div className="folder-form-entry-list">
            {entries.map((entry) => (
              <label key={entry.id} className="folder-form-entry-row">
                <input
                  type="checkbox"
                  checked={selectedEntryIds.has(entry.id)}
                  onChange={() => toggleEntry(entry.id)}
                />
                <span>{entry.title}</span>
                {entry.folderId !== 0 && entry.folderId !== null && (
                  <span className="folder-form-entry-current-badge">već u folderu</span>
                )}
              </label>
            ))}
          </div>
        )}
      </div>

      {error && <div className="folder-form-error">{error}</div>}
      <Button
        type="submit"
        disabled={isSubmitting}
        className="folder-form-submit border-2 border-border nb-shadow nb-shadow-hover"
      >
        {isSubmitting ? 'Učitavanje...' : 'Napravi folder'}
      </Button>
    </form>
  );
}

function RenameFolderForm({
  folder,
  onRenamed,
  onClose,
}: {
  folder: Folder;
  onRenamed: () => void;
  onClose: () => void;
}) {
  const [folderName, setFolderName] = useState(folder.name);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();
    setError(null);

    if (!folderName) {
      setError('Unesite naziv foldera.');
      return;
    }

    setIsSubmitting(true);
    try {
      await folderApi.rename({ folderId: folder.id, newName: folderName });
      onRenamed();
      onClose();
    } catch {
      setError('Greška prilikom preimenovanja foldera.');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <form className="folder-form" onSubmit={handleSubmit} noValidate>
      <div className="brutalist-container">
        <Input
          id="renameFolderName"
          className="brutalist-input"
          value={folderName}
          onChange={(e: ChangeEvent<HTMLInputElement>) => setFolderName(e.target.value)}
          autoFocus
          required
        />
        <Label htmlFor="renameFolderName" className="brutalist-label">
          Novi naziv
        </Label>
      </div>
      {error && <div className="folder-form-error">{error}</div>}
      <Button
        type="submit"
        disabled={isSubmitting}
        className="folder-form-submit border-2 border-border nb-shadow nb-shadow-hover"
      >
        {isSubmitting ? 'Učitavanje...' : 'Sačuvaj'}
      </Button>
    </form>
  );
}

function FoldersContent() {
  const [folders, setFolders] = useState<Folder[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isCreateOpen, setIsCreateOpen] = useState(false);
  const [renamingFolder, setRenamingFolder] = useState<Folder | null>(null);

  const loadFolders = async () => {
    setIsLoading(true);
    try {
      const response = await folderApi.getAll();
      setFolders(response.data);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadFolders();
  }, []);

  const handleDelete = async (folderId: number) => {
    if (!confirm('Folder će biti obrisan. Unosi koji su bili u njemu ostaju sačuvani i prebacuju se u "Bez foldera". Da li si sigurna?')) return;
    try {
      await folderApi.delete(folderId);
      await loadFolders();
    } catch (err: any) {
      alert(err?.apiError?.message ?? 'Greška prilikom brisanja foldera.');
    }
  };

  return (
    <div className="folders-page">
      <div className="folders-page-header">
        <h1 className="folders-page-title">Folderi</h1>
        <Button onClick={() => setIsCreateOpen(true)} className="border-2 border-border nb-shadow nb-shadow-hover">
          + Novi folder
        </Button>
      </div>

      {isLoading && <p className="folders-page-empty">Učitavanje...</p>}

      {!isLoading && folders.length === 0 && (
        <p className="folders-page-empty">
          Nemate još nijedan folder.
        </p>
      )}

      <div className="folders-page-list">
        {folders.map((folder) => (
          <div key={folder.id} className="folder-card">
            <Link to={`/folders/${folder.id}`} className="folder-card-name">
              {folder.name}
            </Link>
            <div className="folder-card-actions">
              <button className="folder-card-action-btn" onClick={() => setRenamingFolder(folder)}>
                Preimenuj
              </button>
              <button
                className="folder-card-action-btn folder-card-action-btn-danger"
                onClick={() => handleDelete(folder.id)}
              >
                <img src={deleteIcon} className="folder-card-icon" alt="" />
                Obriši
              </button>
            </div>
          </div>
        ))}
      </div>

      <Modal isOpen={isCreateOpen} onClose={() => setIsCreateOpen(false)} title="Novi folder">
        <CreateFolderForm onCreated={loadFolders} onClose={() => setIsCreateOpen(false)} />
      </Modal>

      <Modal isOpen={renamingFolder !== null} onClose={() => setRenamingFolder(null)} title="Preimenujte folder">
        {renamingFolder && (
          <RenameFolderForm
            folder={renamingFolder}
            onRenamed={loadFolders}
            onClose={() => setRenamingFolder(null)}
          />
        )}
      </Modal>
    </div>
  );
}

export function FoldersPage() {
  return (
    <UnlockGate>
      <FoldersContent />
    </UnlockGate>
  );
}