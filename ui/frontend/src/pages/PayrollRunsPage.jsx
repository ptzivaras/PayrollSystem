import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { createRun, listRuns, postRun } from '../api/payroll'
import { Link } from 'react-router-dom'
import { useEffect, useState } from 'react'
import Pagination from '../components/Pagination'

export default function PayrollRunsPage() {
  const qc = useQueryClient()
  const [period, setPeriod] = useState(() => new Date().toISOString().slice(0,7) + '-01')
  const [page, setPage] = useState(0)
  const size = 10

  const runsQ = useQuery({
    queryKey: ['runs', { period, page, size }],
    queryFn: () => listRuns({ period, page, size }),
    keepPreviousData: true
  })

  // When filter changes, go back to first page
  useEffect(() => { setPage(0) }, [period])

  const createMut = useMutation({
    mutationFn: (p) => createRun(p),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['runs'] })
  })

  const postMut = useMutation({
    mutationFn: (id) => postRun(id),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['runs'] })
  })

  const totalPages = runsQ.data?.totalPages ?? 0
  const currentPage = runsQ.data?.number ?? page

  return (
    <section>
      <h2>Payroll Runs</h2>
      <div style={{ display: 'flex', gap: 8, marginBottom: 12 }}>
        <input type="date" value={period}
               onChange={(e) => setPeriod(e.target.value)} />
        <button onClick={() => qc.invalidateQueries({ queryKey: ['runs'] })}>Filter</button>
        <button
          onClick={() => createMut.mutate({ period })}
          disabled={createMut.isPending}
        >
          {createMut.isPending ? 'Creating…' : 'Create Run (company-wide)'}
        </button>
      </div>

      {runsQ.isLoading && <p>Loading…</p>}
      {runsQ.error && <p style={{ color: 'crimson' }}>{String(runsQ.error)}</p>}

      <table border="1" cellPadding="6" style={{ width: '100%' }}>
        <thead>
          <tr>
            <th>ID</th><th>Period</th><th>Dept</th><th>Status</th><th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {runsQ.data?.content?.map(r => (
            <tr key={r.id}>
              <td>{r.id}</td>
              <td>{r.period}</td>
              <td>{r.departmentId ?? '-'}</td>
              <td>{r.status}</td>
              <td style={{ display: 'flex', gap: 8 }}>
                <Link to={`/payroll/${r.id}`}>Details</Link>
                {r.status !== 'POSTED' && (
                  <button
                    onClick={() => postMut.mutate(r.id)}
                    disabled={postMut.isPending}
                  >
                    {postMut.isPending ? 'Posting…' : 'Post'}
                  </button>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      <Pagination
        page={currentPage}
        totalPages={totalPages}
        onPageChange={setPage}
      />
    </section>
  )
}
