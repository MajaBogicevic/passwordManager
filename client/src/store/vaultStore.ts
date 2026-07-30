import { create } from 'zustand';

interface VaultState {
  isUnlocked: boolean;
  setUnlocked: (unlocked: boolean) => void;
}

export const useVaultStore = create<VaultState>((set) => ({
  isUnlocked: false,
  setUnlocked: (unlocked) => set({ isUnlocked: unlocked }),
}));