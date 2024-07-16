<template>
  <div>
    <SiteHeader :isLoggedIn="true" :username="userName" @logout="logout" />
    <div class="container">
      <h2>Edit Product Details</h2>
      <div class="fields">
        <PrimeDropdown v-model="selectedField" :options="fields" optionLabel="label" placeholder="Select a Field" class="dropdown"/>
      </div>
      <div v-if="selectedField">
        <div v-if="selectedField.value === 'Category'">
          <PrimeDropdown v-model="selectedCategoryIndex" :options="categoryOptions" optionLabel="label" optionValue="index" placeholder="Select a Category" class="dropdown-category"/>
          <PrimeButton label="Change" @click="updateField(selectedField.value)" class="button"/>
        </div>
        <div class="field-container" v-else>
          <InputText v-model="newDetail" placeholder="New Detail" class="input-text"/>
          <PrimeButton label="Change" @click="updateField(selectedField.value)" class="button"/>
        </div>
      </div>
    </div>
    <PrimeToast />
  </div>
</template>

<script>
import axios from 'axios';
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { InputText } from 'primevue/inputtext';
import { Button as PrimeButton } from 'primevue/button';
import { Dropdown as PrimeDropdown } from 'primevue/dropdown';
import SiteHeader from '@/components/SiteHeader.vue';
import { useToast } from 'primevue/usetoast';

