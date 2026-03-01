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
      <p>Pick yur direction</p>
      <nav>
        <p>
          <RouterLink to="/">Home</RouterLink>
          <RouterLink to="/about">About</RouterLink>
          <RouterLink to="/auth">IAM information</RouterLink>
        </p>
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
  /* width: 700px; */
  flex-basis: 40%;
  /* flex-shrink: 0; */
  align-content: right;
  min-height: 100%;
  /* background-color: blue; */
  color: purple;
}
</style>
