import { Component, inject } from '@angular/core';
import { httpResource } from '@angular/common/http';
import { User } from '../model/user';
import { MessagesService } from '../messages.service';
import { environment } from '../../environments/environment';
import {
  IonButton,
  IonButtons,
  IonContent,
  IonHeader,
  IonIcon,
  IonItem,
  IonLabel,
  IonList,
  IonMenuButton,
  IonTitle,
  IonToolbar,
} from '@ionic/angular/standalone';
import { RelativeTimePipe } from '../relative-time.pipe';
import { addIcons } from 'ionicons';
import { lockOpen } from 'ionicons/icons';

@Component({
  selector: 'app-users',
  templateUrl: './users.page.html',
  styleUrls: ['./users.page.scss'],
  imports: [
    RelativeTimePipe,
    IonHeader,
    IonToolbar,
    IonButtons,
    IonMenuButton,
    IonTitle,
    IonContent,
    IonList,
    IonItem,
    IonLabel,
    IonButton,
    IonIcon,
  ],
})
export class UsersPage {
  readonly users = httpResource<User[]>(
    () => ({
      url: `${environment.serverURL}/admin/users`,
      credentials: 'include',
    }),
    { defaultValue: [] },
  );

  private readonly messagesService = inject(MessagesService);

  constructor() {
    addIcons({ lockOpen });
  }

  async unlock(username: string): Promise<void> {
    const loading = await this.messagesService.showLoading('Unlocking');

    try {
      await fetch(`${environment.serverURL}/admin/unlock`, {
        credentials: 'include',
        method: 'POST',
        body: username,
        headers: {
          'Content-Type': 'text/plain',
        },
      });

      await loading.dismiss();
      this.messagesService.showSuccessToast('Unlock successful');

      this.users.value.update((users) =>
        users.map((user) => (user.userName === username ? { ...user, lockedOut: false } : user)),
      );
    } catch {
      await loading.dismiss();
      await this.messagesService.showErrorToast();
    }
  }
}
