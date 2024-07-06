<template>
  <div>
    <SiteHeader :isLoggedIn="isLoggedIn" :username="username" @logout="logout" />
    <div class="purchase-management">
      <h2>Purchase Policy Management</h2>
      <PrimeButton label="Add New Purchase Policy" icon="pi pi-plus" @click="addPolicy" />
      <PrimeDataTable :value="policies" responsiveLayout="scroll" class="p-mt-3">
        <PrimeColumn field="type" header="Type" :body="typeTemplate"></PrimeColumn>
        <PrimeColumn field="details" header="Details" :body="detailsTemplate"></PrimeColumn>
        <PrimeColumn header="Actions" :body="actionTemplate"></PrimeColumn>
      </PrimeDataTable>
      <PrimeDialog header="Edit Purchase Policy" v-model="editDialogVisible">
        <div class="p-fluid">
          <div class="p-field">
            <label for="type">Type</label>
            <PrimeDropdown id="type" v-model="selectedPolicy.type" :options="policyTypes" optionLabel="label" optionValue="value" />
          </div>
          <div v-if="selectedPolicy.type === 'age'">
            <div class="p-field">
              <label for="age">Age</label>
              <InputNumber id="age" v-model="selectedPolicy.age" :min="0" />
            </div>
            <div class="p-field">
              <label for="category">Category</label>
              <InputNumber id="category" v-model="selectedPolicy.category" :min="0" />
            </div>
          </div>
          <div v-else-if="selectedPolicy.type === 'date' || selectedPolicy.type === 'category-date' || selectedPolicy.type === 'product-date'">
            <div class="p-field">
              <label for="dateTime">Date</label>
              <InputNumber id="dateTime" v-model="selectedPolicy.dateTime" />
            </div>
            <div v-if="selectedPolicy.type !== 'date'" class="p-field">
              <label for="categoryOrProduct">Category/Product</label>
              <InputNumber id="categoryOrProduct" v-model="selectedPolicy.categoryOrProduct" :min="0" />
            </div>
          </div>
          <div v-else-if="selectedPolicy.type === 'cart-max-products' || selectedPolicy.type === 'cart-min-products' || selectedPolicy.type === 'cart-min-products-unit'">
            <div class="p-field">
              <label for="productId">Product ID</label>
              <InputNumber id="productId" v-model="selectedPolicy.productId" :min="0" />
            </div>
            <div class="p-field">
              <label for="numOfQuantity">Quantity</label>
              <InputNumber id="numOfQuantity" v-model="selectedPolicy.numOfQuantity" :min="0" />
            </div>
          </div>
        </div>
        <template #footer>
          <PrimeButton label="Cancel" icon="pi pi-times" @click="editDialogVisible = false" class="p-button-text" />
          <PrimeButton label="Save" icon="pi pi-check" @click="savePolicy" />
        </template>
      </PrimeDialog>
      <PrimeToast ref="toast" position="top-right" :life="3000"></PrimeToast>
    </div>
  </div>
</template>

<script>
import { defineComponent, ref, onMounted, h } from 'vue';
import { useToast } from 'primevue/usetoast';
import { useRouter } from 'vue-router';
import SiteHeader from '@/components/SiteHeader.vue';
import PrimeButton from 'primevue/button';
import PrimeDataTable from 'primevue/datatable';
import PrimeColumn from 'primevue/column';
import PrimeDropdown from 'primevue/dropdown';
import InputNumber from 'primevue/inputnumber';
import PrimeDialog from 'primevue/dialog';
import PrimeToast from 'primevue/toast';

export default defineComponent({
  name: 'EditPurchasePolicy',
  components: {
    SiteHeader,
    PrimeButton,
    PrimeDataTable,
    PrimeColumn,
    PrimeDropdown,
    InputNumber,
    PrimeDialog,
    PrimeToast,
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
    const username = ref(localStorage.getItem('username'));
    const isLoggedIn = ref(!!username.value);

    onMounted(async () => {
      await fetchPolicies();
    });

    const fetchPolicies = async () => {
      try {
        const response = await fetch(`/api/store/${storeName.value}/purchase-policies`);
        policies.value = await response.json();
      } catch (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: 'Failed to fetch policies', life: 3000 });
      }
    };

    const addPolicy = () => {
      selectedPolicy.value = { type: '', age: 0, category: 0, dateTime: null, productId: 0, numOfQuantity: 0 };
      editDialogVisible.value = true;
    };

    const savePolicy = async () => {
      try {
        const response = await fetch(`/api/store/${storeName.value}/purchase-policies`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(selectedPolicy.value)
        });
        if (!response.ok) throw new Error('Failed to save policy');
        await fetchPolicies();
        toast.add({ severity: 'success', summary: 'Success', detail: 'Policy saved', life: 3000 });
      } catch (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: 'Failed to save policy', life: 3000 });
      } finally {
        editDialogVisible.value = false;
      }
    };

    const typeTemplate = (policy) => policy.type;
    const detailsTemplate = (policy) => JSON.stringify(policy.details);

    const actionTemplate = (policy) => {
      return h(PrimeButton, {
        icon: 'pi pi-pencil',
        class: 'p-button-rounded p-button-text',
        onClick: () => editPolicy(policy)
      });
    };

    const editPolicy = (policy) => {
      selectedPolicy.value = { ...policy };
      editDialogVisible.value = true;
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
      typeTemplate,
      detailsTemplate,
      actionTemplate,
      isLoggedIn,
      username,
      logout,
    };
  },
});
</script>

<style scoped>
.purchase-management {
  padding: 20px;
}

.p-field {
  margin-bottom: 1em;
}

.p-mt-3 {
  margin-top: 1.5em;
}
</style>
