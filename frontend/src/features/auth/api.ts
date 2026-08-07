export type UserRole = 'ADMIN' | 'BUYER';
export type AuthUser = { id: string; fullName: string; email: string; role: UserRole; emailVerified: boolean };
export type AuthResponse = { accessToken: string; user: AuthUser };

const baseUrl = import.meta.env.VITE_API_URL ?? 'http://localhost:8080/api/v1';
let accessToken: string | null = null;
export const accessTokenStore = { get: () => accessToken, set: (token: string | null) => { accessToken = token; } };

async function request<T>(path: string, init: RequestInit = {}): Promise<T> {
  const response = await fetch(`${baseUrl}${path}`, { ...init, credentials: 'include', headers: { 'Content-Type': 'application/json', ...init.headers } });
  if (!response.ok) { const problem = await response.json().catch(() => null); throw new Error(problem?.detail ?? problem?.title ?? 'Unable to complete this request.'); }
  if (response.status === 204 || response.status === 202) return undefined as T;
  return response.json() as Promise<T>;
}
export const authApi = {
  register: (payload: { fullName: string; email: string; password: string; confirmPassword: string; rememberMe: boolean }) => request<AuthResponse>('/auth/register', { method: 'POST', body: JSON.stringify(payload) }),
  login: (role: UserRole, payload: { email: string; password: string; rememberMe: boolean }) => request<AuthResponse>(`/auth/${role === 'ADMIN' ? 'admin' : 'buyer'}/login`, { method: 'POST', body: JSON.stringify(payload) }),
  refresh: () => request<AuthResponse>('/auth/refresh', { method: 'POST', body: '{}' }),
  logout: () => request<void>('/auth/logout', { method: 'POST', body: '{}' }),
  forgotPassword: (email: string) => request<void>('/auth/forgot-password', { method: 'POST', body: JSON.stringify({ email }) }),
  verifyEmail: (token: string) => request<void>('/auth/verify-email', { method: 'POST', body: JSON.stringify({ token }) }),
};
