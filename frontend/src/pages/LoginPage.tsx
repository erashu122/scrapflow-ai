import { zodResolver } from '@hookform/resolvers/zod';
import { motion } from 'framer-motion';
import { ArrowRight, LockKeyhole } from 'lucide-react';
import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { Link, useNavigate } from 'react-router-dom';
import { z } from 'zod';
import { useAuth } from '../features/auth/AuthProvider';
import type { UserRole } from '../features/auth/api';

const schema = z.object({ email: z.string().email('Enter a valid email address.'), password: z.string().min(1, 'Password is required.'), rememberMe: z.boolean() });
type FormValues = z.infer<typeof schema>;
export function LoginPage() {
  const { signIn } = useAuth(); const navigate = useNavigate(); const [role, setRole] = useState<UserRole>('BUYER'); const [error, setError] = useState('');
  const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm<FormValues>({ resolver: zodResolver(schema), defaultValues: { rememberMe: true } });
  const submit = async (values: FormValues) => { setError(''); try { const response = await signIn(role, values); navigate(response.user.role === 'ADMIN' ? '/admin' : '/', { replace: true }); } catch (cause) { setError(cause instanceof Error ? cause.message : 'Sign-in failed.'); } };
  return <AuthLayout title="Welcome back" subtitle="Sign in to your ScrapFlow workspace."><div className="role-switch"><button className={role === 'BUYER' ? 'active' : ''} onClick={() => setRole('BUYER')} type="button">Buyer</button><button className={role === 'ADMIN' ? 'active' : ''} onClick={() => setRole('ADMIN')} type="button">Administrator</button></div><form onSubmit={handleSubmit(submit)} noValidate><Field label="Work email" error={errors.email?.message}><input autoComplete="email" {...register('email')} placeholder="you@company.com" /></Field><Field label="Password" error={errors.password?.message}><input autoComplete="current-password" type="password" {...register('password')} placeholder="Enter your password" /></Field><div className="form-row"><label className="check"><input type="checkbox" {...register('rememberMe')} /> Keep me signed in</label><Link to="/forgot-password">Forgot password?</Link></div>{error && <p className="form-error" role="alert">{error}</p>}<button className="primary auth-submit" disabled={isSubmitting}>{isSubmitting ? 'Signing in…' : 'Sign in'} <ArrowRight size={16}/></button></form><p className="auth-footer">New to ScrapFlow? <Link to="/register">Create a buyer account</Link></p></AuthLayout>;
}
export function AuthLayout({ title, subtitle, children }: { title: string; subtitle: string; children: React.ReactNode }) { return <main className="auth-stage"><motion.section className="auth-card" initial={{ opacity: 0, y: 14 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: .3 }}><Link className="auth-brand" to="/login"><span className="brand-mark">S</span> ScrapFlow <i>AI</i></Link><div className="auth-title"><span className="auth-icon"><LockKeyhole size={17}/></span><p className="eyebrow">SECURE ACCESS</p><h1>{title}</h1><p>{subtitle}</p></div>{children}</motion.section></main>; }
function Field({ label, error, children }: { label: string; error?: string; children: React.ReactNode }) { return <label className="field"><span>{label}</span>{children}{error && <small>{error}</small>}</label>; }
