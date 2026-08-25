import {defineStore} from 'pinia'
import { getInfoApi } from '@/api/user/index.ts'
//定义store:defineStore
export const userStore = defineStore('userStore',{
    state:()=>{
        return{
            userId:'',
            nickName:'',
            userName:'',
            token:'',
            codeList: [] as string[]
        }
    },
    getters:{
        getUserId(state){
            return state.userId
        },
        getNickName(state) {
            return state.nickName;
        },
        getUserName(state){
            return state.userName;
        },
        getToken(state){
            return state.token;
        },
        getCodeList(state){
            return state.codeList;
        }
    },
    actions:{
        setUserId(userId:string){
            this.userId = userId;
        },
        setNickName(nickName:string){
            this.nickName = nickName;
        },
        setUserName(userName:string){
            this.userName = userName;
        },
        setToken(token:string){
            this.token = token;
        },
        getInfo() {
            return new Promise((resolve, reject) => {
                getInfoApi(this.userId).then((res) => {
                    if (res && res.code == 200) {
                        this.codeList = res.data.permissons
                    }
                    resolve(this.codeList)
                }).catch((error) => {
                    reject(error)
                })
            })
        }
    },
    persist: {
        storage: sessionStorage,
        pick: ['userId', 'nickName', 'userName', 'token']
    }
})
