import http from '@/http/index.ts'

export type LawyerCert = {
    id?: number
    lawyerId?: number
    certName?: string
    certNo?: string
    issueOrg?: string
    issueDate?: string
    fileUrl?: string
    remark?: string
}

export type LawyerCertParm = {
    currentPage: number
    pageSize: number
    lawyerId?: number
}

export const addApi = (parm: LawyerCert) => http.post('/api/lawyerCert', parm)
export const editApi = (parm: LawyerCert) => http.put('/api/lawyerCert', parm)
export const deleteApi = (id: number) => http.delete(`/api/lawyerCert/${id}`)
export const getListApi = (parm: LawyerCertParm) => http.get('/api/lawyerCert/getList', parm)
