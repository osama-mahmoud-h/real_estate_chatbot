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
    <form onSubmit={onSubmit} className="space-y-4" noValidate>
      <div className="grid grid-cols-2 gap-3">
        <Input
          label="First name"
          autoComplete="given-name"
          error={errors.firstName?.message}
          {...register('firstName')}
        />
        <Input
          label="Last name"
          autoComplete="family-name"
          error={errors.lastName?.message}
          {...register('lastName')}
        />
      </div>
      <Input
        label="Email"
        type="email"
        autoComplete="email"
        placeholder="you@example.com"
        error={errors.email?.message}
        {...register('email')}
      />
      <Input
        label="Phone number"
        type="tel"
        autoComplete="tel"
        placeholder="+20 100 000 0000"
        error={errors.phoneNumber?.message}
        {...register('phoneNumber')}
      />
      <PasswordInput
        label="Password"
        autoComplete="new-password"
        placeholder="At least 8 characters"
        error={errors.password?.message}
        {...register('password')}
      />

      {registerMutation.isError && (
        <p className="rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700">
          {toApiErrorMessage(registerMutation.error, 'Registration failed. Try again.')}
        </p>
      )}

      <Button type="submit" size="lg" loading={registerMutation.isPending} className="w-full">
        Create account
      </Button>
    </form>
  );
}
