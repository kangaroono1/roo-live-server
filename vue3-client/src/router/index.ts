// 创建一个路由器，并暴露出去

// 第一步：引入createRouter
import { createRouter, createWebHistory } from 'vue-router'

const routes = [
    { path: '/', redirect: '/home'},
    { path: '/home', name: 'home', component: () => import('@/views/Home.vue')},
    { path: '/show-all-files/:pathMatch(.*)*', name: 'show-all-files', component: () => import('@/views/AllFiles.vue')},
    { path: '/upload', name: 'upload', component: () => import('@/views/Upload.vue')},
    { path: '/upload-chunked', name: 'upload-chunked', component: () => import('@/views/UploadChunked.vue')},
    { path: '/admin', name: 'admin', component: () => import('@/views/Admin.vue')},
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router