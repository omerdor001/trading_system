import axios from 'axios';

class StoreModel {
    static async openStoreExist(userName, token, storeName) {
        try {
            const response = await axios.post('/api/openStoreExist', {
                userName,
                token,
                storeName
            });
            return response.data;
        } catch (error) {
            throw new Error(error.response.data || 'Failed to open store.');
        }
    }
}

export default StoreModel;
