export interface Note {
  id: number;
  title: string;
  content: string;
  priority: number;
  dueDate: string | null;
  createdAt: string;
  updatedAt?: string;
  user?: {
    uuid: string;
    email: string;
  };
}

export interface NoteRequest {
  title: string;
  content: string;
  priority: number;
  dueDate: string | null;
}

export interface Page<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  number: number;
  size: number;
  first: boolean;
  last: boolean;
}
