import {Component} from '@angular/core';
import {LoginPage} from "../pages/login/login";
import {AuthProvider} from "../providers/auth/auth";
import {TabsPage} from "../pages/tabs/tabs";

@Component({
  templateUrl: 'app.html'
})
export class MyApp {
  rootPage: any = null;

  constructor(authProvider: AuthProvider) {
    authProvider.authorities.subscribe(authorities => {
      if (!location.hash || !location.hash.startsWith('#/change/')) {
        if (authorities) {
          this.rootPage = TabsPage;
        }
        else {
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
}
