import {
  HttpInterceptorFn
} from '@angular/common/http';

import {
  HttpRequest,
  HttpHandlerFn,
  HttpErrorResponse
} from '@angular/common/http';

import { inject }
from '@angular/core';

import { Router }
from '@angular/router';

import {
  catchError
} from 'rxjs/operators';

import {
  throwError
} from 'rxjs';

export const authInterceptor:
HttpInterceptorFn = (

  req: HttpRequest<unknown>,

  next: HttpHandlerFn

) => {

  const router =
    inject(Router);

  const token =
    localStorage.getItem('token');

  const isAuthEndpoint =
    req.url.includes('/api/auth/');

  /*
    ATTACH JWT TOKEN
  */

  let modifiedRequest = req;

  if (token && !isAuthEndpoint) {

    modifiedRequest =
      req.clone({

        setHeaders: {

          Authorization:
            `Bearer ${token}`
        }
      });
  }

  /*
    HANDLE API ERRORS
  */

  return next(
    modifiedRequest
  ).pipe(

    catchError(
      (
        error:
        HttpErrorResponse
      ) => {

        /*
          AUTO LOGOUT
        */

        if (
          error.status === 401
        ) {

          localStorage.clear();

          router.navigate(['/']);
        }

        return throwError(
          () => error
        );
      }
    )
  );
};