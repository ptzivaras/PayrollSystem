import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { listEmployees, createEmployee } from '../api/employees'
import { useState } from 'react'

export default function EmployeesPage() {
  const qc = useQueryClient()
  const [q, setQ] = useState('')

  const { data, isLoading, error } = useQuery({
    queryKey: ['employees', { q }],
    queryFn: () => listEmployees({ q, page: 0, size: 20 })
  })

  const createMut = useMutation({
    mutationFn: createEmployee,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['employees'] })
  })

  return (
    <section>
      <h2>Employees</h2>
      <div style={{ display: 'flex', gap: 8, margin: '12px 0' }}>
        <input
          placeholder="Search by name/email"
          value={q}
          onChange={(e) => setQ(e.target.value)}
        />
        <button onClick={() => qc.invalidateQueries({ queryKey: ['employees'] })}>Search</button>
      </div>

      {isLoading && <p>Loading…</p>}
      {error && <p style={{ color: 'crimson' }}>{String(error)}</p>}

      <table border="1" cellPadding="6">
        <thead>
          <tr>
            <th>ID</th><th>Name</th><th>Email</th><th>Dept</th><th>Salary</th>
          </tr>
        </thead>
        <tbody>
          {data?.content?.map(e => (
            <tr key={e.id}>
              <td>{e.id}</td>
              <td>{e.firstName} {e.lastName}</td>
              <td>{e.email}</td>
              <td>{e.departmentId ?? '-'}</td>
              <td>{e.baseSalary}</td>
            </tr>
          ))}
        </tbody>
      </table>

      <hr style={{ margin: '16px 0' }} />
      <button onClick={() => {
        const payload = {
          firstName: 'Ada',
          lastName: 'Lovelace',
          email: `ada${Math.floor(Math.random()*10000)}@example.com`,
          baseSalary: 1600.00,
          hireDate: new Date().toISOString().slice(0,10),
          active: true
        }
        createMut.mutate(payload)
      }}>
        Quick add employee
      </button>
    </section>
  )
}
