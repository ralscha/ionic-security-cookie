import { Service, signal } from '@angular/core';
import { environment } from '../environments/environment';
import { User } from './model/user';

@Service()
export class AuthService {
  private readonly authoritiesState = signal<ReadonlySet<string>>(new Set<string>());
  readonly authorities = this.authoritiesState.asReadonly();

  async checkLogin(): Promise<boolean> {
    try {
      const response = await fetch(`${environment.serverURL}/authenticate`, {
        credentials: 'include',
      });
      if (response.status === 200) {
        this.setAuthorities(await response.text());
        return true;
      }

      this.clearAuthorities();
      return false;
    } catch {
      this.clearAuthorities();
      return false;
    }
  }

  async login(username: string, password: string, rememberMe: boolean): Promise<boolean> {
    const body = new URLSearchParams({
      username,
      password,
      'remember-me': String(rememberMe),
    });

    const response = await fetch(`${environment.serverURL}/login`, {
      credentials: 'include',
      method: 'POST',
      body,
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
      },
    });

    if (response.ok) {
      const authorities = await response.text();
      if (authorities) {
        this.setAuthorities(authorities);
        return true;
      }
    }

    this.clearAuthorities();
    return false;
  }

  async logout(): Promise<void> {
    await fetch(`${environment.serverURL}/logout`, {
      credentials: 'include',
    });

    this.clearAuthorities();
  }

  async signup(newUser: User): Promise<string | null> {
    const response = await fetch(`${environment.serverURL}/signup`, {
      method: 'POST',
      body: JSON.stringify(newUser),
      headers: {
        'Content-Type': 'application/json',
      },
    });

    const user = await response.text();
    if (user === 'EXISTS') {
      return user;
    }

    await this.login(newUser.userName, newUser.password, false);
    return null;
  }

  async reset(usernameOrEmail: string): Promise<void> {
    await fetch(`${environment.serverURL}/reset`, {
      method: 'POST',
      body: usernameOrEmail,
      headers: {
        'Content-Type': 'text/plain',
      },
    });
  }

  async change(token: string, newPassword: string): Promise<boolean> {
    const body = new URLSearchParams({
      token,
      password: newPassword,
    });

    const response = await fetch(`${environment.serverURL}/change`, {
      method: 'POST',
      body,
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
      },
    });
    return (await response.text()) === 'true';
  }

  loggedIn(): boolean {
    return this.authoritiesState().size > 0;
  }

  private setAuthorities(authorities: string): void {
    this.authoritiesState.set(new Set(authorities.split(',').filter(Boolean)));
  }

  private clearAuthorities(): void {
    this.authoritiesState.set(new Set<string>());
  }
}
