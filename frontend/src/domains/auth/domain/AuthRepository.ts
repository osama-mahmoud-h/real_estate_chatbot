import type { AuthTokens } from './AuthTokens';
import type { User } from './User';

export interface LoginCredentials {
  email: string;
  password: string;
}

export interface RegisterPayload {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  phoneNumber: string;
}

export interface AuthResult {
  tokens: AuthTokens;
  user: User;
}

/** Port for authentication. Implemented in the infrastructure layer. */
export interface AuthRepository {
  login(credentials: LoginCredentials): Promise<AuthResult>;
  register(payload: RegisterPayload): Promise<AuthResult>;
  refresh(refreshToken: string): Promise<AuthResult>;
}
