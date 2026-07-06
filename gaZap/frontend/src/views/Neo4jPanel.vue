<template>
    <div class="dashboard-wrapper">
        <!-- Header Block -->
        <header class="dashboard-header">
            <div class="header-text-group">
                <h1 class="header-title">Neo4j Metadata Statistics</h1>
                <p class="header-subtitle">Real-time constant-time database schema information</p>
            </div>
            <div class="header-button-group">
              <button @click="confirmAndClear" class="header-button">
                Clear Neo4j Database
              </button>
              <button @click="loadDatabaseStats" :disabled="loading" class="header-button">
                <span v-if="loading" class="spinner"></span>
                {{ loading ? 'Refreshing...' : 'Refresh Stats' }}
              </button>
            </div>
        </header>

        <!-- Error Banner Alert -->
        <div v-if="error" class="error-banner">
            <div class="error-icon-wrapper"></div>
            <div class="error-content">
                <p class="error-title">Data Retrieval Error</p>
                <p class="error-message">{{ error }}</p>
            </div>
        </div>

        <!-- Skeleton Loading Grid Overlays -->
        <div v-if="loading && !stats" class="skeleton-grid-wrapper">
            <div class="skeleton-card-row">
                <div v-for="i in 3" :key="i" class="skeleton-card skeleton-pulse-short"></div>
            </div>
            <div class="skeleton-panel-row">
                <div v-for="i in 2" :key="i" class="skeleton-panel skeleton-pulse-tall"></div>
            </div>
        </div>

        <!-- Main Content Area -->
        <div v-else-if="stats" class="main-content-layout">
            <!-- Main Metrics Summary Cards -->
            <section class="metrics-grid">

                <div class="metric-card group-nodes">
                    <div class="metric-info">
                        <p class="metric-label">Total Nodes</p>
                        <p class="metric-value">{{ formatNumber(stats.totalNodes) }}</p>
                    </div>
                    <div class="metric-anchor anchor-nodes"></div>
                </div>

                <div class="metric-card group-edges">
                    <div class="metric-info">
                        <p class="metric-label">Total Edges</p>
                        <p class="metric-value">{{ formatNumber(stats.totalEdges) }}</p>
                    </div>
                    <div class="metric-anchor anchor-edges"></div>
                </div>

                <div class="metric-card group-labels">
                    <div class="metric-info">
                        <p class="metric-label">Active Labels</p>
                        <p class="metric-value">{{ stats.labels?.length || 0 }}</p>
                    </div>
                    <div class="metric-anchor anchor-labels"></div>
                </div>
            </section>

            <!-- Double Column Nested Schema Trees -->
            <div class="schema-panels-grid">
                <!-- Node Schema Subpanel -->
                <section class="schema-panel">
                    <h2 class="panel-heading heading-nodes">Node Labels & Type Schema</h2>

                    <div v-if="Object.keys(stats.nodeSchemaMap || {}).length === 0" class="empty-schema-state">
                        No node schemas declared in database indexes.
                    </div>

                    <div v-else class="panel-scroll-container">
                        <div v-for="(properties, label) in stats.nodeSchemaMap" :key="label" class="schema-group">
                            <div class="schema-group-header">
                                <span class="group-header-title">Label: <span class="group-header-badge badge-nodes">{{
                                        label }}</span></span>
                            </div>
                            <div class="schema-group-body">
                                <div v-for="(types, propName) in properties" :key="propName" class="schema-row">
                                    <span class="property-name">{{ propName }}</span>
                                    <span class="property-type-tag type-nodes">
                                        {{ types.join(' | ') }}
                                    </span>
                                </div>
                            </div>
                        </div>
                    </div>
                </section>

                <!-- Relationship Schema Subpanel -->
                <section class="schema-panel">
                    <h2 class="panel-heading heading-edges">Relationship Type Schema</h2>

                    <div v-if="Object.keys(stats.relSchemaMap || {}).length === 0" class="empty-schema-state">
                        No relationship types declared in database indexes.
                    </div>

                    <div v-else class="panel-scroll-container">
                        <div v-for="(properties, relType) in stats.relSchemaMap" :key="relType" class="schema-group">
                            <div class="schema-group-header">
                                <span class="group-header-title">Type: <span class="group-header-badge badge-edges">[:{{
                                        relType }}]</span></span>
                            </div>
                            <div class="schema-group-body">
                                <div v-for="(types, propName) in properties" :key="propName" class="schema-row">
                                    <span class="property-name">{{ propName }}</span>
                                    <span class="property-type-tag type-edges">
                                        {{ types.join(' | ') }}
                                    </span>
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
import { ref, onMounted } from 'vue';
import { clearGraph, getDatabaseCounts, type GraphStats } from '../services/graphCalls';

