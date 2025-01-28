// 创建Vue应用
const promotionApp = Vue.createApp({
    data() {
        return {
            promotions: [],
            books: [],
            currentPromotion: {
                id: null,
                bookId: '',
                discountPrice: '',
                startTime: '',
                endTime: '',
                description: ''
            },
            modalTitle: '添加促销',
            promotionModal: null,
            loading: false,
            errorMessage: '',
            successMessage: ''
        }
    },

    computed: {
        formattedPromotions() {
            return this.promotions.map(promotion => ({
                ...promotion,
                formattedStartTime: this.formatDateTime(promotion.startTime),
                formattedEndTime: this.formatDateTime(promotion.endTime),
                bookName: this.getBookName(promotion.bookId)
            }));
        }
    },

    mounted() {
        this.initializeApp();
    },

    methods: {
        async initializeApp() {
            const token = this.checkAuthentication();
            if (!token) return;

            this.promotionModal = new bootstrap.Modal(document.getElementById('promotionModal'));
            await Promise.all([
                this.loadPromotions(),
                this.loadBooks()
            ]);
        },

        checkAuthentication() {
            const token = localStorage.getItem('token');
            if (!token) {
                window.location.href = '/login.html';
                return null;
            }
            return token;
        },

        async loadPromotions() {
            try {
                this.loading = true;
                const response = await this.fetchWithAuth('/promotions/list');
                this.promotions = await this.handleResponse(response);
            } catch (error) {
                this.handleError('加载促销列表失败', error);
            } finally {
                this.loading = false;
            }
        },

        async loadBooks() {
            try {
                const response = await this.fetchWithAuth('/books/list');
                this.books = await this.handleResponse(response);
            } catch (error) {
                this.handleError('加载图书列表失败', error);
            }
        },

        showAddForm() {
            this.resetForm();
            this.modalTitle = '添加促销';
            this.promotionModal.show();
        },

        editPromotion(promotion) {
            try {
                this.modalTitle = '编辑促销';
                this.currentPromotion = {
                    ...promotion,
                    startTime: this.formatDateTimeForInput(promotion.startTime),
                    endTime: this.formatDateTimeForInput(promotion.endTime)
                };
                console.log('编辑的促销数据:', this.currentPromotion);
                this.promotionModal.show();
            } catch (error) {
                this.handleError('准备编辑数据失败', error);
            }
        },

        async savePromotion() {
            if (!this.validateForm()) return;

            try {
                this.loading = true;
                const response = await this.fetchWithAuth('/promotions/save', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json;charset=UTF-8'
                    },
                    body: JSON.stringify(this.currentPromotion)
                });

                await this.handleResponse(response);
                this.showSuccessMessage('保存成功');
                await this.loadPromotions();
                this.promotionModal.hide();
            } catch (error) {
                this.handleError('保存促销失败', error);
            } finally {
                this.loading = false;
            }
        },

        async deletePromotion(id) {
            if (!confirm('确定要删除这个促销活动吗？')) return;

            try {
                this.loading = true;
                const response = await this.fetchWithAuth(`/promotions/delete/${id}`, {
                    method: 'DELETE'
                });

                await this.handleResponse(response);
                this.showSuccessMessage('删除成功');
                await this.loadPromotions();
            } catch (error) {
                this.handleError('删除促销失败', error);
            } finally {
                this.loading = false;
            }
        },

        validateForm() {
            const form = document.getElementById('promotionForm');
            if (!form.checkValidity()) {
                form.reportValidity();
                return false;
            }

            const startTime = new Date(this.currentPromotion.startTime);
            const endTime = new Date(this.currentPromotion.endTime);
            
            if (startTime >= endTime) {
                this.showErrorMessage('开始时间必须小于结束时间');
                return false;
            }

            if (this.currentPromotion.discountPrice <= 0) {
                this.showErrorMessage('促销价格必须大于0');
                return false;
            }

            return true;
        },

        resetForm() {
            this.currentPromotion = {
                id: null,
                bookId: '',
                discountPrice: '',
                startTime: '',
                endTime: '',
                description: ''
            };
            this.errorMessage = '';
            this.successMessage = '';
        },

        // 工具方法
        async fetchWithAuth(url, options = {}) {
            const token = localStorage.getItem('token');
            const headers = {
                'Authorization': `Bearer ${token}`,
                ...options.headers
            };

            const response = await fetch(url, { ...options, headers });
            if (response.status === 401) {
                window.location.href = '/login.html';
                throw new Error('未授权访问');
            }
            return response;
        },

        async handleResponse(response) {
            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(errorText || '请求失败');
            }
            return response.json();
        },

        handleError(message, error) {
            console.error(message, error);
            this.showErrorMessage(`${message}: ${error.message}`);
        },

        showErrorMessage(message) {
            this.errorMessage = message;
            this.successMessage = '';
            setTimeout(() => this.errorMessage = '', 5000);
        },

        showSuccessMessage(message) {
            this.successMessage = message;
            this.errorMessage = '';
            setTimeout(() => this.successMessage = '', 3000);
        },

        formatDateTime(dateTimeStr) {
            if (!dateTimeStr) return '';
            try {
                const date = new Date(dateTimeStr);
                return date.toLocaleString('zh-CN', {
                    year: 'numeric',
                    month: '2-digit',
                    day: '2-digit',
                    hour: '2-digit',
                    minute: '2-digit',
                    second: '2-digit'
                });
            } catch (error) {
                console.error('日期格式化错误:', error);
                return dateTimeStr;
            }
        },

        formatDateTimeForInput(dateTimeStr) {
            if (!dateTimeStr) return '';
            try {
                const date = new Date(dateTimeStr);
                if (isNaN(date.getTime())) {
                    console.error('无效的日期时间:', dateTimeStr);
                    return '';
                }
                return date.toISOString().slice(0, 16);
            } catch (error) {
                console.error('日期格式化错误:', error);
                return '';
            }
        },

        getBookName(bookId) {
            const book = this.books.find(b => b.id === bookId);
            return book ? book.name : '未知图书';
        }
    }
});

// 挂载Vue应用
promotionApp.mount('#app');
