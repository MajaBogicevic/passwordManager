import { useEffect, useState, type ChangeEvent } from 'react';
import { auditApi } from '../api/auditApi';
import type { AuditLogEntry } from '../types/audit';
import '../styles/pages/SecurityActivityPage.css';

const PAGE_SIZE = 10;

const EVENT_TYPE_LABELS: Record<string, string> = {
  BUSINESS_ACTION: 'Akcija',
  USER_REGISTERED: 'Registracija',
  LOGIN: 'Prijava',
  REFRESH_TOKEN: 'Osvežavanje sesije',
  LOGOUT: 'Odjava',
  PASSWORD_CHANGED: 'Promena lozinke',
  VAULT_UNLOCK: 'Otključavanje vault-a',
  VAULT_LOCK: 'Zaključavanje vault-a',
  VAULT_AUTO_LOCK: 'Automatsko zaključavanje',
  PASSWORD_COPIED: 'Kopiranje lozinke',
};

const OUTCOME_LABELS: Record<string, { text: string; className: string }> = {
  SUCCESS: { text: 'Uspešno', className: 'audit-badge-success' },
  FAILURE: { text: 'Neuspešno', className: 'audit-badge-failure' },
  RATE_LIMITED: { text: 'Blokirano (previše pokušaja)', className: 'audit-badge-warning' },
  SECURITY_ALERT: { text: 'Bezbednosno upozorenje', className: 'audit-badge-alert' },
};

const OUTCOME_OPTIONS = [
  { value: '', label: 'Svi ishodi' },
  { value: 'SUCCESS', label: 'Uspešno' },
  { value: 'FAILURE', label: 'Neuspešno' },
  { value: 'RATE_LIMITED', label: 'Blokirano (previše pokušaja)' },
  { value: 'SECURITY_ALERT', label: 'Bezbednosno upozorenje' },
];

function formatTimestamp(timestamp: string) {
  return new Date(timestamp).toLocaleString('sr-RS');
}

export function SecurityActivityPage() {
  const [logs, setLogs] = useState<AuditLogEntry[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [eventTypeFilter, setEventTypeFilter] = useState('');
  const [outcomeFilter, setOutcomeFilter] = useState('');
  const [fromDate, setFromDate] = useState('');
  const [toDate, setToDate] = useState('');

  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const loadActivity = async (targetPage: number) => {
    setIsLoading(true);
    setError(null);
    try {
      const response = await auditApi.getActivity({
        eventType: eventTypeFilter || undefined,
        outcome: outcomeFilter || undefined,
        fromDate: fromDate || undefined,
        toDate: toDate || undefined,
        page: targetPage,
        size: PAGE_SIZE,
      });
      setLogs(response.data.items);
      setTotalPages(response.data.totalPages);
      setPage(response.data.page);
    } catch {
      setError('Greška prilikom učitavanja bezbednosne aktivnosti.');
      setLogs([]);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadActivity(0);
  }, [eventTypeFilter, outcomeFilter, fromDate, toDate]);

  const handlePrevPage = () => {
    if (page > 0) loadActivity(page - 1);
  };

  const handleNextPage = () => {
    if (page + 1 < totalPages) loadActivity(page + 1);
  };

  const handleResetFilters = () => {
    setEventTypeFilter('');
    setOutcomeFilter('');
    setFromDate('');
    setToDate('');
  };

  const getPageWindow = (): number[] => {
    const windowSize = 4;
    if (totalPages <= 0) return [];
    let start = Math.max(0, page - 1);
    let end = Math.min(totalPages, start + windowSize);
    start = Math.max(0, end - windowSize);
    const pages: number[] = [];
    for (let i = start; i < end; i++) pages.push(i);
    return pages;
  };

  const handleFirstPage = () => loadActivity(0);
  const handleLastPage = () => loadActivity(totalPages - 1);

  return (
    <div className="audit-page">
      <h1 className="audit-page-title">Bezbednosna aktivnost</h1>

      <div className="audit-filters">
        <div className="audit-filter-field">
          <label htmlFor="auditEventType">Događaj sadrži</label>
          <input
            id="auditEventType"
            type="text"
            placeholder="npr. lock, login, copy..."
            value={eventTypeFilter}
            onChange={(e: ChangeEvent<HTMLInputElement>) => setEventTypeFilter(e.target.value)}
          />
        </div>

        <div className="audit-filter-field">
          <label htmlFor="auditOutcome">Ishod</label>
          <select
            id="auditOutcome"
            value={outcomeFilter}
            onChange={(e) => setOutcomeFilter(e.target.value)}
          >
            {OUTCOME_OPTIONS.map((opt) => (
              <option key={opt.value} value={opt.value}>
                {opt.label}
              </option>
            ))}
          </select>
        </div>

        <div className="audit-filter-field">
          <label htmlFor="auditFromDate">Od datuma</label>
          <input
            id="auditFromDate"
            type="date"
            value={fromDate}
            onChange={(e: ChangeEvent<HTMLInputElement>) => setFromDate(e.target.value)}
          />
        </div>

        <div className="audit-filter-field">
          <label htmlFor="auditToDate">Do datuma</label>
          <input
            id="auditToDate"
            type="date"
            value={toDate}
            onChange={(e: ChangeEvent<HTMLInputElement>) => setToDate(e.target.value)}
          />
        </div>

        <button className="audit-reset-btn" onClick={handleResetFilters}>
          Resetuj filtere
        </button>
      </div>

      {isLoading && <p className="audit-page-empty">Učitavanje...</p>}
      {error && <p className="audit-page-empty">{error}</p>}

      {!isLoading && !error && logs.length === 0 && (
        <p className="audit-page-empty">Nema aktivnosti koja odgovara izabranim filterima.</p>
      )}

      {!isLoading && !error && logs.length > 0 && (
        <>
          <div className="audit-table-wrapper">
            <table className="audit-table">
              <thead>
                <tr>
                  <th>Vreme</th>
                  <th>Događaj</th>
                  <th>Ishod</th>
                  <th>IP adresa</th>
                  <th>Detalji</th>
                </tr>
              </thead>
              <tbody>
                {logs.map((log) => {
                  const outcome = OUTCOME_LABELS[log.outcome] ?? {
                    text: log.outcome,
                    className: 'audit-badge-default',
                  };
                  return (
                    <tr key={log.id}>
                      <td>{formatTimestamp(log.timestamp)}</td>
                      <td>{EVENT_TYPE_LABELS[log.eventType] ?? log.eventType}</td>
                      <td>
                        <span className={`audit-badge ${outcome.className}`}>{outcome.text}</span>
                      </td>
                      <td>{log.ipAddress ?? '—'}</td>
                      <td>{log.details ?? log.reasonCode ?? '—'}</td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>

          <div className="audit-pagination">
            <button onClick={handleFirstPage} disabled={page === 0} title="Prva strana">
              «
            </button>
            <button onClick={handlePrevPage} disabled={page === 0} title="Prethodna">
              ‹
            </button>

            {getPageWindow().map((p) => (
              <button
                key={p}
                className={`audit-page-number ${p === page ? 'audit-page-number-active' : ''}`}
                onClick={() => loadActivity(p)}
              >
                {p + 1}
              </button>
            ))}

            <button onClick={handleNextPage} disabled={page + 1 >= totalPages} title="Sledeća">
              ›
            </button>
            <button onClick={handleLastPage} disabled={page + 1 >= totalPages} title="Poslednja strana">
              »
            </button>
          </div>
        </>
      )}
    </div>
  );
}