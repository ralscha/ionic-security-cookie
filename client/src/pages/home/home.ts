import {Component} from '@angular/core';
import {SERVER_URL} from "../../config";
import {AuthProvider} from "../../providers/auth/auth";
import {HttpClient} from "@angular/common/http";

@Component({
  selector: 'page-home',
  templateUrl: 'home.html'
})
export class HomePage {
  user: string;
  message: string;

  constructor(private readonly authProvider: AuthProvider) {
    this.authProvider.authUser.subscribe(user => {
      this.user = user;
    });
  }

  async ionViewWillEnter() {
    const response = await fetch(`${SERVER_URL}/secret`, {credentials: 'include'});
    if (response.status === 200) {
      this.message = await response.text();
    }
    else {
      console.log('error', response);
    }
  }

  logout() {
    this.authProvider.logout();
  }

}
