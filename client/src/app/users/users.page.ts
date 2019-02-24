import {Component} from '@angular/core';
import {User} from '../model/user';
import {MessagesService} from '../messages.service';
import {environment} from '../../environments/environment';

@Component({
  selector: 'app-users',
  templateUrl: './users.page.html',
  styleUrls: ['./users.page.scss'],
})
export class UsersPage {

  users: User[] = [];

  constructor(private readonly messagesService: MessagesService) {
  }

  async ionViewWillEnter() {
    const response = await fetch(`${environment.serverURL}/admin/users`, {credentials: 'include'});
    if (response.status === 200) {
      this.users = await response.json();
    } else {
      this.messagesService.showErrorToast();
    }
  }

  async unlock(username: string) {
    const loading = await this.messagesService.showLoading('Unlocking');

    try {
      await fetch(`${environment.serverURL}/admin/unlock`, {
        credentials: 'include',
        method: 'POST',
        body: username,
        headers: {
          'Content-Type': 'application/json'
        }
      });

      await loading.dismiss();
      this.messagesService.showSuccessToast('Unlock successful');

      for (const user of this.users) {
        if (user.userName === username) {
          delete user.lockedOut;
        }
      }
    } catch {
      loading.dismiss();
      this.messagesService.showErrorToast();
    }
  }

}
