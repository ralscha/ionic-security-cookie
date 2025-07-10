import {Component, inject, OnInit} from '@angular/core';
import {environment} from '../../environments/environment';
import {MessagesService} from '../messages.service';
import {IonButtons, IonContent, IonHeader, IonMenuButton, IonTitle, IonToolbar} from "@ionic/angular/standalone";

@Component({
  selector: 'app-home',
  templateUrl: './home.page.html',
  styleUrls: ['./home.page.scss'],
  imports: [IonHeader, IonToolbar, IonButtons, IonMenuButton, IonTitle, IonContent]
})
export class HomePage implements OnInit {
  message!: string;
  private readonly messages = inject(MessagesService);

  async ngOnInit(): Promise<void> {
    const response = await fetch(`${environment.serverURL}/secret`, {credentials: 'include'});
    if (response.status === 200) {
      this.message = await response.text();
    } else {
      this.message = 'Error';
      this.messages.showErrorToast();
    }
  }

}
