import {Component, OnInit} from '@angular/core';
import {environment} from '../../environments/environment';
import {MessagesService} from '../messages.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.page.html',
  styleUrls: ['./home.page.scss']
})
export class HomePage implements OnInit {
  message!: string;

  constructor(private readonly messages: MessagesService) {
  }

  async ngOnInit(): Promise<void> {
    const response = await fetch(`${environment.serverURL}/secret`, {credentials: 'include'});
    if (response.status === 200) {
      this.message = await response.text();
    } else {
      this.message = 'Error';
      this.messages.showErrorToast();
    }
  }

}
