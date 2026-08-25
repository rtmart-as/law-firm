import http from '@/http/index.ts'

export type LawyerTransfer = {
    id?: number
    lawyerId?: number
    transferType?: number
    oldOrg?: string
    newOrg?: string
    transferDate?: string
    approvalFileUrl?: string
    status?: number
    confirmBy?: number
    confirmAccount?: string
    confirmTime?: string
    remark?: string
    auditRemark?: string
}

export type LawyerTransferParm = {
    currentPage: number
    pageSize: number
    lawyerId?: number
}

export const addApi = (parm: LawyerTransfer) => http.post('/api/lawyerTransfer', parm)
export const editApi = (parm: LawyerTransfer) => http.put('/api/lawyerTransfer', parm)
export const deleteApi = (id: number) => http.delete(`/api/lawyerTransfer/${id}`)
export const getListApi = (parm: LawyerTransferParm) => http.get('/api/lawyerTransfer/getList', parm)
export const confirmApi = (parm: { id: number; status: number; auditRemark?: string }) =>
    http.post('/api/lawyerTransfer/confirm', parm)
