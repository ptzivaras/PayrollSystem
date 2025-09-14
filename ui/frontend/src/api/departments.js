import { api } from './client'

/**
 * Always return an array of departments.
 * Backend returns a Spring Page -> { content: [...], ... }
 */
export async function listDepartments() {
  const { data } = await api.get('/api/departments', {
    params: { size: 1000, sort: 'name' }
  })
  if (Array.isArray(data)) return data
  if (data && Array.isArray(data.content)) return data.content
  return []
}

export async function createDepartment(payload) {
  const { data } = await api.post('/api/departments', payload)
  return data
}
