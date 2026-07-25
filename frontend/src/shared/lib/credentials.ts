/**
 * Asks the browser's password manager (Google Password Manager, iCloud
 * Keychain, etc.) to offer saving the credentials after a successful sign-in.
 *
 * SPAs navigate via the router instead of a full form POST, so the browser's
 * built-in "save password?" heuristic often never fires. The Credential
 * Management API triggers it explicitly. It's a progressive enhancement:
 * unsupported browsers and insecure contexts are silently skipped.
 */
export async function saveCredential(email: string, password: string, name?: string): Promise<void> {
  if (typeof window === 'undefined' || !window.isSecureContext) return;
  if (!('credentials' in navigator) || !('PasswordCredential' in window)) return;

  try {
    const PasswordCredentialCtor = (window as unknown as {
      PasswordCredential: new (data: { id: string; password: string; name?: string }) => Credential;
    }).PasswordCredential;

    const credential = new PasswordCredentialCtor({ id: email, password, name });
    await navigator.credentials.store(credential);
  } catch {
    // Never block the auth flow on a password-manager hiccup.
  }
}