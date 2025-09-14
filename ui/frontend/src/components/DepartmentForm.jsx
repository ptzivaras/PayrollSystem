import { useMutation, useQueryClient } from '@tanstack/react-query'
import { createDepartment } from '../api/departments'
import { useState } from 'react'

export default function DepartmentForm() {
  const qc = useQueryClient()
  const [form, setForm] = useState({ name: '', code: '' })

  const mut = useMutation({
    mutationFn: createDepartment,
    onSuccess: () => {
      setForm({ name: '', code: '' })
      qc.invalidateQueries({ queryKey: ['departments'] })
    }
  })

  const onChange = (e) => setForm((f) => ({ ...f, [e.target.name]: e.target.value }))
  const onSubmit = (e) => { e.preventDefault(); mut.mutate(form) }

  return (
    <form onSubmit={onSubmit} style={{ display: 'grid', gap: 8, maxWidth: 420 }}>
      <h3>Create Department</h3>
      <input name="name" placeholder="Department name" required value={form.name} onChange={onChange} />
      <input name="code" placeholder="Code (unique)" required value={form.code} onChange={onChange} />
      <button type="submit" disabled={mut.isPending}>
        {mut.isPending ? 'Saving…' : 'Create'}
      </button>
      {mut.error && <p style={{ color: 'crimson' }}>{String(mut.error)}</p>}
    </form>
  )
}
