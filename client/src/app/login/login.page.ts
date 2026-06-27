import { Component, inject, signal } from '@angular/core';
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
  NavController,
} from '@ionic/angular/standalone';
import { AuthService } from '../auth.service';
import { MessagesService } from '../messages.service';
import { FormField, FormRoot, form, required } from '@angular/forms/signals';

interface LoginForm {
  username: string;
  password: string;
  rememberMe: boolean;
}

@Component({
  selector: 'app-login',
  templateUrl: './login.page.html',
  styleUrls: ['./login.page.scss'],
  imports: [
    FormField,
    FormRoot,
    IonHeader,
    IonToolbar,
    IonTitle,
    IonContent,
    IonList,
    IonItem,
    IonButton,
    IonInput,
    IonCheckbox,
  ],
})
export class LoginPage {
  readonly loginModel = signal<LoginForm>({
    username: '',
    password: '',
    rememberMe: false,
  });
  readonly loginForm = form(this.loginModel, (path) => {
    required(path.username);
    required(path.password);
  });

  private readonly navCtrl = inject(NavController);
  private readonly authService = inject(AuthService);
  private readonly messagesService = inject(MessagesService);

  signup(): void {
    this.navCtrl.navigateForward('/signup');
  }

  reset(): void {
    this.navCtrl.navigateForward('/password-reset');
  }

  async login(event: Event): Promise<void> {
    event.preventDefault();

    if (!this.loginForm().valid()) {
      return;
    }

    const loading = await this.messagesService.showLoading('Logging in');
    const value = this.loginModel();

    const success = await this.authService
      .login(value.username, value.password, value.rememberMe)
      .catch(() => false);

    if (success) {
      await this.navCtrl.navigateRoot('/home');
      await loading.dismiss();
    } else {
      await loading.dismiss();
      this.showLoginFailedToast();
    }
  }

  private showLoginFailedToast(): void {
    this.messagesService.showErrorToast('Login failed');
  }
}
