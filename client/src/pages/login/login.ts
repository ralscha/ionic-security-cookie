import {Component} from '@angular/core';
import {LoadingController, NavController, ToastController} from 'ionic-angular';
import {SignupPage} from "../signup/signup";
import {AuthProvider} from "../../providers/auth";
import {PasswordResetPage} from "../password-reset/password-reset";

@Component({
  selector: 'page-login',
  templateUrl: 'login.html'
})
export class LoginPage {
  rememberMe = false;

  constructor(private readonly navCtrl: NavController,
              private readonly loadingCtrl: LoadingController,
              private readonly authProvider: AuthProvider,
              private readonly toastCtrl: ToastController) {
  }

  signup() {
    this.navCtrl.push(SignupPage);
  }

  reset() {
    this.navCtrl.push(PasswordResetPage)
  }

  async login(value: { username: string, password: string, rememberMe: boolean }) {
    let loading = this.loadingCtrl.create({
      spinner: 'bubbles',
      content: 'Logging in ...'
    });

    loading.present();

    const user = await this.authProvider.login(value.username, value.password, value.rememberMe).catch(() => this.showLoginFailedToast());
    loading.dismiss();
    if (user === null) {
      this.showLoginFailedToast();
    }
  }

  private showLoginFailedToast() {
    const toast = this.toastCtrl.create({
      message: 'Login failed',
      duration: 5000,
      position: 'top'
    });

    toast.present();
  }
}
