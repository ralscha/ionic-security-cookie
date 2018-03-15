import {BrowserModule} from '@angular/platform-browser';
import {ErrorHandler, NgModule} from '@angular/core';
import {IonicApp, IonicErrorHandler, IonicModule} from 'ionic-angular';
import {MyApp} from './app.component';
import {HomePage} from '../pages/home/home';
import {LoginPage} from "../pages/login/login";
import {SignupPage} from "../pages/signup/signup";
import {CustomFormsModule} from 'ng2-validation';
import {AuthProvider} from "../providers/auth";
import {PasswordResetPage} from "../pages/password-reset/password-reset";
import {PasswordChangePage} from "../pages/password-change/password-change";
import {ProfilePage} from "../pages/profile/profile";
import {RememberMePage} from "../pages/remember-me/remember-me";
import {SideMenuContentComponent} from "../component/side-menu-content.component";
import {MessagesProvider} from '../providers/messages';
import {UsersPage} from "../pages/users/users";
import {RelativeTime} from "../pipes/realative-time";

@NgModule({
  declarations: [
    MyApp,
    HomePage,
    LoginPage,
    SignupPage,
    PasswordResetPage,
    PasswordChangePage,
    ProfilePage,
    RememberMePage,
    SideMenuContentComponent,
    UsersPage,
    RelativeTime
  ],
  imports: [
    BrowserModule,
    IonicModule.forRoot(MyApp, {}, {
      links: [
        {component: PasswordChangePage, name: 'Change Password', segment: 'change/:token'}
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
    ProfilePage,
    RememberMePage,
    UsersPage
  ],
  providers: [
    {provide: ErrorHandler, useClass: IonicErrorHandler},
    AuthProvider,
    MessagesProvider
  ]
})
export class AppModule {
}
