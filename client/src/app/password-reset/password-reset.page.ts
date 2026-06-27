import { Component, inject, signal } from '@angular/core';
import { AuthService } from '../auth.service';
import { MessagesService } from '../messages.service';
import { FormField, FormRoot, form, required } from '@angular/forms/signals';
import {
  IonBackButton,
  IonButton,
  IonButtons,
  IonCard,
  IonCardContent,
  IonContent,
  IonHeader,
  IonInput,
  IonItem,
  IonList,
  IonTitle,
  IonToolbar,
} from '@ionic/angular/standalone';

interface PasswordResetForm {
  usernameOrEmail: string;
}

@Component({
  selector: 'app-password-reset',
  templateUrl: './password-reset.page.html',
  styleUrls: ['./password-reset.page.scss'],
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
    IonInput,
    IonBackButton,
    IonCardContent,
    IonCard,
  ],
})
export class PasswordResetPage {
  success = false;
  readonly resetModel = signal<PasswordResetForm>({ usernameOrEmail: '' });
  readonly resetForm = form(this.resetModel, (path) => {
    required(path.usernameOrEmail);
  });

  private readonly authService = inject(AuthService);
  private readonly messagesService = inject(MessagesService);

  async reset(event: Event): Promise<void> {
    event.preventDefault();

    if (!this.resetForm().valid()) {
      return;
    }

    const loading = await this.messagesService.showLoading('Working');

    try {
      await this.authService.reset(this.resetModel().usernameOrEmail);
      this.success = true;
    } catch {
      await this.messagesService.showErrorToast('Password Reset failed');
    } finally {
      await loading.dismiss();
    }
  }
}
