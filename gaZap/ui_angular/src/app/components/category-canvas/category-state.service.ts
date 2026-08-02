import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';

export interface CategoryNode {
  id: string;
  label: string;
  categoryType: 'INFORMAL' | 'FORMAL';
  contentSummary: string;
}

export interface FunctorEdge {
  sourceId: string;
  targetId: string;
  strength: string;
}

@Injectable({
  providedIn: 'root'
})
export class CategoryStateService {
  private nodes$ = new BehaviorSubject<CategoryNode[]>([]);
  private edges$ = new BehaviorSubject<FunctorEdge[]>([]);

  constructor(private http: HttpClient) {}

  getNodes(): Observable<CategoryNode[]> { return this.nodes$.asObservable(); }
  getEdges(): Observable<FunctorEdge[]> { return this.edges$.asObservable(); }

  loadCategoryGraph(): void {
    this.http.get<{nodes: CategoryNode[], edges: FunctorEdge[]}>('/api/knowledge/graph')
      .subscribe(data => {
        this.nodes$.next(data.nodes);
        this.edges$.next(data.edges);
      });
  }

  submitInformalNote(noteContent: string, targetFormalId: string): Observable<void> {
    return this.http.post<void>('/api/knowledge/functor-map', {
      content: noteContent,
      formalPaperId: targetFormalId
    });
  }
}
