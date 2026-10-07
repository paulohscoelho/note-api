import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Note, NoteRequest, Page } from '../models/note.model';

@Injectable({ providedIn: 'root' })
export class NoteService {

  private readonly API_URL = 'http://localhost:8080';

  constructor(private http: HttpClient) {}

  list(page: number = 0, size: number = 10, search?: string): Observable<Page<Note>> {
    let params = new HttpParams()
      .set('page', page)
      .set('size', size)
      .set('sort', 'title,asc');

    if (search && search.trim()) {
      params = params.set('search', search.trim());
    }

    return this.http.get<Page<Note>>(`${this.API_URL}/notes`, { params });
  }

  getById(id: number): Observable<Note> {
    return this.http.get<Note>(`${this.API_URL}/notes/${id}`);
  }

  create(request: NoteRequest): Observable<Note> {
    return this.http.post<Note>(`${this.API_URL}/notes`, request);
  }

  update(id: number, request: NoteRequest): Observable<Note> {
    return this.http.put<Note>(`${this.API_URL}/notes/${id}`, request);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/notes/${id}`);
  }
}
