import { ApplicationConfig } from '@angular/core';
import { provideRouter, withHashLocation } from '@angular/router'; // Import withHashLocation
import { routes } from './app.routes';
import { provideHttpClient } from '@angular/common/http';

export const appConfig: ApplicationConfig = {
  providers: [
    // Activates clean, crash-free hash routing for isolated multi-tab access
    provideRouter(routes, withHashLocation()),
    provideHttpClient()
  ]
};
