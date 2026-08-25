import http, {type Result} from '@/http/index.ts'
import {type Lawyer,type LawyerParm} from "@/api/lawyer/Lawyer.ts";
// 新增
export const addApi = (lawyer:Lawyer)=>{
    return http.post('/api/lawyer',lawyer);
}

// 修改
export const editApi = (lawyer:Lawyer) => {
    return http.put('/api/lawyer',lawyer);
}

// 删除
export const deleteApi = (id:number)=>{
    return http.delete(`/api/lawyer/${id}`);
}
// 查询列表
export const getListApi = (searchParam:LawyerParm) => {
    return http.get('/api/lawyer/getList',searchParam)
}
// 通用上传：返回 /images/xxx
export const uploadFileApi = (file: File) => {
    const formData = new FormData()
    formData.append('file', file)
    return http.upload<Result<string>>('/api/upload/uploadImage', formData)
}
// 按id查询律师（供详情页展示基本信息）
export const getByIdApi = (id: number) => {
    return http.get(`/api/lawyer/${id}`)
}
