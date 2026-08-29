import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { useNavigate } from 'react-router-dom';
import { Input } from '@/shared/ui/Input';
import { PasswordInput } from '@/shared/ui/PasswordInput';
import { Button } from '@/shared/ui/Button';
import { MailIcon, LockIcon } from '@/shared/ui/FieldIcons';
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
    <form onSubmit={onSubmit} className="space-y-3.5" noValidate>
      <Input
        variant="pill"
        leading={<MailIcon />}
        type="email"
        aria-label="Email address"
        autoComplete="email"
        placeholder="Email address"
        error={errors.email?.message}
        {...register('email')}
      />
      <PasswordInput
        variant="pill"
        leading={<LockIcon />}
        aria-label="Password"
        autoComplete="current-password"
        placeholder="Password"
        error={errors.password?.message}
        {...register('password')}
      />

      {login.isError && (
        <p className="rounded-2xl border border-brick/25 bg-brick-soft px-4 py-2.5 text-center text-sm text-brick dark:border-brick-bright/30 dark:bg-brick-bright/10 dark:text-brick-bright">
          {toApiErrorMessage(login.error, 'Login failed. Check your credentials.')}
        </p>
      )}

      <Button
        type="submit"
        loading={login.isPending}
        className="mt-1.5 h-14 w-full rounded-full text-[15px] shadow-[0_4px_14px_rgba(28,79,53,0.22)] dark:shadow-none"
      >
        Log in
      </Button>
    </form>
  );
}