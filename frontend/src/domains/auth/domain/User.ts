/** Authenticated user, mirrors AuthResponse.UserInfo from the backend. */
export interface User {
  userId: number;
  firstName: string;
  lastName: string;
  email: string;
}

export function fullName(user: Pick<User, 'firstName' | 'lastName'>): string {
  return `${user.firstName} ${user.lastName}`.trim();
}
