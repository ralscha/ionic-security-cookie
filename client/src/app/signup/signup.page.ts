import {Component, ViewChild} from '@angular/core';
import {AuthService} from '../auth.service';
import {MessagesService} from '../messages.service';
import {NgModel} from '@angular/forms';
import {NavController} from '@ionic/angular';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.page.html',
  styleUrls: ['./signup.page.scss'],
})
export class SignupPage {

  @ViewChild('userName', {static: true})
  userNameModel!: NgModel;

  constructor(private readonly authService: AuthService,
              private readonly messagesService: MessagesService,
              private readonly navCtrl: NavController) {
  }

  // tslint:disable-next-line:no-any
  async signup(value: any): Promise<void> {
    const loading = await this.messagesService.showLoading('Signing up');
    try {
      const username = await this.authService.signup(value);
      await loading.dismiss();
      if (username !== null) {
        this.showSuccesToast(username);
      } else {
        await this.navCtrl.navigateRoot('/home');
      }
    } catch {
      await loading.dismiss();
      await this.messagesService.showErrorToast();
    }
  }

  private showSuccesToast(username: string): void {
    if (username !== 'EXISTS') {
      this.messagesService.showSuccessToast('Sign up successful');
    } else {
      this.messagesService.showErrorToast('Username already registered');
      this.userNameModel.control.setErrors({userNameTaken: true});
    }
  }

}
