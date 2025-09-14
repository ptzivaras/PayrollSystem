import axios from 'axios'

const baseURL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080'

const basicAuth = () => {
  // admin/password matches backend dev defaults
  const token = btoa('admin:password')
  return `Basic ${token}`
}

export const api = axios.create({
  baseURL,
  headers: { 'Content-Type': 'application/json' },
  withCredentials: false
})

api.interceptors.request.use((config) => {
  config.headers.Authorization = basicAuth()
  return config
})
