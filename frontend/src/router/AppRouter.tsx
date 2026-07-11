import { Routes, Route, Navigate } from 'react-router-dom';
import LoginPage from '@/domains/auth/presentation/pages/LoginPage';
import RegisterPage from '@/domains/auth/presentation/pages/RegisterPage';
import ChatPage from '@/domains/chat/presentation/pages/ChatPage';
import { ProtectedRoute, GuestRoute } from './ProtectedRoute';
import { ROUTES } from './routes';

export function AppRouter() {
  return (
    <Routes>
      <Route element={<GuestRoute />}>
        <Route path={ROUTES.login} element={<LoginPage />} />
        <Route path={ROUTES.register} element={<RegisterPage />} />
      </Route>

      <Route element={<ProtectedRoute />}>
        <Route path={ROUTES.chat} element={<ChatPage />} />
      </Route>

      <Route path="*" element={<Navigate to={ROUTES.chat} replace />} />
    </Routes>
  );
}
