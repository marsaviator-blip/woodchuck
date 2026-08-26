<script setup>
import { ref, onMounted, reactive } from 'vue'

// --- State ---
const documents = ref([]) // Array of { id: string, x: number, y: number, title: string }
const isSelecting = ref(false)
const selectionBox = reactive({ startX: 0, startY: 0, endX: 0, endY: 0 })
const canvasRef = ref(null)
const isWorkspaceLoading = ref(false)
const workspaceMetrics = ref(null)

// --- Mock Initial Load (Simulating OpenSearch Vector Scatterplot coordinates) ---
onMounted(() => {
  const canvas = canvasRef.value
  const ctx = canvas.getContext('2d')
  
  // Generate mock document data distributed across a 800x500 canvas
  documents.value = Array.from({ length: 200 }, (_, i) => ({
    id: `doc_${i + 1}`,
    x: Math.random() * 760 + 20,
    y: Math.random() * 460 + 20,
    title: `Scholarly Paper Abstract #${i + 1}`
  }))
  
  drawCanvas()
})

// --- Canvas Rendering ---
const drawCanvas = (selectedIds = new Set()) => {
  const ctx = canvasRef.value.getContext('2d')
  ctx.clearRect(0, 0, 800, 500)
  
  // Draw Document Nodes
  documents.value.forEach(doc => {
    ctx.beginPath()
    ctx.arc(doc.x, doc.y, 5, 0, 2 * Math.PI)
    ctx.fillStyle = selectedIds.has(doc.id) ? '#38bdf8' : '#64748b' // Tailwind sky-400 vs slate-500
    ctx.fill()
  })

  // Draw Interactive Selection Box Drag
  if (isSelecting.value) {
    ctx.strokeStyle = 'rgba(56, 189, 248, 0.5)' // Transparent sky-400
    ctx.fillStyle = 'rgba(56, 189, 248, 0.1)'
    ctx.lineWidth = 1
    const width = selectionBox.endX - selectionBox.startX
    const height = selectionBox.endY - selectionBox.startY
    ctx.fillRect(selectionBox.startX, selectionBox.startY, width, height)
    ctx.strokeRect(selectionBox.startX, selectionBox.startY, width, height)
  }
}

// --- Mouse/Lasso Selection Event Handlers ---
const startSelection = (e) => {
  const rect = canvasRef.value.getBoundingClientRect()
  isSelecting.value = true
  selectionBox.startX = e.clientX - rect.left
  selectionBox.startY = e.clientY - rect.top
  selectionBox.endX = selectionBox.startX
  selectionBox.endY = selectionBox.startY
}

const updateSelection = (e) => {
  if (!isSelecting.value) return
  const rect = canvasRef.value.getBoundingClientRect()
  selectionBox.endX = e.clientX - rect.left
  selectionBox.endY = e.clientY - rect.top
  drawCanvas()
}

const endSelection = () => {
  if (!isSelecting.value) return
  isSelecting.value = false
  
  // Calculate bounding box boundaries
  const minX = Math.min(selectionBox.startX, selectionBox.endX)
  const maxX = Math.max(selectionBox.startX, selectionBox.endX)
  const minY = Math.min(selectionBox.startY, selectionBox.endY)
  const maxY = Math.max(selectionBox.startY, selectionBox.endY)
  
  // Find which documents reside inside our lassoed box coordinates
  const selectedDocs = documents.value.filter(doc => 
    doc.x >= minX && doc.x <= maxX && doc.y >= minY && doc.y <= maxY
  )
  
  const selectedIds = selectedDocs.map(d => d.id)
  
  // Highlight selections on canvas
  drawCanvas(new Set(selectedIds))
  
  if (selectedIds.length > 0) {
    submitWorkspaceCohort(selectedIds)
  }
}

