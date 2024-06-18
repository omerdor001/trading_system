import axios from 'axios';

class ProductModel {
    static async getProductsByStore(storeName) {
        try {
            const response = await axios.get(`/api/products/${storeName}`);
            return response.data;
        } catch (error) {
            throw new Error(error.response.data || 'Failed to fetch products.');
        }
    }

    static async updateProductField(productId, field, newValue) {
        try {
            const response = await axios.patch(`/api/products/${productId}`, {
                field,
                newValue
            });
            return response.data;
        } catch (error) {
            throw new Error(error.response.data || 'Failed to update product.');
        }
    }
}

export default ProductModel;
