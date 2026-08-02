import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-category-canvas',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="canvas-container">
      <div class="sidebar">
        <h3>Map Note to Math Store</h3>
        <textarea [(ngModel)]="noteText" placeholder="Type casual insight..."></textarea>
        <button (click)="mapNote()">Execute Functor Mapping</button>
      </div>
    </div>
  `
})
export class CategoryCanvasComponent {
  noteText = '';
  selectedFormalId = 'paper-doi-12345'; // Contextual academic node identifier

  constructor(private http: HttpClient) {}

  mapNote(): void {
    if (!this.noteText.trim()) return;

    const payload = {
      content: this.noteText,
      formalPaperId: this.selectedFormalId
    };

    // Concrete HTTP POST fetch call to the Spring Boot API
    this.http.post('/api/knowledge/functor-map', payload)
      .subscribe({
        next: (response) => {
          console.log('Category Functor Pipeline Execution complete:', response);
          this.noteText = ''; // Clear workspace text input area
        },
        error: (err) => {
          console.error('Failed to map informal concept to formal category:', err);
        }
      });
  }
}
