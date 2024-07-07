<template>
  <div>
    <SiteHeader :isLoggedIn="isLoggedIn" :username="username" @logout="logout" />
    <h2>Purchase Policy Management</h2>
    <div class="button-group">
      <PrimeButton label="Add New Purchase Policy" icon="pi pi-plus" class="custom-button" @click="addPolicy"/>
    </div>
    <div v-if="editDialogVisible" class="custom-modal">
      <div class="custom-modal-content">
        <button @click="closeModal" class="custom-modal-close">&times;</button>
        <h3>Edit Purchase Policy</h3>
        <form @submit.prevent="savePolicy">
          <div class="p-field">
            <label for="type">Type</label>
            <select id="type" v-model="selectedPolicy.type">
              <option v-for="type in policyTypes" :key="type.value" :value="type.value">{{ type.label }}</option>
            </select>
          </div>
          <div v-if="selectedPolicy.type === 'age'">
            <div class="p-field">
              <label for="age">Age</label>
              <input type="number" id="age" v-model="selectedPolicy.age" min="0" required />
            </div>
            <div class="p-field">
              <label for="category">Category</label>
              <input type="number" id="category" v-model="selectedPolicy.category" min="0" required />
            </div>
          </div>
          <div v-else-if="selectedPolicy.type === 'date' || selectedPolicy.type === 'category-date'">
            <div class="p-field">
              <label for="dateTime">Date</label>
              <input type="date" id="dateTime" v-model="selectedPolicy.dateTime" required />
            </div>
            <div v-if="selectedPolicy.type === 'category-date'" class="p-field">
              <label for="category">Category</label>
              <input type="number" id="category" v-model="selectedPolicy.category" min="0" required />
            </div>
          </div>
          <div v-else-if="selectedPolicy.type === 'product-date'">
          <div class="p-field">
              <label for="dateTime">Date</label>
              <input type="date" id="dateTime" v-model="selectedPolicy.dateTime" required />
            </div>
            <div class="p-field">
              <label for="productId">Product ID</label>
              <input type="number" id="productId" v-model="selectedPolicy.productId" min="0" required />
            </div>
          </div>
          <div v-else-if="['cart-max-products', 'cart-min-products', 'cart-min-products-unit'].includes(selectedPolicy.type)">
            <div class="p-field">
              <label for="productId">Product ID</label>
              <input type="number" id="productId" v-model="selectedPolicy.productId" min="0" required />
            </div>
            <div class="p-field">
              <label for="numOfQuantity">Quantity</label>
              <input type="number" id="numOfQuantity" v-model="selectedPolicy.numOfQuantity" min="0" required />
            </div>
          </div>
          <div class="button-wrapper">
            <button type="button" @click="closeModal" class="custom-button">Cancel</button>
            <button type="submit" class="custom-button">Save</button>
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
import { defineComponent, ref, onMounted } from 'vue';
import axios from 'axios';
import { useToast } from 'primevue/usetoast';
import { useRouter } from 'vue-router';
import SiteHeader from '@/components/SiteHeader.vue';
import PrimeButton from 'primevue/button';
import PrimeDataTable from 'primevue/datatable';
import PrimeColumn from 'primevue/column';

