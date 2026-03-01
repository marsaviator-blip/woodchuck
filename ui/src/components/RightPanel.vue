<script setup lang="ts">
import { ref } from 'vue';

</script>

<template>
    <div class="right">
        <p>Lot number xyz</p>
        <RouterView/>
    </div>
</template>

<style scoped>
.right {
    flex: 1;
    width: 700px;
    /* flex-grow: 1;
    flex-basis: 75vw; */
    flex-shrink: 0; 
    align-content: left;
     /* background-color: green; */
}
</style>
