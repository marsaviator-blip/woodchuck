<script setup lang="ts">
import { RouterLink, RouterView } from 'vue-router'
import HelloWorld from './components/HelloWorld.vue'
</script>

<template>
  <header>
    <h1 class="green">Woodchuck UI</h1>
  </header>
  <div class="outer">

    <div class=".left">
      <img alt="Vue logo" class="logo" src="@/assets/logo.svg" style="width: 50px; height: auto;" />

      <div class="wrapper">
        <HelloWorld msg="You did it!" />

        <nav>
          <RouterLink to="/">Home</RouterLink>
          <RouterLink to="/about">About</RouterLink>
          <RouterLink to="/carousel">Image carousel</RouterLink>
          <RouterLink to="/auth">IAM information</RouterLink>
        </nav>
      </div>
    </div>
    <div class=".right">
      <RouterView />
    </div>
  </div>
</template>

<style scoped>
header {
  /* line-height: 1.5;
  max-height: 100vh; */
}

.logo {
  display: block;
  margin: 0 auto 2rem;
}

.outer {
  display: flex;
  gap: 20px;
}

.left {
  flex-basis: 20%;
  align-content: right;
  height: 100%;
}

.right {
  flex-basis: 80%;
  align-content: left;
  height: 100%;
}

nav {
  /* width: 100%; */
  font-size: 12px;
  text-align: center;
  margin-top: 2rem;
}

nav a.router-link-exact-active {
  color: var(--color-text);
}

nav a.router-link-exact-active:hover {
  background-color: transparent;
}

nav a {
  display: inline-block;
  padding: 0 1rem;
  border-left: 1px solid var(--color-border);
}

nav a:first-of-type {
  border: 0;
}

/* @media (min-width: 2048px) {
  header {
    display: flex;
    place-items: center;
    padding-right: calc(var(--section-gap) / 2);
  }

  .logo {
    margin: 0 2rem 0 0;
  }

  header .wrapper {
    display: flex;
    place-items: flex-start;
    flex-wrap: wrap;
  }

  nav {
    text-align: left;
    margin-left: -1rem;
    font-size: 1rem;

    padding: 1rem 0;
    margin-top: 1rem;
  }
} */
</style>
