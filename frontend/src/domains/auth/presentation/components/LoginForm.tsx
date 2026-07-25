import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { useNavigate } from 'react-router-dom';
import { Input } from '@/shared/ui/Input';
import { PasswordInput } from '@/shared/ui/PasswordInput';
import { Button } from '@/shared/ui/Button';
import { toApiErrorMessage } from '@/shared/api/httpClient';
import { saveCredential } from '@/shared/lib/credentials';
import { fullName } from '../../domain/User';
import { useLogin } from '../../application/useLogin';

const schema = z.object({
  email: z.string().min(1, 'Email is required').email('Enter a valid email'),
  password: z.string().min(1, 'Password is required'),
});

type LoginValues = z.infer<typeof schema>;

export function LoginForm() {
  const navigate = useNavigate();
  const login = useLogin();
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginValues>({ resolver: zodResolver(schema) });

  const onSubmit = handleSubmit((values) => {
    login.mutate(values, {
      onSuccess: async (result) => {
        await saveCredential(values.email, values.password, fullName(result.user));
        navigate('/', { replace: true });
      },
    });
  });

  return (
    <form onSubmit={onSubmit} className="space-y-4" noValidate>
      <Input
        label="Email"
        type="email"
        autoComplete="email"
        placeholder="you@example.com"
        error={errors.email?.message}
        {...register('email')}
      />
      <PasswordInput
        label="Password"
        autoComplete="current-password"
        placeholder="••••••••"
        error={errors.password?.message}
        {...register('password')}
      />

      {login.isError && (
        <p className="rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700">
          {toApiErrorMessage(login.error, 'Login failed. Check your credentials.')}
        </p>
      )}

      <Button type="submit" size="lg" loading={login.isPending} className="w-full">
        Sign in
      </Button>
    </form>
  );
}
