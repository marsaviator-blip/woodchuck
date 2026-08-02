import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { KnowledgeMorphismGroup, StreamConceptNode } from './knowledge-space.models';

@Injectable({
  providedIn: 'root'
})
export class KnowledgeSyncService {
  private http = inject(HttpClient);
  private syncEndpoint = 'http://localhost:8089/api/workspace/sync';

  // Tracks asynchronous network thread status reactively
  isSyncInProgress = signal<boolean>(false);

  commitToNearTermStore(payload: KnowledgeMorphismGroup): Observable<any> {
    return this.http.post<any>(this.syncEndpoint, payload);
  }

  // Auto-builds the final structural OKF envelope directly from current workspace states
  generateMorphismGroup(
    nodes: StreamConceptNode[],
    meta: { focusArea: string; category: string; subject: string; topic: string }
  ): KnowledgeMorphismGroup {
    return {
      groupId: `MGRP-${crypto.randomUUID()}`,
      savedAt: new Date().toISOString(),
      focusArea: meta.focusArea,
      category: meta.category,
      subject: meta.subject,
      topic: meta.topic,
      curatedNodes: [...nodes]
    };
  }
}
