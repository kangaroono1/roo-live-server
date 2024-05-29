<template>
  <el-row @click="emit('send-nav', false)">
    <el-col><RouterLink to="/home" active-class="active">首页</RouterLink></el-col>
    <el-col><RouterLink to="/show-all-files" active-class="active">测试视频预览</RouterLink></el-col>
    <el-col><RouterLink to="/upload" active-class="active">上传文件(普通)</RouterLink></el-col>
    <el-col><RouterLink to="/upload-chunked" active-class="active">上传文件(分片)</RouterLink></el-col>
    <el-col><RouterLink to="/admin" active-class="active" v-if="showAdmin">管理员界面</RouterLink></el-col>
  </el-row>
</template>

<script lang="ts" setup>
  import { ref, defineEmits } from 'vue'
  import { RouterLink } from 'vue-router'

  const showAdmin = ref(true)

  const emit = defineEmits(['send-nav']) // to close header-component's drawer
</script>

<style scoped>
.el-row a {
  display: block;
  text-align: center;
  width: 200px;
  height: 50px;
  line-height: 50px;
  border-radius: 5px;
  text-decoration: none;
  font-size: 18px;
  letter-spacing: 5px;
}
.el-row a.active {
  background-color: #74e6ee;
  color: #2c1ad2;
  font-weight: 900;
  text-shadow: 0 0 1px black;
  font-family: 微软雅黑;
}
</style>