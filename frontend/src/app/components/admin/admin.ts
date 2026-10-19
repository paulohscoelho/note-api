import { Component, OnInit, inject, ChangeDetectorRef, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AdminService } from '../../services/admin';
import { AdminUserResponse, Role } from '../../models/user.model';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './admin.html',
  styleUrl: './admin.css'
})
export class Admin implements OnInit {

  private readonly adminService = inject(AdminService);
  private readonly cdr = inject(ChangeDetectorRef);

  users = signal<AdminUserResponse[]>([]);
  loading = signal<boolean>(false);
  errorMessage = signal<string>('');

  searchTerm = signal<string>('');

  filteredUsers = computed(() => {
    const term = this.searchTerm().toLowerCase().trim();
    const allUsers = this.users();
    if (!term) return allUsers;
    return allUsers.filter(u => u.email.toLowerCase().includes(term));
  });

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.loading.set(true);
    this.errorMessage.set('');

    this.adminService.listAllUsers().subscribe({
      next: (page) => {
        this.users.set(page.content);
        this.loading.set(false);
      },
      error: (err) => {
        this.errorMessage.set('Erro ao carregar usuários.');
        this.loading.set(false);
      }
    });
  }

  toggleRole(user: AdminUserResponse): void {
    const newRole: Role = user.role === 'ADMIN' ? 'USER' : 'ADMIN';
    const confirmMessage = `Mudar a role de ${user.email} para ${newRole}?`;

    if (!confirm(confirmMessage)) return;

    this.adminService.updateUserRole(user.uuid, newRole).subscribe({
      next: () => {
        this.loadUsers();
      },
      error: (err) => {
        alert('Erro ao atualizar role.');
      }
    });
  }

  deleteUser(user: AdminUserResponse): void {
    const confirmMessage = `Tem certeza que deseja DELETAR o usuário ${user.email}?`;

    if (!confirm(confirmMessage)) return;

    this.adminService.deleteUser(user.uuid).subscribe({
      next: () => {
        this.loadUsers();
      },
      error: (err) => {
        alert('Erro ao deletar usuário.');
      }
    });
  }
}
