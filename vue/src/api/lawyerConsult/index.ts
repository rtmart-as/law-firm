import http from '@/http/index.ts'

export type LawyerConsult = {
    id?: number
    lawyerId?: number
    companyName?: string
    position?: string
    startDate?: string
    endDate?: string
    isCurrent?: number
    remark?: string
}

export type LawyerConsultParm = {
    currentPage: number
    pageSize: number
    lawyerId?: number
}

export const addApi = (parm: LawyerConsult) => http.post('/api/lawyerConsult', parm)
export const editApi = (parm: LawyerConsult) => http.put('/api/lawyerConsult', parm)
export const deleteApi = (id: number) => http.delete(`/api/lawyerConsult/${id}`)
export const getListApi = (parm: LawyerConsultParm) => http.get('/api/lawyerConsult/getList', parm)
