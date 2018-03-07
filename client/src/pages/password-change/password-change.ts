import {Component, OnInit} from '@angular/core';
import {LoadingController, NavController, NavParams, ToastController} from 'ionic-angular';
import {AuthProvider} from "../../providers/auth/auth";
import {LoginPage} from "../login/login";

@Component({
  selector: 'page-password-change',
  templateUrl: 'password-change.html',
})
export class PasswordChangePage implements OnInit {

  private token: string = null;

  constructor(private readonly authProvider: AuthProvider,
              private readonly navParams: NavParams,
              private readonly navCtrl: NavController,
              private readonly loadingCtrl: LoadingController,
              private readonly toastCtrl: ToastController) {
  }

  ngOnInit() {
    const navData = this.navParams.data;
    if (navData.token) {
      this.token = navData.token;
    }
    else {
      history.replaceState({}, document.title, ".");
    }
  }

  async change(value: any) {
    let loading = this.loadingCtrl.create({
      spinner: 'bubbles',
      content: 'Changing Password ...'
    });

    loading.present();

    try {
      const success = await this.authProvider.change(this.token, value.password);
      loading.dismiss();
      if (success) {
        this.showSuccesToast();
        history.replaceState({}, document.title, ".");
        this.navCtrl.setRoot(LoginPage);
      }
      else {
        this.handleError();
      }
    }
    catch (e) {
      loading.dismiss();
      this.handleError();
    }
  }

  private showSuccesToast() {
    const toast = this.toastCtrl.create({
      message: 'Password Change successful',
      duration: 3000,
      position: 'bottom'
    });

    toast.present();
  }

  handleError() {
    let message = `Unexpected error occurred`;

    const toast = this.toastCtrl.create({
      message,
      duration: 5000,
      position: 'bottom'
    });

    toast.present();
  }

}
