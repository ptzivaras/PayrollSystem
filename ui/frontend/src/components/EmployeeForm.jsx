import { useMutation, useQueryClient } from '@tanstack/react-query'
import { createEmployee } from '../api/employees'
import { useState } from 'react'

export default function EmployeeForm() {
  const qc = useQueryClient()
  const [form, setForm] = useState({
    firstName: '',
    lastName: '',
    email: '',
    baseSalary: 1500,
    hireDate: new Date().toISOString().slice(0, 10),
    active: true
  })

  const mut = useMutation({
    mutationFn: createEmployee,
    onSuccess: () => {
      setForm((f) => ({ ...f, firstName: '', lastName: '', email: '' }))
      qc.invalidateQueries({ queryKey: ['employees'] })
    }
  })

  const onChange = (e) => {
    const { name, value, type, checked } = e.target
    setForm((f) => ({ ...f, [name]: type === 'checkbox' ? checked : value }))
  }

  const onSubmit = (e) => {
    e.preventDefault()
    mut.mutate({
      ...form,
      baseSalary: Number(form.baseSalary)
    })
  }

  return (
    <form onSubmit={onSubmit} style={{ display: 'grid', gap: 8, maxWidth: 520 }}>
      <h3>Create Employee</h3>
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 8 }}>
        <input name="firstName" placeholder="First name" required value={form.firstName} onChange={onChange} />
        <input name="lastName" placeholder="Last name" required value={form.lastName} onChange={onChange} />
      </div>
      <input name="email" type="email" placeholder="email@example.com" required value={form.email} onChange={onChange} />
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 8 }}>
        <input name="baseSalary" type="number" min="0" step="0.01" required value={form.baseSalary} onChange={onChange} />
        <input name="hireDate" type="date" required value={form.hireDate} onChange={onChange} />
      </div>
      <label style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
        <input name="active" type="checkbox" checked={form.active} onChange={onChange} />
        Active
      </label>
      <button type="submit" disabled={mut.isPending}>
        {mut.isPending ? 'Saving…' : 'Create'}
      </button>
      {mut.error && <p style={{ color: 'crimson' }}>{String(mut.error)}</p>}
    </form>
  )
}
