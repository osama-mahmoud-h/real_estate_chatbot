import { Navigate, Outlet } from 'react-router-dom';
import { useAuthStore } from '@/domains/auth/application/authStore';
import { ROUTES } from './routes';

/** Guards authenticated areas; redirects to /login when there is no session. */
export function ProtectedRoute() {
  const isAuthenticated = useAuthStore((s) => s.isAuthenticated);
  return isAuthenticated ? <Outlet /> : <Navigate to={ROUTES.login} replace />;
}

/** Keeps authenticated users away from the login/register pages. */
export function GuestRoute() {
  const isAuthenticated = useAuthStore((s) => s.isAuthenticated);
  return isAuthenticated ? <Navigate to={ROUTES.chat} replace /> : <Outlet />;
}
