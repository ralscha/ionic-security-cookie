import {Component, inject} from '@angular/core';
import {
  IonButton,
  IonCheckbox,
  IonContent,
  IonHeader,
  IonInput,
  IonItem,
  IonList,
  IonTitle,
  IonToolbar,
  NavController
} from '@ionic/angular/standalone';
import {AuthService} from '../auth.service';
import {MessagesService} from '../messages.service';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-login',
  templateUrl: './login.page.html',
  styleUrls: ['./login.page.scss'],
  imports: [FormsModule, IonHeader, IonToolbar, IonTitle, IonContent, IonList, IonItem, IonButton, IonInput, IonCheckbox]
})
export class LoginPage {
  rememberMe = false;
  private readonly navCtrl = inject(NavController);
  private readonly authService = inject(AuthService);
  private readonly messagesService = inject(MessagesService);

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
