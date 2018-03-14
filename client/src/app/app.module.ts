import {BrowserModule} from '@angular/platform-browser';
import {NgModule, ErrorHandler} from '@angular/core';
import {IonicApp, IonicModule, IonicErrorHandler} from 'ionic-angular';
import {MyApp} from './app.component';
import {HomePage} from '../pages/home/home';
import {LoginPage} from "../pages/login/login";
import {SignupPage} from "../pages/signup/signup";
import {CustomFormsModule} from 'ng2-validation';
import {AuthProvider} from "../providers/auth";
import {PasswordResetPage} from "../pages/password-reset/password-reset";
import {PasswordChangePage} from "../pages/password-change/password-change";
import {TabsPage} from "../pages/tabs/tabs";
import {ProfilePage} from "../pages/profile/profile";
import {RememberMePage} from "../pages/remember-me/remember-me";

@NgModule({
  declarations: [
    MyApp,
    HomePage,
    LoginPage,
    SignupPage,
    PasswordResetPage,
    PasswordChangePage,
    TabsPage,
    ProfilePage,
    RememberMePage
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
    PasswordChangePage,
    TabsPage,
    ProfilePage,
    RememberMePage
  ],
  providers: [
    {provide: ErrorHandler, useClass: IonicErrorHandler},
    AuthProvider
  ]
})
export class AppModule {
}
