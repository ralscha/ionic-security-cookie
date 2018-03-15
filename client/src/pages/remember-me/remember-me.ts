import {Component} from '@angular/core';
import {SERVER_URL} from '../../config';
import {RememberMeToken} from '../../model/remember-me-token';
import UAParser from 'ua-parser-js'
import {AlertController} from "ionic-angular";
import {MessagesProvider} from "../../providers/messages";

@Component({
  selector: 'page-remember-me',
  templateUrl: 'remember-me.html',
})
export class RememberMePage {
  tokens: RememberMeToken[] = [];

  constructor(private readonly messages: MessagesProvider,
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
      this.messages.showErrorToast();
    }
  }

  deleteToken(series: string) {
    const confirm = this.alertCtrl.create({
      title: 'Attention!',
      message: 'Really delete this Remember Me Session?',
      buttons: [{
        text: 'Delete',
        handler: () => {
          this.doDeleteToken(series);
        }
      }, {
        text: 'No'
      }]
    });
    confirm.present();
  }

  private async doDeleteToken(series: string) {
    const loading = this.messages.showLoading('Deleting');

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
      this.messages.showSuccessToast('Delete successful');

      this.ionViewWillEnter();
    }
    catch {
      loading.dismiss();
      this.messages.showErrorToast();
    }
  }

}
