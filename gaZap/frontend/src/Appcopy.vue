<script setup lang="ts">
import { ref } from 'vue';
import LeftPanel from './components/LeftPanel.vue';
import RightPanel from './components/RightPanel.vue';
import { RouterLink, RouterView } from 'vue-router'
import HelloWorld from './components/HelloWorld.vue'
</script>

<template>
  <div class="container">
    <LeftPanel>
      <h1 class="green">Woodchuck
      </h1>
      <p>yuk</p>
    </LeftPanel>
    <RightPanel>
      <p>hey hey</p>
    </RightPanel>
  </div>
</template>

<style>
.container {
  display: flex;
  flex: 1;
  gap: 20px;
  width: 100vw;
}


html, body#app {
  margin: 0;
  padding: 0;
  width: 100%;
  height: 100%;
}
 
/* this is set in main.ts */
#app {
  /* width: 50%; */
  /* width: 100vh; */
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  /* flex: 1;
  padding: 0;
  margin: 0; */
  background-color:rgb(20, 82, 82);
      border: 2px solid blue;

}

</style>
