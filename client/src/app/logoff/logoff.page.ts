import {Component, OnInit} from '@angular/core';
import {AuthService} from '../auth.service';

@Component({
  selector: 'app-logoff',
  templateUrl: './logoff.page.html',
  styleUrls: ['./logoff.page.scss']
})
export class LogoffPage implements OnInit {

  showMsg = false;

  constructor(private readonly authService: AuthService) {
  }

  async ngOnInit(): Promise<void> {
    await this.authService.logout();
    this.showMsg = true;
  }

}
