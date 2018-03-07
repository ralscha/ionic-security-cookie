import {Component} from '@angular/core';
import {HomePage} from '../pages/home/home';
import {LoginPage} from "../pages/login/login";
import {AuthProvider} from "../providers/auth/auth";

@Component({
  templateUrl: 'app.html'
})
export class MyApp {
  rootPage: any = null;

  constructor(authProvider: AuthProvider) {

    authProvider.authUser.subscribe(user => {
      if (user) {
        this.rootPage = HomePage;
      }
      else {
        this.rootPage = LoginPage;
      }
    });

    authProvider.checkLogin().catch(() => {
      this.rootPage = LoginPage;
    });
  }
}
