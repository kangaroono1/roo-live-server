<template>
    <div class="getFile">
      <el-row>
        <el-col>
          <!-- 页头：面包屑 -->
          <el-page-header @back="onBack">
            <template #breadcrumb>
              <el-breadcrumb separator="/">
                <el-breadcrumb-item><a href="/show-all-files">~</a></el-breadcrumb-item>
                <el-breadcrumb-item v-for="(item, index) in breadcrumbList" :key="index">
                  <a :href="item.url">{{ item.folderName }}</a>
                </el-breadcrumb-item>
              </el-breadcrumb>
            </template>
          </el-page-header>
        </el-col>
        <!-- 文件列表 -->
        <el-col><hr/></el-col>
        <el-col>【Tip：若浏览器(on iPhone)不支持h265编码格式的mp4，请使用夸克浏览器】</el-col>
        <el-col><hr/></el-col>
        <el-col v-for="(file, index) in fileList" :key="index">
          <el-icon>
            <component :is="getIconComponent(file.type)" />  
          </el-icon>
          
          <a :href="getURI(file.fileName, file.type)">
            <span> > {{ file.fileName }}</span>
          </a>

        </el-col>
      </el-row>
    </div>
</template>

<script lang="ts" setup>
  import { ref, onMounted, onUnmounted, watch } from 'vue'  
  import { useRoute, useRouter } from 'vue-router'
  import axios from 'axios'
  import { FolderOpened, Document, Film, Headset, VideoCamera, VideoCameraFilled, VideoPlay, Picture } from '@element-plus/icons-vue'

  const route = useRoute()
  const router = useRouter()

  /**
   * 页头返回函数
   */
  function onBack() {
    if (route.params.pathMatch.length > 0) router.back()
  }

  /**
   * interface 面包屑
   */
  interface breadcrumType {
    folderName: string,
    url: string
  }

  /**
   * interface 文件类型
   */
  interface fileListType {
    fileName: string,
    type: string,
    lastModified: string
  }

  /**
   * ref 面包屑文件夹类型
   */
  const breadcrumbList = ref<Array<breadcrumType>>([])

  /**
   * ref 文件列表
   */
  const fileList = ref<Array<fileListType>>([])

  /**
   * 为面包屑中的文件夹设置URL
   */
  const set4Breadcrumb = (subFix: string[]) => {
    breadcrumbList.value.length = 0
    let preFix = '/' + (route.name as string)
    // console.log("begin: " + preFix)
    subFix.forEach((item, index, subFix) => {
      preFix += '/' + item
      breadcrumbList.value.push({
        folderName: item,
        url: preFix
      })
    })
  }

  /**
   * 提供 文件夹URL / 文件URI
   * <a :href="xx">...</a>
   * @param fileName 
   * @param fileType 
   */
  const getURI = (fileName: string, fileType: string) => {
    if (fileType == 'folder') {
      return route.fullPath + "/" + encodeURIComponent(fileName)
    } else {
      // console.log(route.fullPath.substring("show-all-files".length+1) + encodeURI(fileName))
      return '/api/file/getFileStream' + route.fullPath.substring("show-all-files".length+1) + "/" + encodeURIComponent(fileName)
    }
  }

  /**
   * 获取文件列表或文件流
   * (根据文件列表的回调结果，判断获取文件列表还是文件流)
   */
  async function getFileList(subFix: string | string[]) {
    try {
      if(subFix == '') subFix = []
      // console.log(subFix == '')
      // console.log(JSON.stringify(subFix))
      let res = await axios.post(`/api/file/showList`, JSON.stringify(subFix), {
        headers: {
          'Content-Type': 'application/json'
        }
      })
      fileList.value.length = 0
      fileList.value.push(...(res.data as []))
      set4Breadcrumb(subFix as [])
    } catch (error) {  
      console.error(error)  
    }  
  }

  /**
   * 挂载：
   *  1.获取文件列表
   */
  onMounted(() => {
    getFileList(route.params.pathMatch)
  })

  /**
   * el图标组件映射
   */
  const iconMap = { 
    'folder': FolderOpened,
    'mp4': VideoPlay,
    'mp3': Headset,
    'png': Picture,
    'jpg': Picture
    // ... 其他文件类型映射  
  } 


  /**
   * 默认图标
   */
  const DefaultIcon = Document;

  /**
   * 根据图标类型返回相应组件
   * @param fileType
   */
  function getIconComponent(fileType: string) {
    return iconMap[fileType.toLowerCase() as keyof typeof iconMap] || DefaultIcon;  
  }

  /**
   * 监听路由变化
   * （防止点路由页面不变，不会调用重复）
   */
  watch(() => route.path, (toPath, fromPath) => {  
    // 路由变化时的逻辑  
    getFileList(route.params.pathMatch)
  });  
</script>

<style scoped>
  .getFile {
      display: flex;
      padding: 20px 0px 20px 0px; /* urdl */
      justify-content: center;
      align-items: center;
  }
</style>