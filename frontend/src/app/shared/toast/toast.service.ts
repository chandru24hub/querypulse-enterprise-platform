import { Injectable }
from '@angular/core';

import {
  BehaviorSubject
} from 'rxjs';

export interface Toast {

  type:
    'success' |
    'error' |
    'warning';

  message: string;
}

@Injectable({
  providedIn: 'root'
})

export class ToastService {

  private toastSubject =
    new BehaviorSubject<Toast | null>(
      null
    );

  toastState =
    this.toastSubject.asObservable();

  showSuccess(
    message: string
  ): void {

    this.showToast(
      'success',
      message
    );
  }

  showError(
    message: string
  ): void {

    this.showToast(
      'error',
      message
    );
  }

  showWarning(
    message: string
  ): void {

    this.showToast(
      'warning',
      message
    );
  }

  private showToast(

    type:
      'success' |
      'error' |
      'warning',

    message: string

  ): void {

    this.toastSubject.next({
      type,
      message
    });

    setTimeout(() => {

      this.clear();

    }, 3000);
  }

  clear(): void {

    this.toastSubject.next(null);
  }
}