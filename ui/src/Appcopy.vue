<script setup lang="ts">
import { ref } from 'vue';
import LeftPanel from './components/LeftPanel.vue';
import RightPanel from './components/RightPanel.vue';
import { RouterLink, RouterView } from 'vue-router'
import HelloWorld from './components/HelloWorld.vue'
</script>

<template>
  <div class="outer">
    <LeftPanel>
      <h1 class="green">Woodchuckkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk UI
      </h1>
      <p>yuk</p>
    </LeftPanel>
    <RightPanel>
      <p>hey hey</p>
    </RightPanel>
  </div>
</template>

<style scoped>
.outer {
  display: flex;
  flex: 1;
  gap: 20px;
}

.left {
  width: 700px;
  flex-basis: 20vw;
  flex-shrink: 0;
  align-content: right;
  min-height: 100%;
  background-color: blue;
}

.right {
  width: auto;
  flex-grow: 1;
  flex-basis: 75vw;
  flex-shrink: 0;
  align-content: left;
  min-height: 100%;
  background-color: red;
}

body,
html {
  margin: 0;
  padding: 0;
  width: 100%;
}

#app {
  width: 100%;
}
</style>
