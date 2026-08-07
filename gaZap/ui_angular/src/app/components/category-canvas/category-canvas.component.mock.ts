import { Component, OnInit, ElementRef, ViewChild } from '@angular/core';
import { CategoryStateService, CategoryNode, FunctorEdge } from './category-state.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-category-canvas',
  imports: [CommonModule, FormsModule],
  template: `
    <div class="canvas-container">
      <div class="sidebar">
        <h3>Map Note to Math Store</h3>
        <textarea [(ngModel)]="noteText" placeholder="Type casual insight..."></textarea>
        <button (click)]="mapNote()">Execute Functor Mapping</button>
      </div>
      <div class="graph-window">
        <!-- Visualizing distinct Functor categories -->
        <div class="category-zone panel-informal">Category Informal (I)</div>
        <div class="category-zone panel-formal">Category Formal (F)</div>
        <canvas #graphCanvas width="800" height="600"></canvas>
      </div>
    </div>
  `,
  styles: [`
    .canvas-container { display: flex; height: 100vh; }
    .sidebar { width: 300px; padding: 20px; background: #f4f4f6; }
    .graph-window { flex-grow: 1; position: relative; background: #fafafa; display: flex; }
    .category-zone { flex: 1; border: 1px dashed #ccc; text-align: center; padding: 10px; font-weight: bold; }
    canvas { position: absolute; top: 0; left: 0; pointer-events: none; }
  `]
})
export class CategoryCanvasComponent implements OnInit {
  @ViewChild('graphCanvas', { static: true }) canvasRef!: ElementRef<HTMLCanvasElement>;
  noteText = '';
  selectedFormalId = 'paper-doi-12345'; // Pre-selected from active reading context

  constructor(private stateService: CategoryStateService) {}

  ngOnInit(): void {
    this.stateService.loadCategoryGraph();
    this.stateService.getNodes().subscribe(nodes => this.drawGraph());
  }

  mapNote(): void {
    if (!this.noteText.trim()) return;
    this.stateService.submitInformalNote(this.noteText, this.selectedFormalId).subscribe(() => {
      this.noteText = '';
      this.stateService.loadCategoryGraph(); // Trigger diagram update
    });
  }

  drawGraph(): void {
    const ctx = this.canvasRef.nativeElement.getContext('2d');
    if (!ctx) return;
    ctx.clearRect(0, 0, 800, 600);

    // Custom 2D structural rendering loop matching layout zones goes here
  }
}
