import {Component} from '@angular/core';
import {NavController} from 'ionic-angular';
import {SignupPage} from "../signup/signup";
import {AuthProvider} from "../../providers/auth";
import {PasswordResetPage} from "../password-reset/password-reset";
import {MessagesProvider} from "../../providers/messages";

@Component({
  selector: 'page-login',
  templateUrl: 'login.html'
})
export class LoginPage {
  rememberMe = false;

  constructor(private readonly navCtrl: NavController,
              private readonly authProvider: AuthProvider,
              private readonly messages: MessagesProvider) {
  }

  signup() {
    this.navCtrl.push(SignupPage);
  }

  reset() {
    this.navCtrl.push(PasswordResetPage)
  }

  async login(value: { username: string, password: string, rememberMe: boolean }) {
    const loading = this.messages.showLoading('Logging in');

    const user = await this.authProvider.login(value.username, value.password, value.rememberMe)
      .catch(() => this.showLoginFailedToast());
    loading.dismiss();

    if (user === null) {
      this.showLoginFailedToast();
    }
  }

  private showLoginFailedToast() {
    this.messages.showErrorToast('Login failed');
  }
}
