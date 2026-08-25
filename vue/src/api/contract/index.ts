import http, { type Result } from '@/http/index.ts'

export type Contract = {
    id?: number
    contractNo?: string
    contractType?: string
    caseType?: string
    cause?: string
    clientName?: string
    lawyerId?: number
    lawyerName?: string
    receiveDate?: string
    receiver?: string
    isReturned?: number
    contractAmount?: number
    receiptAmount?: number
    invoiceAmount?: number
    manageFee?: number
    acceptAmount?: number
    payTime?: string
    invoiceFlag?: number
    receiptFlag?: number
    status?: number
    remark?: string
}

export type ContractParm = {
    currentPage: number
    pageSize: number
    contractNo?: string
    caseType?: string
    lawyerName?: string
    status?: number
    startDate?: string
    endDate?: string
}

export const getListApi = (parm: ContractParm) => http.get('/api/contract/getList', parm)
export const addApi = (parm: Contract) => http.post('/api/contract', parm)
export const editApi = (parm: Contract) => http.put('/api/contract', parm)
export const deleteApi = (id: number) => http.delete(`/api/contract/${id}`)

export const changeApi = (parm: any) => http.post('/api/contract/change', parm)
export const cancelApi = (parm: any) => http.post('/api/contract/cancel', parm)
export const recoverApi = (parm: any) => http.post('/api/contract/recover', parm)

// 通用上传：返回 /images/xxx（供 情况说明/解除PDF/收回PDF 用）
export const uploadFileApi = (file: File) => {
    const formData = new FormData()
    formData.append('file', file)
    return http.upload<Result<string>>('/api/upload/uploadImage', formData)
}

// ===== 7 类统计 =====
export const statManageFeeApi = (parm: any) => http.get('/api/contract/statistics/manageFee', parm)
export const statManageFeeByLawyerApi = (parm: any) => http.get('/api/contract/statistics/manageFeeByLawyer', parm)
export const statReceivableApi = (parm: any) => http.get('/api/contract/statistics/receivable', parm)
export const statSummaryApi = (parm: any) => http.get('/api/contract/statistics/summary', parm)
export const statNoInvoiceApi = (parm: any) => http.get('/api/contract/statistics/noInvoice', parm)
export const statInvoiceDetailApi = (parm: any) => http.get('/api/contract/statistics/invoiceDetail', parm)
export const statYearSummaryApi = (parm: any) => http.get('/api/contract/statistics/yearSummary', parm)
