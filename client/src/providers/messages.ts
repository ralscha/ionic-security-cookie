import {Injectable} from '@angular/core';
import {LoadingController, ToastController} from "ionic-angular";
import {Loading} from "ionic-angular/components/loading/loading";

@Injectable()
export class MessagesProvider {

  constructor(private readonly toastCtrl: ToastController,
              private readonly loadingCtrl: LoadingController) {
  }

  showLoading(message: string = 'Working'): Loading {
    const loading = this.loadingCtrl.create({
      spinner: 'bubbles',
      content: `${message} ...`
    });

    loading.present();
    return loading;
  }

  showErrorToast(message: string = 'Unexpected error occurred') {
    const toast = this.toastCtrl.create({
      message,
      duration: 4000,
      position: 'bottom'
    });
    toast.present();
  }

  showSuccessToast(message: string) {
    const toast = this.toastCtrl.create({
      message,
      duration: 4000,
      position: 'bottom'
    });
    toast.present();
  }

}
