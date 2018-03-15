import {Component, ViewChild} from '@angular/core';
import {LoginPage} from "../pages/login/login";
import {AuthProvider} from "../providers/auth";
import {HomePage} from "../pages/home/home";
import {MenuOptionModel} from "../component/models/menu-option-model";
import {SideMenuSettings} from "../component/models/side-menu-settings";
import {MenuController, Nav} from "ionic-angular";
import {ProfilePage} from "../pages/profile/profile";
import {RememberMePage} from "../pages/remember-me/remember-me";

@Component({
  templateUrl: 'app.html'
})
export class MyApp {
  rootPage: any = null;
  loggedIn = false;

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
        if (authorities) {
          this.loggedIn = true;
          this.rootPage = HomePage;
          this.initializeMenuOptions();
        }
        else {
          this.loggedIn = false;
          this.rootPage = LoginPage;
        }
      }
    });

    if (!location.hash || !location.hash.startsWith('#/change/')) {
      authProvider.checkLogin().catch(() => {
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
