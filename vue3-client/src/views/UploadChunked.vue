<template>
    <!-- 注意这里的 action="#"，和:auto-upload="false"，如果:auto-upload="true"，由于el-upload的change事件，上传和上传成功都会回调，这里
    将其设置为false，不自动上传，不然会重复请求。 :show-file-list="true"是否展示文件列表 -->
    <el-upload
      class="upload-demo"
      action="#"
      drag
      multiple
      :auto-upload="false"
      :show-file-list="false"
      v-model:file-list="fileList"
      :on-change="handleChange" 
      :on-remove="handleRemove">
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">
            拖拽文件到这里或者<em>点击上传</em>
        </div>
      <template #tip>
            <h3>磁盘剩余空间: TODO</h3>
            <a href="http://192.168.3.12:8080/" target="blank">检查本地文件</a>
      </template>
    </el-upload>
    <el-row>
      <el-table :data="tableData" stripe style="width: 100%">
        <el-table-column prop="name" label="名字"/>
        <el-table-column prop="percentage" label="进度">
          <template #default="scope">
            <el-progress
              :text-inside="true"
              :stroke-width="26"
              :percentage="scope.row.percentage" 
              :status="scope.row.percentage == 100 ? 'success' : ''"
            />
        </template>
        </el-table-column>
        <el-table-column prop="percentage" label="操作">
          <template #default="scope">
            <el-button type="primary" @click="handleRemoveUploadFileList(scope.row)">删除</el-button>
        </template>
        </el-table-column>
      </el-table>
    </el-row>
</template>
  
<script setup lang="ts">
  import axios from 'axios'
  import { UploadFilled } from '@element-plus/icons-vue'
  import type { UploadProps, UploadFile, UploadFiles } from 'element-plus'
  import { ref, onMounted, computed } from 'vue';
  
  /**
   * export interface UploadFile {
   *   name: string;
   *   percentage?: number;
   *   status: UploadStatus;
   *   size?: number;
   *   response?: unknown;
   *   uid: number;
   *   url?: string;
   *   raw?: UploadRawFile;
   * }
   * 
   * export declare type UploadFiles = UploadFile[];
   */
  // 文件列表
  const fileList = ref<UploadFiles>([])
  // 切片表
  const tableData = ref<UploadFiles>([])
  // 分片大小50MB
  const chunkSize = 1024*1024*50

  // handleChange -> [ uploadFileToServer ] -> mergeFiles
  /**
   * 分片文件上传
   * @param file 上传的分块文件
   * @param chunkNumber 当前是第几块
   * @param chunkTotal 文件分块的总数
   * @param fileName 文件名称
   */
  const uploadFileToServer = async (file: any, chunkNumber: any, chunkTotal: any, fileName: any) => {
    const form = new FormData();
    // 这里的data是文件
    form.append("file", file);
    form.append("chunkNumber", chunkNumber);
    form.append("chunkTotal", chunkTotal);
    form.append("fileName", fileName)
    const result = await axios.post("/api/file/uploadBigFile", form)
    return result
  }
  
  
  /**
   * 合并文件
   * @param chunkTotal 文件分块的总数量
   * @param fileName 文件名称
   */
  const mergeFiles = async (chunkTotal: number, fileName: string) => {
    axios.put(`/api/file/merge?chunkTotal=${chunkTotal}&fileName=${encodeURIComponent(fileName)}`)
  }
  
  

  /**
   * 根据文件名删除文件
   * @param fileName 文件名
   */
  const deleteFileByFileName = async (fileName: string) => {
    const result = await axios.delete(`/api/file/deleteByFileName?fileName=${encodeURIComponent(fileName)}`)
  }
  
  
  /**
   * 分片发送，回调执行逻辑(TODO：仅串行，待优化)
   * 
   * (el-upload内置的change函数，文件上传或者上传成功时的回调，不过这里因为
   * :auto-upload="false"的缘故，上床成功的回调不会执行)
   * (v-model:file-list="fileList" 就是这里的uploadFiles。在删除的时候，从fileList清除某一项，uploadFiles这里也会清除的)
   * @param uploadFile el-upload当前上传的文件对象
   * @param uploadFiles el-upload上传的文件列表
   */
  const handleChange: UploadProps['onChange'] = async (uploadFile, uploadFiles) => {

    tableData.value.push({...uploadFile})       // 上传文件放入切片表
    const index = tableData.value.findIndex(item => item.uid === uploadFile.uid) // 循环查找，获取文件在列表中的索引
    const fileName = uploadFile.name            // 上传文件名
    const fileSize = uploadFile.size || 0       // 上传文件大小 (若传输文件大小为0，会阻塞？)
    let chunkTotals = Math.ceil(fileSize / chunkSize);

    if (chunkTotals > 0) {
      for (let chunkNumber = 0, start = 0; chunkNumber < chunkTotals; chunkNumber++,  start += chunkSize) {
        let end = Math.min(fileSize, start + chunkSize);
          // uploadFile.raw这个是element plus 中 el-upload的自己上传的文件就放在这个raw里面，可以console.log(uploadFile)看一下
          // 加 ？是因为ts语法提示“uploadFile.raw”可能为“未定义”，加了这个就不过有报错了
        const files = uploadFile.raw?.slice(start, end)
        const result = await uploadFileToServer(files, chunkNumber+1, chunkTotals, fileName)
        const percents = parseFloat(result.data.replace("%",''))
        uploadFile.percentage = percents
        tableData.value[index].percentage = percents
      }
      await mergeFiles(chunkTotals, fileName)
    } else {
        handleRemoveUploadFileList(uploadFile)
    }
  
    console.log(uploadFiles)
  
  }
  
  
  
  /**
   * 从el-upload封装的文件列表中删除文件
   * // el-upload的（内置方法），点击el-upload默认的文件展示以列表的 X 那个图标时，会移除文件，并从文件列表中去掉
   * @param uploadFile el-upload当前删除点击的文件
   * @param uploadFiles el-upload的文件列表
   */
  const handleRemove: UploadProps['onRemove'] = async (uploadFile, uploadFiles) => {
    // 这个删除表格tableData中的列表数据
    const index2 = tableData.value.findIndex((item2: UploadFile) => item2.uid === uploadFile.uid)
    if (index2 !== -1) {
      tableData.value.splice(index2,1)
    }
    await deleteFileByFileName(uploadFile.name)
    // handleRemove内置的删除文件方法，
    // 这里的uploadFiles跟绑定的v-model:file-list="fileList"可以看成是同一个数组，这两个数组的数据是一样的，从这两个数组中删除或者移除任何一个数据，
    // 另外一个数据也会跟着变化的，这个element plus已经实现了
    console.log(uploadFiles)
  }
  
  
  
  /**
   * 自定义的表格删除的方法
   * @param file 表格某一行的数据
   */
  const handleRemoveUploadFileList = async (file: UploadFile) => {
    const index = fileList.value.findIndex((item: UploadFile) => item.uid === file.uid)
    if (index !== -1) {
      fileList.value.splice(index,1)
    }
    const index2 = tableData.value.findIndex((item2: UploadFile) => item2.uid === file.uid)
    if (index2 !== -1) {
      tableData.value.splice(index2,1)
    }
  
    // 删除磁盘中的文件
     await deleteFileByFileName(file.name)
  }
  
  
</script>
  
<style scoped>
</style>
  