import axios from 'axios';

class StoreModel {
    static async checkStoreExistence(userName, token, storeName) {
        try {
            const response = await axios.post('/api/storeExistence', {
                userName,
                token,
                storeName
            });
            return response.data;
        } catch (error) {
            throw new Error(error.response?.data || 'Failed to check store existence.');
        }
    }

    static async getStoreProducts(userName, token, storeName) {
        try {
            const response = await axios.post('/api/getStoreProducts', {
                userName,
                token,
                storeName
            });
            return response.data;
        } catch (error) {
            throw new Error(error.response?.data || 'Failed to get store products.');
        }
    }
}

export default StoreModel;
