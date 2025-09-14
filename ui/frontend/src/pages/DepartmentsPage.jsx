import { useQuery } from '@tanstack/react-query'
import { listDepartments } from '../api/departments'
import DepartmentForm from '../components/DepartmentForm'

export default function DepartmentsPage() {
  const { data, isLoading, error } = useQuery({
    queryKey: ['departments'],
    queryFn: listDepartments
  })

  return (
    <section>
      <h2>Departments</h2>
      {isLoading && <p>Loading…</p>}
      {error && <p style={{ color: 'crimson' }}>{String(error)}</p>}

      <ul>
        {(data ?? []).map(d => (
          <li key={d.id}>{d.name} ({d.code})</li>
        ))}
      </ul>

      <hr />
      <DepartmentForm />
    </section>
  )
}
