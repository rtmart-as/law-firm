import axios from 'axios'
import {type AxiosInstance, type AxiosRequestConfig, type AxiosResponse, type InternalAxiosRequestConfig} from 'axios'
import {ElMessage} from 'element-plus';
import { userStore } from "@/stores/user";
//axios请求配置
const config = {
    // baseURL: 'http://localhost:8089',
    timeout: 10000,
    baseURL: '/api',
    withCredentials: true
}

//定义返回值类型
export interface Result<T = any> {
    code: number;
    msg: string;
    data: T;
}

class Http {
    //axios实例
    private instance: AxiosInstance;

    //构造函数里面初始化
    constructor(config: AxiosRequestConfig) {
        //创建axios的实例
        this.instance = axios.create(config)
        //定义拦截器
        this.interceptors()
    }

    //拦截器
    private interceptors() {
        //axios发送请求之前的处理
        this.instance.interceptors.request.use((config: InternalAxiosRequestConfig) => {
            const store = userStore()
            //在请求头部携带token
            let token = store.getToken;
            if (token) {
                config.headers!['token'] = token
            }
            return config;
        }, (error: any) => {
            error.data = {}
            error.data.msg = '服务器异常，请联系管理员!'
            return error;
        })
        //axios请求返回之后的处理
        this.instance.interceptors.response.use((res: AxiosResponse) => {
            if (res.data.code == 200) {
                return res.data
            } else {
                ElMessage.error(res.data.msg || '服务器出错!')
                return Promise.reject(res.data.msg || '服务器出错')
            }
        }, (error) => {
            console.log('进入错误')
            error.data = {};
            if (error && error.response) {
                switch (error.response.status) {
                    case 400:
                        error.data.msg = '错误请求';
                        ElMessage.error(error.data.msg)
                        break
                    case 401:
                        error.data.msg = '未授权，请重新登录';
                        ElMessage.error(error.data.msg)
                        break
                    case 403:
                        error.data.msg = '拒绝访问';
                        ElMessage.error(error.data.msg)
                        break
                    case 404:
                        error.data.msg = '请求错误,未找到接口';
                        ElMessage.error(error.data.msg)
                        break
                    case 405:
                        error.data.msg = '请求方法未允许';
                        ElMessage.error(error.data.msg)
                        break
                    case 408:
                        error.data.msg = '请求超时';
                        ElMessage.error(error.data.msg)
                        break
                    case 500:
                        error.data.msg = '服务器端出错';
                        ElMessage.error(error.data.msg)
                        break
                    case 501:
                        error.data.msg = '网络未实现';
                        ElMessage.error(error.data.msg)
                        break
                    case 502:
                        error.data.msg = '网络错误';
                        ElMessage.error(error.data.msg)
                        break
                    case 503:
                        error.data.msg = '服务不可用';
                        ElMessage.error(error.data.msg)
                        break
                    case 504:
                        error.data.msg = '网络超时';
                        ElMessage.error(error.data.msg)
                        break
                    case 505:
                        error.data.msg = 'http版本不支持该请求';
                        ElMessage.error(error.data.msg)
                        break
                    default:
                        error.data.msg = `连接错误${error.response.status}`;
                        ElMessage.error(error.data.msg)
                }
            } else {
                error.data.msg = "连接到服务器失败";
                ElMessage.error(error.data.msg)
            }
            return Promise.reject(error)
        })
    }

    /* GET 方法 */
    get<T = Result>(url: string, params?: object): Promise<T> {
        return this.instance.get(url, {params}) as unknown as Promise<T>
    }

    /* 文件下载（二进制流）：绕过 JSON 拦截器，直接触发浏览器下载 */
    download<T = Blob>(url: string, filename?: string): Promise<T> {
        const store = userStore()
        return axios.request<Blob>({
            url,
            method: 'GET',
            baseURL: this.instance.defaults.baseURL,
            responseType: 'blob',
            headers: { token: store.getToken }
        }).then((res) => {
            // 用 Blob 创建临时链接，点击触发下载（后端 Content-Disposition 已带文件名）
            const blobUrl = URL.createObjectURL(res.data)
            const a = document.createElement('a')
            a.href = blobUrl
            a.download = filename || '下载文件'
            document.body.appendChild(a)
            a.click()
            document.body.removeChild(a)
            URL.revokeObjectURL(blobUrl)
            return res.data as T
        })
    }

    /* POST 方法 */
    post<T = Result>(url: string, data?: object): Promise<T> {
        return this.instance.post(url, data) as unknown as Promise<T>
    }

    /* PUT 方法 */
    put<T = Result>(url: string, data?: object): Promise<T> {
        return this.instance.put(url, data) as unknown as Promise<T>
    }

    /* DELETE 方法 */
    delete<T = Result>(url: string): Promise<T> {
        return this.instance.delete(url) as unknown as Promise<T>
    }

    //图片上传
    upload<T = Result>(url: string, params?: object): Promise<T> {
        return this.instance.post(url, params, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        }) as unknown as Promise<T>
    }
}

export default new Http(config)
