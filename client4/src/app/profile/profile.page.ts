import {Component} from '@angular/core';
import {MessagesService} from '../messages.service';
import {User} from '../model/user';
import {environment} from '../../environments/environment';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.page.html',
  styleUrls: ['./profile.page.scss'],
})
export class ProfilePage {

  user: User;

  constructor(private readonly messagesService: MessagesService) {
  }

  async ionViewWillEnter() {
    const response = await fetch(`${environment.serverURL}/profile`, {credentials: 'include'});
    if (response.status === 200) {
      this.user = await response.json();
    } else {
      this.messagesService.showErrorToast();
    }
  }

  async updateProfile(value: any) {
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
