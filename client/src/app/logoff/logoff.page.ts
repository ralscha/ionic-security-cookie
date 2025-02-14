import {Component, OnInit} from '@angular/core';
import {AuthService} from '../auth.service';
import {RouterLink} from '@angular/router';
import {IonButton, IonContent, IonHeader, IonRouterLink, IonTitle, IonToolbar} from "@ionic/angular/standalone";

@Component({
  selector: 'app-logoff',
  templateUrl: './logoff.page.html',
  styleUrls: ['./logoff.page.scss'],
  imports: [RouterLink, IonRouterLink, IonHeader, IonToolbar, IonTitle, IonContent, IonButton]
})
export class LogoffPage implements OnInit {

  showMsg = false;

  constructor(private readonly authService: AuthService) {
  }

  async ngOnInit(): Promise<void> {
    await this.authService.logout();
    this.showMsg = true;
  }

}
