<script setup lang="ts">
import { RouterLink, RouterView } from 'vue-router'
import Splitter from 'primevue/splitter';
import SplitterPanel from 'primevue/splitterpanel';
import NavPanel from './components/NavPanel.vue';
import LeftPanel from './components/LeftPanel.vue';
import RightPanel from './components/RightPanel.vue';
</script>

<template>
  <header>
    <h1 class="green padSmall">Life's Interest Model</h1>
    <h2 class="green padMedium"> - your memory recall tool</h2>
  </header>
  <div class="outer">
    <div class="leftNav">
      <NavPanel />
    </div>
    <div class="content">
      <Splitter style="height: 50vh">
        <SplitterPanel class="flex items-center justify-center">
          <p>howdy ho</p>
          <LeftPanel />
        </SplitterPanel>
        <SplitterPanel class="flex items-center justify-center">
          <p>Information Alley</p>
          <!-- <RouterView name="main"/> -->
          <RightPanel />
        </SplitterPanel>
      </Splitter>
    </div>
  </div>
</template>

<style scoped>
.padSmall {
  padding-left: 125px;
}
.padMedium {
  padding-left: 250px;
}
.outer {
  display: flex; /* Makes the children flex items and aligns them in a row */
  column-gap: 20px; /* Adds space between the divs */
  width: 100%;
  height: 100%;
  color: rgb(194, 135, 194);
  border: 2px solid rgb(255, 38, 0);
}
.leftNav {
  flex: 1;
}
.content {
  flex: 7;
}
</style>
