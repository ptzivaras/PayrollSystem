import { api } from './client'

function toFirstDay(period) {
  // expects "YYYY-MM-DD" -> returns "YYYY-MM-01"
  return typeof period === 'string' && period.length >= 7
    ? `${period.slice(0, 7)}-01`
    : period
}

export async function listRuns({ departmentId = '', period = '', page = 0, size = 20 } = {}) {
  const params = { page, size }
  if (departmentId) params.departmentId = departmentId
  if (period) params.period = toFirstDay(period)   // <-- force 1st of month
  const { data } = await api.get('/api/payroll/runs', { params })
  return data
}

export async function createRun({ period, departmentId = null }) {
  const params = { period: toFirstDay(period) }    // <-- force 1st of month
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
