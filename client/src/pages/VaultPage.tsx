import { useEffect, useState, type ChangeEvent } from 'react';
import { UnlockGate } from '../components/vault/UnlockGate';
import { EntryCard } from '../components/entries/EntryCard';
import { EntryForm, type EntryFormValues } from '../components/entries/EntryForm';
import { Modal } from '../components/ui/Modal';
import { Button } from '../components/ui/button';
import { Input } from '../components/ui/input';
import { entriesApi } from '../api/entriesApi';
import { useVaultStore } from '../store/vaultStore';
import type { PasswordEntry } from '../types/entry';
import searchIcon from '../assets/search-svgrepo-com.svg';
import '../styles/pages/VaultPage.css';

function VaultContent() {
  const setUnlocked = useVaultStore((state) => state.setUnlocked);
  const [entries, setEntries] = useState<PasswordEntry[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isCreateOpen, setIsCreateOpen] = useState(false);
  const [editingEntry, setEditingEntry] = useState<PasswordEntry | null>(null);
  const [searchQuery, setSearchQuery] = useState('');

  const loadEntries = async () => {
    setIsLoading(true);
    try {
      const response = await entriesApi.getAll();
      setEntries(response.data);
    } catch (err: any) {
      if (err?.apiError?.code === 'VAULT_LOCKED') {
        setUnlocked(false);
      }
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadEntries();
  }, []);

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
    if (!confirm('Da li sigurno želite da obrišete ovaj unos?')) return;
    await entriesApi.delete(entryId);
    await loadEntries();
  };

  const handleSearch = async () => {
    if (!searchQuery.trim()) {
      await loadEntries();
      return;
    }
    setIsLoading(true);
    try {
      const response = await entriesApi.search(searchQuery);
      setEntries((response.data as any).items ?? []);
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

  return (
    <div className="vault-page">
      <div className="vault-page-header">
        <h1 className="vault-page-title">Sef</h1>
        <Button onClick={() => setIsCreateOpen(true)} className="border-2 border-border nb-shadow nb-shadow-hover">
         + Novi unos
        </Button>
      </div>

      <div className="vault-page-search">
        <Input
          className="border-2 border-border"
          placeholder="Pretraži po nazivu..."
          value={searchQuery}
          onChange={(e: ChangeEvent<HTMLInputElement>) => setSearchQuery(e.target.value)}
          onKeyDown={(e) => e.key === 'Enter' && handleSearch()}
        />
        <Button variant="secondary" onClick={handleSearch} className="border-2 border-border">
          <img src={searchIcon} className="vault-search-icon" alt="" />
          Pretraži
        </Button>
      </div>

      {isLoading && <p className="vault-page-empty">Učitavanje...</p>}

      {!isLoading && entries.length === 0 && (
        <p className="vault-page-empty">Nemate još ni jednu lozinku</p>
      )}

      <div className="vault-page-list">
        {entries.map((entry) => (
          <EntryCard key={entry.id} entry={entry} onEdit={setEditingEntry} onDelete={handleDelete} />
        ))}
      </div>

      <Modal isOpen={isCreateOpen} onClose={() => setIsCreateOpen(false)} title="Novi unos">
        <EntryForm onSubmit={handleCreate} submitLabel="Sačuvaj" />
      </Modal>

      <Modal isOpen={editingEntry !== null} onClose={() => setEditingEntry(null)} title="Izmeni unos">
        {editingEntry && (
          <EntryForm initialEntry={editingEntry} onSubmit={handleUpdate} submitLabel="Sačuvaj izmene" />
        )}
      </Modal>
    </div>
  );
}

export function VaultPage() {
  return (
    <UnlockGate>
      <VaultContent />
    </UnlockGate>
  );
}