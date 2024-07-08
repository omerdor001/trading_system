<template>
  <div>
    <SiteHeader :isLoggedIn="isLoggedIn" :username="username" @logout="logout" />
    <h2>Purchase Policy Management</h2>
    <div class="button-group">
      <PrimeButton label="Add New Purchase Policy" icon="pi pi-plus" class="custom-button" @click="addPolicy" />
    </div>
    <div v-if="editDialogVisible" class="custom-modal">
      <div class="custom-modal-content">
        <button @click="closeModal" class="custom-modal-close">&times;</button>
        <h3>Edit Purchase Policy</h3>
        <form @submit.prevent="savePolicy">
          <div class="p-field">
            <label for="type">Type: </label>
            <PrimeDropdown v-model="selectedPolicy.type" :options="policyTypes" optionLabel="label" optionValue="value" placeholder="Select a Type"></PrimeDropdown>
          </div>
          <div v-if="selectedPolicy.type === 'age'">
            <div class="p-field">
              <label for="age">Age: </label>
              <InputNumber v-model="selectedPolicy.age" mode="decimal" :min="0" required />
            </div>
            <div class="p-field">
              <label for="category">Category: </label>
              <PrimeDropdown v-model="selectedPolicy.categoryIndex" :options="categoryOptions" optionLabel="label" optionValue="index" placeholder="Select a Category" required />
            </div>
          </div>
          <div v-else-if="selectedPolicy.type === 'date' || selectedPolicy.type === 'category-date'">
            <div class="p-field">
              <label for="dateTime">Date: </label>
              <Calendar v-model="selectedPolicy.dateTime" required />
            </div>
            <div v-if="selectedPolicy.type === 'category-date'" class="p-field">
              <label for="category">Category: </label>
              <PrimeDropdown v-model="selectedPolicy.categoryIndex" :options="categoryOptions" optionLabel="label" optionValue="index" placeholder="Select a Category" required />
            </div>
          </div>
          <div v-else-if="selectedPolicy.type === 'product-date'">
            <div class="p-field">
              <label for="dateTime">Date: </label>
              <Calendar v-model="selectedPolicy.dateTime" required />
            </div>
            <div class="p-field">
              <label for="productId">Product ID: </label>
              <InputNumber v-model="selectedPolicy.productId" mode="decimal" :min="0" required />
            </div>
          </div>
          <div v-else-if="['cart-max-products', 'cart-min-products', 'cart-min-products-unit'].includes(selectedPolicy.type)">
            <div class="p-field">
              <label for="productId">Product ID: </label>
              <InputNumber v-model="selectedPolicy.productId" mode="decimal" :min="0" required />
            </div>
            <div class="p-field">
              <label for="numOfQuantity">Quantity: </label>
              <InputNumber v-model="selectedPolicy.numOfQuantity" mode="decimal" :min="0" required />
            </div>
          </div>
          <div class="button-wrapper">
            <Button type="button" @click="closeModal" class="custom-button" label="Cancel" />
            <Button type="submit" class="custom-button" label="Save" />
          </div>
        </form>
      </div>
    </div>
    <p-table :value="policies" responsiveLayout="scroll">
      <p-column field="type" header="Type"></p-column>
      <p-column field="details" header="Details" :body="detailsTemplate"></p-column>
      <p-column header="Actions" :body="actionTemplate"></p-column>
    </p-table>
    <p-toast></p-toast>
  </div>
</template>

<script>
import { defineComponent, ref, onMounted, h } from 'vue';
import axios from 'axios';
import { useToast } from 'primevue/usetoast';
import { useRouter } from 'vue-router';
import SiteHeader from '@/components/SiteHeader.vue';
import PrimeButton from 'primevue/button';
import PrimeDataTable from 'primevue/datatable';
import PrimeColumn from 'primevue/column';
import PrimeDropdown from 'primevue/dropdown';
import InputNumber from 'primevue/inputnumber';
import Calendar from 'primevue/calendar';
import Button from 'primevue/button';

