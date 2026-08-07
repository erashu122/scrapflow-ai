import { zodResolver } from '@hookform/resolvers/zod';
import { ArrowRight } from 'lucide-react';
import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { Link } from 'react-router-dom';
import { z } from 'zod';
import { authApi } from '../features/auth/api';
import { AuthLayout } from './LoginPage';
const schema = z.object({ email: z.string().email('Enter a valid email address.') });
export function ForgotPasswordPage() { const [sent, setSent] = useState(false); const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm<z.infer<typeof schema>>({ resolver: zodResolver(schema) }); const submit = async ({ email }: z.infer<typeof schema>) => { await authApi.forgotPassword(email); setSent(true); }; return <AuthLayout title="Reset your password" subtitle="We’ll send a secure reset link if an account exists.">{sent ? <div className="auth-success"><h2>Check your inbox</h2><p>If your address is registered, a password-reset message is on its way.</p><Link className="primary auth-submit" to="/login">Return to sign in</Link></div> : <form onSubmit={handleSubmit(submit)} noValidate><label className="field"><span>Work email</span><input autoComplete="email" {...register('email')} placeholder="you@company.com" />{errors.email && <small>{errors.email.message}</small>}</label><button className="primary auth-submit" disabled={isSubmitting}>{isSubmitting ? 'Sending…' : 'Send reset link'} <ArrowRight size={16}/></button><p className="auth-footer"><Link to="/login">Back to sign in</Link></p></form>}</AuthLayout>; }
