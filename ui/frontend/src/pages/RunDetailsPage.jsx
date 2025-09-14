import { useParams } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { listItemsByRun } from '../api/payroll'

export default function RunDetailsPage() {
  const { runId } = useParams()
  const { data, isLoading, error } = useQuery({
    queryKey: ['run-items', runId],
    queryFn: () => listItemsByRun(runId, { page: 0, size: 100 })
  })

  return (
    <section>
      <h2>Run #{runId} — Items</h2>
      {isLoading && <p>Loading…</p>}
      {error && <p style={{ color: 'crimson' }}>{String(error)}</p>}

      <table border="1" cellPadding="6">
        <thead>
        <tr>
          <th>ID</th><th>Employee</th><th>Gross</th><th>Tax</th><th>Net</th><th>Notes</th>
        </tr>
        </thead>
        <tbody>
        {data?.content?.map(i => (
          <tr key={i.id}>
            <td>{i.id}</td>
            <td>{i.employeeId}</td>
            <td>{i.grossAmount}</td>
            <td>{i.taxAmount}</td>
            <td>{i.netAmount}</td>
            <td>{i.notes ?? ''}</td>
          </tr>
        ))}
        </tbody>
      </table>
    </section>
  )
}
