import { Component, computed, inject } from '@angular/core';
import { AuthService } from './auth.service';
import { RouterLink, RouterLinkActive } from '@angular/router';
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
  IonToolbar,
} from '@ionic/angular/standalone';
import { addIcons } from 'ionicons';
import { attach, home, logOut, people, person } from 'ionicons/icons';

interface AppPage {
  title: string;
  url: string;
  icon: string;
}

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
  imports: [
    RouterLinkActive,
    RouterLink,
    IonRouterLink,
    IonSplitPane,
    IonApp,
    IonMenu,
    IonHeader,
    IonToolbar,
    IonTitle,
    IonContent,
    IonList,
    IonMenuToggle,
    IonItem,
    IonIcon,
    IonLabel,
    IonRouterOutlet,
  ],
})
export class AppComponent {
  private readonly authService = inject(AuthService);

  readonly appPages = computed<AppPage[]>(() => {
    const authorities = this.authService.authorities();
    if (authorities.size === 0) {
      return [];
    }

    const pages: AppPage[] = [
      {
        title: 'Home',
        url: '/home',
        icon: 'home',
      },
    ];

    if (authorities.has('ADMIN')) {
      pages.push(
        {
          title: 'Users',
          url: '/users',
          icon: 'people',
        },
        {
          title: 'Profile',
          url: '/profile',
          icon: 'person',
        },
      );
    }

    pages.push(
      {
        title: 'Remember Me Sessions',
        url: '/remember-me',
        icon: 'attach',
      },
      {
        title: 'Log off',
        url: '/logoff',
        icon: 'log-out',
      },
    );

    return pages;
  });

  constructor() {
    addIcons({ home, people, person, attach, logOut });
  }
}
