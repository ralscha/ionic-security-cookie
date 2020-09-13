import {Component, OnInit} from '@angular/core';
import {AuthService} from './auth.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {

  appPages: Array<{ title: string, url: string, icon: string }> = [];

  constructor(private readonly authService: AuthService) {
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
