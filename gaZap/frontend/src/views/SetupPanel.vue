<template>
  <div class="setup-container">
    <!-- Header Section -->
    <header class="setup-header">
      <h2>System Setup Guide</h2>
      <p>Follow these quick steps to get your relationship search environment running.</p>
    </header>

    <!-- Timeline Steps -->
    <div class="steps-timeline">
      <div 
        v-for="(step, index) in steps" 
        :key="index" 
        class="step-card"
        :class="{ 'is-completed': step.completed }"
      >
        <!-- Step Number Indicator -->
        <div class="step-badge">
          <span class="step-number">{{ index + 1 }}</span>
        </div>

        <!-- Step Content Content Box -->
        <div class="step-content">
          <div class="step-meta">
            <h3>{{ step.title }}</h3>
            <span class="time-estimate">{{ step.duration }}</span>
          </div>
          
          <p class="step-description">{{ step.description }}</p>
          
          <!-- Optional Action Slot Placeholder -->
          <div v-if="step.hasAction" class="step-action-placeholder">
            <!-- You can add buttons or code blocks here later -->
            <code>// Placeholder for commands or links</code>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

// Modify or expand these objects to change your steps layout
const steps = ref([
  {
    title: 'Configure Database Connection',
    duration: '2 mins',
    description: 'Update your application properties with your credentials to allow repository access.',
    hasAction: true,
    completed: false
  },
  {
    title: 'Initialize Model Definitions',
    duration: '5 mins',
    description: 'Ensure your PaperNode and Author model entities match your network database schemas.',
    hasAction: false,
    completed: false
  },
  {
    title: 'Run Search UI Components',
    duration: '1 min',
    description: 'Start up your front-end development server and open the Search Relationship Panel.',
    hasAction: true,
    completed: false
  }
])
</script>

<style scoped>
/* Scoped styles guarantee your text colors and opacity won't be stripped by external sheets */
.setup-container {
  max-width: 650px;
  margin: 2rem auto;
  padding: 2.5rem;
  background: #ffffff;
  border-radius: 16px;
  box-shadow: 0 4px 30px rgba(0, 0, 0, 0.03);
  border: 1px solid #f0f0f0;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
}

.setup-header {
  margin-bottom: 2.5rem;
  border-bottom: 1px solid #f5f5f5;
  padding-bottom: 1.5rem;
}

.setup-header h2 {
  color: #1a1a1a !important; /* Force visibility against fading */
  font-size: 1.5rem;
  font-weight: 700;
  margin: 0 0 0.5rem 0;
}

.setup-header p {
  color: #666666 !important;
  font-size: 0.95rem;
  margin: 0;
}

.steps-timeline {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  position: relative;
}

/* Vertical line connecting badges */
.steps-timeline::before {
  content: '';
  position: absolute;
  left: 20px;
  top: 10px;
  bottom: 10px;
  width: 2px;
  background: #eef2f6;
  z-index: 1;
}

.step-card {
  display: flex;
  gap: 1.25rem;
  position: relative;
  z-index: 2;
  transition: transform 0.2s ease;
}

.step-card:hover {
  transform: translateX(4px);
}

.step-badge {
  width: 42px;
  height: 42px;
  background: #f4f7fa;
  border: 2px solid #e2e8f0;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.step-number {
  font-weight: 700;
  color: #4f46e5 !important;
  font-size: 0.95rem;
}

.step-content {
  flex-grow: 1;
  background: #fbfcfd;
  padding: 1.25rem;
  border-radius: 12px;
  border: 1px solid #f0f4f8;
}

.step-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.5rem;
}

.step-meta h3 {
  margin: 0;
  font-size: 1.05rem;
  font-weight: 600;
  color: #2d3748 !important;
}

.time-estimate {
  font-size: 0.75rem;
  background: #e0e7ff;
  color: #4f46e5 !important;
  padding: 0.25rem 0.6rem;
  border-radius: 20px;
  font-weight: 600;
}

.step-description {
  margin: 0;
  font-size: 0.9rem;
  color: #4a5568 !important;
  line-height: 1.5;
}

.step-action-placeholder {
  margin-top: 0.75rem;
  background: #2d3748;
  padding: 0.6rem 1rem;
  border-radius: 6px;
}

.step-action-placeholder code {
  color: #a0aec0 !important;
  font-size: 0.8rem;
  font-family: monospace;
}
.is-completed .step-badge {
  background: #d1fae5;
  border-color: #34d399;
}
</style>
