import {Component, OnInit} from '@angular/core';
import {AuthService} from '../auth.service';
import {NavController} from '@ionic/angular';
import {MessagesService} from '../messages.service';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-password-change',
  templateUrl: './password-change.page.html',
  styleUrls: ['./password-change.page.scss'],
})
export class PasswordChangePage implements OnInit {
  private token: string | null = null;

  constructor(private readonly authService: AuthService,
              private readonly route: ActivatedRoute,
              private readonly navCtrl: NavController,
              private readonly messagesService: MessagesService) {
  }

  ngOnInit(): void {
    this.token = this.route.snapshot.paramMap.get('token');
  }

  // tslint:disable-next-line:no-any
  async change(value: any): Promise<void> {
    const loading = await this.messagesService.showLoading('Changing Password');
    if (!this.token) {
      throw new Error('token not set');
    }

    try {
      const success = await this.authService.change(this.token, value.password);
      await loading.dismiss();
      if (success) {
        await this.messagesService.showSuccessToast('Password Change successful');
        history.replaceState({}, document.title, '.');
        await this.navCtrl.navigateRoot('/login', {replaceUrl: true});
      } else {
        await this.messagesService.showErrorToast();
      }
    } catch {
      await loading.dismiss();
      await this.messagesService.showErrorToast();
    }
  }

}
