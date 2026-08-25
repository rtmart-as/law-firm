import http from '@/http/index.ts'

export type LawyerArchiveTransfer = {
    id?: number
    lawyerId?: number
    transferType?: number
    fromOrg?: string
    toOrg?: string
    transferDate?: string
    fileUrl?: string
    remark?: string
}

export type LawyerArchiveTransferParm = {
    currentPage: number
    pageSize: number
    lawyerId?: number
}

export const addApi = (parm: LawyerArchiveTransfer) => http.post('/api/lawyerArchiveTransfer', parm)
export const editApi = (parm: LawyerArchiveTransfer) => http.put('/api/lawyerArchiveTransfer', parm)
export const deleteApi = (id: number) => http.delete(`/api/lawyerArchiveTransfer/${id}`)
export const getListApi = (parm: LawyerArchiveTransferParm) => http.get('/api/lawyerArchiveTransfer/getList', parm)
