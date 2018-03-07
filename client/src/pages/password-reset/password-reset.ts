import { Component } from '@angular/core';
import {LoadingController, NavController, NavParams, ToastController} from 'ionic-angular';
import {AuthProvider} from "../../providers/auth/auth";

@Component({
  selector: 'page-password-reset',
  templateUrl: 'password-reset.html',
})
export class PasswordResetPage {
  success: boolean | void = false;

  constructor(public navCtrl: NavController,
              public navParams: NavParams,
              private readonly authProvider: AuthProvider,
              private readonly toastCtrl: ToastController,
              private readonly loadingCtrl: LoadingController) {
  }

  async reset(value: { usernameOrEmail: string }) {
    let loading = this.loadingCtrl.create({
      spinner: 'bubbles',
      content: 'Working ...'
    });

    loading.present();

    this.success = await this.authProvider.reset(value.usernameOrEmail)
      .catch(() => this.showFailedToast());

    loading.dismiss();

    if (!this.success) {
      this.showFailedToast();
    }
  }

  private showFailedToast() {
    const toast = this.toastCtrl.create({
      message: 'Password Reset failed',
      duration: 5000,
      position: 'bottom'
    });

    toast.present();
  }

}
