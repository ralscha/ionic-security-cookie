import {Injectable} from '@angular/core';
import {LoadingController, ToastController} from '@ionic/angular';

@Injectable({
  providedIn: 'root'
})
export class MessagesService {

  constructor(private readonly toastCtrl: ToastController,
              private readonly loadingCtrl: LoadingController) {
  }

  async showLoading(message: string = 'Working') {
    const loading = await this.loadingCtrl.create({
      spinner: 'bubbles',
      message: `${message} ...`
    });
    await loading.present();
    return loading;
  }

  async showErrorToast(message: string = 'Unexpected error occurred') {
    const toast = await this.toastCtrl.create({
      message,
      duration: 4000,
      position: 'bottom'
    });
    await toast.present();
  }

  async showSuccessToast(message: string) {
    const toast = await this.toastCtrl.create({
      message,
      duration: 4000,
      position: 'bottom'
    });
    await toast.present();
  }
}
