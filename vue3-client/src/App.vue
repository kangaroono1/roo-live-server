<template>
  <div class="app common-layout">
    <!-- 上容器 -->
    <el-container class="layout-container-demo" style="height: 600px">
      <!-- 页头-Header -->
      <el-header><Header /></el-header>

      <!-- 中容器 -->
      <el-container>
        <!-- 侧边栏-Nav -->
        <el-aside width="200px" v-if="!(isMoble)">
          <el-scrollbar><Navigation /></el-scrollbar>
        </el-aside>
        <!-- 展示区-Main -->
        <el-main><RouterView/><el-backtop :right="100" :bottom="100" /></el-main>    
      </el-container>

    </el-container>
    <!-- 下容器-页脚-Footer --> 
    <!-- <el-container class="layout-container-demo"><el-footer></el-footer></el-container> -->
  </div>
</template>

<script setup lang="ts" name="App">
  import { ref, onBeforeMount } from 'vue'
  import { RouterView } from 'vue-router'
  import { isMobileDevice } from '@/utils/mobile-detect'
  import Navigation from '@/components/Navigation.vue'
  import Header from '@/components/Header.vue'

  let isMoble = ref(false)

  onBeforeMount(() => { 
    isMoble.value = isMobileDevice()
  })
</script>

<style>
.layout-container-demo .el-header {
  background-color: var(--el-color-primary-light-7);
  color: var(--el-text-color-primary);
  border-radius: 15px 15px 0px 0px; /* 10:30 -> 1:30 -> 4:30 -> 7:30 */
}
.layout-container-demo .el-aside {
  position: relative;
  color: var(--el-text-color-primary);
  background: var(--el-color-primary-light-8);
}
.layout-container-demo .el-main {
  padding: 0;
}
.layout-container-demo .el-footer {
  position: relative;
  /* background-color: var(--el-color-primary-light-7); */
  color: var(--el-text-color-primary);
  border-radius: 0px 0px 15px 15px;
}
</style>


