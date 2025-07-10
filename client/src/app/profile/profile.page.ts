import {Component, inject} from '@angular/core';
import {MessagesService} from '../messages.service';
import {User} from '../model/user';
import {environment} from '../../environments/environment';
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
  ViewWillEnter
} from '@ionic/angular/standalone';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.page.html',
  styleUrls: ['./profile.page.scss'],
  imports: [FormsModule, IonHeader, IonToolbar, IonButtons, IonMenuButton, IonTitle, IonContent, IonList, IonItem, IonButton, IonInput]
})
export class ProfilePage implements ViewWillEnter {
  user: User | null = null;
  private readonly messagesService = inject(MessagesService);

  async ionViewWillEnter(): Promise<void> {
    const response = await fetch(`${environment.serverURL}/profile`, {credentials: 'include'});
    if (response.status === 200) {
      this.user = await response.json();
    } else {
      this.messagesService.showErrorToast();
    }
  }

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  async updateProfile(value: any): Promise<void> {
    const loading = await this.messagesService.showLoading('Saving');

    try {
      await fetch(`${environment.serverURL}/updateProfile`, {
        credentials: 'include',
        method: 'POST',
        body: JSON.stringify(value),
        headers: {
          'Content-Type': 'application/json'
        }
      });

      await loading.dismiss();
      this.messagesService.showSuccessToast('Save successful');
    } catch {
      await loading.dismiss();
      this.messagesService.showErrorToast();
    }
  }

}
