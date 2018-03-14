import {Component, ViewChild} from '@angular/core';
import {LoadingController, ToastController} from 'ionic-angular';
import {NgModel} from "@angular/forms";
import {AuthProvider} from "../../providers/auth";

@Component({
  selector: 'page-signup',
  templateUrl: 'signup.html'
})
export class SignupPage {

  @ViewChild('username')
  usernameModel: NgModel;

  constructor(private readonly authProvider: AuthProvider,
              private readonly loadingCtrl: LoadingController,
              private readonly toastCtrl: ToastController) {
  }

  async signup(value: any) {
    let loading = this.loadingCtrl.create({
      spinner: 'bubbles',
      content: 'Signing up ...'
    });

    loading.present();

    try {
      const username = await this.authProvider.signup(value);
      loading.dismiss();
      this.showSuccesToast(username);
    }
    catch(e) {
      loading.dismiss();
      this.handleError();
    }
  }

  private showSuccesToast(username) {
    if (username !== 'EXISTS') {
      const toast = this.toastCtrl.create({
        message: 'Sign up successful',
        duration: 3000,
        position: 'top'
      });

      toast.present();
    }
    else {
      const toast = this.toastCtrl.create({
        message: 'Username already registered',
        duration: 3000,
        position: 'top'
      });

      toast.present();

      this.usernameModel.control.setErrors({'usernameTaken': true});
    }
  }

  handleError() {
    let message = `Unexpected error occurred`;

    const toast = this.toastCtrl.create({
      message,
      duration: 5000,
      position: 'top'
    });

    toast.present();
  }
}
