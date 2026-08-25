import http from '@/http/index.ts'

export type AdmissionApply = {
    id?: number
    applicantName?: string
    applyAccount?: string
    gender?: number
    idCard?: string
    phone?: string
    email?: string
    formUrl?: string
    status?: number
    auditBy?: number
    auditTime?: string
    auditRemark?: string
    auditAccount?: string
    lawyerId?: number
}

export type AdmissionApplyParm = {
    currentPage: number
    pageSize: number
    applicantName?: string
    status?: number
    lawyerId?: number
}

export type AdmissionAttachment = {
    id?: number
    applyId?: number
    attType?: number
    attName?: string
    attUrl?: string
}

export const downloadTemplateApi = () => http.download('/api/admission/template/download', '入所审批表模板.docx')
export const addApplyApi = (parm: AdmissionApply) => http.post('/api/admission/apply', parm)
export const getListApi = (parm: AdmissionApplyParm) => http.get('/api/admission/apply/getList', parm)
export const auditApi = (parm: { id: number; status: number; auditRemark: string }) => http.post('/api/admission/apply/audit', parm)
export const deleteApi = (id: number) => http.delete(`/api/admission/apply/${id}`)
export const addAttachmentApi = (parm: AdmissionAttachment) => http.post('/api/admission/attachment', parm)
export const getAttachmentListApi = (applyId: number) => http.get('/api/admission/attachment/list', { applyId })
export const deleteAttachmentApi = (id: number) => http.delete(`/api/admission/attachment/${id}`)
