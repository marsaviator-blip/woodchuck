<template>
  <div class="author-panel">
    <h2>Author Repository Workspace</h2>

    <div v-if="loading" class="spinner">Processing graph queries...</div>

    <div v-else>
      <!-- Part 1: Node Count Banner -->
      <div class="count-banner">
        <span>Global Author Nodes Count:</span>
        <strong>{{ data.authorCount }}</strong>
      </div>

      <div class="workspace-layout">
        <!-- Part 2: Isolated Author List -->
        <div class="column-card">
          <h3>Author Directory</h3>
          <ul class="clean-list">
            <li v-for="author in data.authorList" :key="author.id" class="list-item">
              <span class="avatar">👤</span> {{ author.name }} <small>({{ author.id }})</small>
            </li>
          </ul>
        </div>

        <!-- Part 3: Nested Author and Documents view -->
        <div class="column-card">
          <h3>Authors & Connected Documents</h3>
          <div v-for="item in data.authorsWithDocs" :key="item.authorId" class="nested-row">
            <div class="author-header">✍️ {{ item.authorName }}</div>
            
            <div class="docs-container">
              <ul v-if="item.relatedDocuments.length > 0">
                <li v-for="doc in item.relatedDocuments" :key="doc.id">
                  📄 <strong>{{ doc.title }}</strong> <small>({{ doc.id }})</small>
                </li>
              </ul>
              <span v-else class="no-data">No associated document links found.</span>
            </div>
          </div>
        </div>
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
.author-panel { padding: 24px; font-family: sans-serif; }
.count-banner {
  background: #f0fdf4; border: 1px solid #bbf7d0; color: #166534;
  padding: 16px; border-radius: 6px; font-size: 1.15rem; margin-bottom: 20px;
}
.workspace-layout { display: grid; grid-template-columns: 1fr 2fr; gap: 20px; }
.column-card { background: #ffffff; border: 1px solid #e5e7eb; border-radius: 8px; padding: 20px; }
.clean-list { list-style: none; padding: 0; }
.list-item { padding: 8px 0; border-bottom: 1px solid #f3f4f6; }
.nested-row { border-bottom: 1px solid #e5e7eb; padding: 12px 0; }
.author-header { font-weight: bold; color: #1e3a8a; margin-bottom: 6px; }
.docs-container { padding-left: 14px; }
.no-data { color: #9ca3af; font-style: italic; font-size: 0.9rem; }
.spinner { color: #4b5563; font-style: italic; }
</style>
