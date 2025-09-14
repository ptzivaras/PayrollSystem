import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { listDepartments, createDepartment } from '../api/departments'

export default function DepartmentsPage() {
  const qc = useQueryClient()
  const { data, isLoading, error } = useQuery({
    queryKey: ['departments'],
    queryFn: listDepartments
  })

  const createMut = useMutation({
    mutationFn: createDepartment,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['departments'] })
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
      <button onClick={() => createMut.mutate({
        name: 'Engineering ' + Math.floor(Math.random()*1000),
        code: 'ENG' + Math.floor(Math.random()*1000)
      })}>
        Quick add department
      </button>
    </section>
  )
}
