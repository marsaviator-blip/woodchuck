<template>
  <div class="project-landing">
    <!-- Hero Brand Header -->
    <header class="hero-section">
      <div class="brand-badge">Project Workspace</div>
      <h1>Knowledge Orchestration Hub</h1>
      <p class="hero-subtitle">
        An AI-assisted platform for thematic sensemaking, knowledge retention, and collaborative scientific discovery.
      </p>
    </header>

    <!-- Project Metrics / Quick Stats Overview -->
    <section class="metrics-bar">
      <div class="metric-stat">
        <span class="stat-number">1,240</span>
        <span class="stat-label">Knowledge Nodes</span>
      </div>
      <div class="metric-stat">
        <span class="stat-number">14</span>
        <span class="stat-label">Active Experiments</span>
      </div>
      <div class="metric-stat">
        <span class="stat-number">3</span>
        <span class="stat-label">Connected Run Environments</span>
      </div>
    </section>

    <!-- Modular Workspaces Navigation Grid -->
    <main class="workspace-grid">
      
      <!-- Module 1: Knowledge Management -->
      <div class="workspace-card" @click="navigateTo('knowledge')">
        <div class="card-icon blue-theme">🧠</div>
        <div class="card-body">
          <h3>Knowledge & Retention</h3>
          <p>Organize data with structured taxonomy and faceted classifications. Build your personal world sensemaking map.</p>
          <span class="action-link">Open Workspace &rarr;</span>
        </div>
      </div>

      <!-- Module 2: LLM Science Experiments -->
      <div class="workspace-card" @click="navigateTo('experiments')">
        <div class="card-icon purple-theme">🔬</div>
        <div class="card-body">
          <h3>LLM Lab Experiments</h3>
          <p>Execute and collaborate on complex math and science tasks directed by LLMs and validated by human teams.</p>
          <span class="action-link">Open Lab &rarr;</span>
        </div>
      </div>

      <!-- Module 3: Graph Explorations -->
      <div class="workspace-card" @click="navigateTo('graph')">
        <div class="card-icon green-theme">🕸️</div>
        <div class="card-body">
          <h3>Graph Network Engine</h3>
          <p>Explore thematic relationships and structured data associations powered directly by backend graph infrastructure.</p>
          <span class="action-link">Launch Visualizer &rarr;</span>
        </div>
      </div>

      <!-- Module 4: Live Infrastructure Status -->
      <div class="workspace-card" @click="navigateTo('status')">
        <div class="card-icon orange-theme">📦</div>
        <div class="card-body">
          <h3>Container Status Panel</h3>
          <p>Monitor real-time host network conditions, operational environments, active systems, and resource limits.</p>
          <span class="action-link">Check Metrics &rarr;</span>
        </div>
      </div>

    </main>

    <!-- Getting Started Section -->
    <section class="getting-started-box">
      <h3>Quick Launch Checklist</h3>
      <ul class="checklist">
        <li class="checked">
          <span class="check-icon">✓</span> 
          <span>Backend Spring API connection active</span>
        </li>
        <li>
          <span class="check-icon">→</span> 
          <span>Import your local project schema or research PDF documents</span>
        </li>
        <li>
          <span class="check-icon">→</span> 
          <span>Initialize Neo4j database graph parameters</span>
        </li>
      </ul>
    </section>
  </div>
</template>

<script setup lang="ts">
// Handles navigation routing between components/views
const navigateTo = (route: string) => {
  console.log(`Routing user straight to: ${route}`)
  // Integrate standard vue-router hooks here: router.push({ name: route })
}
</script>

<style scoped>
.project-landing {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 24px;
  font-family: system-ui, -apple-system, BlinkMacSystemFont, sans-serif;
  color: #1e293b;
  background-color: #fafafa;
  min-height: 100vh;
}

/* Hero Section Styles */
.hero-section {
  text-align: center;
  margin-bottom: 48px;
}
.brand-badge {
  display: inline-block;
  background: #e2e8f0;
  color: #475569;
  padding: 6px 14px;
  border-radius: 9999px;
  font-size: 0.8rem;
  font-weight: 600;
  letter-spacing: 0.05em;
  text-transform: uppercase;
  margin-bottom: 16px;
}
.hero-section h1 {
  font-size: 2.5rem;
  font-weight: 800;
  color: #0f172a;
  margin: 0 0 16px 0;
  letter-spacing: -0.02em;
}
.hero-subtitle {
  font-size: 1.15rem;
  color: #64748b;
  max-width: 640px;
  margin: 0 auto;
  line-height: 1.6;
}

/* High Level Operational Metrics */
.metrics-bar {
  display: flex;
  justify-content: center;
  gap: 40px;
  background: #ffffff;
  border: 1px solid #e2e8f0;
  padding: 20px 40px;
  border-radius: 12px;
  margin-bottom: 48px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.02);
}
.metric-stat {
  display: flex;
  flex-direction: column;
  align-items: center;
}
.stat-number {
  font-size: 1.75rem;
  font-weight: 700;
  color: #0f172a;
}
.stat-label {
  font-size: 0.85rem;
  color: #64748b;
  margin-top: 4px;
  font-weight: 500;
}

/* Operational Workspace Navigation Grid */
.workspace-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 24px;
  margin-bottom: 48px;
}
@media (min-width: 768px) {
  .workspace-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

.workspace-card {
  background: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 16px;
  padding: 24px;
  display: flex;
  gap: 20px;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease, border-color 0.2s ease;
}
.workspace-card:hover {
  transform: translateY(-4px);
  border-color: #cbd5e1;
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.05);
}

/* Contextual Card Colors */
.card-icon {
  font-size: 2rem;
  width: 56px;
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
  flex-shrink: 0;
}
.blue-theme   { background: #eff6ff; }
.purple-theme { background: #faf5ff; }
.green-theme  { background: #f0fdf4; }
.orange-theme { background: #fff7ed; }

.card-body h3 {
  margin: 0 0 8px 0;
  font-size: 1.2rem;
  color: #0f172a;
  font-weight: 600;
}
.card-body p {
  margin: 0 0 16px 0;
  font-size: 0.9rem;
  color: #475569;
  line-height: 1.5;
}
.action-link {
  font-size: 0.85rem;
  font-weight: 600;
  color: #3b82f6;
}

/* User Checklist Box */
.getting-started-box {
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  padding: 24px;
}
.getting-started-box h3 {
  margin: 0 0 16px 0;
  font-size: 1.05rem;
  color: #334155;
}
.checklist {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.checklist li {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 0.9rem;
  color: #475569;
}
.checklist .checked {
  color: #94a3b8;
  text-decoration: line-through;
}
.check-icon {
  font-weight: bold;
  color: #3b82f6;
  width: 16px;
}
.checked .check-icon {
  color: #22c55e;
}
</style>
