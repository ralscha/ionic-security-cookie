import {
  PreloadAllModules,
  provideRouter,
  RouteReuseStrategy,
  Routes,
  withHashLocation,
  withPreloading,
} from '@angular/router';
import { bootstrapApplication } from '@angular/platform-browser';
import { inject, provideZoneChangeDetection } from '@angular/core';
import { provideHttpClient } from '@angular/common/http';
import { AuthGuard } from './app/auth.guard';
import { AppComponent } from './app/app.component';
import { IonicRouteStrategy, provideIonicAngular } from '@ionic/angular/standalone';

const canActivateAuthenticated = () => inject(AuthGuard).canActivate();

const routes: Routes = [
  {
    path: '',
    redirectTo: 'home',
    pathMatch: 'full',
  },
  {
    path: 'home',
    loadComponent: () => import('./app/home/home.page').then((m) => m.HomePage),
    canActivate: [canActivateAuthenticated],
  },
  {
    path: 'login',
    loadComponent: () => import('./app/login/login.page').then((m) => m.LoginPage),
  },
  {
    path: 'change/:token',
    loadComponent: () =>
      import('./app/password-change/password-change.page').then((m) => m.PasswordChangePage),
  },
  {
    path: 'password-reset',
    loadComponent: () =>
      import('./app/password-reset/password-reset.page').then((m) => m.PasswordResetPage),
  },
  {
    path: 'profile',
    loadComponent: () => import('./app/profile/profile.page').then((m) => m.ProfilePage),
    canActivate: [canActivateAuthenticated],
  },
  {
    path: 'remember-me',
    loadComponent: () => import('./app/remember-me/remember-me.page').then((m) => m.RememberMePage),
    canActivate: [canActivateAuthenticated],
  },
  {
    path: 'signup',
    loadComponent: () => import('./app/signup/signup.page').then((m) => m.SignupPage),
  },
  {
    path: 'users',
    loadComponent: () => import('./app/users/users.page').then((m) => m.UsersPage),
    canActivate: [canActivateAuthenticated],
  },
  {
    path: 'logoff',
    loadComponent: () => import('./app/logoff/logoff.page').then((m) => m.LogoffPage),
  },
];

bootstrapApplication(AppComponent, {
  providers: [
    provideZoneChangeDetection(),
    provideHttpClient(),
    provideIonicAngular(),
    { provide: RouteReuseStrategy, useClass: IonicRouteStrategy },
    provideRouter(routes, withHashLocation(), withPreloading(PreloadAllModules)),
  ],
}).catch((err) => console.error(err));
