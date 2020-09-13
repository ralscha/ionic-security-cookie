import {Component} from '@angular/core';
import {RememberMeToken} from '../model/remember-me-token';
import {AlertController, ViewWillEnter} from '@ionic/angular';
import {MessagesService} from '../messages.service';
import {environment} from '../../environments/environment';
// @ts-ignore
import * as UAParser from 'ua-parser-js';

@Component({
  selector: 'app-remember-me',
  templateUrl: './remember-me.page.html',
  styleUrls: ['./remember-me.page.scss'],
})
export class RememberMePage implements ViewWillEnter {

  tokens: RememberMeToken[] = [];

  constructor(private readonly messagesService: MessagesService,
              private readonly alertCtrl: AlertController) {
  }

  async ionViewWillEnter(): Promise<void> {
    const response = await fetch(`${environment.serverURL}/rememberMeTokens`, {credentials: 'include'});
    if (response.status === 200) {
      this.tokens = await response.json();
      for (const token of this.tokens) {
        const ua = new UAParser(token.userAgent).getResult();
        token.uaBrowser = `${ua.browser.name} ${ua.browser.major}`;
        token.uaOs = `${ua.os.name} ${ua.os.version}`;
        if (ua.device.vendor) {
          token.uaDevice = `${ua.device.vendor}${ua.device.type ? `(${ua.device.type})` : ''}`;
        }
      }
    } else {
      this.messagesService.showErrorToast();
    }
  }

  async deleteToken(series: string): Promise<void> {
    const confirm = await this.alertCtrl.create({
      header: 'Attention!',
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
    await confirm.present();
  }

  private async doDeleteToken(series: string): Promise<void> {
    const loading = await this.messagesService.showLoading('Deleting');

    try {
      await fetch(`${environment.serverURL}/deleteRememberMeTokens`, {
        credentials: 'include',
        method: 'POST',
        body: series,
        headers: {
          'Content-Type': 'application/json'
        }
      });

      await loading.dismiss();
      this.messagesService.showSuccessToast('Delete successful');

      this.ionViewWillEnter();
    } catch {
      await loading.dismiss();
      this.messagesService.showErrorToast();
    }
  }

}
