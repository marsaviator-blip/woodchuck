// import { bootstrapApplication } from '@angular/platform-browser';
// import { appConfig } from './app/app.config';
// import { App } from './app/app';
// import { CommonModule } from '@angular/common';
// import { SplitPane } from './splitPane/splitPaneApp';

// bootstrapApplication(SplitPane)
//  .catch((err) => console.error(err));

//import { Component } from '@angular/core';
import { bootstrapApplication } from '@angular/platform-browser';
//import { CommonModule } from '@angular/common';
//import { FormsModule } from '@angular/forms'; // 1. Import FormsModule for [(ngModel)]
import { provideHttpClient } from '@angular/common/http'; // 2. Import HTTP provider
//import { HttpClient } from '@angular/common/http';
//import { SplitComponent, SplitAreaComponent } from 'angular-split';
//import { SplitPane } from './app/components/splitPane/splitPaneApp';
import { AppComponent } from './app/app.component'; // <-- Imports your component code


// PASS THE PROVIDER HERE IN YOUR BOOTSTRAP CONFIG
bootstrapApplication(AppComponent, {
  providers: [provideHttpClient()]
}).catch((err) => console.error(err));
