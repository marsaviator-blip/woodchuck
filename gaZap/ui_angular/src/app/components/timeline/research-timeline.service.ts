import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';

export interface ResearchSession {
  sessionId: string;
  focusArea: string;
  category: string;
  subject: string;
  topic: string;
  timestamp: string;
}

export interface CognitiveShift {
  sourceSessionId: string;
  targetSessionId: string;
  explanation: string;
  timestamp: string;
}

export interface TimelineState {
  sessions: ResearchSession[];
  shifts: CognitiveShift[];
  loading: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class ResearchTimelineService {
  private state$ = new BehaviorSubject<TimelineState>({
    sessions: [],
    shifts: [],
    loading: false
  });

  private baseUrl = 'http://localhost:8089/api/sessions';

  constructor(private http: HttpClient) {}

  watchState(): Observable<TimelineState> {
    return this.state$.asObservable();
  }

  loadTimelineData(): void {
    // 1. Force the UI into a loading state during data fetching operations
    this.state$.next({ ...this.state$.getValue(), loading: true });

    // 2. Fetch both sessions and natural transformation shift bridges simultaneously
    this.http.get<ResearchSession[]>(`${this.baseUrl}/timeline`).subscribe({
      next: (sessions) => {
        this.http.get<CognitiveShift[]>(`${this.baseUrl}/shifts`).subscribe({
          next: (shifts) => {
            this.state$.next({
              sessions: sessions.sort((a, b) => new Date(a.timestamp).getTime() - new Date(b.timestamp).getTime()),
              shifts: shifts,
              loading: false
            });
          },
          error: (err) => this.handleError('Failed to load natural transformation bridges', err)
        });
      },
      error: (err) => this.handleError('Failed to load structural research trajectory timeline', err)
    });
  }

  private handleError(message: string, error: any): void {
    console.error(message, error);
    this.state$.next({ ...this.state$.getValue(), loading: false });
  }
}