// --- API Sync with Bun Backend ---
const submitWorkspaceCohort = async (documentIds) => {
  isWorkspaceLoading.value = true
  try {
    const response = await fetch('http://localhost:3000/api/workspace/refresh', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ ids: documentIds })
    })
    
    if (!response.ok) throw new Error('Failed to hone-in on cohort analysis')
    
    // Bun returns scoped analysis metrics over the selected nodes
    workspaceMetrics.value = await response.json()
  } catch (error) {
    console.error('Error refreshing workspace:', error)
  } finally {
    isWorkspaceLoading.value = false
  }
}
</script>

<template>
  <div class="p-6 max-w-6xl mx-auto space-y-6">
    <!-- Header Controls -->
    <div class="flex justify-between items-center bg-slate-900 p-4 rounded-xl border border-slate-800">
      <div>
        <h1 class="text-xl font-bold text-white">Dynamic Analytical Cohort Workspace</h1>
        <p class="text-sm text-slate-400">Click and drag a box across the map to hone-in on a target group of documents.</p>
      </div>
      <div v-if="isWorkspaceLoading" class="flex items-center space-x-2 text-sky-400">
        <div class="w-4 h-4 border-2 border-sky-400 border-t-transparent rounded-full animate-spin"></div>
        <span class="text-xs font-semibold uppercase tracking-wider">Analyzing Store Cohorts...</span>
      </div>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <!-- 2D Document Topology Embed Canvas (Left/Center) -->
      <div class="lg:col-span-2 bg-slate-950 p-4 rounded-xl border border-slate-800 relative">
        <div class="absolute top-6 left-6 bg-slate-900/80 backdrop-blur text-xs text-slate-300 px-3 py-1.5 rounded-md border border-slate-700/50 pointer-events-none">
          📊 OpenSearch Vector Embeddings
        </div>
        <canvas 
          ref="canvasRef" 
          width="800" 
          height="500" 
          class="w-full bg-slate-900 rounded-lg cursor-crosshair border border-slate-800"
          @mousedown="startSelection"
          @mousemove="updateSelection"
          @mouseup="endSelection"
        ></canvas>
      </div>

      <!-- Scoped Analytical Feedback Panel (Right) -->
      <div class="bg-slate-900 p-6 rounded-xl border border-slate-800 flex flex-col justify-between">
        <div>
          <h2 class="text-md font-semibold text-slate-200 border-b border-slate-800 pb-3 mb-4">Cohort Insights Matrix</h2>
          
          <div v-if="workspaceMetrics" class="space-y-4">
            <!-- Active Node Aggregation -->
            <div class="bg-slate-950 p-3 rounded-lg border border-slate-800">
              <span class="text-xs text-slate-500 block mb-1">Active Selection Size</span>
              <div class="text-2xl font-bold text-sky-400">{{ workspaceMetrics.totalSelected }} Papers</div>
            </div>

            <!-- Topic Drift Assessment -->
            <div class="bg-slate-950 p-3 rounded-lg border border-slate-800">
              <span class="text-xs text-slate-500 block mb-1">Topic Drift Vector Status</span>
              <div class="text-sm font-medium text-emerald-400 flex items-center space-x-1">
                <span>⚡ {{ workspaceMetrics.topicStatus }}</span>
              </div>
            </div>

            <!-- Top Quality Driver -->
            <div class="bg-slate-950 p-3 rounded-lg border border-slate-800">
              <span class="text-xs text-slate-500 block mb-1">Top Innovation Catalyst (Neo4j PageRank)</span>
              <div class="text-sm font-semibold text-slate-300 truncate">{{ workspaceMetrics.topCatalyst }}</div>
            </div>
          </div>

          <!-- Empty State Prompt -->
          <div v-else class="text-center py-20 text-slate-500 text-sm">
            No active group isolated. Use the vector canvas lasso to define a workspace subset.
          </div>
        </div>

        <!-- System Architecture Footprint Indicator -->
        <div class="pt-4 border-t border-slate-800/60 flex justify-between text-[10px] tracking-wide text-slate-600 uppercase font-mono">
          <span>🗄️ Neo4j Scoped</span>
          <span>🔍 OpenSearch Synced</span>
          <span>🐘 Postgres Read</span>
        </div>
      </div>
    </div>
  </div>
</template>
