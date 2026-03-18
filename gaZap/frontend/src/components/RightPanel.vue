<script setup lang="ts">
import { ref } from 'vue';

</script>

<template>
    <div class="right">
        <p class="notsmallSpace" />
        <RouterView/>
    </div>
</template>

<style scoped>
.right {
    flex: 1;
    min-width: 20%;
    /* flex-grow: 1;
    flex-basis: 75vw; */
    /* flex-shrink: 0;  */
    align-content: left;
    min-height: 100%;
    border: 2px solid yellow;
}

.smallSpace {
  height: 40px;
}</style>
