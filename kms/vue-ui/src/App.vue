<template>
  <div id="app" class="flex flex-col h-screen max-h-screen w-screen overflow-hidden bg-[#0b0d12]">
    
    <!-- Header (Kept exactly as you had it, but now safely budgeted in the flex layout) -->
    <h3 class="text-xs font-semibold uppercase tracking-wider text-[#94a3b8] p-4 flex-shrink-0 border-b border-[#1e293b]">
      Knowledge Management System (KMS)
    </h3>
        <div class="flex-1 min-h-0 w-full relative">
      <RouterView /> 
    </div>
  </div>
</template>
<style>
/* Global reset to make sure the browser window itself doesn't try to scroll */
html, body {
  margin: 0;
  padding: 0;
  height: 100%;
  max-height: 100vh;
  overflow: hidden;
  background-color: #0b0d12;
}
</style>

