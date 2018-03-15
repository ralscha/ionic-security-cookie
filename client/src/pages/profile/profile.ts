import {Component} from '@angular/core';
import {ENV} from '@app/env';
import {User} from "../../model/user";
import {MessagesProvider} from "../../providers/messages";

@Component({
  selector: 'page-profile',
  templateUrl: 'profile.html',
})
export class ProfilePage {

  user: User;

  constructor(private readonly messages: MessagesProvider) {
  }

  async ionViewWillEnter() {
    const response = await fetch(`${ENV.SERVER_URL}/profile`, {credentials: 'include'});
    if (response.status === 200) {
      this.user = await response.json();
    }
    else {
      this.messages.showErrorToast();
    }
  }

  async updateProfile(value: any) {
    const loading = this.messages.showLoading('Saving');

    try {
      await fetch(`${ENV.SERVER_URL}/updateProfile`, {
        credentials: 'include',
        method: 'POST',
        body: JSON.stringify(value),
        headers: {
          'Content-Type': 'application/json'
        }
      });

      loading.dismiss();
      this.messages.showSuccessToast('Save successful');
    }
    catch {
      loading.dismiss();
      this.messages.showErrorToast();
    }
  }

}
