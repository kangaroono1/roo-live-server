<template>
  <!-- 抽屉（组件） -->
  <el-drawer
    v-model="showDrawer"
    direction="ltr"
    size="225px"
  >
    <Navigation @send-nav="closeDrawer"/>
  </el-drawer>
  <el-row class="header-bar">
    <!-- 1-[3]-15-[2-2]-1 -->
    <!-- [24] -->
    <el-col :span="1"></el-col>
    <el-col :span="3">
      <el-icon @click="showDrawer = true" class="icon-expand" v-if="isMoble"><Expand /></el-icon>
    </el-col>
    <el-col :span="15"></el-col>
    <el-col :span="2">
      <el-dropdown class="icon-tools">
        <el-icon><Tools /></el-icon>
        <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item>个人中心</el-dropdown-item>
          <el-dropdown-item>帮助中心</el-dropdown-item>
          <el-dropdown-item>切换账号</el-dropdown-item>
        </el-dropdown-menu>
        </template>
      </el-dropdown>
    </el-col>
    <el-col :span="3" class="user-name"><span>Dong</span></el-col>
  </el-row>
</template>

<script lang="ts" setup>
  import { ref, onBeforeMount } from 'vue'
  import { isMobileDevice } from '@/utils/mobile-detect'
  import Navigation from '@/components/Navigation.vue'

  const showDrawer = ref(false)
  let isMoble = ref(false)

  onBeforeMount(() => { 
    isMoble.value = isMobileDevice()
  })

  function closeDrawer(val: boolean) {
    showDrawer.value =  val
  }
</script>

<style scoped>
.header-bar {
  margin-top: 20px;
}
.icon-expand {
  font-size: 22px;
  margin-top: 3px;
}
.icon-tools {
  font-size: 22px;
  float: right;
  margin-top: 3px;
  margin-right: 10px;
}
.user-name {
  font-size: 22px;
  float: left;
  /* 12:00Up 3:00Right 6:00 9:00 */
}
</style>