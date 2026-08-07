import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router'; // Ensure RouterOutlet is imported

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet], // 1. Register the router portal token
  template: `
    <!-- 2. The router reads the URL and injects the matching component here -->
    <div class="block min-h-screen w-full bg-white">
      <router-outlet></router-outlet>
    </div>
  `
})
export class AppComponent {} // Clear out the old ngOnInit hash evaluations!
