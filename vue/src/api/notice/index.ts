import http from '@/http/index.ts'

export type SysNotice = {
    id?: number
    title?: string
    content?: string
    createTime?: string
    updateTime?: string
    delFlag?: number
}

export type SysNoticeParm = {
    currentPage: number
    pageSize: number
    title?: string
}

export const getListApi = (parm: SysNoticeParm) => http.get('/api/notice/getList', parm)
export const addApi = (parm: SysNotice) => http.post('/api/notice', parm)
export const editApi = (parm: SysNotice) => http.put('/api/notice', parm)
export const deleteApi = (id: number) => http.delete(`/api/notice/${id}`)
