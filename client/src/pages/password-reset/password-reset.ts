import {Component} from '@angular/core';
import {AuthProvider} from "../../providers/auth";
import {MessagesProvider} from "../../providers/messages";

@Component({
  selector: 'page-password-reset',
  templateUrl: 'password-reset.html',
})
export class PasswordResetPage {
  success: boolean | void = false;

  constructor(private readonly authProvider: AuthProvider,
              private readonly messages: MessagesProvider) {
  }

  async reset(value: { usernameOrEmail: string }) {
    const loading = this.messages.showLoading('Working');

    this.success = await this.authProvider.reset(value.usernameOrEmail)
      .catch(() => this.showFailedToast());

    loading.dismiss();

    if (!this.success) {
      this.showFailedToast();
    }
  }

  private showFailedToast() {
    this.messages.showErrorToast('Password Reset failed');
  }

}
