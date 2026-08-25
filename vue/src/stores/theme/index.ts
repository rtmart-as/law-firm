import { defineStore } from 'pinia'
//定义主题store:白天/黑夜主题切换
export const themeStore = defineStore('themeStore', {
    state: () => {
        return {
            isDark: false
        }
    },
    getters: {
        getIsDark(state) {
            return state.isDark
        }
    },
    actions: {
        //把当前主题应用到 <html>（Element Plus 暗色模式通过 html.dark 生效）
        applyTheme() {
            const el = document.documentElement
            if (this.isDark) {
                el.classList.add('dark')
            } else {
                el.classList.remove('dark')
            }
        },
        //切换白天/黑夜
        toggle() {
            this.isDark = !this.isDark
            this.applyTheme()
        },
        //设置指定主题
        setDark(val: boolean) {
            this.isDark = val
            this.applyTheme()
        }
    },
    persist: {
        storage: localStorage,
        pick: ['isDark']
    }
})
