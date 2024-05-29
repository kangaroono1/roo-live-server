<template>
    <div class="home" v-loading="loading">
        <img class="img" src="https://img1.baidu.com/it/u=2332296034,2224058381&fm=253&fmt=auto&app=138&f=JPEG?w=942&h=500" alt="">
    </div>
</template>

<script setup lang="ts" name="Home">
    import { ref } from 'vue'

    let loading = ref(false)
</script>

<style scoped>
.img {
    width: 100%;
}
</style>