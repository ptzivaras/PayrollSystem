import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { createRun, listRuns, postRun } from '../api/payroll'
import { Link } from 'react-router-dom'
import { useEffect, useMemo, useState } from 'react'
import Pagination from '../components/Pagination'
import ErrorBanner from '../components/ErrorBanner'

function firstDay(dateLike = new Date()) {
  const d = new Date(dateLike)
  return new Date(d.getFullYear(), d.getMonth(), 1).toISOString().slice(0, 10)
}
function shiftMonth(periodISO, delta) {
  const d = periodISO ? new Date(periodISO) : new Date()
  const x = new Date(d.getFullYear(), d.getMonth() + delta, 1)
  return x.toISOString().slice(0, 10)
}

export default function PayrollRunsPage() {
  const qc = useQueryClient()
  const [period, setPeriod] = useState(() => firstDay())
  const [useCustom, setUseCustom] = useState(false)
  const [page, setPage] = useState(0)
  const size = 10

  const runsQ = useQuery({
    queryKey: ['runs', { period, page, size }],
    queryFn: () => listRuns({ period, page, size }),
    keepPreviousData: true,
  })

  useEffect(() => {
    setPage(0)
  }, [period])

  const createMut = useMutation({
    mutationFn: (p) => createRun(p),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['runs'] }),
  })
  const postMut = useMutation({
    mutationFn: (id) => postRun(id),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['runs'] }),
  })

  const totalPages = runsQ.data?.totalPages ?? 0
  const currentPage = runsQ.data?.number ?? page
  const rows = runsQ.data?.content ?? []

  const buttonsDisabled = runsQ.isFetching || createMut.isPending || postMut.isPending

  const header = useMemo(() => {
    const d = new Date(period)
    return isNaN(d)
      ? '—'
      : d.toLocaleDateString(undefined, { year: 'numeric', month: 'long' })
  }, [period])

  return (
    <section>
      <h2>Payroll Runs</h2>

      {/* Period controls */}
      <div
        style={{
          display: 'flex',
          gap: 8,
          alignItems: 'center',
          marginBottom: 12,
          flexWrap: 'wrap',
        }}
      >
        <strong>Period:</strong>
        <button
          disabled={buttonsDisabled}
          onClick={() => {
            setUseCustom(false)
            setPeriod(shiftMonth(period, -1))
          }}
        >
          Prev
        </button>
        <button
          disabled={buttonsDisabled}
          onClick={() => {
            setUseCustom(false)
            setPeriod(firstDay())
          }}
        >
          This month
        </button>
        <button
          disabled={buttonsDisabled}
          onClick={() => {
            setUseCustom(false)
            setPeriod(shiftMonth(period, +1))
          }}
        >
          Next
        </button>

        <label style={{ display: 'inline-flex', gap: 6, alignItems: 'center', marginLeft: 8 }}>
          <input
            type="checkbox"
            checked={useCustom}
            onChange={(e) => setUseCustom(e.target.checked)}
          />
          Custom
        </label>

        <input
          type="date"
          value={period}
          disabled={!useCustom || buttonsDisabled}
          onChange={(e) => setPeriod(e.target.value)}
        />

        <span style={{ opacity: 0.8 }}>({header})</span>

        <button
          onClick={() => qc.invalidateQueries({ queryKey: ['runs'] })}
          disabled={buttonsDisabled}
        >
          Apply
        </button>

        <button onClick={() => createMut.mutate({ period })} disabled={buttonsDisabled}>
          {createMut.isPending ? 'Creating…' : 'Create Run (company-wide)'}
        </button>
      </div>

      <ErrorBanner error={runsQ.error || createMut.error || postMut.error} />
      {runsQ.isLoading && <p>Loading…</p>}

      {!runsQ.isLoading && rows.length === 0 && !runsQ.error && <p>No payroll runs.</p>}

      {rows.length > 0 && (
        <table border="1" cellPadding="6" style={{ width: '100%' }}>
          <thead>
            <tr>
              <th>ID</th>
              <th>Period</th>
              <th>Dept</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {rows.map((r) => (
              <tr key={r.id}>
                <td>{r.id}</td>
                <td>{r.period}</td>
                <td>{r.departmentId ?? '-'}</td>
                <td>{r.status}</td>
                <td style={{ display: 'flex', gap: 8 }}>
                  <Link to={`/payroll/${r.id}`}>Details</Link>
                  {r.status !== 'POSTED' && (
                    <button onClick={() => postMut.mutate(r.id)} disabled={buttonsDisabled}>
                      {postMut.isPending ? 'Posting…' : 'Post'}
                    </button>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}

      <Pagination page={currentPage} totalPages={totalPages} onPageChange={setPage} />
    </section>
  )
}
