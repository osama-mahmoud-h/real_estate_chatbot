import { Link } from 'react-router-dom';
import { AuthShell } from '../components/AuthShell';
import { LoginForm } from '../components/LoginForm';

export default function LoginPage() {
  return (
    <AuthShell
      title="Log in"
      subtitle="Enter your email and password to pick up where your last conversation left off."
      footer={
        <>
          Don&apos;t have an account?{' '}
          <Link
            to="/register"
            className="font-medium text-forest-accent hover:text-forest hover:underline dark:text-forest-bright"
          >
            Create one
          </Link>
        </>
      }
    >
      <LoginForm />
    </AuthShell>
  );
}
