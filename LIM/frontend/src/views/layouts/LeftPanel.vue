<script setup lang="ts">
import { ref } from 'vue';
import { RouterLink, RouterView } from 'vue-router'

</script>

<template>
    <div class="left">
        <p>yippie - where is i  </p>
        <RouterView/>
    </div>
</template>
<style scoped>
.left {
    width: 100%;
    border: 5px solid rgb(128, 0, 255);
}
</style>