const stats = ref<GraphStats | null>(null);
const loading = ref<boolean>(false);
const error = ref<string | null>(null);

    const confirmAndClear = async () => {
  // Opens native browser popup with 'Cancel' and 'OK' (Yes)
  const isConfirmed = window.confirm("Are you sure you want to clear the Neo4j database? This action cannot be undone.");
  
  // Exit immediately if user clicks Cancel
  if (!isConfirmed) return;

  try {
    const response = await clearGraph();
    alert("Neo4j database has been cleared successfully.");
        
  } catch (error) {
    console.error("Network or server error:", error);
    alert("Failed to clear database. Check console for details.");
  }
};

const loadDatabaseStats = async () => {
    loading.value = true;
    error.value = null;
    try {
        stats.value = await getDatabaseCounts();
    } catch (err: any) {
        error.value = err.message || 'Failed to establish connection to Spring Boot Neo4j endpoint.';
    } finally {
        loading.value = false;
    }
};

const formatNumber = (num: number | undefined | null): string => {
    if (num === undefined || num === null) return '0';
    return num.toLocaleString();
};

onMounted(() => {
    loadDatabaseStats();
});
</script>

<style scoped>
/* ==========================================================================
   CSS Design Token System Variables
   ========================================================================== */
:has(*) {
    --bg-main: #f8fafc;
    --bg-card: #ffffff;
    --border-color: #e2e8f0;
    --text-primary: #0f172a;
    --text-secondary: #475569;
    --text-muted: #94a3b8;

    --theme-blue: #2563eb;
    --theme-blue-soft: rgba(37, 99, 235, 0.1);
    --theme-indigo: #4f46e5;
    --theme-indigo-soft: rgba(79, 70, 229, 0.1);
    --theme-emerald: #10b981;
    --theme-emerald-soft: rgba(16, 185, 129, 0.1);
    --theme-rose: #f43f5e;
    --theme-rose-soft: rgba(244, 63, 94, 0.08);

    --scrollbar-thumb: #cbd5e1;
    --scrollbar-thumb-hover: #94a3b8;
}

@media (prefers-color-scheme: dark) {
    :has(*) {
        --bg-main: #0f172a;
        --bg-card: #1e293b;
        --border-color: #334155;
        --text-primary: #f8fafc;
        --text-secondary: #94a3b8;
        --text-muted: #64748b;

        --theme-blue: #60a5fa;
        --theme-blue-soft: rgba(96, 165, 250, 0.15);
        --theme-indigo: #818cf8;
        --theme-indigo-soft: rgba(129, 140, 248, 0.15);
        --theme-emerald: #34d399;
        --theme-emerald-soft: rgba(52, 211, 153, 0.15);
        --theme-rose: #f87171;
        --theme-rose-soft: rgba(248, 113, 113, 0.12);

        --scrollbar-thumb: #475569;
        --scrollbar-thumb-hover: #64748b;
    }
}

/* ==========================================================================
   Structural Layout Containers
   ========================================================================== */
.dashboard-wrapper {
    padding: 1.5rem;
    max-width: 80rem;
    margin-left: auto;
    margin-right: auto;
    min-height: 100vh;
    background-color: var(--bg-main);
    color: var(--text-secondary);
    transition: background-color 0.3s ease, color 0.3s ease;
}

.main-content-layout {
    display: flex;
    flex-direction: column;
    gap: 1.5rem;
    animation: fadeIn 0.4s cubic-bezier(0.16, 1, 0.3, 1) forwards;
}

/* ==========================================================================
   Header Sub-Engine
   ========================================================================== */
.dashboard-header {
    display: flex;
    flex-direction: column;
    gap: 1rem;
    border-bottom: 1px solid var(--border-color);
    padding-bottom: 1.25rem;
}

@media (min-width: 640px) {
    .dashboard-header {
        flex-direction: row;
        align-items: center;
        justify-content: space-between;
    }
}

.header-title {
    text-transform: none;
    font-size: 1.5rem;
    font-weight: 800;
    color: var(--text-primary);
    letter-spacing: -0.025em;
}

.header-subtitle {
    font-size: 0.875rem;
    color: var(--text-muted);
    margin-top: 0.25rem;
}

.header-button {
    margin-left: 0.25rem;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    padding: 0.5rem 1rem;
    background-color: var(--theme-blue);
    color: #ffffff;
    font-size: 0.875rem;
    font-weight: 600;
    border: none;
    border-radius: 0.5rem;
    box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
    cursor: pointer;
    transition: transform 0.1s ease, background-color 0.15s ease;
}

.refresh-button:hover {
    background-color: #1d4ed8;
}

