import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { useNavigate } from 'react-router-dom';
import { Input } from '@/shared/ui/Input';
import { PasswordInput } from '@/shared/ui/PasswordInput';
import { Button } from '@/shared/ui/Button';
import { MailIcon, LockIcon, PersonIcon, PhoneIcon } from '@/shared/ui/FieldIcons';
import { toApiErrorMessage } from '@/shared/api/httpClient';
import { saveCredential } from '@/shared/lib/credentials';
import { fullName } from '../../domain/User';
import { useRegister } from '../../application/useRegister';

const schema = z.object({
  firstName: z.string().min(1, 'First name is required'),
  lastName: z.string().min(1, 'Last name is required'),
  email: z.string().min(1, 'Email is required').email('Enter a valid email'),
  phoneNumber: z.string().min(6, 'Enter a valid phone number'),
  password: z.string().min(8, 'Password must be at least 8 characters'),
});

type RegisterValues = z.infer<typeof schema>;

export function RegisterForm() {
  const navigate = useNavigate();
  const registerMutation = useRegister();
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<RegisterValues>({ resolver: zodResolver(schema) });

  const onSubmit = handleSubmit((values) => {
    registerMutation.mutate(values, {
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
        leading={<PersonIcon />}
        aria-label="First name"
        autoComplete="given-name"
        placeholder="First name"
        error={errors.firstName?.message}
        {...register('firstName')}
      />
      <Input
        variant="pill"
        leading={<PersonIcon />}
        aria-label="Last name"
        autoComplete="family-name"
        placeholder="Last name"
        error={errors.lastName?.message}
        {...register('lastName')}
      />
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
      <Input
        variant="pill"
        leading={<PhoneIcon />}
        type="tel"
        aria-label="Phone number"
        autoComplete="tel"
        placeholder="Phone number"
        error={errors.phoneNumber?.message}
        {...register('phoneNumber')}
      />
      <PasswordInput
        variant="pill"
        leading={<LockIcon />}
        aria-label="Password"
        autoComplete="new-password"
        placeholder="Password (8 characters or more)"
        error={errors.password?.message}
        {...register('password')}
      />

      {registerMutation.isError && (
        <p className="rounded-2xl border border-brick/25 bg-brick-soft px-4 py-2.5 text-center text-sm text-brick dark:border-brick-bright/30 dark:bg-brick-bright/10 dark:text-brick-bright">
          {toApiErrorMessage(registerMutation.error, 'Registration failed. Try again.')}
        </p>
      )}

      <Button
        type="submit"
        loading={registerMutation.isPending}
        className="mt-1.5 h-14 w-full rounded-full text-[15px] shadow-[0_4px_14px_rgba(28,79,53,0.22)] dark:shadow-none"
      >
        Create account
      </Button>
    </form>
  );
}