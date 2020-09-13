import {Injectable} from '@angular/core';
import {BehaviorSubject} from 'rxjs';
import {environment} from '../environments/environment';
import {User} from './model/user';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly authorities = new BehaviorSubject<Set<string>>(new Set<string>());
  readonly authoritiesObservable = this.authorities.asObservable();

  async checkLogin(): Promise<boolean> {
    try {
      const response = await fetch(`${environment.serverURL}/authenticate`, {credentials: 'include'});
      if (response.status === 200) {
        const authorities = await response.text();
        this.authorities.next(new Set<string>(authorities.split(',')));
        return true;
      } else {
        this.clearAuthorites();
        return false;
      }
    } catch {
      return false;
    }
  }

  async login(username: string, password: string, rememberMe: boolean): Promise<boolean> {
    const response = await fetch(`${environment.serverURL}/login`, {
      credentials: 'include',
      method: 'POST',
      body: `username=${username}&password=${password}&remember-me=${rememberMe}`,
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      }
    });

    if (response.ok) {
      const authorities = await response.text();
      if (authorities) {
        this.authorities.next(new Set<string>(authorities.split(',')));
        return true;
      } else {
        this.clearAuthorites();
      }
    } else {
      this.clearAuthorites();
    }
    return false;
  }

  async logout(): Promise<void> {
    await fetch(`${environment.serverURL}/logout`, {
      credentials: 'include'
    });

    this.clearAuthorites();
  }

  async signup(newUser: User): Promise<string | null> {
    const response = await fetch(`${environment.serverURL}/signup`, {
      method: 'POST',
      body: JSON.stringify(newUser),
      headers: {
        'Content-Type': 'application/json'
      }
    });

    const user = await response.text();
    if (user === 'EXISTS') {
      return user;
    } else {
      await this.login(newUser.userName, newUser.password, false);
      return null;
    }
  }

  async reset(usernameOrEmail: string): Promise<boolean> {
    const response = await fetch(`${environment.serverURL}/reset`, {
      method: 'POST',
      body: usernameOrEmail
    });
    return await response.text() === 'true';
  }

  async change(token: string, newPassword: string): Promise<boolean> {
    const response = await fetch(`${environment.serverURL}/change`, {
      method: 'POST',
      body: `token=${token}&password=${newPassword}`,
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      }
    });
    return await response.text() === 'true';
  }

  loggedIn(): boolean {
    return this.authorities.value.size > 0;
  }

  private clearAuthorites(): void {
    this.authorities.next(new Set<string>());
  }

}
