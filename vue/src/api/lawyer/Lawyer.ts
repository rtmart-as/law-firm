export type Lawyer = {
    id:string;
    name:string;
    gender:string;
    nation?:string;
    birthDate?:string;
    graduateSchool?:string;
    major?:string;
    education?: string;
    politicalStatus?: string;
    partyTime?: string;
    idCard?: string;
    phone?: string;
    email?: string;
    address?: string;
    practiceCertNo?: string;
    qualificationGetDate?: string;
    businessSpecialty?: string;
    isPartner?: string;
    hireDate?: string;
    lawyerType?: string;
    partTimeCompany?: string;
    isCpcOrCommittee?: string;
    awardRecord?: string;
    punishRecord?: string;
    workStatus?: string;
    remark?: string;
    photoUrl?: string;
    resumeUrl?: string;
    firstSocialDate?: string;
    socialNo?: string;
}

export type LawyerParm = {
    currentPage:number;
    pageSize:number;
    name?:string;
}

// 转所记录类型
export interface TransferRecord {
    id?: number
    lawyerId: number
    transferType: number
    oldOrg?: string
    newOrg?: string
    transferDate?: string
    approvalFileUrl?: string
    status: number
    remark?: string
}


