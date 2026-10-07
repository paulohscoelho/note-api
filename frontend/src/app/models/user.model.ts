export type Role = 'USER' | 'ADMIN';

export interface MeResponse{
  uuid: string;
  email: string;
  role: Role;
}

export interface UserResponse{
  uuid: string;
  email: string;
}
