<template>
  <div>
    <SiteHeader :isLoggedIn="true" :username="username" @logout="logout" />
    <div class="products-container">
      <h2>All Products</h2>
      <PrimeDataTable :value="products" class="p-datatable-striped">
        <PrimeColumn field="product_id" header="ID" sortable></PrimeColumn>
        <PrimeColumn field="product_name" header="Name" sortable></PrimeColumn>
        <PrimeColumn field="product_description" header="Description"></PrimeColumn>
        <PrimeColumn field="product_price" header="Price" sortable></PrimeColumn>
        <PrimeColumn field="product_quantity" header="Quantity" sortable></PrimeColumn>
        <PrimeColumn field="rating" header="Rating" sortable></PrimeColumn>
        <PrimeColumn field="category" header="Category" sortable></PrimeColumn>
        <PrimeColumn header="Keywords" :body="keywordsTemplate"></PrimeColumn>
        <PrimeColumn header="Actions" :body="manageTemplate"></PrimeColumn>
      </PrimeDataTable>
    </div>
    <p-toast></p-toast>
  </div>
</template>

<script>
import { defineComponent, ref, onMounted } from 'vue';
import axios from 'axios';
import SiteHeader from '@/components/SiteHeader.vue';
import { PrimeDataTable, PrimeColumn } from 'primevue/datatable';
import PrimeToast from 'primevue/toast';
import { useToast } from 'primevue/usetoast';
// import PrimeButton from 'primevue/button';
import { useRouter } from 'vue-router';

export default defineComponent({
  name: 'ProductsForStore',
  components: {
    SiteHeader,
    PrimeDataTable,
    PrimeColumn,
    'p-toast': PrimeToast,
    // PrimeButton,
  },
  setup() {
    const router = useRouter();
    const products = ref([]);
    const username = localStorage.getItem('username');
    const token = localStorage.getItem('token');
    const toast = useToast();
    const loading = ref(true);

    onMounted(async () => {
      try {
        const response = await axios.get('http://localhost:8082/api/products', {
          params: {
            username,
            token,
          },
        });
        products.value = response.data;
      } catch (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: error.response?.data || 'Failed to load products', life: 3000 });
      } finally {
        loading.value = false;
      }
    });

    const logout = () => {
      localStorage.removeItem('username');
      localStorage.removeItem('token');
      router.push('/login');
    };

    const manageProduct = (product) => {
      router.push({ name: 'ManageProduct', params: { productId: product.product_id } });
    };

    const keywordsTemplate = (slotProps) => {
      return slotProps.data.keyWords.join(', ');
    };

    const manageTemplate = (slotProps) => {
      return(
        'PrimeButton',
        {
          label: 'Manage',
          icon: 'pi pi-cog',
          class: 'p-button-secondary',
          onClick: () => manageProduct(slotProps.data),
        }
      );
    };

    return {
      products,
      username,
      token,
      logout,
      loading,
      manageProduct,
      keywordsTemplate,
      manageTemplate,
    };
  },
});
</script>

<style scoped>
.products-container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 80vh;
  background-color: #f9f9f9;
  padding: 20px;
}

.products-container h2 {
  color: #e67e22;
  margin-bottom: 20px;
  text-align: center;
}

.p-datatable {
  width: 100%;
  max-width: 1200px;
  margin-top: 20px;
  border: 1px solid #ddd;
  border-radius: 10px;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
}

.p-datatable-striped .p-datatable-tbody > tr:nth-child(even) {
  background-color: #f9f9f9;
}

.p-datatable-header,
.p-datatable-thead > tr > th {
  background-color: #007ad9;
  color: #fff;
}

.p-datatable-tbody > tr > td {
  padding: 10px;
  text-align: left;
}

.p-datatable-tbody > tr > td.sortable-column {
  cursor: pointer;
}

.p-datatable-tbody > tr > td.sortable-column:hover {
  background-color: #f1f1f1;
}

.p-datatable-tbody > tr:hover {
  background-color: #e1f5fe;
}

.p-datatable-tbody > tr > td:last-child {
  border-right: 0;
}

.p-datatable-tbody > tr > td:first-child {
  border-left: 0;
}
</style>
