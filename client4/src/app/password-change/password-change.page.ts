import {Component, OnInit} from '@angular/core';
import {AuthService} from '../auth.service';
import {NavController, NavParams} from '@ionic/angular';
import {MessagesService} from '../messages.service';

@Component({
  selector: 'app-password-change',
  templateUrl: './password-change.page.html',
  styleUrls: ['./password-change.page.scss'],
})
export class PasswordChangePage implements OnInit {
  private token: string = null;

  constructor(private readonly authService: AuthService,
              private readonly navParams: NavParams,
              private readonly navCtrl: NavController,
              private readonly messagesService: MessagesService) {
  }

  ngOnInit() {
    const navData = this.navParams.data;
    if (navData.token) {
      this.token = navData.token;
    } else {
      history.replaceState({}, document.title, '.');
    }
  }

  async change(value: any) {
    const loading = await this.messagesService.showLoading('Changing Password');

    try {
      const success = await this.authService.change(this.token, value.password);
      await loading.dismiss();
      if (success) {
        this.messagesService.showSuccessToast('Password Change successful');
        history.replaceState({}, document.title, '.');
        this.navCtrl.navigateRoot('/login', {replaceUrl: true});
      } else {
        this.messagesService.showErrorToast();
      }
    } catch {
      await loading.dismiss();
      this.messagesService.showErrorToast();
    }
  }

}
