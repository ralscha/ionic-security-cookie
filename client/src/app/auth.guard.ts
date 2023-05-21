import {Injectable} from '@angular/core';
import {Router, UrlTree} from '@angular/router';
import {Observable} from 'rxjs';
import {AuthService} from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard {

  constructor(private readonly authService: AuthService, private readonly router: Router) {
  }

  canActivate():
    Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    if (this.authService.loggedIn()) {
      return true;
    }
    return this.authService.checkLogin().then(success => success ? success : this.router.createUrlTree(['/login']));
  }

}
