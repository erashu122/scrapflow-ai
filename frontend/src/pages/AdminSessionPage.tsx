import { LogOut } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../features/auth/AuthProvider';
export function AdminSessionPage() { const { user, signOut } = useAuth(); const navigate = useNavigate(); const logout = async () => { await signOut(); navigate('/login'); }; return <main className="auth-stage"><section className="auth-card auth-success"><p className="eyebrow">ADMINISTRATOR SESSION</p><h1>Welcome, {user?.fullName}</h1><p>Your identity is verified. The operational admin workspace will be added by its dedicated module.</p><button className="primary auth-submit" onClick={logout}>Sign out <LogOut size={16}/></button></section></main>; }
