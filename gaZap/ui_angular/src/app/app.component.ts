import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SplitPaneComponent } from './components/splitPane/split-pane.component';
import { CategoryCanvasComponent } from './components/category-canvas/category-canvas.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, SplitPaneComponent, CategoryCanvasComponent],
  template: `
    <!-- Simple top-level switcher looking at the window location -->
    <div class="app-container">
      <app-split-pane *ngIf="activeRoute === 'stream'"></app-split-pane>
      <app-category-canvas *ngIf="activeRoute === 'vault'"></app-category-canvas>
    </div>
  `
})
export class AppComponent implements OnInit {
  activeRoute: 'stream' | 'vault' = 'stream';

  ngOnInit(): void {
    // Read the exact URL path currently entered into the browser address bar
    const path = window.location.hash;

    if (path.includes('category-vault')) {
      this.activeRoute = 'vault';
    } else {
      this.activeRoute = 'stream'; // Fallback default for /vault-workspace or root
    }
  }
}
