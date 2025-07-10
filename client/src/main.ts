import {
  PreloadAllModules,
  provideRouter,
  RouteReuseStrategy,
  Routes,
  withHashLocation,
  withPreloading
} from '@angular/router';
import {bootstrapApplication} from '@angular/platform-browser';
import {HomePage} from './app/home/home.page';
import {inject} from '@angular/core';
import {AuthGuard} from './app/auth.guard';
import {LoginPage} from './app/login/login.page';
import {PasswordChangePage} from './app/password-change/password-change.page';
import {PasswordResetPage} from './app/password-reset/password-reset.page';
import {ProfilePage} from './app/profile/profile.page';
import {RememberMePage} from './app/remember-me/remember-me.page';
import {SignupPage} from './app/signup/signup.page';
import {UsersPage} from './app/users/users.page';
import {LogoffPage} from './app/logoff/logoff.page';
import {AppComponent} from './app/app.component';
import {IonicRouteStrategy, provideIonicAngular} from '@ionic/angular/standalone';

const routes: Routes = [
  {
    path: '',
    redirectTo: 'home',
    pathMatch: 'full'
  },
  {
    path: 'home',
    component: HomePage,
    canActivate: [() => inject(AuthGuard).canActivate()]
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
    canActivate: [() => inject(AuthGuard).canActivate()]
  },
  {
    path: 'remember-me',
    component: RememberMePage,
    canActivate: [() => inject(AuthGuard).canActivate()]
  },
  {
    path: 'signup',
    component: SignupPage
  },
  {
    path: 'users',
    component: UsersPage,
    canActivate: [() => inject(AuthGuard).canActivate()]
  },
  {
    path: 'logoff',
    component: LogoffPage
  }
];

bootstrapApplication(AppComponent, {
  providers: [
    provideIonicAngular(),
    {provide: RouteReuseStrategy, useClass: IonicRouteStrategy},
    provideRouter(routes, withHashLocation(), withPreloading(PreloadAllModules))
  ]
})
  .catch(err => console.error(err));
