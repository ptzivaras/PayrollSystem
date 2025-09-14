import { useQuery } from '@tanstack/react-query'
import { listEmployees } from '../api/employees'
import { listDepartments } from '../api/departments'
import { useEffect, useMemo, useState } from 'react'
import Pagination from '../components/Pagination'
import EmployeeForm from '../components/EmployeeForm'

export default function EmployeesPage() {
  const [q, setQ] = useState('')
  const [departmentId, setDepartmentId] = useState('') // string to allow empty
  const [page, setPage] = useState(0)
  const size = 10

  // Employees
  const { data, isLoading, error, refetch, isFetching } = useQuery({
    queryKey: ['employees', { q, departmentId, page, size }],
    queryFn: () => listEmployees({
      q,
      departmentId: departmentId ? Number(departmentId) : '',
      page,
      size
    }),
    keepPreviousData: true
  })

  // Departments (used as a simple array)
  const deptQ = useQuery({
    queryKey: ['departments'],
    queryFn: listDepartments,
    staleTime: 5 * 60 * 1000
  })
  const departments = useMemo(() => Array.isArray(deptQ.data) ? deptQ.data : [], [deptQ.data])

  // reset page on filters change
  useEffect(() => { setPage(0) }, [q, departmentId])

  const totalPages = data?.totalPages ?? 0
  const currentPage = data?.number ?? page

  return (
    <section>
      <h2>Employees</h2>

      {/* Filters */}
      <div style={{ display: 'flex', gap: 8, margin: '12px 0', flexWrap: 'wrap' }}>
        <input
          placeholder="Search by name/email"
          value={q}
          onChange={(e) => setQ(e.target.value)}
        />

        <select
          value={departmentId}
          onChange={(e) => setDepartmentId(e.target.value)}
          disabled={deptQ.isLoading}
        >
          <option value="">All departments</option>
          {departments.map(d => (
            <option key={d.id} value={d.id}>
              {d.name} ({d.code})
            </option>
          ))}
        </select>

        <button onClick={() => refetch()} disabled={isFetching}>
          {isFetching ? 'Filtering…' : 'Apply'}
        </button>
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
