import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { Suspense } from "react";

import Layout from './components/Layout'
import EmployeesPage from './pages/EmployeesPage'
import DepartmentsPage from './pages/DepartmentsPage'
import PayrollRunsPage from './pages/PayrollRunsPage'
import RunDetailsPage from './pages/RunDetailsPage'
import { useState } from 'react'
import NotFound from "./pages/NotFound";

function App() {
  const [count, setCount] = useState(0)

  return (
    <BrowserRouter>
    <Suspense fallback={<p style={{ padding: 16 }}>Loading app…</p>}>
      <Routes>
        <Route element={<Layout />}>
          <Route index element={<Navigate to="/employees" replace />} />
          <Route path="/employees" element={<EmployeesPage />} />
          <Route path="/departments" element={<DepartmentsPage />} />
          <Route path="/payroll" element={<PayrollRunsPage />} />
          <Route path="/payroll/:runId" element={<RunDetailsPage />} />
          <Route path="*" element={<NotFound />} />
        </Route>
      </Routes>
    </Suspense>
    </BrowserRouter>
  );
}

export default App
