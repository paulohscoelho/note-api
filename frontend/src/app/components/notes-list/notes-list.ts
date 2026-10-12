import { Component, OnInit, inject,ChangeDetectorRef  } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { NoteService } from '../../services/note.service';
import { AuthService } from '../../services/auth.service';
import { Note } from '../../models/note.model';

@Component({
  imports: [FormsModule, RouterLink],
  selector: 'app-notes-list',
  styleUrl: './notes-list.css',
  templateUrl: './notes-list.html',
})
export class NotesList implements OnInit {

  private readonly noteService = inject(NoteService);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private readonly cdr = inject(ChangeDetectorRef);

  notes: Note[] = [];
  loading: boolean = false;
  errorMessage: string = '';

  search: string = '';

  currentPage: number = 0;
  pageSize: number = 10;
  totalPages: number = 0;
  totalElements: number = 0;
  isFirstPage: boolean = true;
  isLastPage: boolean = false;

  ngOnInit(): void {
    this.loadNotes();
  }

  loadNotes(): void {
    this.loading = true;
    this.errorMessage = '';

    this.noteService.list(this.currentPage, this.pageSize, this.search).subscribe({
      next: (page) => {
        this.notes = page.content;
        this.totalPages = page.totalPages;
        this.totalElements = page.totalElements;
        this.isFirstPage = page.first;
        this.isLastPage = page.last;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err: HttpErrorResponse) => {
        this.loading = false;

        if (err.status === 401 || err.status === 403) {
          this.authService.logout();
          this.router.navigate(['/login']);
        } else {
          this.errorMessage = 'Erro ao carregar notas.';
        }

        console.error('Erro ao carregar notas:', err);
      }
    });
  }

  onSearch(): void {
    this.currentPage = 0;
    this.loadNotes();
  }

  clearSearch(): void {
    this.search = '';
    this.currentPage = 0;
    this.loadNotes();
  }

  nextPage(): void {
    if (!this.isLastPage) {
      this.currentPage++;
      this.loadNotes();
    }
  }

  previousPage(): void {
    if (!this.isFirstPage) {
      this.currentPage--;
      this.loadNotes();
    }
  }

  deleteNote(id: number, title: string): void {
    const confirmado = confirm(`Excluir a nota "${title}"?`);
    if (!confirmado) {
      return;
    }

    this.noteService.delete(id).subscribe({
      next: () => {
        this.loadNotes();
      },
      error: (err: HttpErrorResponse) => {
        this.errorMessage = 'Erro ao excluir nota.';
        console.error('Erro ao excluir:', err);
      }
    });
  }
}
