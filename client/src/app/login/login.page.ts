import {Component} from '@angular/core';
import {NavController} from '@ionic/angular';
import {AuthService} from '../auth.service';
import {MessagesService} from '../messages.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.page.html',
  styleUrls: ['./login.page.scss'],
})
export class LoginPage {

  rememberMe = false;

  constructor(private readonly navCtrl: NavController,
              private readonly authService: AuthService,
              private readonly messagesService: MessagesService) {
  }

  signup(): void {
    this.navCtrl.navigateForward('/signup');
  }

  reset(): void {
    this.navCtrl.navigateForward('/password-reset');
  }

  async login(value: { username: string, password: string, rememberMe: boolean }): Promise<void> {
    const loading = await this.messagesService.showLoading('Logging in');

    const success = await this.authService.login(value.username, value.password, value.rememberMe)
      .catch(() => this.showLoginFailedToast());

    await loading.dismiss();

    if (success) {
      this.navCtrl.navigateRoot('/home');
    } else {
      this.showLoginFailedToast();
    }
  }

  private showLoginFailedToast(): void {
    this.messagesService.showErrorToast('Login failed');
  }

}
