<template>
  <div>
    <SiteHeader :isLoggedIn="true" :username="username" @logout="logout" />
    <div class="main-content">
      <div class="content">
        <h2>Stores</h2>
        <hr>
        <!-- Stores list -->
        <div class="store-container">
          <div v-for="store in stores" :key="store.id" class="store-item">
            <div class="store-content">
              <h3 class="store-name">Store: {{ store.name }}</h3>
              <PrimeButton label="Enter Store" @click="showStoreDetails(store)" />
            </div>
          </div>
        </div>
        <!-- Detailed store information -->
        <div v-if="selectedStore" class="store-details">
          <h3>Store Details</h3>
          <hr>
          <!-- Left column for description and products -->
          <div class="details-column">
            <p><strong>Description:</strong> {{ selectedStore.description }}</p>
            <h4>Products:</h4>
            <ul>
              <li v-for="product in selectedStore.products" :key="product.id">{{ product.name }}</li>
            </ul>
          </div>
          <!-- Right column for founder, isActive, rating, isOpen -->
          <div class="details-column">
            <p><strong>Founder:</strong> {{ selectedStore.founder }}</p>
            <label><input type="checkbox" v-model="selectedStore.active"> Active</label><br>
            <label><input type="checkbox" v-model="selectedStore.isOpen"> Is Open</label><br>
            <p><strong>Rating:</strong> {{ selectedStore.rating }}</p>
          </div>
          <!-- Manage Products button for store owner and store manager -->
          <div v-if="isStoreOwner || isStoreManager">
            <PrimeButton label="Manage Products" @click="manageProducts(selectedStore.name)" />
          </div>
        </div>
      </div>
    </div>
    <footer>
      <div class="external-links">
        <h3>External Links</h3>
        <ul>
          <li><a href="#">Link 1</a></li>
          <li><a href="#">Link 2</a></li>
          <li><a href="#">Link 3</a></li>
        </ul>
      </div>
    </footer>
  </div>
</template>

<script>
import { defineComponent, ref, onMounted } from 'vue';
import SiteHeader from '@/components/SiteHeader.vue';
import { useRouter } from 'vue-router';
import { Button as PrimeButton } from 'primevue/button';

export default defineComponent({
  name: 'MyStoresIOwn',
  components: {
    SiteHeader,
    PrimeButton
  },
  setup() {
    const router = useRouter();
    const stores = ref([]);
    const username = ref(localStorage.getItem('username') || '');
    const selectedStore = ref(null); // Track selected store for detailed view
    const roles = JSON.parse(localStorage.getItem('roles') || '[]');
    const isStoreOwner = ref(roles.includes('storeOwner'));
    const isStoreManager = ref(roles.includes('storeManager'));

    onMounted(() => {
      // Fetch stores data here
      stores.value = [
        {
          id: 1,
          name: 'store 1',
          description: 'Description for Store 1',
          products: [
            { id: 1, name: 'Product A' },
            { id: 2, name: 'Product B' },
            { id: 3, name: 'Product C' }
          ],
          founder: 'John Doe',
          rating: 4.5,
          active: true,
          isOpen: false
        },
        {
          id: 2,
          name: 'store 2',
          description: 'Description for Store 2',
          products: [
            { id: 4, name: 'Product D' },
            { id: 5, name: 'Product E' }
          ],
          founder: 'Jane Smith',
          rating: 4.2,
          active: false,
          isOpen: true
        }
      ];
    });

    const showStoreDetails = (store) => {
      selectedStore.value = store;
    };

    const manageProducts = (storeName) => {
      router.push({ name: 'ProductList', params: { storeName } });
    };

    const logout = () => {
      localStorage.removeItem('isLoggedIn');
      localStorage.removeItem('username');
      router.push('/login');
    };

    return {
      stores,
      username,
      showStoreDetails,
      selectedStore,
      isStoreOwner,
      isStoreManager,
      manageProducts,
      logout
    };
  }
});
</script>

<style scoped>
.main-content {
  display: flex;
  justify-content: center; /* Center content horizontally */
}

.content {
  width: 80%; /* Adjust width as needed */
  padding: 15px;
}

.store-container {
  display: flex;
  flex-wrap: wrap;
}

.store-item {
  flex: 0 0 calc(33.33% - 20px); /* Adjust item width */
  margin: 10px;
  background-color: #f9f9f9;
  border: 1px solid #ccc;
  border-radius: 5px;
  padding: 10px;
}

.store-item .store-content {
  display: flex;
  flex-direction: column;
}

.store-name {
  margin-bottom: 10px;
  cursor: pointer;
}

.store-details {
  display: grid;
  grid-template-columns: 1fr 1fr; /* Adjust columns as needed */
  gap: 20px; /* Adjust gap between columns */
  margin-top: 15px;
  background-color: #f9f9f9;
  border: 1px solid #ccc;
  border-radius: 5px;
  padding: 15px;
}

.details-column {
  padding: 0 15px;
}

.details-column:first-child {
  border-right: 1px solid #ccc;
}

.external-links {
  margin-top: 15px;
  padding: 15px;
  background-color: #f0f0f0;
  text-align: center;
}

.external-links ul {
  list-style-type: none;
  padding: 0;
}

.external-links ul li {
  margin-bottom: 5px;
}

.external-links ul li a {
  text-decoration: none;
  color: #333;
}
</style>
