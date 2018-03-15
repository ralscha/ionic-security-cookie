import {Component} from '@angular/core';
import {LoadingController, ToastController} from 'ionic-angular';
import {SERVER_URL} from "../../config";
import {User} from "../../model/user";

@Component({
  selector: 'page-profile',
  templateUrl: 'profile.html',
})
export class ProfilePage {

  user: User;

  constructor(private readonly loadingCtrl: LoadingController,
              private readonly toastCtrl: ToastController) {
  }

  async ionViewWillEnter() {
    const response = await fetch(`${SERVER_URL}/profile`, {credentials: 'include'});
    if (response.status === 200) {
      this.user = await response.json();
    }
    else {
      console.log('error', response);
    }
  }

  async updateProfile(value: any) {
    let loading = this.loadingCtrl.create({
      spinner: 'bubbles',
      content: 'Saving ...'
    });

    loading.present();

    try {
      await fetch(`${SERVER_URL}/updateProfile`, {
        credentials: 'include',
        method: 'POST',
        body: JSON.stringify(value),
        headers: {
          'Content-Type': 'application/json'
        }
      });

      loading.dismiss();
      this.showSuccesToast();
    }
    catch(e) {
      loading.dismiss();
      this.handleError();
    }
  }

  private showSuccesToast() {
    const toast = this.toastCtrl.create({
      message: 'Save successful',
      duration: 3000,
      position: 'top'
    });

    toast.present();
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
