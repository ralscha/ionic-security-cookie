import {Component, ViewChild} from '@angular/core';
import {LoginPage} from "../pages/login/login";
import {AuthProvider} from "../providers/auth";
import {HomePage} from "../pages/home/home";
import {MenuOptionModel} from "../component/models/menu-option-model";
import {SideMenuSettings} from "../component/models/side-menu-settings";
import {MenuController, Nav} from "ionic-angular";
import {ProfilePage} from "../pages/profile/profile";
import {RememberMePage} from "../pages/remember-me/remember-me";
import {UsersPage} from "../pages/users/users";
import Visibility from 'visibilityjs';

@Component({
  templateUrl: 'app.html'
})
export class MyApp {
  rootPage: any = null;
  authorities: string[] = [];

  public options: Array<MenuOptionModel>;

  @ViewChild(Nav)
  private nav: Nav;

  public sideMenuSettings: SideMenuSettings = {
    accordionMode: true,
    showSelectedOption: true,
    selectedOptionClass: 'active-side-menu-option',
    subOptionIndentation: {
      md: '56px',
      ios: '64px',
      wp: '56px'
    }
  };

  constructor(private authProvider: AuthProvider,
              private menuCtrl: MenuController) {
    authProvider.authorities.subscribe(authorities => {
      if (!location.hash || !location.hash.startsWith('#/change/')) {
        if (authorities && authorities.length > 0) {
          this.authorities = authorities;
          this.rootPage = HomePage;
          this.initializeMenuOptions();
        }
        else {
          this.authorities = null
          this.rootPage = LoginPage;
        }
      }
    });

    this.checkLogin();

    Visibility.change((e, state) => {
      if (state === 'visible') {
        this.checkLogin();
      }
    });
  }

  private checkLogin() {
    if (!location.hash || !location.hash.startsWith('#/change/')) {
      this.authProvider.checkLogin().catch(() => {
        this.rootPage = LoginPage;
      });
    }
  }

  async selectOption(option: MenuOptionModel) {
    await this.menuCtrl.close();

    if (option.custom && option.custom.logout) {
      this.authProvider.logout();
    }
    else if (option.custom) {
      this.nav.setRoot(option.component, option.custom);
    }
    else {
      this.nav.setRoot(option.component);
    }
  }

  private initializeMenuOptions(): void {
    this.options = [];

    this.options.push({
      iconName: 'home',
      displayName: 'Home',
      component: HomePage,
      selected: true
    });

    if (this.authorities.indexOf('ADMIN') !== -1) {
      this.options.push({
        iconName: 'people',
        displayName: 'Users',
        component: UsersPage
      });
    }

    this.options.push({
      iconName: 'person',
      displayName: 'Profile',
      component: ProfilePage
    });

    this.options.push({
      iconName: 'attach',
      displayName: 'Remember Me Sessions',
      component: RememberMePage
    });

    this.options.push({
      iconName: 'log-out',
      displayName: 'Log off',
      custom: {logout: true}
    });

  }
}
