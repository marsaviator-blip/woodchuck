<script setup lang="ts">
import { ref } from 'vue';
import { RouterLink, RouterView } from 'vue-router'
import HelloWorld from './HelloWorld.vue'
import { R } from 'vue-router/dist/router-CWoNjPRp.mjs';
</script>

<template>
  <div class="outer">

    <div class="left">
      <h1 class="green">Woodchuck UI</h1>
      <h2>Pick yur direction</h2>
      <nav>
        <div class="smallSpace" />
        <p>Software subjects:</p>
        <p>
          <RouterLink to="/">Home</RouterLink>
          <RouterLink to="/about">About</RouterLink>
          <RouterLink to="/auth">IAM information</RouterLink>
        </p>
        <div class="smallSpace" />
        <p>Woodchuck subjects:</p>
        <p>
          <RouterLink to="/carousel">Image carousel</RouterLink>
          <RouterLink to="/formview">Form view</RouterLink>
        </p>
      </nav>

    </div>
  </div>
</template>

<style scoped>
.left {
  flex: 1;
  width: 450px;
  margin-left: 25px;;
  /* flex-shrink: 0; */
  align-items: center;
  min-height: 100%;
  /* background-color: blue; */
  color: rgb(194, 135, 194);
    border: 2px solid yellow;
}

.smallSpace {
  height: 40px;
}
</style>
