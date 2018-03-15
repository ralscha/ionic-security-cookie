import {ReplaySubject} from "rxjs";
import {Injectable} from "@angular/core";
import {User} from "../model/user";
import {ENV} from '@app/env';

@Injectable()
export class AuthProvider {

  authorities = new ReplaySubject<string[]>(1);

  async checkLogin() {
    const response = await fetch(`${ENV.SERVER_URL}/authenticate`, {credentials: 'include'});
    if (response.status === 200) {
      const authorities = await response.text();
      this.authorities.next(authorities.split(','));
    }
    else {
      this.authorities.next(null);
    }
  }

  async login(username: string, password: string, rememberMe: boolean): Promise<string> {
    const response = await fetch(`${ENV.SERVER_URL}/login`, {
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
        this.authorities.next(authorities.split(','));
        return authorities;
      }
      else {
        this.authorities.next(null);
      }
    }
    else {
      this.authorities.next(null);
    }

    return null;
  }

  async logout() {
    await fetch(`${ENV.SERVER_URL}/logout`, {
      credentials: 'include'
    });

    this.authorities.next(null);
  }

  async signup(newUser: User): Promise<string> {
    const response = await fetch(`${ENV.SERVER_URL}/signup`, {
      method: 'POST',
      body: JSON.stringify(newUser),
      headers: {
        'Content-Type': 'application/json'
      }
    });

    const user = await response.text();
    if (user === 'EXISTS') {
      return user;
    }
    else {
      this.login(newUser.username, newUser.password, false);
      return null;
    }
  }

  async reset(usernameOrEmail: string): Promise<boolean> {
    const response = await fetch(`${ENV.SERVER_URL}/reset`, {
      method: 'POST',
      body: usernameOrEmail
    });
    return await response.text() === 'true';
  }

  async change(token: string, newPassword: string): Promise<boolean> {
    const response = await fetch(`${ENV.SERVER_URL}/change`, {
      method: 'POST',
      body: `token=${token}&password=${newPassword}`,
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      }
    });
    return await response.text() === 'true';
  }

}