export default defineComponent({
  name: 'PurchasePolicyManagement',
  components: {
    SiteHeader,
    PrimeButton,
    'p-table': PrimeDataTable,
    'p-column': PrimeColumn,
    PrimeDropdown,
    InputNumber,
    Calendar,
    Button,
  },
  setup() {
    const router = useRouter();
    const toast = useToast();
    const policies = ref([]);
    const policyTypes = ref([
      { label: 'Age', value: 'age' },
      { label: 'Date', value: 'date' },
      { label: 'Category and Date', value: 'category-date' },
      { label: 'Product and Date', value: 'product-date' },
      { label: 'Cart Max Products', value: 'cart-max-products' },
      { label: 'Cart Min Products', value: 'cart-min-products' },
      { label: 'Cart Min Products Unit', value: 'cart-min-products-unit' },
      { label: 'And', value: 'and' },
      { label: 'Or', value: 'or' },
      { label: 'Conditioning', value: 'conditioning' },
    ]);
    const editDialogVisible = ref(false);
    const selectedPolicy = ref({});
    const storeName = ref(router.currentRoute.value.params.storeName);
    const username = ref(localStorage.getItem('username') || '');
    const token = ref(localStorage.getItem('token') || '');
    const isLoggedIn = ref(!!username.value);
    const categoryOptions = ref([]);

    onMounted(async () => {
      await fetchPolicies();
      await fetchCategories();
    });

    const fetchPolicies = async () => {
      try {
        const response = await axios.get('http://localhost:8082/api/trading/store/purchase-policies/info', {
          params: {
            username: username.value,
            token: token.value,
            storeName: storeName.value
          }
        });
        policies.value = JSON.Stringify(response.data);
      } catch (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: 'Failed to fetch policies', life: 3000 });
      }
    };

    const fetchCategories = async () => {
      try {
        const response = await axios.get('http://localhost:8082/api/trading/categories', {
          params: {
            username: username.value,
            token: token.value,
          }
        });
        const categoryArray = response.data.slice(1, response.data.length - 1).split(',');
        categoryOptions.value = categoryArray.map((category, index) => ({ label: category, value: category, index: index + 1 }));
      } catch (error) {
        console.error('Failed to fetch categories', error);
        toast.add({ severity: 'error', summary: 'Error', detail: 'Failed to fetch categories' });
      }
    };

    const addPolicy = () => {
      selectedPolicy.value = { type: '', age: 0, categoryIndex: null, dateTime: null, productId: 0, numOfQuantity: 0 };
      editDialogVisible.value = true;
    };

    const savePolicy = async () => {
      try {
        let response;
        let formattedDate;
        let formattedCategoryDate;
        let formattedProductDate;
        switch (selectedPolicy.value.type) {
          case 'age':
            response = await axios.post(`http://localhost:8082/api/trading/store/purchase-policies/addPurchasePolicyByAge`, null, {
              params: {
                username: username.value,
                token: token.value,
                storeName: storeName.value,
                ageToCheck: selectedPolicy.value.age,
                category: selectedPolicy.value.categoryIndex
              }
            });
            break;
          case 'date':
            formattedDate = new Date(selectedPolicy.value.dateTime).toISOString().slice(0, -1); 
            response = await axios.post(`http://localhost:8082/api/trading/store/purchase-policies/addPurchasePolicyByDate`, null, {
              params: {
                username: username.value,
                token: token.value,
                storeName: storeName.value,
                dateTime: formattedDate
              }
            });
            break;
          case 'category-date':
            formattedCategoryDate = new Date(selectedPolicy.value.dateTime).toISOString().slice(0, -1); 
            response = await axios.post(`http://localhost:8082/api/trading/store/purchase-policies/addPurchasePolicyByCategoryAndDate`, null, {
              params: {
                username: username.value,
                token: token.value,
                storeName: storeName.value,
                category: selectedPolicy.value.categoryIndex,
                dateTime: formattedCategoryDate
              }
            });
            break;
          case 'product-date':
            formattedProductDate = new Date(selectedPolicy.value.dateTime).toISOString().slice(0, -1); 
            response = await axios.post(`http://localhost:8082/api/trading/store/purchase-policies/addPurchasePolicyByProductAndDate`, null, {
              params: {
                username: username.value,
                token: token.value,
                storeName: storeName.value,
                productId: selectedPolicy.value.productId,
                dateTime: formattedProductDate
              }
            });
            break;
          case 'cart-max-products':
            response = await axios.post(`http://localhost:8082/api/trading/store/purchase-policies/addPurchasePolicyByShoppingCartMaxProductsUnit`, null, {
              params: {
                username: username.value,
                token: token.value,
                storeName: storeName.value,
                productId: selectedPolicy.value.productId,
                numOfQuantity: selectedPolicy.value.numOfQuantity
              }
            });
            break;
          case 'cart-min-products':
            response = await axios.post(`http://localhost:8082/api/trading/store/purchase-policies/addPurchasePolicyByShoppingCartMinProducts`, null, {
              params: {
                username: username.value,
                token: token.value,
                storeName: storeName.value,
                numOfQuantity: selectedPolicy.value.numOfQuantity
              }
            });
            break;
          case 'cart-min-products-unit':
            response = await axios.post(`http://localhost:8082/api/trading/store/purchase-policies/addPurchasePolicyByShoppingCartMinProductsUnit`, null, {
              params: {
                username: username.value,
                token: token.value,
                storeName: storeName.value,
                productId: selectedPolicy.value.productId,
                numOfQuantity: selectedPolicy.value.numOfQuantity
              }
            });
            break;
          case 'and':
            response = await axios.post(`http://localhost:8082/api/trading/store/purchase-policies/addAndPurchasePolicy`, null, {
              params: {
                username: username.value,
                token: token.value,
                storeName: storeName.value
              }
            });
            break;
          case 'or':
            response = await axios.post(`http://localhost:8082/api/trading/store/purchase-policies/addOrPurchasePolicy`, null, {
              params: {
                username: username.value,
                token: token.value,
                storeName: storeName.value
              }
            });
            break;
          case 'conditioning':
            response = await axios.post(`http://localhost:8082/api/trading/store/purchase-policies/addConditioningPurchasePolicy`, null, {
              params: {
                username: username.value,
                token: token.value,
                storeName: storeName.value
              }
            });
            break;
          default:
            toast.add({ severity: 'error', summary: 'Error', detail: 'Invalid policy type' });
            return;
        }
        if (response.data) {
          toast.add({ severity: 'success', summary: 'Success', detail: 'Purchase policy saved', life: 3000 });
          await fetchPolicies();
          closeModal();
        }
      } catch (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: 'Failed to save policy', life: 3000 });
      }
    };

    const closeModal = () => {
      editDialogVisible.value = false;
      selectedPolicy.value = {};
    };

    const detailsTemplate = (policy) => {
      switch (policy.type) {
        case 'age':
          return `Age: ${policy.age}, Category: ${categoryOptions.value.find(cat => cat.index === policy.categoryIndex)?.label || 'N/A'}`;
        case 'date':
          return `Date: ${policy.dateTime}`;
        case 'category-date':
          return `Category: ${categoryOptions.value.find(cat => cat.index === policy.categoryIndex)?.label || 'N/A'}, Date: ${policy.dateTime}`;
        case 'product-date':
          return `Product ID: ${policy.productId}, Date: ${policy.dateTime}`;
        case 'cart-max-products':
          return `Product ID: ${policy.productId}, Max Quantity: ${policy.numOfQuantity}`;
        case 'cart-min-products':
          return `Product ID: ${policy.productId}, Min Quantity: ${policy.numOfQuantity}`;
        case 'cart-min-products-unit':
          return `Product ID: ${policy.productId}, Min Quantity: ${policy.numOfQuantity}`;
        default:
          return '';
      }
    };

    const actionTemplate = (policy) => {
      return h('div', [
        h(Button, {
          icon: "pi pi-pencil",
          class: "p-button-rounded p-button-success",
          onClick: () => editPolicy(policy)
        }),
        h(Button, {
          icon: "pi pi-trash",
          class: "p-button-rounded p-button-danger",
          onClick: () => deletePolicy(policy)
        })
      ]);
    };

    const deletePolicy = async (policy) => {
      try {
        let response;
        switch (policy.type) {
          case 'age':
            response = await axios.delete(`http://localhost:8082/api/trading/store/purchase-policies/removePurchasePolicyByAge`, {
              params: {
                username: username.value,
                token: token.value,
                storeName: storeName.value,
                ageToCheck: policy.age,
                category: policy.categoryIndex
              }
            });
            break;
          case 'date':
            response = await axios.delete(`http://localhost:8082/api/trading/store/purchase-policies/removePurchasePolicyByDate`, {
              params: {
                username: username.value,
                token: token.value,
                storeName: storeName.value,
                dateTime: policy.dateTime
              }
            });
            break;
          case 'category-date':
            response = await axios.delete(`http://localhost:8082/api/trading/store/purchase-policies/removePurchasePolicyByCategoryAndDate`, {
              params: {
                username: username.value,
                token: token.value,
                storeName: storeName.value,
                category: policy.categoryIndex,
                dateTime: policy.dateTime
              }
            });
            break;
          case 'product-date':
            response = await axios.delete(`http://localhost:8082/api/trading/store/purchase-policies/removePurchasePolicyByProductAndDate`, {
              params: {
                username: username.value,
                token: token.value,
                storeName: storeName.value,
                productId: policy.productId,
                dateTime: policy.dateTime
              }
            });
            break;
          case 'cart-max-products':
            response = await axios.delete(`http://localhost:8082/api/trading/store/purchase-policies/removePurchasePolicyByMaxAmountForProductInCart`, {
              params: {
                username: username.value,
                token: token.value,
                storeName: storeName.value,
                productId: policy.productId,
                maxQuantity: policy.numOfQuantity
              }
            });
            break;
          case 'cart-min-products':
            response = await axios.delete(`http://localhost:8082/api/trading/store/purchase-policies/removePurchasePolicyByMinAmountForProductInCart`, {
              params: {
                username: username.value,
                token: token.value,
                storeName: storeName.value,
                productId: policy.productId,
                minQuantity: policy.numOfQuantity
              }
            });
            break;
          case 'cart-min-products-unit':
            response = await axios.delete(`http://localhost:8082/api/trading/store/purchase-policies/removePurchasePolicyByMinAmountForProductInCartByUnits`, {
              params: {
                username: username.value,
                token: token.value,
                storeName: storeName.value,
                productId: policy.productId,
                minQuantity: policy.numOfQuantity
              }
            });
            break;
        }

        if (response.data) {
          toast.add({ severity: 'success', summary: 'Success', detail: 'Purchase policy deleted', life: 3000 });
          fetchPolicies();
        }
      } catch (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: 'Failed to delete policy', life: 3000 });
      }
    };

    const editPolicy = (policy) => {
      selectedPolicy.value = { ...policy };
      editDialogVisible.value = true;
    };

    const logout = () => {
      localStorage.removeItem('username');
      localStorage.removeItem('token');
      router.push({ name: 'Login' });
    };

    return {
      isLoggedIn,
      username,
      logout,
      policies,
      policyTypes,
      editDialogVisible,
      selectedPolicy,
      addPolicy,
      savePolicy,
      closeModal,
      detailsTemplate,
      actionTemplate,
      deletePolicy,
      categoryOptions
    };
  },
});
</script>

<style scoped>
.custom-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.custom-modal-content {
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  width: 90%;
  max-width: 600px;
  position: relative;
}

.custom-modal-close {
  position: absolute;
  top: 10px;
  right: 10px;
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
}

.custom-button {
  margin: 5px;
}

.button-group {
  margin-bottom: 20px;
}

.button-wrapper {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
}

.p-field {
  margin-bottom: 20px;
}
</style>
