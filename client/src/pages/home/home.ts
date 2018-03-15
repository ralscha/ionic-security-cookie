import {Component} from '@angular/core';
import {SERVER_URL} from "../../config";
import {AuthProvider} from "../../providers/auth";
import {MessagesProvider} from "../../providers/messages";

@Component({
  selector: 'page-home',
  templateUrl: 'home.html'
})
export class HomePage {
  authorities: string;
  message: string;

  constructor(authProvider: AuthProvider, private readonly messages: MessagesProvider) {
    authProvider.authorities.subscribe(authorities => {
      this.authorities = authorities;
    });
  }

  async ionViewWillEnter() {
    const response = await fetch(`${SERVER_URL}/secret`, {credentials: 'include'});
    if (response.status === 200) {
      this.message = await response.text();
    }
    else {
      this.messages.showErrorToast();
    }
  }


}
