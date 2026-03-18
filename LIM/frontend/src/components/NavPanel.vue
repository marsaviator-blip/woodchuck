<script setup lang="ts">
import { ref } from 'vue';
import { RouterLink, RouterView } from 'vue-router'
import { Router, useRoute } from 'vue-router';
</script>

<template>
  <div class="nav">
    <h1 class="green">LIM</h1>
    <nav>
      <div class="smallSpace" />
      <h2>Collector agents</h2>
      <p>Run against Interest</p>
      <p>Setup agents</p>
      <p>View agents</p>
      <div class="smallSpace" />
      <h2>Interests</h2>
      <p>Add Interest</p>
      <p>Modify Interest</p>
      <p>Remove Interest</p>
      <p>View Interest</p>
      <p>
        <!-- <RouterLink to="/">Home</RouterLink>
          <RouterLink to="/auth">IAM information</RouterLink> -->
      </p>
      <div class="smallSpace" />
      <h2>Basic model</h2>
      <p>Create</p>
      <p>Delete</p>
      <p>Modify</p>
      <p>View</p>
      <p>
        <!-- <RouterLink to="/carousel">Image carousel</RouterLink> -->
        <RouterLink to="/formview">Form view</RouterLink>
        <!-- <RouterLink to="/about">About</RouterLink> -->
      </p>
    </nav>
  </div>
</template>

<style scoped>
.nav {
  width: 200px;
  height: 100%;
  border: 10px solid rgb(249, 246, 251);
}

.smallSpace {
  height: 30px;
}
</style>