export default {
  name: 'ProductManagement',
  components: {
    InputText,
    PrimeButton,
    PrimeDropdown,
    SiteHeader
  },
  setup() {
    const fields = ref([
      { label: 'Name', value: 'Name' },
      { label: 'Description', value: 'Description' },
      { label: 'Price', value: 'Price' },
      { label: 'Quantity', value: 'Quantity' },
      { label: 'Rating', value: 'Rating' },
      { label: 'Category', value: 'Category' },
      { label: 'Add Key-Word', value: 'Add Key-Word' },
      { label: 'Remove Key-Word', value: 'Remove Key-Word' }
    ]);
    const selectedField = ref(null);
    const newDetail = ref('');
    const categoryOptions = ref([]);
    const selectedCategoryIndex = ref(null);
    const route = useRoute();
    const toast = useToast();
    const storeName = ref('');
    const productId = ref('');
    const userName = localStorage.getItem('username');
    const token = localStorage.getItem('token');

    const selectField = (field) => {
      selectedField.value = field;
      if (field === 'Category') {
        fetchCategories();
      }
    };

    const updateField = async (field) => {
      try {
        if (field !== 'Category' && !newDetail.value) {
          toast.add({ severity: 'warn', summary: 'Warning', detail: `${field} cannot be empty`, life: 3000 });
          return;
        }
        if (field === 'Category' && (selectedCategoryIndex.value === null || selectedCategoryIndex.value === '')) {
          toast.add({ severity: 'warn', summary: 'Warning', detail: 'Category cannot be empty', life: 3000 });
          return;
        }

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
            updateFunction = updateProductCategory;
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
        const detail = field === 'Category' ? selectedCategoryIndex.value : newDetail.value;
        await updateFunction(detail);
        newDetail.value = '';
        selectedCategoryIndex.value = null;
      } catch (error) {
        console.error('Failed to update product field', error);
      }
    };

    const fetchCategories = async () => {
      try {
        const response = await axios.get('http://localhost:8082/api/trading/categories', {
          params: {
            username: userName,
            token: token,
          }
        });
        const categoryArray = response.data.slice(1, response.data.length - 1).split(',');
        categoryOptions.value = categoryArray.map((category, index) => ({ label: category, value: category, index: index + 1 }));
      } catch (error) {
        console.error('Failed to fetch categories', error);
        toast.add({ severity: 'error', summary: 'Error', detail: 'Failed to fetch categories', life: 3000});
      }
    };

    const updateProductName = async (newName) => {
    try {
      const response = await axios.post('http://localhost:8082/api/trading/setProductName', null, {
        params: {
          username: userName,
          token: token,
          storeName: storeName.value,
          productId: productId.value,
          productName: newName
        }
      });
      toast.add({ severity: 'success', summary: 'Success', detail: 'Product name updated successfully', life: 3000 });
      return response.data;
    } catch (error) {
      toast.add({ severity: 'error', summary: 'Error', detail: error.response.data || 'Failed to update product name', life: 3000 });
      throw new Error(`Failed to update product name: ${error.message}`);
    }
  };

  const updateProductDescription = async (newDescription) => {
    try {
      const response = await axios.post('http://localhost:8082/api/trading/setProductDescription', null, {
        params: {
          username: userName,
          token: token,
          storeName: storeName.value,
          productId: productId.value,
          productDescription: newDescription
        }
      });
      toast.add({ severity: 'success', summary: 'Success', detail: 'Product description updated successfully', life: 3000 });
      return response.data;
    } catch (error) {
      toast.add({ severity: 'error', summary: 'Error', detail: error.response.data || 'Failed to update product description', life: 3000 });
      throw new Error(`Failed to update product description: ${error.message}`);
    }
  };

  const updateProductPrice = async (newPrice) => {
    if (isNaN(parseFloat(newPrice)) || !isFinite(newPrice)) {
      toast.add({ severity: 'warn', summary: 'Warning', detail: 'New price must be a valid number', life: 3000 });
      throw new Error('New price must be a valid number');
    }
    try {
      const response = await axios.post('http://localhost:8082/api/trading/setProductPrice', null, {
        params: {
          username: userName,
          token: token,
          storeName: storeName.value,
          productId: productId.value,
          productPrice: newPrice
        }
      });
      toast.add({ severity: 'success', summary: 'Success', detail: 'Product price updated successfully', life: 3000 });
      return response.data;
    } catch (error) {
      toast.add({ severity: 'error', summary: 'Error', detail: error.response.data || 'Failed to update product price', life: 3000 });
      throw new Error(`Failed to update product price: ${error.message}`);
    }
  };

  const updateProductQuantity = async (newQuantity) => {
    // Check if newQuantity is an integer
    if (!Number.isInteger(parseInt(newQuantity)) || newQuantity < 0) {
      toast.add({ severity: 'warn', summary: 'Warning', detail: 'Quantity must be a non-negative integer', life: 3000 });
      throw new Error('Quantity must be a non-negative integer');
    }
    
    try {
      const response = await axios.post('http://localhost:8082/api/trading/setProductQuantity', null, {
        params: {
          username: userName,
          token: token,
          storeName: storeName.value,
          productId: productId.value,
          productQuantity: parseInt(newQuantity)
        }
      });
      toast.add({ severity: 'success', summary: 'Success', detail: 'Product quantity updated successfully', life: 3000 });
      return response.data;
    } catch (error) {
      toast.add({ severity: 'error', summary: 'Error', detail: error.response.data || 'Failed to update product quantity', life: 3000 });
      throw new Error(`Failed to update product quantity: ${error.message}`);
    }
  };

  const updateProductRating = async (newRating) => {
    if (isNaN(newRating) || newRating < 0 || newRating > 10) {
      toast.add({ severity: 'warn', summary: 'Warning', detail: 'Rating must be a number between 1 and 10', life: 3000 });
      throw new Error('Rating must be a number between 1 and 10');
    }
    try {
      const response = await axios.post('http://localhost:8082/api/trading/setRating', null, {
        params: {
          username: userName,
          token: token,
          storeName: storeName.value,
          productId: productId.value,
          rating: newRating
        }
      });
      toast.add({ severity: 'success', summary: 'Success', detail: 'Product rating updated successfully', life: 3000 });
      return response.data;
    } catch (error) {
      toast.add({ severity: 'error', summary: 'Error', detail: error.response.data || 'Failed to update product rating', life: 3000 });
      throw new Error(`Failed to update product rating: ${error.message}`);
    }
  };

  const updateProductCategory = async (selectedIndex) => {
    try {
      const response = await axios.post('http://localhost:8082/api/trading/setCategory', null, {
        params: {
          username: userName,
          token: token,
          storeName: storeName.value,
          productId: productId.value,
          category: selectedIndex - 1,
        }
      });
      toast.add({ severity: 'success', summary: 'Success', detail: 'Product category updated successfully', life: 3000 });
      return response.data;
    } catch (error) {
      toast.add({ severity: 'error', summary: 'Error', detail: error.response.data || 'Failed to update product category', life: 3000 });
      throw new Error(`Failed to update product category: ${error.message}`);
    }
  };

  const addProductKeyword = async (keyword) => {
    if (!keyword) {
      toast.add({ severity: 'warn', summary: 'Warning', detail: 'Keyword cannot be empty', life: 3000 });
      throw new Error('Keyword cannot be empty');
    }
    try {
      const response = await axios.post('http://localhost:8082/api/trading/addKeyword', null, {
        params: {
          username: userName,
          token: token,
          storeName: storeName.value,
          productId: productId.value,
          keyword: keyword
        }
      });
      toast.add({ severity: 'success', summary: 'Success', detail: 'Keyword added successfully', life: 3000 });
      return response.data;
    } catch (error) {
      toast.add({ severity: 'error', summary: 'Error', detail: error.response.data || 'Failed to add product keyword', life: 3000 });
      throw new Error(`Failed to add keyword: ${error.message}`);
    }
  };

  const removeProductKeyword = async (keyword) => {
    if (!keyword) {
      toast.add({ severity: 'warn', summary: 'Warning', detail: 'Keyword cannot be empty', life: 3000 });
      throw new Error('Keyword cannot be empty');
    }
    try {
      const response = await axios.post('http://localhost:8082/api/trading/removeKeyword', null, {
        params: {
          username: userName,
          token: token,
          storeName: storeName.value,
          productId: productId.value,
          keyword: keyword
        }
      });
      toast.add({ severity: 'success', summary: 'Success', detail: 'Keyword removed successfully', life: 3000 });
      return response.data;
    } catch (error) {
      toast.add({ severity: 'error', summary: 'Error', detail: error.response.data || 'Failed to remove product keyword', life: 3000 });
      throw new Error(`Failed to remove keyword: ${error.message}`);
    }
  };

    onMounted(() => {
      storeName.value = route.params.storeName;
      productId.value = route.params.productId;
      fetchCategories();
    });

    return {
      fields,
      selectedField,
      newDetail,
      selectField,
      updateField,
      categoryOptions,
      selectedCategoryIndex,
      userName,
      token,
      toast
    };
  }
};
</script>

<style scoped>
.container {
  padding: 20px;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.fields {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.field-container {
  display: flex;
  flex-direction: column;
  margin-top: 10px; /* Add margin to create vertical space */
}

.input-text {
  margin-bottom: 10px; /* Add margin to create vertical space */
}

.dropdown {
  width: 300px; /* Adjust the width as needed */
}

.dropdown-category {
  width: 300px; /* Adjust the width as needed */
  margin-top: 10px; /* Add margin to create vertical space */
}
</style>
