import http from '@/http/index.ts'

export type SealRegister = {
    id?: number
    useDate?: string
    useReason?: string
    copyCount?: number
    sealType?: string
    handlerLawyerId?: number
    handlerName?: string
    remark?: string
    status?: number
    registerAccount?: string
    auditBy?: number
    auditAccount?: string
    auditTime?: string
    auditRemark?: string
}

export type SealRegisterParm = {
    currentPage: number
    pageSize: number
    useDateStart?: string
    useDateEnd?: string
    handlerName?: string
    status?: number
}

export const getListApi = (parm: SealRegisterParm) => http.get('/api/seal/getList', parm)
export const addApi = (parm: SealRegister) => http.post('/api/seal', parm)
export const editApi = (parm: SealRegister) => http.put('/api/seal', parm)
export const deleteApi = (id: number) => http.delete(`/api/seal/${id}`)
export const auditApi = (parm: { id: number; status: number; auditRemark: string }) => http.post('/api/seal/audit', parm)
