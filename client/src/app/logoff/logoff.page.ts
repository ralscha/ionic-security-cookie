import { Component, inject, OnInit, signal } from '@angular/core';
import { AuthService } from '../auth.service';
import { RouterLink } from '@angular/router';
import {
  IonButton,
  IonContent,
  IonHeader,
  IonRouterLink,
  IonTitle,
  IonToolbar,
} from '@ionic/angular/standalone';

@Component({
  selector: 'app-logoff',
  templateUrl: './logoff.page.html',
  styleUrls: ['./logoff.page.scss'],
  imports: [
    RouterLink,
    IonRouterLink,
    IonHeader,
    IonToolbar,
    IonTitle,
    IonContent,
    IonButton,
  ],
})
export class LogoffPage implements OnInit {
  readonly showMsg = signal(false);
  private readonly authService = inject(AuthService);

  async ngOnInit(): Promise<void> {
    await this.authService.logout();
    this.showMsg.set(true);
  }
}
