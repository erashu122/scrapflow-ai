import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from './AuthProvider';
import type { UserRole } from './api';

export function ProtectedRoute({ roles }: { roles?: UserRole[] }) {
  const { user, loading } = useAuth();
  if (loading) return <main className="auth-stage"><div className="auth-card"><p className="eyebrow">SCRAPFLOW AI</p><h1>Restoring your session</h1></div></main>;
  if (!user) return <Navigate to="/login" replace />;
  if (roles && !roles.includes(user.role)) return <Navigate to={user.role === 'ADMIN' ? '/admin' : '/'} replace />;
  return <Outlet />;
}
