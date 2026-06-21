<template>
  <div class="kg-container">
    <!-- Graph Controls/Header Topbar -->
    <header class="kg-header">
      <div class="kg-title-block">
        <h2 class="kg-main-title">Interactive Knowledge Graph Topology</h2>
        <p class="kg-sub-title">Force-directed simulation canvas pulling straight from Neo4j</p>
      </div>
      <div class="kg-actions">
        <label class="kg-limit-label">
          Node Limit:
          <input type="number" v-model.number="nodeLimit" min="10" max="500" class="kg-limit-input" />
        </label>
        <button @click="loadGraphVisualization" :disabled="loading" class="kg-btn-refresh">
          {{ loading ? 'Computing Intersections...' : 'Render Layout' }}
        </button>
      </div>
    </header>

    <!-- Error Dashboard Banner -->
    <div v-if="error" class="kg-error-alert">
      <span class="kg-error-indicator"></span>
      <p class="kg-error-text">{{ error }}</p>
    </div>

    <!-- Main Dynamic Core Interactive Engine Split Window Layout -->
    <div class="kg-viewport-layout">
      <!-- Left Anchor Space: The D3 SVG Element -->
      <div class="kg-canvas-card">
        <div v-if="loading" class="kg-canvas-loader">
          <div class="kg-spinner"></div>
          <p>Iterating physical vectors...</p>
        </div>
        <svg ref="svgCanvasRef" class="kg-d3-canvas"></svg>
      </div>

      <!-- Right Anchor Space: Reactive Inspector Panel -->
      <aside class="kg-inspector-panel">
        <h3 class="kg-inspector-title">Metadata Node Properties</h3>
        
        <div v-if="selectedNode" class="kg-inspector-details">
          <div class="kg-inspector-meta">
            <span class="kg-meta-badge">{{ selectedNode.label }}</span>
            <span class="kg-meta-id">ID: {{ selectedNode.id }}</span>
          </div>
          <div class="kg-properties-table">
            <div v-for="(val, key) in selectedNode.properties" :key="key" class="kg-property-row">
              <span class="kg-prop-key">{{ key }}</span>
              <span class="kg-prop-val">{{ val }}</span>
            </div>
          </div>
        </div>
        
        <div v-else class="kg-inspector-placeholder">
          Click on any node in the structural viewport mesh to inspect its key-value transactional properties.
        </div>
      </aside>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick, watch } from 'vue';
import * as d3 from 'd3';
import { fetchGraphTopology, type NetworkGraphPayload, type VisNode } from '../services/graphCalls';

// Extended definitions required by D3.js runtime simulations
interface D3Node extends d3.SimulationNodeDatum, VisNode {}
interface D3Link extends d3.SimulationLinkDatum<D3Node> {
  type: string;
}

const svgCanvasRef = ref<SVGSVGElement | null>(null);
const nodeLimit = ref<number>(100);
const loading = ref<boolean>(false);
const error = ref<string | null>(null);
const selectedNode = ref<VisNode | null>(null);

let simulation: d3.Simulation<D3Node, D3Link> | null = null;

const loadGraphVisualization = async () => {
  loading.value = true;
  error.value = null;
  selectedNode.value = null;

  try {
    const rawData = await fetchGraphTopology(nodeLimit.value);
    await nextTick();
    renderD3SimulationMesh(rawData);
  } catch (err: any) {
    error.value = err.message || 'Fatal dependency interruption during Neo4j network extraction.';
  } finally {
    loading.value = false;
  }
};

