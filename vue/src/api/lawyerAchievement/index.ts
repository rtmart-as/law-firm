import http from '@/http/index.ts'

export type LawyerAchievement = {
    id?: number
    lawyerId?: number
    paperTitle?: string
    journal?: string
    journalIssue?: string
    publisher?: string
    publishDate?: string
    fileUrl?: string
    remark?: string
}

export type LawyerAchievementParm = {
    currentPage: number
    pageSize: number
    lawyerId?: number
}

export const addApi = (parm: LawyerAchievement) => http.post('/api/lawyerAchievement', parm)
export const editApi = (parm: LawyerAchievement) => http.put('/api/lawyerAchievement', parm)
export const deleteApi = (id: number) => http.delete(`/api/lawyerAchievement/${id}`)
export const getListApi = (parm: LawyerAchievementParm) => http.get('/api/lawyerAchievement/getList', parm)
