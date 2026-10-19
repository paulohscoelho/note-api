import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink, ActivatedRoute } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { NoteService } from '../../services/note.service';
import { NoteRequest } from '../../models/note.model';

@Component({
  imports: [FormsModule, RouterLink],
  selector: 'app-note-form',
  styleUrl: './note-form.css',
  templateUrl: './note-form.html',
})
export class NoteForm implements OnInit {

  private readonly noteService = inject(NoteService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly cdr = inject(ChangeDetectorRef);

  noteId: number | null = null;
  isEditMode: boolean = false;

  title: string = '';
  content: string = '';
  priority: number = 0;
  dueDate: string | null = null;

  loading: boolean = false;
  errorMessage: string = '';

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');

    if (idParam) {
      this.noteId = Number(idParam);
      this.isEditMode = true;
      this.loadNote();
    }
  }

  loadNote(): void {
    if (this.noteId === null) return;

    this.loading = true;
    this.noteService.getById(this.noteId).subscribe({
      next: (note) => {
        this.title = note.title;
        this.content = note.content;
        this.priority = note.priority || 0;
        this.dueDate = note.dueDate ? note.dueDate.substring(0, 16) : null;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err: HttpErrorResponse) => {
        this.loading = false;
        this.errorMessage = 'Erro ao carregar nota.';
        this.cdr.detectChanges();
      }
    });
  }

  onSubmit(): void {
    this.errorMessage = '';

    if (!this.title || !this.content) {
      this.errorMessage = 'Preencha título e conteúdo.';
      return;
    }

    this.loading = true;

    const request: NoteRequest = {
      title: this.title,
      content: this.content,
      priority: this.priority,
      dueDate: this.dueDate ? this.dueDate + ':00' : null
    };

    if (this.isEditMode && this.noteId !== null) {
      this.noteService.update(this.noteId, request).subscribe({
        next: () => {
          this.loading = false;
          this.cdr.detectChanges();
          this.router.navigate(['/notes']);
        },
        error: (err) => this.handleError(err, 'Erro ao atualizar nota.')
      });
    } else {
      this.noteService.create(request).subscribe({
        next: () => {
          this.loading = false;
          this.cdr.detectChanges();
          this.router.navigate(['/notes']);
        },
        error: (err) => this.handleError(err, 'Erro ao criar nota.')
      });
    }
  }

  private handleError(err: HttpErrorResponse, fallbackMessage: string): void {
    this.loading = false;
    this.errorMessage = fallbackMessage;
    this.cdr.detectChanges();
  }
}
