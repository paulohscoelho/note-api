import { UserResponse } from './user.model';

export interface Note {
  id: number;
  title: string;
  content: string;
  createdAt: string;
  user: UserResponse;
}

export interface NoteRequest {
  title: string;
  content: string;
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}
