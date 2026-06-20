import { DatePipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { httpResource } from '@angular/common/http';
import { RememberMeToken } from '../model/remember-me-token';
import {
  AlertController,
  IonButton,
  IonButtons,
  IonContent,
  IonHeader,
  IonIcon,
  IonItem,
  IonLabel,
  IonList,
  IonMenuButton,
  IonTitle,
  IonToolbar,
} from '@ionic/angular/standalone';
import { MessagesService } from '../messages.service';
import { environment } from '../../environments/environment';
import { UAParser } from 'ua-parser-js';
import { addIcons } from 'ionicons';
import { trash } from 'ionicons/icons';

@Component({
  selector: 'app-remember-me',
  templateUrl: './remember-me.page.html',
  styleUrls: ['./remember-me.page.scss'],
  imports: [
    IonHeader,
    IonToolbar,
    IonButtons,
    IonMenuButton,
    IonTitle,
    IonContent,
    IonList,
    IonItem,
    IonLabel,
    IonButton,
    IonIcon,
    DatePipe,
  ],
})
export class RememberMePage {
  readonly tokens = httpResource<RememberMeToken[]>(
    () => ({
      url: `${environment.serverURL}/rememberMeTokens`,
      credentials: 'include',
    }),
    {
      defaultValue: [],
      parse: (value) => enrichTokens(value as RememberMeToken[]),
    },
  );

  private readonly messagesService = inject(MessagesService);
  private readonly alertCtrl = inject(AlertController);

  constructor() {
    addIcons({ trash });
  }

  async deleteToken(series: string): Promise<void> {
    const confirm = await this.alertCtrl.create({
      header: 'Attention!',
      message: 'Really delete this Remember Me Session?',
      buttons: [
        {
          text: 'Delete',
          handler: () => {
            this.doDeleteToken(series);
          },
        },
        {
          text: 'No',
        },
      ],
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
          'Content-Type': 'text/plain',
        },
      });

      await loading.dismiss();
      this.messagesService.showSuccessToast('Delete successful');
      this.tokens.reload();
    } catch {
      await loading.dismiss();
      this.messagesService.showErrorToast();
    }
  }
}

function enrichTokens(tokens: RememberMeToken[]): RememberMeToken[] {
  return tokens.map((token) => {
    const ua = new UAParser(token.userAgent).getResult();
    return {
      ...token,
      uaBrowser: `${ua.browser.name ?? 'Unknown'} ${ua.browser.major ?? ''}`.trim(),
      uaOs: `${ua.os.name ?? 'Unknown'} ${ua.os.version ?? ''}`.trim(),
      uaDevice: ua.device.vendor
        ? `${ua.device.vendor}${ua.device.type ? `(${ua.device.type})` : ''}`
        : undefined,
    };
  });
}
