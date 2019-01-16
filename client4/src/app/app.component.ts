import {Component, OnInit} from '@angular/core';
import {AuthService} from './auth.service';

@Component({
  selector: 'app-root',
  templateUrl: 'app.component.html'
})
export class AppComponent implements OnInit {

  appPages: Array<{ title: string, url: string, icon: string }> = [];

  constructor(private readonly authService: AuthService) {
  }

  ngOnInit() {
    this.authService.authoritiesObservable.subscribe(this.updateMenu.bind(this));
  }

  private updateMenu(authorities: Set<string>) {
    const isAdmin = authorities.has('ADMIN');

    if (authorities.size > 0) {
      const pages: Array<{ title: string, url: string, icon: string }> = [];

      pages.push({
        title: 'Home',
        url: '/home',
        icon: 'home'
      });

      if (isAdmin) {
        pages.push({
          title: 'Users',
          url: '/users',
          icon: 'people'
        });
        pages.push({
          title: 'Profile',
          url: '/profile',
          icon: 'person'
        });
        pages.push({
          title: 'Remember Me Sessions',
          url: '/remember-me',
          icon: 'attach'
        });
      }

      pages.push({
        title: 'Log off',
        url: '/logoff',
        icon: 'log-out'
      });

      this.appPages = pages;
    } else {
      this.appPages = [];
    }
  }


}
