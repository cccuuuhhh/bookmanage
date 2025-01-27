// 创建Vue应用
const app = Vue.createApp({
    // 数据
    data() {
        return {
            // 搜索表单数据
            searchForm: {
                name: '',
                author: '',
                isbn: '',
                minPrice: '',
                maxPrice: ''
            },
            // 图书列表
            books: [],
            // 当前编辑的图书
            currentBook: {},
            // 是否是编辑模式
            isEdit: false,
            // Bootstrap模态框实例
            bookModal: null
        }
    },

    // 生命周期钩子：组件挂载后执行
    mounted() {
        // 检查是否已登录
        const token = localStorage.getItem('token');
        if (!token) {
            window.location.href = '/login.html';
            alert('请先登录');
            return;
        }

        // 初始化Bootstrap模态框
        this.bookModal = new bootstrap.Modal(document.getElementById('bookModal'));
        // 加载图书列表
        this.loadBooks();
    },

    // 方法
    methods: {
        // 注销方法
        logout() {
            localStorage.removeItem('token');
            window.location.href = '/login.html';
        },

        // 加载图书列表
        async loadBooks() {
            try {
                // 构建查询参数
                const params = new URLSearchParams();
                if (this.searchForm.name) params.append('name', this.searchForm.name);
                if (this.searchForm.author) params.append('author', this.searchForm.author);
                if (this.searchForm.isbn) params.append('isbn', this.searchForm.isbn);
                if (this.searchForm.minPrice) params.append('minPrice', this.searchForm.minPrice);
                if (this.searchForm.maxPrice) params.append('maxPrice', this.searchForm.maxPrice);

                // 添加token到请求头
                const headers = {
                    'Authorization': `Bearer ${localStorage.getItem('token')}`
                };

                // 发送请求获取数据
                const response = await fetch(`/books/list?${params.toString()}`, { headers });
                
                if (response.status === 401) {
                    // token失效，跳转到登录页
                    window.location.href = '/login.html';
                    return;
                }
                this.books = await response.json();
            } catch (error) {
                console.error('加载图书列表失败:', error);
                alert('加载图书列表失败');
            }
        },

        // 显示添加表单
        showAddForm() {
            this.isEdit = false;
            this.currentBook = {};
            this.bookModal.show();
        },

        // 显示编辑表单
        editBook(book) {
            this.isEdit = true;
            this.currentBook = { ...book };
            this.bookModal.show();
        },

        // 保存图书
        async saveBook() {
            try {
                const response = await fetch('/books/save', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(this.currentBook)
                });

                if (!response.ok) {
                    throw new Error('保存失败');
                }

                // 重新加载图书列表
                await this.loadBooks();
                // 关闭模态框
                this.bookModal.hide();
            } catch (error) {
                console.error('保存图书失败:', error);
                alert('保存图书失败');
            }
        },

        // 删除图书
        async deleteBook(id) {
            if (!confirm('确定要删除这本书吗？')) {
                return;
            }

            try {
                const response = await fetch(`/books/delete/${id}`, {
                    method: 'DELETE'
                });

                if (!response.ok) {
                    throw new Error('删除失败');
                }

                // 重新加载图书列表
                await this.loadBooks();
            } catch (error) {
                console.error('删除图书失败:', error);
                alert('删除图书失败');
            }
        },

        // 搜索图书
        searchBooks() {
            this.loadBooks();
        },

        // 重置搜索条件
        resetSearch() {
            this.searchForm = {
                name: '',
                author: '',
                isbn: '',
                minPrice: '',
                maxPrice: ''
            };
            this.loadBooks();
        }
    }
});
const token = localStorage.getItem('token');
if (token) {
    // 设置Authorization头部
    fetch('/api/some-protected-route', {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });
}
// 挂载Vue应用
app.mount('#app'); 