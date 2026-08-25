import http, { type Result } from '@/http/index.ts'

export type InvoiceRecord = {
    id?: number
    recordYear?: number
    batchNo?: string
    invoiceNo?: string
    invoiceDate?: string
    contractNo?: string
    contractId?: number
    lawyerId?: number
    lawyerName?: string
    invoiceAmount?: number
    taxAmount?: number
    invoiceTotal?: number
    extJson?: Record<string, any>
}

export type InvoiceTemplate = {
    id?: number
    recordYear?: number
    colKey?: string
    colLabel?: string
    colOrder?: number
    isCore?: number
}

// ===== 表头模板 =====
export const getTemplateApi = (recordYear: number) => http.get('/api/invoice/template', { recordYear })
export const saveTemplateApi = (parm: InvoiceTemplate[]) => http.post('/api/invoice/template', parm)
// ===== Excel 模板下载（按已保存模板生成可填写的 Excel 文件） =====
export const exportTemplateApi = (recordYear: number) =>
    http.download(`/api/invoice/template/export?recordYear=${recordYear}`, `${recordYear}年发票登记模板.xlsx`)

// ===== Excel 导入 =====
export const importApi = (file: File, recordYear: number) => {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('recordYear', String(recordYear))
    return http.upload<Result<any>>('/api/invoice/import', formData)
}

// ===== 发票记录 =====
export const getRecordListApi = (parm: any) => http.get('/api/invoice/record/getList', parm)
export const updateRecordApi = (parm: InvoiceRecord) => http.put('/api/invoice/record', parm)
export const deleteRecordApi = (id: number) => http.delete(`/api/invoice/record/${id}`)
export const diffApi = (batchNo: string) => http.get('/api/invoice/record/diff', { batchNo })

// ===== 合同原件 =====
export const saveFileApi = (parm: any) => http.post('/api/invoice/file', parm)
export const fileListApi = (parm: any) => http.get('/api/invoice/file/list', parm)

// 通用上传：返回 /images/xxx
export const uploadFileApi = (file: File) => {
    const formData = new FormData()
    formData.append('file', file)
    return http.upload<Result<string>>('/api/upload/uploadImage', formData)
}
