// import { Routes } from '@angular/router';

// export const routes: Routes = [];

// import { Routes } from '@angular/router';
// import { SplitpaneComponent } from './components/splitpane/splitpane.component';
// import { CategoryCanvasComponent } from './components/category-canvas/category-canvas.component';

// export const routes: Routes = [
//   { path: '', redirectTo: 'reader', pathMatch: 'full' },
//   { path: 'reader', component: SplitpaneComponent },
//   { path: 'category-vault', component: CategoryCanvasComponent }
// ];

import { Routes } from '@angular/router';
// The './' prefix looks inside the current 'app' folder where 'app.routes.ts' lives
import { SplitPaneComponent } from './components/splitPane/split-pane.component';
import { CategoryCanvasComponent } from './components/category-canvas/category-canvas.component';
//import { ReaderDashboardComponent } from './components/reader-dashboard/reader-dashboard.component';

export const routes: Routes = [
  { path: '', redirectTo: 'vault-workspace/stream', pathMatch: 'full' },
  {
    path: 'vault-workspace',
    component: SplitPaneComponent,
    children: [
      { path: '', redirectTo: 'stream', pathMatch: 'full' },
      { path: 'stream', component: SplitPaneComponent },
      { path: 'category-vault', component: CategoryCanvasComponent }
    ]
  },
  { path: '**', redirectTo: 'vault-workspace/stream' }
];

