export type Role = 'USER' | 'ADMIN';

export interface MeResponse {
  uuid: string;
  email: string;
  role: Role;
}

export interface UserResponse {
  uuid: string;
  email: string;
}

export interface AdminUserResponse {
  uuid: string;
  email: string;
  role: Role;
}

export interface RoleUpdateRequest {
  role: Role;
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
  first: boolean;
  last: boolean;
}
