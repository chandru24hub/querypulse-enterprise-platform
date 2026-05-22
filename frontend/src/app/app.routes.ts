import { Routes } from '@angular/router';

import { Login }
from './pages/login/login';

import { AdminDashboard }
from './pages/admin-dashboard/admin-dashboard';

import { UserDashboard }
from './pages/user-dashboard/user-dashboard';

import { authGuard }
from './guards/auth-guard';

export const routes: Routes = [

    {
        path: '',
        component: Login
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
        path: '**',
        redirectTo: ''
    }
];