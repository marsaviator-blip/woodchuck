<template>
  <div id="basicmodel">
    <h1>Basic Model</h1>
    <div>
      <D3NetworkGraph :nodes="nodes" :links="links" :options="options" />
    </div>
  </div>
</template>


<script lang="ts" setup>
import { ref, reactive } from 'vue';
import D3NetworkGraph from 'vue-d3-network-graph';

const nodes = ref([
  { id: 1, name: "Node 1" },
  { id: 2, name: "Node 2" },
  { id: 3, name: "Node 3" },
  { id: 4, name: "Node 4" },
]);

const links = ref([
  { source: 2, target: 1, name: "Link A" },
  { source: 4, target: 3, name: "Link B" },
  { source: 4, target: 2, name: "Link C" },
]);


const options = reactive({
  layout: {
    draggable: true, // Allows nodes to be dragged
  },
  nodeSize: 15, // Set node radius
  linkWidth: 2, // Set link thickness
  // Additional options available, see documentation
});
</script>

<style>
/* Optional styling */
.link {
  stroke: #777;
  stroke-width: 2px;
}

.node {
  fill: #ccc;
  stroke: #fff;
  stroke-width: 2px;
}
</style>
