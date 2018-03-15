import {Component, OnInit} from '@angular/core';
import {NavController, NavParams} from 'ionic-angular';
import {AuthProvider} from "../../providers/auth";
import {LoginPage} from "../login/login";
import {MessagesProvider} from "../../providers/messages";

@Component({
  selector: 'page-password-change',
  templateUrl: 'password-change.html',
})
export class PasswordChangePage implements OnInit {

  private token: string = null;

  constructor(private readonly authProvider: AuthProvider,
              private readonly navParams: NavParams,
              private readonly navCtrl: NavController,
              private readonly messages: MessagesProvider) {
  }

  ngOnInit() {
    const navData = this.navParams.data;
    if (navData.token) {
      this.token = navData.token;
    }
    else {
      history.replaceState({}, document.title, ".");
    }
  }

  async change(value: any) {
    const loading = this.messages.showLoading('Changing Password');

    try {
      const success = await this.authProvider.change(this.token, value.password);
      loading.dismiss();
      if (success) {
        this.messages.showSuccessToast('Password Change successful');
        history.replaceState({}, document.title, ".");
        this.navCtrl.setRoot(LoginPage);
      }
      else {
        this.messages.showErrorToast();
      }
    }
    catch {
      loading.dismiss();
      this.messages.showErrorToast();
    }
  }
  
}
