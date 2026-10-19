import { Routes } from '@angular/router';
import { Login } from './components/login/login';
import { Register } from './components/register/register';
import { NotesList } from './components/notes-list/notes-list';
import { NoteForm } from './components/note-form/note-form';
import { Admin } from './components/admin/admin';
import { authGuard } from './guards/auth.guard';
import { adminGuard } from './guards/admin.guard';

export const routes: Routes = [
  { path: 'login', component: Login },
  { path: 'register', component: Register },
  { path: 'notes', component: NotesList, canActivate: [authGuard] },
  { path: 'notes/new', component: NoteForm, canActivate: [authGuard] },
  { path: 'notes/:id/edit', component: NoteForm, canActivate: [authGuard] },
  { path: 'admin', component: Admin, canActivate: [authGuard, adminGuard] },
  { path: '', redirectTo: '/notes', pathMatch: 'full' },
  { path: '**', redirectTo: '/notes' }
];
