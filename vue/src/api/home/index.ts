import http from "@/http/index.ts";
//首页总数统计（用户总数/律师总数/进行案件/结算案件）
export const getTotalApi = () => {
    return http.get("/api/home/getTotal")
}
//首页最受欢迎律师（环形图）
export const getBestSaleApi = () => {
    return http.get("/api/home/getBestSale")
}
//首页案件类型统计（饼状图）
export const getCaseTypeApi = () => {
    return http.get("/api/home/getCaseType")
}
//首页每月案件统计（柱状图）
export const getEchartTotalApi = () => {
    return http.get("/api/home/getEchartTotal")
}
//首页公告
export const getNoticeListApi = () => {
    return http.get("/api/home/getNoticeList")
}
