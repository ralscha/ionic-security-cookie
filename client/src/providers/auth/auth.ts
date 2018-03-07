import {ReplaySubject} from "rxjs";
import {SERVER_URL} from "../../config";
import {Injectable} from "@angular/core";
import {User} from "../../model/user";

@Injectable()
export class AuthProvider {

  authUser = new ReplaySubject<any>(1);

  async checkLogin() {
    const response = await fetch(`${SERVER_URL}/authenticate`, {credentials: 'include'});
    if (response.status === 200) {
      const user = await response.text();
      this.authUser.next(user);
    }
    else {
      this.authUser.next(null);
    }
  }

  async login(username: string, password: string, rememberMe: boolean): Promise<string> {
    const response = await fetch(`${SERVER_URL}/login`, {
      credentials: 'include',
      method: 'POST',
      body: `username=${username}&password=${password}&remember-me=${rememberMe}`,
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      }
    });

    if (response.ok) {
      const user = await response.text();
      if (user) {
        this.authUser.next(user);
        return user;
      }
      else {
        this.authUser.next(null);
      }
    }
    else {
      this.authUser.next(null);
    }

    return null;
  }

  async logout() {
    await fetch(`${SERVER_URL}/logout`, {
      credentials: 'include'
    });

    this.authUser.next(null);
  }

  async signup(newUser: User): Promise<string> {
    const response = await fetch(`${SERVER_URL}/signup`, {
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
    const response = await fetch(`${SERVER_URL}/reset`, {
      method: 'POST',
      body: usernameOrEmail
    });
    return await response.text() === 'true';
  }

  async change(token: string, newPassword: string): Promise<boolean> {
    const response = await fetch(`${SERVER_URL}/change`, {
      method: 'POST',
      body: `token=${token}&password=${newPassword}`,
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      }
    });
    return await response.text() === 'true';
  }

}
