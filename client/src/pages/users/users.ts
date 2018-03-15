import {Component} from '@angular/core';
import {MessagesProvider} from "../../providers/messages";
import {SERVER_URL} from "../../config";
import {User} from "../../model/user";

@Component({
  selector: 'page-users',
  templateUrl: 'users.html',
})
export class UsersPage {
  users: User[] = [];

  constructor(private readonly messages: MessagesProvider) {
  }

  async ionViewWillEnter() {
    const response = await fetch(`${SERVER_URL}/admin/users`, {credentials: 'include'});
    if (response.status === 200) {
      this.users = await response.json();
    }
    else {
      this.messages.showErrorToast();
    }
  }

  private async unlock(username: string) {
    const loading = this.messages.showLoading('Unlocking');

    try {
      await fetch(`${SERVER_URL}/admin/unlock`, {
        credentials: 'include',
        method: 'POST',
        body: username,
        headers: {
          'Content-Type': 'application/json'
        }
      });

      loading.dismiss();
      this.messages.showSuccessToast('Unlock successful');

      for (const user of this.users) {
        if (user.username === username) {
          delete user.lockedOutUntil;
        }
      }
    }
    catch {
      loading.dismiss();
      this.messages.showErrorToast();
    }
  }

}
