import { Link } from 'react-router-dom';
import { AuthShell } from '../components/AuthShell';
import { RegisterForm } from '../components/RegisterForm';

export default function RegisterPage() {
  return (
    <AuthShell
      title="Create account"
      subtitle="A few details and Sarah can start searching the listings for you."
      footer={
        <>
          Already have an account?{' '}
          <Link
            to="/login"
            className="font-medium text-forest-accent hover:text-forest hover:underline dark:text-forest-bright"
          >
            Sign in
          </Link>
        </>
      }
    >
      <RegisterForm />
    </AuthShell>
  );
}
