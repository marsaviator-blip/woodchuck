<template>
  <div class="setup-container">
    <!-- Header Section -->
    <header class="setup-header">
      <h2>System Setup Guide</h2>
      <p>Follow these quick steps to get your relationship search environment running.</p>
    </header>

    <!-- Interactive D3 Graph Card -->
    <div class="graph-card">
      <div class="graph-header">
        <h3>Live Schema Relationships Visualization</h3>
        <p>Drag nodes to test physics. Nodes represent your entities and data connections.</p>
      </div>
      <!-- D3 Appends Elements inside this SVG Container -->
      <div class="svg-wrapper">
        <svg ref="graphSvg" width="100%" height="250"></svg>
      </div>
    </div>

    <!-- Timeline Steps -->
    <div class="steps-timeline">
      <div v-for="(step, index) in steps" :key="index" class="step-card">
        <div class="step-badge">
          <span class="step-number">{{ index + 1 }}</span>
        </div>
        <div class="step-content">
          <div class="step-meta">
            <h3>{{ step.title }}</h3>
            <span class="time-estimate">{{ step.duration }}</span>
          </div>
          <p class="step-description">{{ step.description }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import * as d3 from 'd3'

// 1. Setup Panel Steps Data Configuration
const steps = ref([
  { title: 'Configure Database Connection', duration: '2 mins', description: 'Update your application properties with your credentials to allow repository access.' },
  { title: 'Initialize Model Definitions', duration: '5 mins', description: 'Ensure your PaperNode and Author model entities match your network database schemas.' },
  { title: 'Run Search UI Components', duration: '1 min', description: 'Start up your front-end development server and open the Search Relationship Panel.' }
])

// 2. D3 Graph Container Template Binding Reference
const graphSvg = ref(null)

// 3. Mock Node and Relationship Structural Network Mock Dataset
const graphData = {
  nodes: [
    { id: 'PaperNode', type: 'paper', label: 'Paper' },
    { id: 'AuthorNode', type: 'author', label: 'Author' },
    { id: 'InstNode', type: 'institution', label: 'Institution' }
  ],
  links: [
    { source: 'AuthorNode', target: 'PaperNode', relation: 'WRITTEN_BY' },
    { source: 'AuthorNode', target: 'InstNode', relation: 'AFFILIATED_WITH' },
    { source: 'PaperNode', target: 'PaperNode', relation: 'CITES' }
  ]
}

// 4. Color Palette for Entity Grouping
const nodeColors = {
  paper: '#6366f1',      // Indigo
  author: '#10b981',     // Emerald Green
  institution: '#f59e0b' // Amber
}

onMounted(() => {
  if (!graphSvg.value) return

  const svg = d3.select(graphSvg.value)
  const width = graphSvg.value.clientWidth || 600
  const height = 250

  // Standard D3 Force Physics Engine Engine Setup
  const simulation = d3.forceSimulation(graphData.nodes)
    .force('link', d3.forceLink(graphData.links).id(d => d.id).distance(100))
    .force('charge', d3.forceManyBody().strength(-200))
    .force('center', d3.forceCenter(width / 2, height / 2))

  // Render Relationship Paths Link Elements
  const link = svg.append('g')
    .attr('stroke', '#cbd5e1')
    .attr('stroke-width', 2)
    .selectAll('line')
    .data(graphData.links)
    .join('line')

  // Render Graphical Entity Node Group Containers
  const node = svg.append('g')
    .selectAll('.node')
    .data(graphData.nodes)
    .join('g')
    .attr('class', 'node')
    .call(d3.drag() // Bind Drag Function Handling 
      .on('start', (event, d) => {
        if (!event.active) simulation.alphaTarget(0.3).restart()
        d.fx = d.x; d.fy = d.y
      })
      .on('drag', (event, d) => { d.fx = event.x; d.fy = event.y })
      .on('end', (event, d) => {
        if (!event.active) simulation.alphaTarget(0)
        d.fx = null; d.fy = null
      })
    )

  // Append Core Circles Elements inside Node Layout Containers
  node.append('circle')
    .attr('r', 14)
    .attr('fill', d => nodeColors[d.type] || '#94a3b8')
    .attr('stroke', '#ffffff')
    .attr('stroke-width', 2)

  // Append Scaled Typography Text Labels to Each Graph Element Point Node
  node.append('text')
    .text(d => d.label)
    .attr('dx', 18)
    .attr('dy', 5)
    .style('font-family', 'sans-serif')
    .style('font-size', '12px')
    .style('font-weight', '600')
    .style('fill', '#334155') // Strong Dark slate color avoids transparent color fading bug

  // Engine Physics Tick Pipeline Refresh Loop Handler Updates Locations
  simulation.on('tick', () => {
    link
      .attr('x1', d => d.source.x)
      .attr('y1', d => d.source.y)
      .attr('x2', d => d.target.x)
      .attr('y2', d => d.target.y)

    node.attr('transform', d => `translate(${d.x}, ${d.y})`)
  })
})
</script>

<style scoped>
/* Base Form layouts containers */
.setup-container {
  max-width: 650px;
  margin: 2rem auto;
  padding: 2.5rem;
  background: #ffffff;
  border-radius: 16px;
  box-shadow: 0 4px 30px rgba(0, 0, 0, 0.03);
  border: 1px solid #f0f0f0;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
}

.setup-header {
  margin-bottom: 2rem;
  border-bottom: 1px solid #f5f5f5;
  padding-bottom: 1.5rem;
}

.setup-header h2 { color: #1a1a1a !important; font-size: 1.5rem; font-weight: 700; margin: 0 0 0.5rem 0; }
.setup-header p { color: #666666 !important; font-size: 0.95rem; margin: 0; }

/* Beautiful New Component Graph Styling Panel Wrapper */
.graph-card {
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  padding: 1.25rem;
  margin-bottom: 2rem;
}

.graph-header h3 { margin: 0 0 0.25rem 0; font-size: 1rem; font-weight: 600; color: #1e293b !important; }
.graph-header p { margin: 0 0 1rem 0; font-size: 0.85rem; color: #64748b !important; }

.svg-wrapper {
  background: #ffffff;
  border-radius: 8px;
  border: 1px dashed #cbd5e1;
  overflow: hidden;
}

/* Steps Timelines list wrappers */
.steps-timeline { display: flex; flex-direction: column; gap: 1.5rem; position: relative; }
.steps-timeline::before {
  content: ''; position: absolute; left: 20px; top: 10px; bottom: 10px; width: 2px; background: #eef2f6; z-index: 1;
}

.step-card { display: flex; gap: 1.25rem; position: relative; z-index: 2; }
.step-badge {
  width: 42px; height: 42px; background: #f4f7fa; border: 2px solid #e2e8f0; border-radius: 50%;
  display: flex; align-items: center; justify-content: center; flex-shrink: 0;
}
.step-number { font-weight: 700; color: #4f46e5 !important; font-size: 0.95rem; }

.step-content { flex-grow: 1; background: #fbfcfd; padding: 1.25rem; border-radius: 12px; border: 1px solid #f0f4f8; }
.step-meta { display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.5rem; }
.step-meta h3 { margin: 0; font-size: 1.05rem; font-weight: 600; color: #2d3748 !important; }
.time-estimate { font-size: 0.75rem; background: #e0e7ff; color: #4f46e5 !important; padding: 0.25rem 0.6rem; border-radius: 20px; font-weight: 600; }
.step-description { margin: 0; font-size: 0.9rem; color: #4a5568 !important; line-height: 1.5; }
</style>
