import { reactive } from 'vue';
import StoreModel from '../models/StoreModel';

const state = reactive({
    storeName: '',
    openStatus: '',
    error: null
});

const actions = {
    async openStoreExist(userName, token, storeName) {
        try {
            await StoreModel.openStoreExist(userName, token, storeName);
            state.storeName = storeName;
            state.openStatus = 'Store opened successfully';
            state.error = null;
        } catch (error) {
            state.error = error.message;
            state.openStatus = 'Failed to open store';
        }
    }
};

const getters = {
    getStoreName() {
        return state.storeName;
    },
    getOpenStatus() {
        return state.openStatus;
    },
    getError() {
        return state.error;
    }
};

export default {
    state,
    actions,
    getters
};