.refresh-button:active {
    transform: scale(0.95);
}

.refresh-button:disabled {
    opacity: 0.5;
    transform: scale(1);
    cursor: not-allowed;
}

.spinner {
    display: inline-block;
    width: 1rem;
    height: 1rem;
    border: 2px solid #ffffff;
    border-top-color: transparent;
    border-radius: 50%;
    margin-right: 0.5rem;
    animation: spin 0.6s linear infinite;
}

/* ==========================================================================
   Error Alert Mechanics
   ========================================================================== */
.error-banner {
    display: flex;
    align-items: start;
    gap: 0.75rem;
    padding: 1rem;
    background-color: var(--theme-rose-soft);
    border-left: 4px solid var(--theme-rose);
    color: var(--theme-rose);
    border-radius: 0.75rem;
    font-size: 0.875rem;
}

.error-icon-wrapper::before {
    content: "!";
    display: inline-flex;
    align-items: center;
    justify-content: center;
    width: 1.25rem;
    height: 1.25rem;
    background-color: var(--theme-rose);
    color: var(--bg-card);
    font-weight: 900;
    border-radius: 50%;
}

.error-title {
    font-weight: 700;
}

.error-message {
    margin-top: 0.125rem;
    opacity: 0.9;
}

/* ==========================================================================
   Metrics Dash Cards Grid
Use code with caution.========================================================================== */
/* .metrics-grid {
    display: grid;
    grid-template-cols: 1fr;
    gap: 1.5rem;
} */
.metrics-grid {
  display: flex;
  align-items: center;
  gap: 24px;
  margin-top: 15px;
}
@media (min-width: 768px) {
    .metrics-grid {
        grid-template-cols: repeat(3, 1fr);
    }
}

.metric-card {
    background-color: var(--bg-card);
    padding: 1.5rem;
    border-radius: 1rem;
    border: 1px solid var(--border-color);
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: space-between;
    transition: transform 0.2s ease, box-shadow 0.2s ease, border-color 0.2s ease;
}

.metric-card:hover {
    transform: translateY(-2px);
    border-color: var(--text-muted);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.03);
}

.metric-label {
    font-size: 0.75rem;
    font-weight: 700;
    color:
        var(--text-muted);
    text-transform: uppercase;
    letter-spacing: 0.05em;
}

.metric-value {
    font-size: 1.875rem;
    font-weight: 900;
    color: var(--text-primary);
    margin-top: 0.25rem;
    font-family: ui-monospace, monospace;
}

/* Visual Anchors (Replacing raw emojis with pure vector designs) */
.metric-anchor {
    width: 3rem;
    height: 3rem;
    border-radius: 0.75rem;
    display: flex;
    align-items: center;
    justify-content: center;
    font-weight: bold;
    transition: transform 0.2s ease;
}

.metric-card:hover .metric-anchor {
    transform: scale(1.1);
}

.anchor-nodes {
    background-color: var(--theme-blue-soft);
    color: var(--theme-blue);
}

.anchor-nodes::before {
    content: "●";
    font-size: 1.25rem;
}

.anchor-edges {
    background-color: var(--theme-indigo-soft);
    color: var(--theme-indigo);
}

.anchor-edges::before {
    content: "⎌";
    font-size: 1.5rem;
    transform: rotate(45deg);
}

.anchor-labels {
    background-color: var(--theme-emerald-soft);
    color: var(--theme-emerald);
}

.anchor-labels::before {
    content: "⌥";
    font-size: 1.15rem;
}

/* ==========================================================================Structural Schema Subpanels========================================================================== */
.schema-panels-grid {
    display: grid;
    grid-template-cols: 1fr;
    gap: 1.5rem;
}

@media (min-width: 1024px) {
    .schema-panels-grid {
        grid-template-cols: repeat(2, 1fr);
    }
}

.schema-panel {
    background-color: var(--bg-card);
    padding: 1.5rem;
    border-radius: 1rem;
    border: 1px solid var(--border-color);
    display: flex;
    flex-direction: column;
    height: 36.25rem;
}

.panel-heading {
    font-size: 1.125rem;
    font-weight: 700;
    color: var(--text-primary);
    padding-bottom: 0.75rem;
    border-bottom: 1px solid var(--border-color);
    display: flex;
    align-items: center;
    gap: 0.5rem;
}

.panel-heading::before {
    font-size: 1.25rem;
}

.heading-nodes::before {
    content: "📁";
    color: var(--theme-blue);
}

.heading-edges::before {
    content: "⚡";
    color: var(--theme-indigo);
}

.empty-schema-state {
    color: var(--text-muted);
    font-size: 0.875rem;
    text-align: center;
    font-style: italic;
    margin-top: auto;
    margin-bottom: auto;
    padding-bottom: 2rem;
}

