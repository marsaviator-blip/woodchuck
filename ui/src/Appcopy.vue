<script setup lang="ts">
import { RouterLink, RouterView } from 'vue-router'
import HelloWorld from './components/HelloWorld.vue'
</script>

<template>
  <div class="outer">

    <div class="left">
  <header>
    <h1 class="green">Woodchuckkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk UI</h1>
  </header>
      <p>yuk</p>
    </div>
    <div class="right">
      <p>hey hey</p>
    </div>
  </div>
</template>

<style scoped>
header {
  line-height: 1.5;
  max-height: 100vh;
}

.outer {
  display: flex;
  gap: 20px;
}

.left {
  flex-basis: 20%;
  flex-shrink: 0;
  align-content: right;
  height: 100%;
  background-color: blue;
}

.right {
  flex-basis: 80%;
  flex-shrink: 0;
  align-content: left;
  height: 100%;
  background-color: red;
}
</style>
