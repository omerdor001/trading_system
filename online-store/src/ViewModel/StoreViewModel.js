import { reactive } from 'vue';
import StoreModel from '../models/StoreModel';

const state = reactive({
    stores: ['store 1'], // Mock store list including "store 1"
    products: [],
    error: null,
});

const actions = {
    async checkStoreExistence(userName, token, storeName) {
        try {
            if (storeName === 'store 1') {
                state.error = null;
                return { exists: true };
            }
            const response = await StoreModel.checkStoreExistence(userName, token, storeName);
            state.error = null;
            return response;
        } catch (error) {
            state.error = error.message;
            return { exists: false };
        }
    },

    async getStoreProducts(userName, token, storeName) {
        try {
            // Mock products for "store 1"
            if (storeName === 'store 1') {
                state.products = [
                    { id: 1, name: 'Product 1' },
                    { id: 2, name: 'Product 2' },
                    { id: 3, name: 'Product 3' },
                ];
            } else {
                const response = await StoreModel.getStoreProducts(userName, token, storeName);
                state.products = response;
            }
            state.error = null;
        } catch (error) {
            state.error = error.message;
        }
    },
};

const getters = {
    getStores() {
        return state.stores;
    },
    getProducts() {
        return state.products;
    },
    getError() {
        return state.error;
    },
};

export default {
    state,
    actions,
    getters,
};
