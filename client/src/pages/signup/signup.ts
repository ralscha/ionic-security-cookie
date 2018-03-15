import {Component, ViewChild} from '@angular/core';
import {NgModel} from "@angular/forms";
import {AuthProvider} from "../../providers/auth";
import {MessagesProvider} from "../../providers/messages";

@Component({
  selector: 'page-signup',
  templateUrl: 'signup.html'
})
export class SignupPage {

  @ViewChild('username')
  usernameModel: NgModel;

  constructor(private readonly authProvider: AuthProvider,
              private readonly messages: MessagesProvider) {
  }

  async signup(value: any) {
    const loading = this.messages.showLoading('Signing up');
    try {
      const username = await this.authProvider.signup(value);
      loading.dismiss();
      this.showSuccesToast(username);
    }
    catch {
      loading.dismiss();
      this.messages.showErrorToast();
    }
  }

  private showSuccesToast(username) {
    if (username !== 'EXISTS') {
      this.messages.showSuccessToast('Sign up successful');
    }
    else {
      this.messages.showErrorToast('Username already registered');
      this.usernameModel.control.setErrors({'usernameTaken': true});
    }
  }

}
