import {BrowserModule} from '@angular/platform-browser';
import {NgModule, ErrorHandler} from '@angular/core';
import {IonicApp, IonicModule, IonicErrorHandler} from 'ionic-angular';
import {MyApp} from './app.component';
import {HomePage} from '../pages/home/home';
import {LoginPage} from "../pages/login/login";
import {SignupPage} from "../pages/signup/signup";
import {CustomFormsModule} from 'ng2-validation';
import {AuthProvider} from "../providers/auth/auth";
import {PasswordResetPage} from "../pages/password-reset/password-reset";
import {PasswordChangePage} from "../pages/password-change/password-change";

@NgModule({
  declarations: [
    MyApp,
    HomePage,
    LoginPage,
    SignupPage,
    PasswordResetPage,
    PasswordChangePage
  ],
  imports: [
    BrowserModule,
    IonicModule.forRoot(MyApp, {}, {
      links: [
        { component: PasswordChangePage, name: 'Change Password', segment: 'change/:token' }
      ]
    }),
    CustomFormsModule
  ],
  bootstrap: [IonicApp],
  entryComponents: [
    MyApp,
    HomePage,
    LoginPage,
    SignupPage,
    PasswordResetPage,
    PasswordChangePage
  ],
  providers: [
    {provide: ErrorHandler, useClass: IonicErrorHandler},
    AuthProvider
  ]
})
export class AppModule {
}
