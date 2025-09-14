import { useQuery } from '@tanstack/react-query'
import { listEmployees } from '../api/employees'
import { useEffect, useState } from 'react'
import Pagination from '../components/Pagination'
import EmployeeForm from '../components/EmployeeForm'

export default function EmployeesPage() {
  const [q, setQ] = useState('')
  const [page, setPage] = useState(0)
  const size = 10

  const { data, isLoading, error, refetch, isFetching } = useQuery({
    queryKey: ['employees', { q, page, size }],
    queryFn: () => listEmployees({ q, page, size }),
    keepPreviousData: true
  })

  useEffect(() => { setPage(0) }, [q])

  const totalPages = data?.totalPages ?? 0
  const currentPage = data?.number ?? page

  return (
    <section>
      <h2>Employees</h2>
      <div style={{ display: 'flex', gap: 8, margin: '12px 0' }}>
        <input
          placeholder="Search by name/email"
          value={q}
          onChange={(e) => setQ(e.target.value)}
        />
        <button onClick={() => refetch()}>Search</button>
      </div>

      {(isLoading || isFetching) && <p>Loading…</p>}
      {error && <p style={{ color: 'crimson' }}>{String(error)}</p>}

      <table border="1" cellPadding="6" style={{ width: '100%', marginTop: 8 }}>
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

      <Pagination
        page={currentPage}
        totalPages={totalPages}
        onPageChange={setPage}
      />

      <hr style={{ margin: '16px 0' }} />
      <EmployeeForm />
    </section>
  )
}
