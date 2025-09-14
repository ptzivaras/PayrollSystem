import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { createRun, listRuns, listItemsByRun, postRun } from '../api/payroll'
import { Link } from 'react-router-dom'
import { useState } from 'react'

export default function PayrollRunsPage() {
  const qc = useQueryClient()
  const [period, setPeriod] = useState(() => new Date().toISOString().slice(0,7) + '-01')

  const runsQ = useQuery({
    queryKey: ['runs', { period }],
    queryFn: () => listRuns({ period, page: 0, size: 20 })
  })

  const createMut = useMutation({
    mutationFn: (p) => createRun(p),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['runs'] })
  })

  const postMut = useMutation({
    mutationFn: (id) => postRun(id),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['runs'] })
  })

  return (
    <section>
      <h2>Payroll Runs</h2>
      <div style={{ display: 'flex', gap: 8, marginBottom: 12 }}>
        <input type="date" value={period}
               onChange={(e) => setPeriod(e.target.value)} />
        <button onClick={() => qc.invalidateQueries({ queryKey: ['runs'] })}>Filter</button>
        <button onClick={() => createMut.mutate({ period })}>Create Run (company-wide)</button>
      </div>

      {runsQ.isLoading && <p>Loading…</p>}
      {runsQ.error && <p style={{ color: 'crimson' }}>{String(runsQ.error)}</p>}

      <table border="1" cellPadding="6">
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
                  <button onClick={() => postMut.mutate(r.id)}>Post</button>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </section>
  )
}
