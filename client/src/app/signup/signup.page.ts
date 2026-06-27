import { Component, inject, signal } from '@angular/core';
import { AuthService } from '../auth.service';
import { MessagesService } from '../messages.service';
import { FormField, FormRoot, email, form, required } from '@angular/forms/signals';
import {
  IonBackButton,
  IonButton,
  IonButtons,
  IonContent,
  IonHeader,
  IonInput,
  IonItem,
  IonList,
  IonTitle,
  IonToolbar,
  NavController,
} from '@ionic/angular/standalone';

interface SignupForm {
  firstName: string;
  lastName: string;
  userName: string;
  email: string;
  password: string;
}

@Component({
  selector: 'app-signup',
  templateUrl: './signup.page.html',
  styleUrls: ['./signup.page.scss'],
  imports: [
    FormField,
    FormRoot,
    IonHeader,
    IonToolbar,
    IonButtons,
    IonTitle,
    IonContent,
    IonList,
    IonItem,
    IonButton,
    IonBackButton,
    IonInput,
  ],
})
export class SignupPage {
  readonly signupModel = signal<SignupForm>({
    firstName: '',
    lastName: '',
    userName: '',
    email: '',
    password: '',
  });
  readonly signupForm = form(this.signupModel, (path) => {
    required(path.firstName);
    required(path.lastName);
    required(path.email);
    email(path.email);
    required(path.userName);
    required(path.password);
  });

  private readonly authService = inject(AuthService);
  private readonly messagesService = inject(MessagesService);
  private readonly navCtrl = inject(NavController);

  async signup(event: Event): Promise<void> {
    event.preventDefault();

    if (!this.signupForm().valid()) {
      return;
    }

    const loading = await this.messagesService.showLoading('Signing up');
    try {
      const username = await this.authService.signup(this.signupModel());
      await loading.dismiss();
      if (username !== null) {
        this.showSuccesToast(username);
      } else {
        await this.navCtrl.navigateRoot('/home');
      }
    } catch {
      await loading.dismiss();
      await this.messagesService.showErrorToast();
    }
  }

  private showSuccesToast(username: string): void {
    if (username !== 'EXISTS') {
      this.messagesService.showSuccessToast('Sign up successful');
    } else {
      this.messagesService.showErrorToast('Username already registered');
    }
  }
}
