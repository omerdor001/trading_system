<template>
  <div>
    <SiteHeader :isLoggedIn="true" :username="username" @logout="logout" />
    <div class="container">
      <h2>Choose What To Change</h2>
      <div class="fields">
        <PrimeButton v-for="field in fields" :key="field" :label="field" @click="selectField(field)" />
      </div>
      <div v-if="selectedField">
        <h3>{{ selectedField }}</h3>
        <InputText v-model="newDetail" placeholder="New Detail" />
        <PrimeButton label="Change" @click="updateField(selectedField)" />
      </div>
    </div>
    <PrimeToast />
  </div>
</template>

<script>
import axios from 'axios';
import { ref } from 'vue';
import { useRoute } from 'vue-router';
import { InputText } from 'primevue/inputtext';
import { Button as PrimeButton } from 'primevue/button';
import SiteHeader from '@/components/SiteHeader.vue';
import { useToast } from 'primevue/usetoast';

export default {
  name: 'ProductManagement',
  components: {
    InputText,
    PrimeButton,
    SiteHeader
  },
  setup() {
    const fields = ref(['Name', 'Description', 'Price', 'Quantity', 'Rating', 'Category', 'Add Key-Word', 'Remove Key-Word']);
    const selectedField = ref('');
    const newDetail = ref('');
    const route = useRoute();
    const toast = useToast();
    const storeName = route.params.storeName;
    const productId = route.params.productId;
    const userName = localStorage.getItem('username');
    const token = localStorage.getItem('token');

    const selectField = (field) => {
      selectedField.value = field;
    };

    const updateField = async (field) => {
      try {
        let updateFunction;
        switch (field) {
          case 'Name':
            updateFunction = updateProductName;
            break;
          case 'Description':
            updateFunction = updateProductDescription;
            break;
          case 'Price':
            updateFunction = updateProductPrice;
            break;
          case 'Quantity':
            updateFunction = updateProductQuantity;
            break;
          case 'Rating':
            updateFunction = updateProductRating;
            break;
          case 'Category':
          //  updateFunction = updateProductCategory;
            break;
          case 'Add Key-Word':
            updateFunction = addProductKeyword;
            break;
          case 'Remove Key-Word':
            updateFunction = removeProductKeyword;
            break;
          default:
            alert('Unsupported field');
            return;
        }
        await updateFunction(newDetail.value);
        toast.add({ severity: 'success', summary: 'Success', detail: `${field} updated successfully` });
      } catch (error) {
        console.error('Failed to update product field', error);
        toast.add({ severity: 'error', summary: 'Error', detail: `Failed to update ${field} - ${error.message}`,life:3000 });
      }
    };

   const updateProductName = async (newName) => {
      try {
        const response = await axios.post('http://localhost:8082/api/trading/setProductName', null, {
          params: {
          username: userName,
          token: token,
          storeName: storeName,
          productId: productId,
          productName: newName
          }
        });
        return response.data; 
      } catch (error) {
        throw new Error(`Failed to update product name: ${error.message}`);
      }
    };

    const updateProductDescription = async (newDescription) => {
      try {
        const response = await axios.post('http://localhost:8082/api/trading/setProductDescription', null , {
          params: {
          username: userName,
          token: token,
          storeName: storeName,
          productId: productId,
          productDescription: newDescription
          }
        });
        return response.data; 
      } catch (error) {
        throw new Error(`Failed to update product description: ${error.message}`);
      }
    };

     const updateProductPrice = async (newPrice) => {
      if (isNaN(parseFloat(newPrice)) || !isFinite(newPrice)) {
        throw new Error('New price must be a valid number');
      }
      try {
        const response = await axios.post('http://localhost:8082/api/trading/setProductPrice',null,{
          params: {
          username: userName,
          token: token,
          storeName: storeName,
          productId: productId,
          productPrice: newPrice
          }
        });
        return response.data; 
      } catch (error) {
        throw new Error(`Failed to update product price: ${error.message}`);
      }
    };

    const updateProductQuantity = async (newQuantity) => {
      if (!Number.isInteger(newQuantity) || newQuantity < 0) {
        throw new Error('Quantity must be a non-negative integer');
      }
      try {
        const response = await axios.post('http://localhost:8082/api/trading/setProductQuantity',null, {
          params: {
          username: userName,
          token: token,
          storeName: storeName,
          productId: productId,
          productQuantity: newQuantity
          }
        });
        return response.data; 
      } catch (error) {
        throw new Error(`Failed to update product quantity: ${error.message}`);
      }
    };

    const updateProductRating = async (newRating) => {
      if (isNaN(newRating) || newRating < 0 || newRating > 11) {
        throw new Error('Rating must be a number between 1 and 10');
      }
      try {
        const response = await axios.post('http://localhost:8082/api/trading/setRating',null,{
        params: {
          username: userName,
          token: token,
          storeName: storeName,
          productId: productId,
          rating: newRating
        }
        });
        return response.data; 
      } catch (error) {
        throw new Error(`Failed to update product rating: ${error.message}`);
      }
    };

    const addProductKeyword = async (keyword) => {
      try {
        const response = await axios.post('http://localhost:8082/api/trading/addKeyword',null,{
        params: {
          username: userName,
          token: token,
          storeName: storeName,
          productId: productId,
          keyword: keyword
        }
        });
        return response.data; 
      } catch (error) {
        throw new Error(`Failed to add product keyword: ${error.message}`);
      }
    };

    const removeProductKeyword = async (keyword) => {
      try {
        const response = await axios.post('http://localhost:8082/api/trading/removeKeyword',null,{
        params: {
          username: userName,
          token: token,
          storeName: storeName,
          productId: productId,
          keyword: keyword
        }
        });
        return response.data; 
      } catch (error) {
        throw new Error(`Failed to remove product keyword: ${error.message}`);
      }
    };



    return {
      fields,
      selectedField,
      newDetail,
      selectField,
      updateField,
    };
  }
};
</script>

<style scoped>
.container {
  padding: 20px;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.fields {
  display: flex;
  flex-direction: column;
  gap: 20px;
}
</style>
