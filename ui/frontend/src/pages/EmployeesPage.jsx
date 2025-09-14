import { useQuery } from '@tanstack/react-query'
import { listEmployees } from '../api/employees'
import { listDepartments } from '../api/departments'
import { useEffect, useMemo, useState } from 'react'
import Pagination from '../components/Pagination'
import EmployeeForm from '../components/EmployeeForm'

export default function EmployeesPage() {
  // filters
  const [q, setQ] = useState('')
  const [departmentId, setDepartmentId] = useState('') // string for the <select>
  // paging
  const [page, setPage] = useState(0)
  const size = 10

  // departments for filter dropdown
  const depsQ = useQuery({
    queryKey: ['departments'],
    queryFn: listDepartments,
    staleTime: 5 * 60_000,
  })

  // employees (respect filters + paging)
  const employeesQ = useQuery({
    queryKey: ['employees', { q, departmentId, page, size }],
    queryFn: () =>
      listEmployees({
        q,
        departmentId: departmentId || undefined, // avoid sending empty string
        page,
        size,
      }),
    keepPreviousData: true,
  })

  // when a filter changes, go back to first page
  useEffect(() => {
    setPage(0)
  }, [q, departmentId])

  const totalPages = employeesQ.data?.totalPages ?? 0
  const currentPage = employeesQ.data?.number ?? page

  const departments = useMemo(() => depsQ.data ?? [], [depsQ.data])

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
        >
          <option value="">All departments</option>
          {departments.map((d) => (
            <option key={d.id} value={d.id}>
              {d.name} ({d.code})
            </option>
          ))}
        </select>

        <button onClick={() => employeesQ.refetch()}>Apply</button>
        {(q || departmentId) && (
          <button
            type="button"
            onClick={() => {
              setQ('')
              setDepartmentId('')
            }}
          >
            Clear
          </button>
        )}
      </div>

      {(employeesQ.isLoading || employeesQ.isFetching) && <p>Loading…</p>}
      {employeesQ.error && (
        <p style={{ color: 'crimson' }}>{String(employeesQ.error)}</p>
      )}
      {depsQ.error && (
        <p style={{ color: 'crimson' }}>Departments: {String(depsQ.error)}</p>
      )}

      <table border="1" cellPadding="6" style={{ width: '100%', marginTop: 8 }}>
        <thead>
          <tr>
            <th>ID</th><th>Name</th><th>Email</th><th>Dept</th><th>Salary</th>
          </tr>
        </thead>
        <tbody>
          {employeesQ.data?.content?.map((e) => (
            <tr key={e.id}>
              <td>{e.id}</td>
              <td>
                {e.firstName} {e.lastName}
              </td>
              <td>{e.email}</td>
              <td>{e.departmentName ?? '-'}</td>
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
