import { Link, NavLink, Outlet } from 'react-router-dom'

const linkClass = ({ isActive }) =>
  `px-3 py-2 rounded ${isActive ? 'bg-black text-white' : 'bg-gray-200'}`

export default function Layout() {
  return (
    <div style={{ fontFamily: 'system-ui, sans-serif', padding: 16 }}>
      <header style={{ display: 'flex', gap: 16, alignItems: 'center', marginBottom: 24 }}>
        <Link to="/" style={{ fontWeight: 700, fontSize: 18 }}>HR/Payroll</Link>
        <nav style={{ display: 'flex', gap: 8 }}>
          <NavLink to="/employees" className={linkClass}>Employees</NavLink>
          <NavLink to="/departments" className={linkClass}>Departments</NavLink>
          <NavLink to="/payroll" className={linkClass}>Payroll</NavLink>
        </nav>
      </header>
      <main>
        <Outlet />
      </main>
    </div>
  )
}
