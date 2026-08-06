import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ResearchTimelineService, TimelineState, CognitiveShift } from './research-timeline.service';

@Component({
  selector: 'app-timeline-dashboard',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="timeline-container" *ngIf="state() as data">
      <h2>🧠 Cognitive Trajectory & Session Evolution</h2>
      <p class="subtitle">Tracking algebraic shifts across varying research domains</p>

      <div *ngIf="data.loading" class="loader">Traversing graph topology...</div>

      <!-- Chronological Vertical Timeline Stack -->
      <div class="timeline-track" *ngIf="!data.loading">
        <div class="timeline-item" *ngFor="let session of data.sessions; let i = index">

          <!-- Session Context Card Block -->
          <div class="session-card">
            <span class="timestamp">{{ session.timestamp | date:'short' }}</span>
            <h3>Session Block: {{ session.topic }}</h3>

            <div class="metadata-tags">
              <span class="tag focus">🎯 {{ session.focusArea }}</span>
              <span class="tag subject">📚 {{ session.subject }}</span>
            </div>
          </div>

          <!-- Paradigm Shift Connector: Renders a bridge link if a natural transformation exists to the next session -->
          <div class="shift-bridge" *ngIf="getShiftBetween(session.sessionId, data.sessions[i+1]?.sessionId, data.shifts) as shift">
            <div class="bridge-arrow">⚡ Natural Transformation (Paradigm Shift)</div>
            <div class="bridge-content">
              <strong>Context Evolution:</strong> {{ shift.explanation }}
            </div>
          </div>

        </div>
      </div>
    </div>
  `,
  styles: [`
    .timeline-container { padding: 30px; font-family: system-ui, sans-serif; max-width: 800px; margin: 0 auto; color: #1e293b; }
    .subtitle { color: #64748b; font-size: 14px; margin-bottom: 30px; }
    .timeline-track { position: relative; border-left: 3px solid #cbd5e1; padding-left: 20px; margin-left: 10px; }
    .timeline-item { position: relative; margin-bottom: 40px; }
    .timeline-item::before { content: ''; position: absolute; left: -27px; top: 20px; width: 12px; height: 12px; border-radius: 50%; background: #3b82f6; border: 2px solid #fff; }
    .session-card { background: #ffffff; border: 1px solid #e2e8f0; border-radius: 8px; padding: 20px; box-shadow: 0 1px 3px rgba(0,0,0,0.05); }
    .timestamp { font-size: 11px; color: #94a3b8; font-weight: bold; text-transform: uppercase; }
    h3 { margin: 5px 0 12px 0; font-size: 18px; color: #0f172a; }
    .metadata-tags { display: flex; gap: 10px; }
    .tag { font-size: 12px; padding: 4px 8px; border-radius: 4px; font-weight: 500; }
    .tag.focus { background: #f0fdf4; color: #166534; }
    .tag.subject { background: #eff6ff; color: #1e40af; }
    .shift-bridge { background: #fff7ed; border: 1px dashed #fed7aa; border-radius: 6px; padding: 15px; margin-top: 15px; margin-left: 20px; position: relative; }
    .bridge-arrow { font-size: 12px; font-weight: bold; color: #c2410c; margin-bottom: 5px; }
    .bridge-content { font-size: 13px; color: #475569; }
    .loader { font-style: italic; color: #64748b; }
  `]
})
export class TimelineDashboardComponent implements OnInit {
  state = signal<TimelineState>({ sessions: [], shifts: [], loading: false });

  constructor(private timelineService: ResearchTimelineService) {}

  ngOnInit(): void {
    this.timelineService.watchState().subscribe(newState => {
      this.state.set(newState);
    });
    this.timelineService.loadTimelineData();
  }

  getShiftBetween(sourceId: string, targetId: string | undefined, shifts: CognitiveShift[]): CognitiveShift | undefined {
  if (!targetId) return undefined;
  return shifts.find(s => s.sourceSessionId === sourceId && s.targetSessionId === targetId);
}
}
