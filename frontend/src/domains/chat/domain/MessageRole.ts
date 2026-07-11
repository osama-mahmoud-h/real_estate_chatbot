/** Message roles as emitted by the backend `MessageRole` enum. */
export type MessageRole = 'USER' | 'ASSISTANT' | 'SYSTEM' | 'TOOL' | 'FUNCTION';

/** Which side of the thread a role renders on. */
export function isUserRole(role: MessageRole): boolean {
  return role === 'USER';
}
