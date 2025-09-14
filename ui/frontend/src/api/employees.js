import { api } from './client'

export async function listEmployees({ q = '', departmentId = '', page = 0, size = 20 } = {}) {
  const params = {}
  if (q) params.q = q
  if (departmentId) params.departmentId = departmentId
  params.page = page
  params.size = size
  const { data } = await api.get('/api/employees', { params })
  return data
}

export async function createEmployee(payload) {
  const { data } = await api.post('/api/employees', payload)
  return data
}