export default defineComponent({
  name: 'PurchasePolicyManagement',
  components: {
    SiteHeader,
    PrimeButton,
    'p-table': PrimeDataTable,
    'p-column': PrimeColumn,
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

    onMounted(async () => {
      await fetchPolicies();
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
        policies.value = response.data;
      } catch (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: 'Failed to fetch policies', life: 3000 });
      }
    };

    const addPolicy = () => {
      console.log('Add Policy Button Clicked');
      selectedPolicy.value = { type: '', age: 0, category: 0, dateTime: null, productId: 0, numOfQuantity: 0 };
      editDialogVisible.value = true;
    };

    const savePolicy = async () => {
      try {
        let response;
        switch (selectedPolicy.value.type) {
          case 'age':
            response = await axios.post(`http://localhost:8082/api/trading/store/purchase-policies/addPurchasePolicyByAge`, {
              username: username.value,
              token: token.value,
              storeName: storeName.value,
              ageToCheck: selectedPolicy.value.age,
              category: selectedPolicy.value.category
            });
            break;
          case 'date':
            response = await axios.post(`http://localhost:8082/api/trading/store/purchase-policies/addPurchasePolicyByDate`, {
              username: username.value,
              token: token.value,
              storeName: storeName.value,
              dateTime: selectedPolicy.value.dateTime
            });
            break;
          case 'category-date':
            response = await axios.post(`http://localhost:8082/api/trading/store/purchase-policies/addPurchasePolicyByCategoryAndDate`, {
              username: username.value,
              token: token.value,
              storeName: storeName.value,
              category: selectedPolicy.value.category,
              dateTime: selectedPolicy.value.dateTime
            });
            break;
          case 'product-date':
            response = await axios.post(`http://localhost:8082/api/trading/store/purchase-policies/addPurchasePolicyByProductAndDate`, {
              username: username.value,
              token: token.value,
              storeName: storeName.value,
              productId: selectedPolicy.value.productId,
            });
            break;
          case 'cart-max-products':
            response = await axios.post(`http://localhost:8082/api/trading/store/purchase-policies/addPurchasePolicyByShoppingCartMaxProductsUnit`, {
              username: username.value,
              token: token.value,
              storeName: storeName.value,
              productId: selectedPolicy.value.productId,
              numOfQuantity: selectedPolicy.value.numOfQuantity
            });
            break;
          case 'cart-min-products':
            response = await axios.post(`http://localhost:8082/api/trading/store/purchase-policies/addPurchasePolicyByShoppingCartMinProducts`, {
              username: username.value,
              token: token.value,
              storeName: storeName.value,
              numOfQuantity: selectedPolicy.value.numOfQuantity
            });
            break;
          case 'cart-min-products-unit':
            response = await axios.post(`http://localhost:8082/api/trading/store/purchase-policies/addPurchasePolicyByShoppingCartMinProductsUnit`, {
              username: username.value,
              token: token.value,
              storeName: storeName.value,
              productId: selectedPolicy.value.productId,
              numOfQuantity: selectedPolicy.value.numOfQuantity
            });
            break;
          default:
            throw new Error('Unsupported policy type');
        }

        if (response.status !== 200) throw new Error('Failed to save policy');
        await fetchPolicies();
        toast.add({ severity: 'success', summary: 'Success', detail: 'Policy saved', life: 3000 });
      } catch (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: 'Failed to save policy', life: 3000 });
      } finally {
        editDialogVisible.value = false;
      }
    };

    const detailsTemplate = (policy) => JSON.stringify(policy.details);
    const actionTemplate = () => {
      return {
        template: `
          <button class="p-button-rounded p-button-text" @click="editPolicy">
            <i class="pi pi-pencil"></i>
          </button>
        `,
        methods: {
          editPolicy() {
            // Implement your logic here
            editDialogVisible.value = true;
          }
        }
      };
    };

    const closeModal = () => {
      editDialogVisible.value = false;
    };

    const logout = () => {
      localStorage.removeItem('username');
      localStorage.removeItem('token');
      router.push('/login');
    };

    return {
      policies,
      policyTypes,
      editDialogVisible,
      selectedPolicy,
      addPolicy,
      savePolicy,
      detailsTemplate,
      actionTemplate,
      isLoggedIn,
      username,
      logout,
      closeModal,
    };
  },
});
</script>

<style scoped>
h2 {
  text-align: center;
  margin-bottom: 20px;
}

.p-field {
  margin-bottom: 1em;
}

.p-datatable {
  margin: auto;
  width: 80%;
}

.button-group {
  text-align: center;
  margin-bottom: 20px;
}

.custom-modal {
  display: block;
  position: fixed;
  z-index: 1;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
  overflow: auto;
  background-color: rgba(0,0,0,0.4);
}

.custom-modal-content {
  background-color: #fefefe;
  margin: 10% auto;
  padding: 20px;
  border: 1px solid #888;
  width: 80%;
}

.custom-modal-close {
  color: #aaa;
  float: right;
  font-size: 28px;
  font-weight: bold;
}

.custom-modal-close:hover,
.custom-modal-close:focus {
  color: black;
  text-decoration: none;
  cursor: pointer;
}

.button-wrapper {
  text-align: center;
  margin-top: 10px;
}

.custom-button {
  margin: 5px;
}
</style>
