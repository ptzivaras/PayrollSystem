import { api } from './client'

export async function listRuns({ departmentId = '', period = '', page = 0, size = 20 } = {}) {
  const params = { page, size }
  if (departmentId) params.departmentId = departmentId
  if (period) params.period = period
  const { data } = await api.get('/api/payroll/runs', { params })
  return data
}

export async function createRun({ period, departmentId = null }) {
  const params = { period }
  if (departmentId !== null) params.departmentId = departmentId
  const { data } = await api.post('/api/payroll/runs', null, { params })
  return data
}

export async function postRun(runId) {
  const { data } = await api.post(`/api/payroll/runs/${runId}/post`)
  return data
}

export async function listItemsByRun(runId, { page = 0, size = 20 } = {}) {
  const params = { runId, page, size }
  const { data } = await api.get('/api/payroll/items', { params })
  return data
}
