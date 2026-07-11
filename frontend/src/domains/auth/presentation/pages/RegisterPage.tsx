import { Link } from 'react-router-dom';
import { AuthShell } from '../components/AuthShell';
import { RegisterForm } from '../components/RegisterForm';

export default function RegisterPage() {
  return (
    <AuthShell
      title="Create your account"
      subtitle="Start chatting with the Real Estate Assistant"
      footer={
        <>
          Already have an account?{' '}
          <Link to="/login" className="font-medium text-primary-600 hover:text-primary-700">
            Sign in
          </Link>
        </>
      }
    >
      <RegisterForm />
    </AuthShell>
  );
}
