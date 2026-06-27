<template>
  <div class="author-panel-workspace">
    <header class="panel-header">
      <h2 class="title text-primary">Author Repository Dashboard</h2>
      <p class="subtitle">Real-time Neo4j Graph Database analytics and node infrastructure mapping</p>
    </header>

    <!-- Global Loading Visual Guard -->
    <div v-if="loading" class="loading-state-box">
      <div class="spinner-icon"></div>
      <span class="loading-text">Querying Graph Metadata...</span>
    </div>

    <div v-else class="dashboard-body">
      <!-- Node Count Metric Banner -->
      <section class="count-banner-card">
        <div class="banner-icon">📊</div>
        <div class="banner-details">
          <span class="banner-label">Global Author Nodes Indexed</span>
          <strong class="banner-value">{{ data.authorCount.toLocaleString() }}</strong>
        </div>
      </section>

      <!-- Main Two-Column Layout Grid -->
      <div class="workspace-grid">
        
        <!-- Left Column: Plain Directory list -->
        <section class="grid-card">
          <h3 class="card-heading">👤 Author Directory</h3>
          <div class="scroll-container">
            <ul class="directory-list">
              <li v-for="author in data.authorList" :key="author.id" class="directory-item">
                <span class="author-avatar">🪪</span>
                <div class="directory-details">
                  <span class="author-name-text">{{ author.name }}</span>
                  <code class="node-id-badge">ID: {{ author.id }}</code>
                </div>
              </li>
            </ul>
          </div>
        </section>

        <!-- Right Column: Graph Relational Paths -->
        <section class="grid-card">
          <h3 class="card-heading">🔗 Authors & Connected Documents</h3>
          <div class="scroll-container px-2">
            
            <div v-for="item in data.authorsWithDocs" :key="item.authorId" class="relation-row">
              <div class="relation-author-bar">
                <span class="author-icon">✍️</span>
                <span class="relation-author-name">{{ item.authorName }}</span>
              </div>
              
              <div class="nested-docs-block">
                <ul v-if="item.relatedDocuments.length > 0" class="docs-list">
                  <li v-for="doc in item.relatedDocuments" :key="doc.id" class="doc-item">
                    <span class="doc-icon">📄</span>
                    <div class="doc-details">
                      <span class="doc-title-text">{{ doc.title }}</span>
                      <code class="doc-id-badge">ID: {{ doc.id }}</code>
                    </div>
                  </li>
                </ul>
                <div v-else class="empty-state-notice">
                  ⚠️ No associated document links found in the current graph state.
                </div>
              </div>
            </div>

          </div>
        </section>

      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getAuthorAnalytics, type AuthorPayloadDTO } from '../services/graphCalls'

const loading = ref<boolean>(true)
const data = ref<AuthorPayloadDTO>({ authorCount: 0, authorList: [], authorsWithDocs: [] })

onMounted(async () => {
  try {
    data.value = await getAuthorAnalytics()
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
/* Base Container Styles & Explicit Text Resets */
.author-panel-workspace {
  padding: 24px;
  background-color: #f8fafc; /* Slate 50 */
  min-height: 100vh;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
  color: #1e293b; /* Slate 800 - Forces deep dark slate text color */
}

/* Header Text Contrast Typography */
.panel-header {
  margin-bottom: 24px;
}
.title {
  font-size: 1.75rem;
  font-weight: 800;
  color: #0f172a; /* Slate 900 */
  margin: 0 0 4px 0;
}
.subtitle {
  font-size: 0.95rem;
  color: #475569; /* Slate 600 */
  margin: 0;
}

/* Loading Box Overhaul */
.loading-state-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px;
  background: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
}
.loading-text {
  font-size: 1rem;
  color: #64748b;
  font-weight: 600;
}

/* Global Counter Card Design */
.count-banner-card {
  display: flex;
  align-items: center;
  gap: 16px;
  background: #ffffff;
  border: 1px solid #cbd5e1;
  border-left: 6px solid #2563eb; /* Strong Blue Accent Line */
  padding: 20px;
  border-radius: 10px;
  margin-bottom: 24px;
  box-shadow: 0 4px 6px -1px rgb(0 0 0 / 0.05);
}
.banner-icon {
  font-size: 2rem;
}
.banner-details {
  display: flex;
  flex-direction: column;
}
.banner-label {
  font-size: 0.85rem;
  font-weight: 700;
  text-transform: uppercase;
  color: #64748b; /* Slate 500 */
  letter-spacing: 0.05em;
}
.banner-value {
  font-size: 2.25rem;
  font-weight: 900;
  color: #1e3a8a; /* Deep Navy Blue */
  line-height: 1;
  margin-top: 4px;
}

/* Dashboard Columns Grid Configuration */
.workspace-grid {
  display: grid;
  grid-template-columns: 1fr 2fr;
  gap: 24px;
}
@media (max-width: 900px) {
  .workspace-grid {
    grid-template-columns: 1fr; /* Stack columns on laptops/tablets */
  }
}

/* Content Panel Structure */
.grid-card {
  background: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 4px 6px -1px rgb(0 0 0 / 0.05);
  display: flex;
  flex-direction: column;
  max-height: 700px;
}
.card-heading {
  font-size: 1.2rem;
  font-weight: 700;
  color: #0f172a;
  margin: 0 0 16px 0;
  padding-bottom: 12px;
  border-bottom: 2px solid #f1f5f9;
}
.scroll-container {
  overflow-y: auto;
  flex-grow: 1;
  padding-right: 4px;
}

/* Code Badge Styling (Node IDs) */
.node-id-badge, .doc-id-badge {
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, monospace;
  font-size: 0.75rem;
  background-color: #f1f5f9;
  color: #475569;
  padding: 2px 6px;
  border-radius: 4px;
  border: 1px solid #e2e8f0;
  display: inline-block;
}

/* Left Column: List Item Layout */
.directory-list {
  list-style: none;
  padding: 0;
  margin: 0;
}
.directory-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 8px;
  border-bottom: 1px solid #f1f5f9;
  transition: background-color 0.2s;
}
.directory-item:hover {
  background-color: #f8fafc;
}
.author-avatar {
  font-size: 1.2rem;
}
.directory-details {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.author-name-text {
  font-weight: 600;
  color: #0f172a;
}

/* Right Column: Path & Relationship Matrix UI */
.relation-row {
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  margin-bottom: 16px;
  overflow: hidden;
}
.relation-author-bar {
  background: #edf2f7;
  padding: 12px 16px;
  font-weight: 700;
  color: #1e40af; /* Bold Navy Blue */
  border-bottom: 1px solid #e2e8f0;
  display: flex;
  align-items: center;
  gap: 8px;
}
.nested-docs-block {
  padding: 16px;
  background: #ffffff;
}
.docs-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.doc-item {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f1f5f9;
}
.doc-item:last-child {
  padding-bottom: 0;
  border-bottom: none;
}
.doc-details {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.doc-title-text {
  font-weight: 600;
  color: #334155; /* Dark Slate Gray */
  line-height: 1.4;
}

/* Fallback Empty States styling */
.empty-state-notice {
  color: #94a3b8;
  font-style: italic;
  font-size: 0.9rem;
}

/* Custom scrollbars to look cleaner on modern browsers */
.scroll-container::-webkit-scrollbar {
  width: 6px;
}
.scroll-container::-webkit-scrollbar-track {
  background: transparent;
}
.scroll-container::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 3px;
}
</style>
