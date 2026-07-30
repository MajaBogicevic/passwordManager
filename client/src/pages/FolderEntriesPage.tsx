import { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { UnlockGate } from '../components/vault/UnlockGate';
import { EntryCard } from '../components/entries/EntryCard';
import { EntryForm, type EntryFormValues } from '../components/entries/EntryForm';
import { Modal } from '../components/ui/Modal';
import { Button } from '../components/ui/button';
import { entriesApi } from '../api/entriesApi';
import { folderApi } from '../api/folderApi';
import { useVaultStore } from '../store/vaultStore';
import type { PasswordEntry } from '../types/entry';
import type { Folder } from '../types/folder';
import '../styles/pages/VaultPage.css';
import '../styles/pages/FoldersPage.css';

function FolderEntriesContent() {
  const { id } = useParams<{ id: string }>();
  const folderId = Number(id);
  const setUnlocked = useVaultStore((state) => state.setUnlocked);

  const [folder, setFolder] = useState<Folder | null>(null);
  const [entries, setEntries] = useState<PasswordEntry[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isCreateOpen, setIsCreateOpen] = useState(false);
  const [editingEntry, setEditingEntry] = useState<PasswordEntry | null>(null);

  const loadFolder = async () => {
    const response = await folderApi.getAll();
    const found = response.data.find((f) => f.id === folderId) ?? null;
    setFolder(found);
  };

  const loadEntries = async () => {
    setIsLoading(true);
    try {
      const response = await entriesApi.getByFolder(folderId);
      setEntries(response.data);
    } catch (err: any) {
      if (err?.apiError?.code === 'VAULT_LOCKED') {
        setUnlocked(false);
      } else {
        setEntries([]);
      }
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadFolder();
    loadEntries();
  }, [folderId]);

  const handleCreate = async (values: EntryFormValues) => {
    await entriesApi.create({
      title: values.title,
      url: values.url || null,
      username: values.username,
      plainPassword: values.plainPassword,
      notes: values.notes || null,
      folderId: values.folderId,
    });
    setIsCreateOpen(false);
    await loadEntries();
  };

  const handleUpdate = async (values: EntryFormValues) => {
    if (!editingEntry) return;
    await entriesApi.update({
      entryId: editingEntry.id,
      title: values.title,
      url: values.url || null,
      username: values.username,
      plainPassword: values.plainPassword,
      notes: values.notes || null,
      folderId: values.folderId,
    });
    setEditingEntry(null);
    await loadEntries();
  };

  const handleDelete = async (entryId: number) => {
    if (!confirm('Da li sigurno želiš da obrišeš ovaj unos?')) return;
    await entriesApi.delete(entryId);
    await loadEntries();
  };

  return (
    <div className="vault-page">
      <div className="vault-page-header">
        <div>
          <Link to="/folders" className="folders-page-back-link">
            ← Nazad na foldere
          </Link>
          <h1 className="vault-page-title">{folder ? folder.name : 'Folder'}</h1>
        </div>
        <Button onClick={() => setIsCreateOpen(true)} className="border-2 border-border nb-shadow nb-shadow-hover">
          + Novi unos
        </Button>
      </div>

      {isLoading && <p className="vault-page-empty">Učitavanje...</p>}

      {!isLoading && entries.length === 0 && (
        <p className="vault-page-empty">Ovaj folder je prazan.</p>
      )}

      <div className="vault-page-list">
        {entries.map((entry) => (
          <EntryCard key={entry.id} entry={entry} onEdit={setEditingEntry} onDelete={handleDelete} />
        ))}
      </div>

      <Modal isOpen={isCreateOpen} onClose={() => setIsCreateOpen(false)} title="Novi unos">
        <EntryForm onSubmit={handleCreate} submitLabel="Sačuvaj" defaultFolderId={folderId} />
      </Modal>

      <Modal isOpen={editingEntry !== null} onClose={() => setEditingEntry(null)} title="Izmeni unos">
        {editingEntry && (
          <EntryForm initialEntry={editingEntry} onSubmit={handleUpdate} submitLabel="Sačuvaj izmene" />
        )}
      </Modal>
    </div>
  );
}

export function FolderEntriesPage() {
  return (
    <UnlockGate>
      <FolderEntriesContent />
    </UnlockGate>
  );
}