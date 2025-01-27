// 创建Vue应用
const loginApp = Vue.createApp({
    // 数据
    data() {
        return {
            // 登录表单数据
            loginForm: {
                username: '',
                password: '',
                rememberMe: false
            },
            // 是否正在登录
            isLoading: false,
            // 错误信息
            errorMessage: ''
        }
    },

    // 生命周期钩子：组件挂载后执行
    mounted() {
        // 检查是否有保存的用户名
        const savedUsername = localStorage.getItem('username');
        if (savedUsername) {
            this.loginForm.username = savedUsername;
            this.loginForm.rememberMe = true;
        }
    },

    // 方法
    methods: {
        // 登录方法
        async login() {
            this.isLoading = true;
            this.errorMessage = '';

            try {
                const response = await fetch('/api/login', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(this.loginForm)
                });

                if (!response.ok) {
                    throw new Error(await response.text());
                }

                const data = await response.json();

                if (!data.token) {
                    throw new Error('未收到token');
                }

                // 如果选择了"记住我"，保存用户名
                if (this.loginForm.rememberMe) {
                    localStorage.setItem('username', this.loginForm.username);
                } else {
                    localStorage.removeItem('username');
                }

                // 保存token
                localStorage.setItem('token', data.token);
                console.log('Token saved:', data.token);
                
                // 跳转到主页
                window.location.href = '/index.html';
            } catch (error) {
                this.errorMessage = error.message || '登录失败，请检查用户名和密码';
                console.error('登录失败:', error);
            } finally {
                this.isLoading = false;
            }
        }
    }
});

// 挂载Vue应用
loginApp.mount('#loginApp');