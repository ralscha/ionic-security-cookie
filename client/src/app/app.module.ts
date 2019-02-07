import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {RouteReuseStrategy, RouterModule, Routes} from '@angular/router';
import {IonicModule, IonicRouteStrategy} from '@ionic/angular';
import {AppComponent} from './app.component';
import {FormsModule} from '@angular/forms';
import {CommonModule} from '@angular/common';
import {HomePage} from './home/home.page';
import {RelativeTimePipe} from './relative-time.pipe';
import {LoginPage} from './login/login.page';
import {PasswordChangePage} from './password-change/password-change.page';
import {PasswordResetPage} from './password-reset/password-reset.page';
import {ProfilePage} from './profile/profile.page';
import {RememberMePage} from './remember-me/remember-me.page';
import {SignupPage} from './signup/signup.page';
import {UsersPage} from './users/users.page';
import {AuthGuard} from './auth.guard';
import {LogoffPage} from './logoff/logoff.page';

const routes: Routes = [
  {
    path: '',
    redirectTo: 'home',
    pathMatch: 'full'
  },
  {
    path: 'home',
    component: HomePage,
    canActivate: [AuthGuard]
  },
  {
    path: 'login',
    component: LoginPage
  },
  {
    path: 'change/:token',
    component: PasswordChangePage
  },
  {
    path: 'password-reset',
    component: PasswordResetPage
  },
  {
    path: 'profile',
    component: ProfilePage,
    canActivate: [AuthGuard]
  },
  {
    path: 'remember-me',
    component: RememberMePage,
    canActivate: [AuthGuard]
  },
  {
    path: 'signup',
    component: SignupPage
  },
  {
    path: 'users',
    component: UsersPage,
    canActivate: [AuthGuard]
  },
  {
    path: 'logoff',
    component: LogoffPage
  }
];

@NgModule({
  declarations: [AppComponent, HomePage, RelativeTimePipe, LoginPage, PasswordResetPage, PasswordChangePage,
    ProfilePage, RememberMePage, SignupPage, UsersPage, LogoffPage],
  entryComponents: [],
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    BrowserModule,
    RouterModule.forRoot(routes, {useHash: true}),
    IonicModule.forRoot()
  ],
  providers: [
    {provide: RouteReuseStrategy, useClass: IonicRouteStrategy}
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
