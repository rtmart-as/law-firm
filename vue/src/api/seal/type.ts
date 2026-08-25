import http from '@/http/index.ts'

export type SealType = {
    id?: number
    name?: string
    createBy?: string
    createTime?: string
    remark?: string
}

export type SealTypeParm = {
    currentPage: number
    pageSize: number
    name?: string
}

// 新增
export const addApi = (parm: SealType) => http.post('/api/sealType', parm)
// 编辑
export const editApi = (parm: SealType) => http.put('/api/sealType', parm)
// 删除
export const deleteApi = (id: number) => http.delete(`/api/sealType/${id}`)
// 查询列表（分页 + 名称模糊）
export const getListApi = (parm: SealTypeParm) => http.get('/api/sealType/getList', parm)
