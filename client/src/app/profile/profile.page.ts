import {Component} from '@angular/core';
import {MessagesService} from '../messages.service';
import {User} from '../model/user';
import {environment} from '../../environments/environment';
import {ViewWillEnter} from '@ionic/angular';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.page.html',
  styleUrls: ['./profile.page.scss'],
})
export class ProfilePage implements ViewWillEnter {

  user: User | null = null;

  constructor(private readonly messagesService: MessagesService) {
  }

  async ionViewWillEnter(): Promise<void> {
    const response = await fetch(`${environment.serverURL}/profile`, {credentials: 'include'});
    if (response.status === 200) {
      this.user = await response.json();
    } else {
      this.messagesService.showErrorToast();
    }
  }

  // tslint:disable-next-line:no-any
  async updateProfile(value: any): Promise<void> {
    const loading = await this.messagesService.showLoading('Saving');

    try {
      await fetch(`${environment.serverURL}/updateProfile`, {
        credentials: 'include',
        method: 'POST',
        body: JSON.stringify(value),
        headers: {
          'Content-Type': 'application/json'
        }
      });

      await loading.dismiss();
      this.messagesService.showSuccessToast('Save successful');
    } catch {
      await loading.dismiss();
      this.messagesService.showErrorToast();
    }
  }

}
