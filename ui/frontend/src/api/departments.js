import { api } from './client'

export async function listDepartments() {
  const { data } = await api.get('/api/departments')
  return data
}

export async function createDepartment(payload) {
  const { data } = await api.post('/api/departments', payload)
  return data
}
