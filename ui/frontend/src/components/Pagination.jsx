export default function Pagination({ page, totalPages, onPageChange }) {
  if (totalPages <= 1) return null

  const prev = () => onPageChange(Math.max(0, page - 1))
  const next = () => onPageChange(Math.min(totalPages - 1, page + 1))

  return (
    <div style={{ display: 'flex', gap: 8, alignItems: 'center', marginTop: 12 }}>
      <button disabled={page === 0} onClick={prev}>Prev</button>
      <span>Page {page + 1} / {totalPages}</span>
      <button disabled={page >= totalPages - 1} onClick={next}>Next</button>
    </div>
  )
}
