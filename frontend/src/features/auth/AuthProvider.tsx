import { createContext, useContext, useEffect, useMemo, useState } from 'react';
import { accessTokenStore, authApi, type AuthResponse, type AuthUser, type UserRole } from './api';

type AuthContextValue = { user: AuthUser | null; loading: boolean; signIn: (role: UserRole, values: { email: string; password: string; rememberMe: boolean }) => Promise<AuthResponse>; register: (values: { fullName: string; email: string; password: string; confirmPassword: string; rememberMe: boolean }) => Promise<AuthResponse>; signOut: () => Promise<void> };
const AuthContext = createContext<AuthContextValue | undefined>(undefined);
function persist(response: AuthResponse, setUser: (user: AuthUser) => void) { accessTokenStore.set(response.accessToken); setUser(response.user); return response; }
export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<AuthUser | null>(null); const [loading, setLoading] = useState(true);
  useEffect(() => { authApi.refresh().then(response => persist(response, setUser)).catch(() => accessTokenStore.set(null)).finally(() => setLoading(false)); }, []);
  const value = useMemo<AuthContextValue>(() => ({ user, loading,
    signIn: async (role, values) => persist(await authApi.login(role, values), setUser),
    register: async values => persist(await authApi.register(values), setUser),
    signOut: async () => { try { await authApi.logout(); } finally { accessTokenStore.set(null); setUser(null); } },
  }), [user, loading]);
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
export function useAuth() { const context = useContext(AuthContext); if (!context) throw new Error('useAuth must be used within AuthProvider'); return context; }
