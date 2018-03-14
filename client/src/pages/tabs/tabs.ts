import {Component} from "@angular/core";
import {HomePage} from "../home/home";
import {ProfilePage} from "../profile/profile";
import {RememberMePage} from "../remember-me/remember-me";

@Component({
  templateUrl: 'tabs.html'
})
export class TabsPage {
  homePage = HomePage;
  profilePage = ProfilePage;
  rememberMePage = RememberMePage
}