const renderD3SimulationMesh = (data: NetworkGraphPayload) => {
  if (!svgCanvasRef.value) return;

  // Clear older structural paths to prevent viewport overlapping duplication
  const svg = d3.select(svgCanvasRef.value);
  svg.selectAll('*').remove();

  const width = svgCanvasRef.value.clientWidth || 800;
  const height = svgCanvasRef.value.clientHeight || 550;

  // Deep clone structural payloads to protect downstream reactive reference loops
  const nodes: D3Node[] = data.nodes.map(n => ({ ...n }));
  const links: D3Link[] = data.links.map(l => ({ ...l }));

  // Color generator engine mapping nodes cleanly by Node Label string patterns
  const colorScale = d3.scaleOrdinal(d3.schemeCategory10);

  // Group container initialization supporting structural pan-and-zoom actions
  const containerGroup = svg.append('g').attr('class', 'graph-container-layer');

  // Activate zoom behaviors
  svg.call(d3.zoom<SVGSVGElement, unknown>()
    .scaleExtent([0.1, 4])
    .on('zoom', (event) => {
      containerGroup.attr('transform', event.transform);
    })
  );

  // Initialize the force simulation loop
  simulation = d3.forceSimulation<D3Node, D3Link>(nodes)
    .force('link', d3.forceLink<D3Node, D3Link>(links).id(d => d.id).distance(120))
    .force('charge', d3.forceManyBody().strength(-200))
    .force('center', d3.forceCenter(width / 2, height / 2))
    .force('collision', d3.forceCollide().radius(25));

  // Render Relationship Edges Links
  const linkElements = containerGroup.append('g')
    .attr('class', 'links-group')
    .selectAll('line')
    .data(links)
    .enter()
    .append('line')
    .attr('class', 'graph-edge-line');

  // Render Core Interactive Vertex Nodes
  const nodeElements = containerGroup.append('g')
    .attr('class', 'nodes-group')
    .selectAll('g')
    .data(nodes)
    .enter()
    .append('g')
    .attr('class', 'node-vertex-group')
    .on('click', (_event, d) => {
      selectedNode.value = { id: d.id, label: d.label, properties: d.properties };
    })
    .call(d3.drag<SVGGElement, D3Node>()
      .on('start', dragStarted)
      .on('drag', dragged)
      .on('end', dragEnded)
    );

  // Structural Node Body Circles
  nodeElements.append('circle')
    .attr('r', 14)
    .attr('fill', d => colorScale(d.label))
    .attr('class', 'node-circle');

  // Text labels overlaying the vector circles
  nodeElements.append('text')
    .attr('dy', 22)
    .attr('text-anchor', 'middle')
    .attr('class', 'node-text-label')
    .text(d => d.properties.name || d.properties.title || d.id);

  // Synchronize positions into D3 mathematical timeline loop updates
  simulation.on('tick', () => {
    linkElements
      .attr('x1', d => (d.source as any).x)
      .attr('y1', d => (d.source as any).y)
      .attr('x2', d => (d.target as any).x)
      .attr('y2', d => (d.target as any).y);

    nodeElements.attr('transform', d => `translate(${d.x}, ${d.y})`);
  });

  // Physics Drag Helper Operations
  function dragStarted(event: any, d: D3Node) {
    if (!event.active) simulation?.alphaTarget(0.3).restart();
    d.fx = d.x;
    d.fy = d.y;
  }

  function dragged(event: any, d: D3Node) {
    d.fx = event.x;
    d.fy = event.y;
  }

  function dragEnded(event: any, d: D3Node) {
    if (!event.active) simulation?.alphaTarget(0);
    d.fx = null;
    d.fy = null;
  }
};

onMounted(() => {
  loadGraphVisualization();
});
</script>

<style scoped>
/* ==========================================================================
   CSS Design Token System Variables (FIXED: Targeted class mapping)
   ========================================================================== */
.kg-container {
  --canvas-bg: #f8fafc;
  --panel-bg: #ffffff;
  --line-color: #cbd5e1;
  --text-dark: #0f172a;
  --text-muted: #64748b;
  --accent-blue: #3b82f6;
  --border-element: #e2e8f0;
}

@media (prefers-color-scheme: dark) {
  .kg-container {
    --canvas-bg: #0f172a;
    --panel-bg: #1e293b;
    --line-color: #334155;
    --text-dark: #f8fafc;
    --text-muted: #94a3b8;
    --accent-blue: #60a5fa;
    --border-element: #334155;
  }
}

@media (prefers-color-scheme: dark) {
  :has(*) {
    --canvas-bg: #0f172a;
    --panel-bg: #1e293b;
    --line-color: #334155;
    --text-dark: #010306;
    --text-muted: #94a3b8;
    --accent-blue: #60a5fa;
    --border-element: #334155;
  }
}

/* .kg-container {
  padding: 1.5rem;
  max-width: 85rem;
  margin: 0 auto;
  font-family: system-ui, -apple-system, sans-serif;
} */

.kg-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid var(--border-element);
  padding-bottom: 1rem;
  margin-bottom: 1.5rem;
}

.kg-main-title {
  font-size: 1.5rem;
  font-weight: 800;
  color: var(--text-dark);
}

.kg-sub-title {
  font-size: 0.875rem;
  color: var(--text-muted);
  margin-top: 0.25rem;
}

