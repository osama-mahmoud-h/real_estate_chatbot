import { Link } from 'react-router-dom';
import { AuthShell } from '../components/AuthShell';
import { LoginForm } from '../components/LoginForm';

export default function LoginPage() {
  return (
    <AuthShell
      title="Welcome back"
      subtitle="Sign in to your Real Estate Assistant"
      footer={
        <>
          Don&apos;t have an account?{' '}
          <Link to="/register" className="font-medium text-primary-600 hover:text-primary-700">
            Create one
          </Link>
        </>
      }
    >
      <LoginForm />
    </AuthShell>
  );
}
