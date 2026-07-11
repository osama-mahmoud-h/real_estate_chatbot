/** JWT token pair returned by login / register / refresh. */
export interface AuthTokens {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  /** Access token lifetime in seconds. */
  expiresIn: number;
}
