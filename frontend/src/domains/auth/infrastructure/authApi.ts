import { httpClient, unwrap } from '@/shared/api/httpClient';
import type { ApiResponse } from '@/shared/api/ApiResponse';
import type {
  AuthRepository,
  AuthResult,
  LoginCredentials,
  RegisterPayload,
} from '../domain/AuthRepository';
import type { AuthTokens } from '../domain/AuthTokens';
import type { User } from '../domain/User';

/** Raw shape of AuthResponse from the backend. */
interface AuthResponseDto {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
  user: User;
}

function toAuthResult(dto: AuthResponseDto): AuthResult {
  const tokens: AuthTokens = {
    accessToken: dto.accessToken,
    refreshToken: dto.refreshToken,
    tokenType: dto.tokenType,
    expiresIn: dto.expiresIn,
  };
  return { tokens, user: dto.user };
}

export const authApi: AuthRepository = {
  async login(credentials: LoginCredentials): Promise<AuthResult> {
    const res = await httpClient.post<ApiResponse<AuthResponseDto>>('/auth/login', credentials);
    return toAuthResult(unwrap(res));
  },

  async register(payload: RegisterPayload): Promise<AuthResult> {
    const res = await httpClient.post<ApiResponse<AuthResponseDto>>('/auth/register', payload);
    return toAuthResult(unwrap(res));
  },

  async refresh(refreshToken: string): Promise<AuthResult> {
    const res = await httpClient.post<ApiResponse<AuthResponseDto>>('/auth/refresh', {
      refreshToken,
    });
    return toAuthResult(unwrap(res));
  },
};
