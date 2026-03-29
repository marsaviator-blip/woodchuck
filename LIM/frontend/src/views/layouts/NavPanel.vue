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
      <p>Run against an Interest</p>
      <p>Setup agents</p>
      <p>View agents</p>
      <div class="smallSpace" />
      <h2>Interests</h2>
      <p><RouterLink to="/addinterest">Add Interests</RouterLink></p>
      <p><RouterLink to="/modifyinterest">Modify Interests</RouterLink></p>
      <p><RouterLink to="/removeinterest">Remove Interests</RouterLink></p>
      <p><RouterLink to="/viewinterest">View Interests</RouterLink></p>
      <div class="smallSpace" />
      <h2>Basic model</h2>
      <p>Create</p>
      <p>Delete</p>
      <p>Modify</p>
      <p><RouterLink to="/basicmodel">View Basic Model</RouterLink></p>
    </nav>
  </div>
</template>

<style scoped>
.nav {
  width: 200px;
  height: 100%;
}

.smallSpace {
  height: 30px;
}
</style>