/* Scroll Window Area Setup */
.panel-scroll-container {
    overflow-y: auto;
    flex-1: 1 1 0%;
    padding-right: 0.25rem;
    margin-top: 1rem;
    scrollbar-width: thin;
    scrollbar-color: var(--scrollbar-thumb) transparent;
}

.panel-scroll-container::-webkit-scrollbar {
    width: 6px;
}

.panel-scroll-container::-webkit-scrollbar-track {
    background: transparent;
}

.panel-scroll-container::-webkit-scrollbar-thumb {
    background: var(--scrollbar-thumb);
    border-radius: 10px;
}

.panel-scroll-container::-webkit-scrollbar-thumb:hover {
    background: var(--scrollbar-thumb-hover);
}

/* Nested Object Sub-groupings */
.schema-group {
    border: 1px solid var(--border-color);
    border-radius: 0.75rem;
    margin-bottom: 1rem;
    overflow: hidden;
    box-shadow: 0 1px 2px rgba(0, 0, 0, 0.01);
}

.schema-group-header {
    background-color: var(--bg-main);
    padding: 0.625rem 1rem;
    font-weight: 700;
    font-size: 0.75rem;
    text-transform: uppercase;
    letter-spacing: 0.05em;
    border-bottom: 1px solid var(--border-color);
}

.group-header-badge {
    font-family: ui-monospace, monospace;
    font-size: 0.875rem;
    text-transform: none;
    letter-spacing: normal;
    margin-left: 0.25rem;
    padding: 0.125rem 0.5rem;
    border-radius: 0.375rem;
    font-weight: 600;
}

.badge-nodes {
    background-color: var(--theme-blue-soft);
    color: var(--theme-blue);
}

.badge-edges {
    background-color: var(--theme-indigo-soft);
    color: var(--theme-indigo);
}

.schema-group-body {
    padding: 0.75rem;
    background-color: var(--bg-card);
}

.schema-row {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0.5rem 0.25rem;
    font-size: 0.875rem;
    border-radius: 0.5rem;
    transition: background-color 0.15s ease;
}

.schema-row:hover {
    background-color: var(--bg-main);
}

.schema-row:not(:last-child) {
    border-bottom: 1px solid rgba(0, 0, 0, 0.02);
}

.property-name {
    font-family: ui-monospace, monospace;
    font-weight: 500;
    color: var(--text-secondary);
}

.schema-row:hover .property-name {
    color: var(--text-primary);
}

.property-type-tag {
    padding: 0.125rem 0.5rem;
    border-radius: 0.375rem;
    font-size: 0.75rem;
    font-family: ui-monospace, monospace;
    font-weight: 700;
    border-width: 1px;
    border-style: solid;
}

.type-nodes {
    background-color: var(--theme-blue-soft);
    color: var(--theme-blue);
    border-color: rgba(37, 99, 235, 0.15);
}

.type-edges {
    background-color: var(--theme-indigo-soft);
    color: var(--theme-indigo);
    border-color: rgba(79, 70, 229, 0.15);
}

/* ==========================================================================Skeleton Loading / Animation Engine========================================================================== */
.skeleton-grid-wrapper {
    display: flex;
    flex-direction: column;
    gap: 1.5rem;
}

.skeleton-card-row {
    display: grid;
    grid-template-cols: 1fr;
    gap: 1.5rem;
}

@media (min-width: 768px) {
    .skeleton-card-row {
        grid-template-cols: repeat(3, 1fr);
    }
}

.skeleton-panel-row {
    display: grid;
    grid-template-cols: 1fr;
    gap: 1.5rem;
}

@media (min-width: 1024px) {
    .skeleton-panel-row {
        grid-template-cols: repeat(2, 1fr);
    }
}

.skeleton-card,
.skeleton-panel {
    background-color: var(--bg-card);
    border: 1px solid var(--border-color);
    border-radius: 1rem;
}

.skeleton-pulse-short {
    height: 6rem;
    animation: pulse 1.5s infinite ease-in-out;
}

.skeleton-pulse-tall {
    height: 36.25rem;
    animation: pulse 1.5s infinite ease-in-out;
}

@keyframes pulse {

    0%,
    100% {
        opacity: 0.6;
        transform: scale(1);
    }

    50% {
        opacity: 0.3;
        transform: scale(0.998);
    }
}

@keyframes spin {
    to {
        transform: rotate(360deg);
    }
}

@keyframes fadeIn {
    from {
        opacity: 0;
        transform: translateY(4px);
    }

    to {
        opacity: 1;
        transform: translateY(0);
    }
}
</style>