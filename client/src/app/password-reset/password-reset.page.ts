import {Component} from '@angular/core';
import {AuthService} from '../auth.service';
import {MessagesService} from '../messages.service';

@Component({
  selector: 'app-password-reset',
  templateUrl: './password-reset.page.html',
  styleUrls: ['./password-reset.page.scss'],
})
export class PasswordResetPage {

  success: boolean | void = false;

  constructor(private readonly authService: AuthService,
              private readonly messagesService: MessagesService) {
  }

  async reset(value: { usernameOrEmail: string }): Promise<void> {
    const loading = await this.messagesService.showLoading('Working');

    await this.authService.reset(value.usernameOrEmail)
      .catch(() => this.showFailedToast());

    await loading.dismiss();
    this.success = true;
  }

  private showFailedToast(): void {
    this.messagesService.showErrorToast('Password Reset failed');
  }

}
