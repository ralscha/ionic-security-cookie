import {Component} from '@angular/core';
import {User} from '../model/user';
import {MessagesService} from '../messages.service';
import {environment} from '../../environments/environment';
import {ViewWillEnter} from '@ionic/angular';
import {RelativeTimePipe} from '../relative-time.pipe';
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
  IonToolbar
} from "@ionic/angular/standalone";
import {addIcons} from "ionicons";
import {lockOpen} from "ionicons/icons";

@Component({
  selector: 'app-users',
  templateUrl: './users.page.html',
  styleUrls: ['./users.page.scss'],
  imports: [RelativeTimePipe, IonHeader, IonToolbar, IonButtons, IonMenuButton, IonTitle, IonContent, IonList, IonItem, IonLabel, IonButton, IonIcon]
})
export class UsersPage implements ViewWillEnter {

  users: User[] = [];

  constructor(private readonly messagesService: MessagesService) {
    addIcons({lockOpen})
  }

  async ionViewWillEnter(): Promise<void> {
    const response = await fetch(`${environment.serverURL}/admin/users`, {credentials: 'include'});
    if (response.status === 200) {
      this.users = await response.json();
    } else {
      this.messagesService.showErrorToast();
    }
  }

  async unlock(username: string): Promise<void> {
    const loading = await this.messagesService.showLoading('Unlocking');

    try {
      await fetch(`${environment.serverURL}/admin/unlock`, {
        credentials: 'include',
        method: 'POST',
        body: username,
        headers: {
          'Content-Type': 'application/json'
        }
      });

      await loading.dismiss();
      this.messagesService.showSuccessToast('Unlock successful');

      for (const user of this.users) {
        if (user.userName === username) {
          delete user.lockedOut;
        }
      }
    } catch {
      loading.dismiss();
      this.messagesService.showErrorToast();
    }
  }

}
