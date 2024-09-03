<template>
    <div class="upload">
        <el-upload
            v-model:file-list="fileList"
            class=""
            action="/api/file/upload"
            multiple
            :before-remove="beforeRemove"
            :limit="numOfLimit"
            :on-exceed="handleExceed"
            :on-success="handleSuccess"
        >
            <el-button type="primary">点击上传（自动）</el-button>
            <template #tip>
            <div class="el-upload__tip">
                <h3>【Tip：未做传输限制，要提高单次文件传输大小，请提高tomcat单次接受请求大小，测试1G可行，比分片传快很多】</h3>
                <h3>限传文件{{ numOfLimit }}个, 磁盘剩余空间：{{ showFreeSpace }}</h3>
            </div>
            <button @click="pingServer">刷新</button>
            </template>
        </el-upload>
    </div>
</template>

<script lang="ts" setup>
  import { ref, computed, onMounted } from 'vue'
  import { ElMessage, ElMessageBox } from 'element-plus'
  import axios from 'axios'
  import type { UploadProps, UploadUserFile } from 'element-plus'
  
  let freeSpace = ref(0)
  const BYTES_PER_KB = 1024
  const BYTES_PER_MB = 1024 * 1024
  const BYTES_PER_GB = 1024 * 1024 * 1024

  const numOfLimit = ref(200)

  const fileList = ref<UploadUserFile[]>([])
  
  const handleExceed: UploadProps['onExceed'] = (files, uploadFiles) => {
    ElMessage.warning(
      `The limit is ${numOfLimit.value}, you selected ${files.length} files this time, add up to ${
        files.length + uploadFiles.length
      } totally`
    )
  }
  
  const handleSuccess: UploadProps['onSuccess'] = (response, uploadFile) => {
    freeSpace.value = response.free_space // 更新内存空间
  }

  const beforeRemove: UploadProps['beforeRemove'] = (uploadFile, uploadFiles) => {
    return ElMessageBox.confirm(
      `确定要取消${uploadFile.name}的传输吗?`
    ).then(
      () => true,
      () => false
    )
  }

  let showFreeSpace = computed(() => {
    let free = freeSpace.value

    if (free > BYTES_PER_GB) {
        return (free / BYTES_PER_GB).toFixed(2).toString() + 'GB'

    } else if (free > BYTES_PER_MB) {
        return (free / BYTES_PER_MB).toFixed(2).toString() + 'MB'

    } else if (free > BYTES_PER_KB) {
        return (free / BYTES_PER_KB).toFixed(2).toString() + 'KB'
    }
  })

  async function pingServer() {
    try {
        let result = await axios.get('/api/file/ping')
        freeSpace.value = result.data.free_space
        console.log(result.data.free_space)
    } catch (error) {
        alert(error)
    }
  }

  onMounted(() => {
    pingServer()
    console.log(freeSpace)
  })
</script>

<style scoped>
    .upload {
        display: flex;
        padding: 20px 0px 20px 0px; /* urdl */
        justify-content: center;
        align-items: center;
    }
</style>