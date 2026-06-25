import { Routes } from '@angular/router';

import { Login }
from './pages/login/login';

import { Register }
from './pages/register/register';

import { AdminDashboard }
from './pages/admin-dashboard/admin-dashboard';

import { UserDashboard }
from './pages/user-dashboard/user-dashboard';

import { authGuard }
from './guards/auth-guard';

import { DatabaseManagement }
from './pages/database-management/database-management';

import { GlobalAlertsComponent }
from './pages/global-alerts/global-alerts';

export const routes: Routes = [

  {
    path: '',
    component: Login
  },

  {
    path: 'register',
    component: Register
  },

  {
    path: 'admin-dashboard',

    component: AdminDashboard,

    canActivate: [authGuard]
  },

  {
    path: 'user-dashboard',

    component: UserDashboard,

    canActivate: [authGuard]
  },
 {
  path: 'database-management',

  component:
      DatabaseManagement,

  canActivate: [
      authGuard
  ]
},
  {
    path: 'alerts',

    component: GlobalAlertsComponent,

    canActivate: [authGuard]
  },
  {
    path: '**',

    redirectTo: ''
  }

 
];