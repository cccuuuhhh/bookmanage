import axios from 'axios';

// 创建一个 axios 实例
const instance = axios.create({
    baseURL: 'http://localhost', // 设置请求的基础URL
    timeout: 5000, // 设置请求超时时间
});

// 请求拦截器
instance.interceptors.request.use(
    config => {
        // 在发送请求之前做些什么
        const token = localStorage.getItem('token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    error => {
        // 对请求错误做些什么
        return Promise.reject(error);
    }
);

// 响应拦截器
instance.interceptors.response.use(
    response => {
        // 对响应数据做点什么
        return response;
    },
    error => {
        // 对响应错误做点什么
        return Promise.reject(error);
    }
);

export default instance;
