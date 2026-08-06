import { Routes } from '@angular/router';
import { SplitPaneComponent } from './components/splitPane/split-pane.component';
import { CategoryCanvasComponent } from './components/category-canvas/category-canvas.component';
import { TimelineDashboardComponent } from './components/timeline/timeline-dashboard.component';


export const routes: Routes = [
  // 1. Direct landing page mapping
  { path: '', component: SplitPaneComponent },

  // 2. Clear, isolated sibling targets for your separate tabs
  { path: 'split-pane', component: SplitPaneComponent },
  { path: 'category-canvas', component: CategoryCanvasComponent },
  { path: 'timeline', component: TimelineDashboardComponent }
];
