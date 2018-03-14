import {Component} from '@angular/core';
import {SERVER_URL} from '../../config';
import {RememberMeToken} from '../../model/remember-me-token';
import UAParser from 'ua-parser-js'
import {AlertController, ItemSliding, LoadingController, ToastController} from "ionic-angular";

@Component({
  selector: 'page-remember-me',
  templateUrl: 'remember-me.html',
})
export class RememberMePage {
  tokens: RememberMeToken[] = [];

  constructor(private readonly loadingCtrl: LoadingController,
              private readonly toastCtrl: ToastController,
              private readonly alertCtrl: AlertController) {
  }

  async ionViewWillEnter() {
    const response = await fetch(`${SERVER_URL}/rememberMeTokens`, {credentials: 'include'});
    if (response.status === 200) {
      this.tokens = await response.json();
      for (const token of this.tokens) {
        const ua = new UAParser(token.userAgent).getResult();
        token.ua_browser = `${ua.browser.name} ${ua.browser.major}`;
        token.ua_os = `${ua.os.name} ${ua.os.version}`;
        if (ua.device.vendor) {
          token.ua_device = `${ua.device.vendor}${ua.device.type ? `(${ua.device.type})` : ''}`;
        }
      }
    }
    else {
      console.log('error', response);
    }
  }

  deleteToken(slidingItem: ItemSliding, series: string) {
    slidingItem.close();
    const confirm = this.alertCtrl.create({
      title: 'Attention!',
      message: 'Really delete this Remember Me Session?',
      buttons: [{
        text: 'Delete',
        handler: () => {
          this.doDeleteToken(series);
        }
      }, {
        text: 'Cancel'
      }]
    });
    confirm.present();
  }

  private async doDeleteToken(series: string) {
    let loading = this.loadingCtrl.create({
      spinner: 'bubbles',
      content: 'Deleting ...'
    });

    loading.present();

    try {
      await fetch(`${SERVER_URL}/deleteRememberMeTokens`, {
        credentials: 'include',
        method: 'POST',
        body: series,
        headers: {
          'Content-Type': 'application/json'
        }
      });

      loading.dismiss();
      this.showSuccesToast();
      this.ionViewWillEnter();
    }
    catch (e) {
      loading.dismiss();
      this.handleError();
    }
  }

  private showSuccesToast() {
    const toast = this.toastCtrl.create({
      message: 'Delete successful',
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
