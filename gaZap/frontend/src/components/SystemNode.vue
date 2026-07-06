<script setup lang="ts">
import { computed } from 'vue';

const props = defineProps<{
  id: string;
  label: string;
  type: 'client' | 'gateway' | 'controller' | 'service' | 'activity' | 'database' | 'llm' | 'chunker';
  isActive: boolean;
}>();

const nodeStyles = computed(() => {
  const base = "p-3 rounded-xl border font-semibold text-center transition-all duration-500 shadow-md z-10 bg-slate-900 ";
  
  if (!props.isActive) {
    return base + "border-slate-800 text-slate-500 opacity-40 scale-95";
  }
  
  switch (props.type) {
    case 'client': return base + "border-blue-500 text-blue-400 shadow-blue-950/40 scale-105";
    case 'gateway': return base + "border-purple-500 text-purple-400 shadow-purple-950/40 scale-105";
    case 'controller': return base + "border-emerald-500 text-emerald-400 shadow-emerald-950/40 scale-105";
    case 'service': return base + "border-emerald-500 text-emerald-400 shadow-emerald-950/40 scale-105";
    case 'activity': return base + "border-cyan-500 text-cyan-400 shadow-cyan-950/40 scale-105";
    case 'database': return base + "border-amber-500 text-amber-400 shadow-amber-950/40 scale-105";
    case 'llm': return base + "border-rose-500 text-rose-400 shadow-rose-950/40 scale-105";
    case 'chunker': return base + "border-lime-500 text-lime-400 shadow-lime-950/40 scale-105";
    default: return base + "border-slate-700 text-slate-300";
  }
});
</script>

<template>
  <div :class="nodeStyles">
    <div class="text-[9px] uppercase tracking-widest opacity-50 mb-0.5">{{ type }}</div>
    <div class="text-xs tracking-wide truncate">{{ label }}</div>
  </div>
</template>
