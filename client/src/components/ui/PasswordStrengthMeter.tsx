interface PasswordStrengthMeterProps {
  password: string;
}

function calculateStrength(password: string) {
  let score = 0;
  if (password.length >= 8) score++;
  if (password.length >= 12) score++;
  if (/[a-z]/.test(password) && /[A-Z]/.test(password)) score++;
  if (/\d/.test(password)) score++;
  if (/[^A-Za-z0-9]/.test(password)) score++;

  const levels = [
    { label: 'Vrlo slaba', color: '#f87171' },
    { label: 'Slaba', color: '#fb923c' },
    { label: 'Srednja', color: '#fbbf24' },
    { label: 'Dobra', color: '#a3e635' },
    { label: 'Jaka', color: '#4ade80' },
    { label: 'Vrlo jaka', color: '#22c55e' },
  ];

  return { score, ...levels[score] };
}

export function PasswordStrengthMeter({ password }: PasswordStrengthMeterProps) {
  if (!password) return null;

  const { score, label, color } = calculateStrength(password);
  const percent = (score / 5) * 100;

  return (
    <div className="password-strength">
      <div className="password-strength-bar-track">
        <div className="password-strength-bar-fill" style={{ width: `${percent}%`, backgroundColor: color }} />
      </div>
      <span className="password-strength-label" style={{ color }}>
        {label}
      </span>
    </div>
  );
}