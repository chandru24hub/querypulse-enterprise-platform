import {
  Component,
  OnInit
} from '@angular/core';

import { CommonModule }
from '@angular/common';

import {
  ToastService,
  Toast
} from './toast.service';

@Component({
  selector: 'app-toast',

  standalone: true,

  imports: [
    CommonModule
  ],

  templateUrl: './toast.html',

  styleUrls: ['./toast.css']
})

export class ToastComponent
implements OnInit {

  toast:
  Toast | null = null;

  constructor(
    private toastService:
    ToastService
  ) {}

  ngOnInit(): void {

    this.toastService
      .toastState
      .subscribe({

        next: (
          toast
        ) => {

          this.toast = toast;
        }
      });
  }
}