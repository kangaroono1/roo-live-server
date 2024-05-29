<template>
    <div v-if="show" v-loading="loading">
        管理员界面（）
    </div>
    <div v-else>
        无权访问！
    </div>
</template>

<script setup lang="ts" name="Home">
    import { ref } from 'vue'

    const loading = ref(false)
    const show = ref(false)
</script>

<style scoped>

</style>