// 创建Vue应用
const promotionApp = Vue.createApp({
    data() {
        return {
            // 促销列表
            promotions: [],
            // 当前编辑的促销
            currentPromotion: {},
            // 图书列表
            books: [],
            // 模态框标题
            modalTitle: '添加促销',
            // 模态框实例
            promotionModal: null
        }
    },

    mounted() {
        // 检查是否已登录
        const token = localStorage.getItem('token');
        if (!token) {
            window.location.href = '/login.html';
            return;
        }

        // 初始化Bootstrap模态框
        this.promotionModal = new bootstrap.Modal(document.getElementById('promotionModal'));
        // 加载数据
        this.loadPromotions();
        this.loadBooks();
    },

    methods: {
        // 加载促销列表
        async loadPromotions() {
            try {
                const response = await fetch('/promotions/list', {
                    headers: {
                        'Authorization': `Bearer ${localStorage.getItem('token')}`
                    }
                });

                if (response.status === 401) {
                    window.location.href = '/login.html';
                    return;
                }

                this.promotions = await response.json();
            } catch (error) {
                console.error('加载促销列表失败:', error);
                alert('加载促销列表失败');
            }
        },

        // 加载图书列表
        async loadBooks() {
            try {
                const response = await fetch('/books/list', {
                    headers: {
                        'Authorization': `Bearer ${localStorage.getItem('token')}`
                    }
                });

                if (response.status === 401) {
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
            this.modalTitle = '添加促销';
            this.currentPromotion = {};
            this.promotionModal.show();
        },

        // 显示编辑表单
        editPromotion(promotion) {
            this.modalTitle = '编辑促销';
            this.currentPromotion = { ...promotion };
            this.promotionModal.show();
        },

        // 保存促销
        async savePromotion() {
            try {
                // 验证表单
                if (!this.validateForm()) {
                    return;
                }

                const response = await fetch('/promotions/save', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json;charset=UTF-8',
                        'Authorization': `Bearer ${localStorage.getItem('token')}`
                    },
                    body: JSON.stringify(this.currentPromotion)
                });

                if (response.status === 401) {
                    window.location.href = '/login.html';
                    return;
                }

                if (!response.ok) {
                    throw new Error(await response.text());
                }

                // 重新加载数据
                await this.loadPromotions();
                // 关闭模态框
                this.promotionModal.hide();
            } catch (error) {
                console.error('保存促销失败:', error);
                alert('保存促销失败: ' + error.message);
            }
        },

        // 删除促销
        async deletePromotion(id) {
            if (!confirm('确定要删除这个促销活动吗？')) {
                return;
            }

            try {
                const response = await fetch(`/promotions/delete/${id}`, {
                    method: 'DELETE',
                    headers: {
                        'Authorization': `Bearer ${localStorage.getItem('token')}`
                    }
                });

                if (response.status === 401) {
                    window.location.href = '/login.html';
                    return;
                }

                // 重新加载数据
                await this.loadPromotions();
            } catch (error) {
                console.error('删除促销失败:', error);
                alert('删除促销失败');
            }
        },

        // 验证表单
        validateForm() {
            const form = document.getElementById('promotionForm');
            if (!form.checkValidity()) {
                form.reportValidity();
                return false;
            }

            if (this.currentPromotion.startTime >= this.currentPromotion.endTime) {
                alert('开始时间必须小于结束时间');
                return false;
            }

            return true;
        },

        // 格式化日期时间
        formatDateTime(dateTimeLocal) {
            if (!dateTimeLocal) return '';
            const dt = new Date(dateTimeLocal);
            return dt.getFullYear() +
                   '-' + this.pad(dt.getMonth() + 1) +
                   '-' + this.pad(dt.getDate()) +
                   ' ' + this.pad(dt.getHours()) +
                   ':' + this.pad(dt.getMinutes()) +
                   ':' + this.pad(dt.getSeconds());
        },

        // 补零函数
        pad(num) {
            return String(num).padStart(2, '0');
        }
    }
});

// 挂载Vue应用
promotionApp.mount('#app'); 