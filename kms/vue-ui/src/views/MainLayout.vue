<script setup>
import { RouterLink, RouterView } from 'vue-router'
</script>

<template>
  <!-- h-screen and overflow-hidden here lock the browser viewport completely -->
  <!-- <div class="flex h-screen bg-slate-950 font-sans text-slate-100 overflow-hidden"> -->
  <div class="flex h-full max-h-full w-full overflow-hidden bg-slate-950 text-slate-100">
   
    <!-- Persistent Left Sidebar Navigation Pane -->
    <nav class="w-52 bg-slate-900 border-r border-slate-800 flex flex-col justify-between p-5 shrink-0">
      <div class="space-y-6">
        <!-- Brand Signature -->
        <div class="flex items-center space-x-2 px-2">
          <span class="text-xl font-extrabold bg-gradient-to-r from-sky-400 to-indigo-500 bg-clip-text text-transparent">
            ScholarIntel
          </span>
        </div>

        <!-- Navigation Anchors -->
        <div class="space-y-1.5">
          <RouterLink 
            to="/workspace" 
            class="flex items-center space-x-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-all"
            active-class="bg-sky-500/10 text-sky-400 border border-sky-500/20"
            inactive-class="text-slate-400 hover:bg-slate-800/60 hover:text-slate-200"
          >
            <span>💼</span>
            <span>KMS Workspace</span>
          </RouterLink>

          <RouterLink 
            to="/landscape" 
            class="flex items-center space-x-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-all"
            active-class="bg-sky-500/10 text-sky-400 border border-sky-500/20"
            inactive-class="text-slate-400 hover:bg-slate-800/60 hover:text-slate-200"
          >
            <span>📊</span>
            <span>Landscape Map</span>
          </RouterLink>
        </div>
      </div>

      <!-- Infrastructure Tag -->
      <div class="p-3 bg-slate-950 rounded-lg border border-slate-800/60 text-xs">
        <div class="text-emerald-400 flex items-center space-x-1.5 font-mono">
          <span class="w-2 h-2 bg-emerald-400 rounded-full animate-pulse"></span>
          <span>Bun Active</span>
        </div>
      </div>
    </nav>

    <!-- Content Target Panel -->
    <!-- overflow-y-auto enables independent scrolling for KmsWorkspace and DocumentLandscape -->
    <!-- <main class="flex-1 overflow-y-auto bg-slate-950"> -->
    <main class="flex-1 h-full max-h-full overflow-hidden bg-slate-950 relative">
      <RouterView />
    </main>

  </div>
</template>
