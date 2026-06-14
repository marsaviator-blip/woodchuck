<template>
  <div class="instructional-panel">
    <!-- Header Section -->
    <div class="panel-header">
      <div class="icon-wrapper">💡</div>
      <div>
        <h2>Container Management Guide</h2>
        <p class="subtitle">Quick reference for system administrators and developers.</p>
      </div>
    </div>

    <!-- Instruction Steps -->
    <div class="steps-container">
      <div class="step-card">
        <div class="step-number">01</div>
        <div class="step-content">
          <h3>Monitor Status</h3>
          <p>Check the live dashboard grid. Green badges indicate active running environments, while red flags require immediate triage.</p>
        </div>
      </div>

      <div class="step-card">
        <div class="step-number">02</div>
        <div class="step-content">
          <h3>Verify Networks</h3>
          <p>Ensure isolated containers are joined to their appropriate visual networks. Hover over any network tag to view structural details.</p>
        </div>
      </div>

      <div class="step-card">
        <div class="step-number">03</div>
        <div class="step-content">
          <h3>Isolate Issues</h3>
          <p>Use truncated IDs to quickly reference container states inside your command line terminal during active debug sessions.</p>
        </div>
      </div>
    </div>

    <!-- Action/Tip Banner Footer -->
    <div class="info-banner">
      <strong>Pro Tip:</strong> Container states automatically cycle through strict health assessments. Refreshes mirror current infrastructure configurations instantly.
    </div>
  </div>
</template>

<script setup lang="ts">
// No complex state needed for a simple static instructional layout!
</script>

<style scoped>
.instructional-panel {
  background: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  font-family: system-ui, -apple-system, sans-serif;
  color: #1a1a1a;
  max-width: 800px;
  margin: 0 auto;
}

/* Header UI */
.panel-header {
  display: flex;
  align-items: center;
  gap: 16px;
  border-bottom: 1px solid #f1f5f9;
  padding-bottom: 16px;
  margin-bottom: 24px;
}
.icon-wrapper {
  font-size: 2rem;
  background: #fef9c3;
  padding: 8px 12px;
  border-radius: 10px;
}
.panel-header h2 {
  margin: 0;
  font-size: 1.4rem;
  font-weight: 700;
  color: #0f172a;
}
.subtitle {
  margin: 4px 0 0 0;
  color: #64748b;
  font-size: 0.95rem;
}

/* Steps Layout Grid */
.steps-container {
  display: grid;
  grid-template-columns: 1fr;
  gap: 16px;
  margin-bottom: 24px;
}
@media (min-width: 640px) {
  .steps-container {
    grid-template-columns: repeat(3, 1fr);
  }
}

.step-card {
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  position: relative;
  transition: transform 0.2s, box-shadow 0.2s;
}
.step-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
}

.step-number {
  font-size: 1.5rem;
  font-weight: 800;
  color: #cbd5e1;
  line-height: 1;
}
.step-content h3 {
  margin: 0 0 6px 0;
  font-size: 1rem;
  color: #1e293b;
}
.step-content p {
  margin: 0;
  font-size: 0.85rem;
  color: #475569;
  line-height: 1.5;
}

/* Banner Footer */
.info-banner {
  background: #eff6ff;
  border-left: 4px solid #3b82f6;
  color: #1e40af;
  padding: 12px 16px;
  border-radius: 4px;
  font-size: 0.85rem;
  line-height: 1.4;
}
</style>
