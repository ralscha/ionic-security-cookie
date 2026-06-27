import { Component, effect, inject, signal } from '@angular/core';
import { httpResource } from '@angular/common/http';
import { MessagesService } from '../messages.service';
import { User } from '../model/user';
import { environment } from '../../environments/environment';
import {
  IonButton,
  IonButtons,
  IonContent,
  IonHeader,
  IonInput,
  IonItem,
  IonList,
  IonMenuButton,
  IonTitle,
  IonToolbar,
} from '@ionic/angular/standalone';
import {
  FormField,
  FormRoot,
  email,
  form,
  readonly as readonlyField,
  required,
} from '@angular/forms/signals';

interface ProfileForm {
  firstName: string;
  lastName: string;
  userName: string;
  email: string;
  oldPassword: string;
  password: string;
}

const emptyProfile: ProfileForm = {
  firstName: '',
  lastName: '',
  userName: '',
  email: '',
  oldPassword: '',
  password: '',
};

@Component({
  selector: 'app-profile',
  templateUrl: './profile.page.html',
  styleUrls: ['./profile.page.scss'],
  imports: [
    FormField,
    FormRoot,
    IonHeader,
    IonToolbar,
    IonButtons,
    IonMenuButton,
    IonTitle,
    IonContent,
    IonList,
    IonItem,
    IonButton,
    IonInput,
  ],
})
export class ProfilePage {
  readonly profile = httpResource<User>(() => ({
    url: `${environment.serverURL}/profile`,
    credentials: 'include',
  }));

  readonly profileModel = signal<ProfileForm>(emptyProfile);
  readonly profileForm = form(this.profileModel, (path) => {
    required(path.firstName);
    required(path.lastName);
    required(path.email);
    email(path.email);
    required(path.userName);
    readonlyField(path.userName);
  });

  private readonly messagesService = inject(MessagesService);

  constructor() {
    effect(() => {
      if (!this.profile.hasValue()) {
        return;
      }

      const user = this.profile.value();
      this.profileModel.set({
        firstName: user.firstName ?? '',
        lastName: user.lastName ?? '',
        userName: user.userName ?? '',
        email: user.email ?? '',
        oldPassword: '',
        password: '',
      });
    });
  }

  async updateProfile(event: Event): Promise<void> {
    event.preventDefault();

    if (!this.profileForm().valid()) {
      return;
    }

    const loading = await this.messagesService.showLoading('Saving');

    try {
      await fetch(`${environment.serverURL}/updateProfile`, {
        credentials: 'include',
        method: 'POST',
        body: JSON.stringify(this.profileModel()),
        headers: {
          'Content-Type': 'application/json',
        },
      });

      await loading.dismiss();
      this.messagesService.showSuccessToast('Save successful');
      this.profile.reload();
    } catch {
      await loading.dismiss();
      this.messagesService.showErrorToast();
    }
  }
}
