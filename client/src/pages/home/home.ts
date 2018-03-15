import {Component} from '@angular/core';
import {SERVER_URL} from "../../config";
import {MessagesProvider} from "../../providers/messages";

@Component({
  selector: 'page-home',
  templateUrl: 'home.html'
})
export class HomePage {
  message: string;

  constructor(private readonly messages: MessagesProvider) {
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
