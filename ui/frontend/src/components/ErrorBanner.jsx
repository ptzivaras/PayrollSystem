export default function ErrorBanner({ error }) {
  if (!error) return null
  return (
    <p style={{
      background: '#fee',
      color: '#900',
      padding: '8px 12px',
      borderRadius: 4,
      marginBottom: 12
    }}>
      {String(error)}
    </p>
  )
}