.kg-actions {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.kg-limit-label {
  font-size: 0.875rem;
  font-weight: 600;
  color: var(--text-muted);
}

.kg-limit-input {
  width: 5rem;
  padding: 0.375rem;
  background-color: var(--panel-bg);
  border: 1px solid var(--border-element);
  color: ivory; /*var(--text-dark);*/
  border-radius: 0.375rem;
  margin-left: 0.5rem;
  font-weight: bold;
}

.kg-btn-refresh {
  padding: 0.5rem 1rem;
  background-color: var(--accent-blue);
  color: #ffffff;
  border: none;
  font-size: 0.875rem;
  font-weight: 600;
  border-radius: 0.5rem;
  cursor: pointer;
}

.kg-viewport-layout {
  display: grid;
  grid-template-cols: 1fr;
  gap: 1.5rem;
}

@media (min-width: 1024px) {
  .kg-viewport-layout {
    grid-template-cols: 3fr 1fr;
  }
}

.kg-canvas-card {
  background-color: var(--canvas-bg);
  border: 1px solid var(--border-element);
  border-radius: 1rem;
  height: 35rem;
  position: relative;
  overflow: hidden;
  box-shadow: inset 0 2px 4px rgba(0,0,0,0.02);
}

.kg-d3-canvas {
  width: 100%;
  height: 100%;
  display: block;
}

:deep(.graph-edge-line) {
  stroke: var(--line-color);
  stroke-opacity: 0.6;
  stroke-width: 1.5px;
}

:deep(.node-circle) {
  stroke: var(--panel-bg);
  stroke-width: 2px;
  transition: r 0.15s ease;
  cursor: grab;
}

:deep(.node-vertex-group:hover .node-circle) {
  r: 17px;
  filter: drop-shadow(0 4px 6px rgba(0,0,0,0.15));
}

:deep(.node-text-label) {
  font-size: 0.75rem;
  font-weight: 600;
  fill: var(--text-dark);
  pointer-events: none;
  user-select: none;
}

.kg-inspector-panel {
  background-color: var(--panel-bg);
  border: 1px solid var(--border-element);
  border-radius: 1rem;
  padding: 1.25rem;
  display: flex;
  flex-direction: column;
  height: 35rem;
  overflow-y: auto;
}

.kg-inspector-title {
  font-size: 1rem;
  font-weight: 700;
  color: IVORY; /*var(--text-dark);*/
  border-bottom: 1px solid var(--border-element);
  padding-bottom: 0.5rem;
  margin-bottom: 1rem;
}

.kg-inspector-placeholder {
  font-size: 0.875rem;
  color: var(--text-muted);
  font-style: italic;
  text-align: center;
  margin: auto 0;
}

.kg-inspector-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.kg-meta-badge {
  background-color: var(--accent-blue);
  color: #ffffff;
  font-size: 0.75rem;
  font-weight: 700;
  padding: 0.25rem 0.5rem;
  border-radius: 0.375rem;
}

.kg-meta-id {
  font-size: 0.75rem;
  color: IVORY; /*var(--text-muted);
  font-family: monospace;
}

.kg-properties-table {
  border: 1px solid var(--border-element);
  border-radius: 0.5rem;
  overflow: hidden;
}

.kg-property-row {
  display: flex;
  flex-direction: column;
  padding: 0.5rem 0.75rem;
  font-size: 0.875rem;
  border-bottom: 1px solid var(--border-element);
}

.kg-property-row:last-child {
  border-bottom: none;
}

.kg-prop-key {
  font-size: 0.7rem;
  text-transform: uppercase;
  font-weight: 700;
  color: IVORY; /*var(--text-muted);*/
  letter-spacing: 0.05em;
}

.kg-prop-val {
  color: IVORY; /*var(--text-dark);*/
  margin-top: 0.125rem;
  word-break: break-all;
}

.kg-canvas-loader {
  position: absolute;
  inset: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background-color: var(--canvas-bg);
  gap: 0.75rem;
  font-size: 0.875rem;
  color: var(--text-muted);
}

.kg-spinner {
  width: 1.5rem;
  height: 1.5rem;
  border: 2px solid var(--accent-blue);
  border-top-color: transparent;
  border-radius: 50%;
  animation: spin 0.6s linear infinite;
}

@keyframes spin { 
  to { transform: rotate(360deg); } 
}
</style>