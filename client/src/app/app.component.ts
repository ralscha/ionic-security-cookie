import {Component, inject, OnInit} from '@angular/core';
import {AuthService} from './auth.service';
import {RouterLink, RouterLinkActive} from '@angular/router';
import {
  IonApp,
  IonContent,
  IonHeader,
  IonIcon,
  IonItem,
  IonLabel,
  IonList,
  IonMenu,
  IonMenuToggle,
  IonRouterLink,
  IonRouterOutlet,
  IonSplitPane,
  IonTitle,
  IonToolbar
} from "@ionic/angular/standalone";
import {addIcons} from "ionicons";
import {attach, home, logOut, people, person} from "ionicons/icons";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
  imports: [RouterLinkActive, RouterLink, IonRouterLink, IonSplitPane, IonApp, IonMenu, IonHeader, IonToolbar, IonTitle, IonContent, IonList, IonMenuToggle, IonItem, IonIcon, IonLabel, IonRouterOutlet]
})
export class AppComponent implements OnInit {
  appPages: Array<{ title: string, url: string, icon: string }> = [];
  private readonly authService = inject(AuthService);

  constructor() {
    addIcons({home, people, person, attach, logOut});
  }

  ngOnInit(): void {
    this.authService.authoritiesObservable.subscribe(this.updateMenu.bind(this));
  }

  private updateMenu(authorities: Set<string>): void {
    const isAdmin = authorities.has('ADMIN');

    if (authorities.size > 0) {
      this.appPages = [
        {
          title: 'Home',
          url: '/home',
          icon: 'home'
        },
        ...(isAdmin ? [{
          title: 'Users',
          url: '/users',
          icon: 'people'
        }, {
          title: 'Profile',
          url: '/profile',
          icon: 'person'
        }] : []),
        {
          title: 'Remember Me Sessions',
          url: '/remember-me',
          icon: 'attach'
        },
        {
          title: 'Log off',
          url: '/logoff',
          icon: 'log-out'
        }
      ];
    } else {
      this.appPages = [];
    }
  }


}
