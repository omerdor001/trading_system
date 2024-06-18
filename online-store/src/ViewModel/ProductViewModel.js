import axios from 'axios';

const actions = {
    async updateProductField(userName, token, storeName, productId, field, value) {
        const urlMap = {
            'Name': '/api/setProductName',
            'Description': '/api/setProductDescription',
            'Price': '/api/setProductPrice',
            'Quantity': '/api/setProductQuantity',
            'Rating': '/api/setRating',
            'Category': '/api/setCategory'
        };

        const url = urlMap[field];
        if (!url) {
            throw new Error('Invalid field selected');
        }

        try {
            const response = await axios.post(url, {
                userName,
                token,
                storeName,
                productId,
                [field.toLowerCase()]: value
            });
            return response.data;
        } catch (error) {
            throw new Error(error.response.data || 'Failed to update product field.');
        }
    }
};

export default {
    actions
};
