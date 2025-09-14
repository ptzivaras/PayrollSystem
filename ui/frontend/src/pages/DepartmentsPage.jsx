import { useQuery } from '@tanstack/react-query'
import { listDepartments } from '../api/departments'
import DepartmentForm from '../components/DepartmentForm'
import ErrorBanner from '../components/ErrorBanner'

export default function DepartmentsPage() {
  const { data, isLoading, error } = useQuery({
    queryKey: ['departments'],
    queryFn: listDepartments,
  })

  const rows = Array.isArray(data) ? data : []

  return (
    <section>
      <h2>Departments</h2>

      <ErrorBanner error={error} />
      {isLoading && <p>Loading…</p>}

      {!isLoading && rows.length === 0 && !error && <p>No departments.</p>}

      {rows.length > 0 && (
        <ul>
          {rows.map((d) => (
            <li key={d.id}>
              {d.name} ({d.code})
            </li>
          ))}
        </ul>
      )}

      <hr />
      <DepartmentForm />
    </section>
  )
}
