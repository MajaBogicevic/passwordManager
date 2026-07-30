import { useEffect, useState, type ChangeEvent } from 'react';
import { generatorApi } from '../api/generatorApi';
import { Button } from '../components/ui/button';
import '../styles/pages/GeneratorPage.css';

const CLIPBOARD_CLEAR_MS = 20_000;

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

export function GeneratorPage() {
  const [length, setLength] = useState(16);
  const [useUppercase, setUseUppercase] = useState(true);
  const [useLowercase, setUseLowercase] = useState(true);
  const [useDigits, setUseDigits] = useState(true);
  const [useSymbols, setUseSymbols] = useState(false);

  const [password, setPassword] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [copied, setCopied] = useState(false);

  const noOptionSelected = !useUppercase && !useLowercase && !useDigits && !useSymbols;

  const handleGenerate = async () => {
    if (noOptionSelected) {
      setError('Izaberi bar jednu grupu karaktera.');
      return;
    }
    setError(null);
    setIsLoading(true);
    try {
      const response = await generatorApi.generate({
        length,
        useUppercase,
        useLowercase,
        useDigits,
        useSymbols,
      });
      setPassword(response.data);
    } catch {
      setError('Greška prilikom generisanja lozinke.');
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    handleGenerate();
  }, []);

  const handleCopy = () => {
    if (!password) return;
    copyWithAutoClear(password);
    setCopied(true);
    setTimeout(() => setCopied(false), 1500);
  };

  return (
    <div className="generator-page">
      <h1 className="generator-title">Generator lozinki</h1>

      <div className="generator-result-row">
        <input className="generator-result-field" value={password} readOnly />
        <button className="generator-mini-btn" onClick={handleCopy} disabled={!password}>
          {copied ? 'Kopirano ✓' : 'Copy'}
        </button>
        <button className="generator-mini-btn" onClick={handleGenerate} disabled={isLoading || noOptionSelected}>
          Osveži
        </button>
      </div>

      <div className="generator-options">
        <div className="generator-option-row">
          <label htmlFor="genLength">Dužina: {length}</label>
          <input
            id="genLength"
            type="range"
            min={4}
            max={64}
            value={length}
            onChange={(e: ChangeEvent<HTMLInputElement>) => setLength(Number(e.target.value))}
          />
        </div>

        <label className="generator-checkbox-row">
          <span className="checkbox-container">
            <input type="checkbox" checked={useUppercase} onChange={(e) => setUseUppercase(e.target.checked)} />
            <span className="checkmark"></span>
          </span>
          Velika slova (A-Z)
        </label>

        <label className="generator-checkbox-row">
          <span className="checkbox-container">
            <input type="checkbox" checked={useLowercase} onChange={(e) => setUseLowercase(e.target.checked)} />
            <span className="checkmark"></span>
          </span>
          Mala slova (a-z)
        </label>

        <label className="generator-checkbox-row">
          <span className="checkbox-container">
            <input type="checkbox" checked={useDigits} onChange={(e) => setUseDigits(e.target.checked)} />
            <span className="checkmark"></span>
          </span>
          Brojevi (0-9)
        </label>

        <label className="generator-checkbox-row">
          <span className="checkbox-container">
            <input type="checkbox" checked={useSymbols} onChange={(e) => setUseSymbols(e.target.checked)} />
            <span className="checkmark"></span>
          </span>
          Simboli (!@#$...)
        </label>
      </div>

      {error && <div className="generator-error">{error}</div>}

      <Button
        onClick={handleGenerate}
        disabled={isLoading || noOptionSelected}
        className="border-2 border-border nb-shadow nb-shadow-hover"
      >
        {isLoading ? 'Učitavanje...' : 'Generišite novu lozinku'}
      </Button>
    </div>
  );
}