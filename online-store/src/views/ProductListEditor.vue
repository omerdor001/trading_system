<template>
  <div>
    <SiteHeader />
    <div class="container">
      <h2>Choose Product To Manage</h2>
      <div class="product-list">
        <div v-for="product in products" :key="product.id" class="product-item">
          <div class="product-image">150 x 150</div>
          <div class="product-name">{{ product.name }}</div>
          <PrimeButton label="Show More" @click="showProductDetails(product.id)" />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { onMounted, ref } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import SiteHeader from '@/components/SiteHeader.vue';
import { Button as PrimeButton } from 'primevue/button';
import StoreViewModel from '@/ViewModel/StoreViewModel';

export default {
  name: 'ProductList',
  components: {
    SiteHeader,
    PrimeButton
  },
  setup() {
    const products = ref([]);
    const route = useRoute();
    const router = useRouter();

    onMounted(async () => {
      const storeName = route.params.storeName;
      const userName = localStorage.getItem('username');
      const token = localStorage.getItem('token');
      try {
        await StoreViewModel.actions.getStoreProducts(userName, token, storeName);
        products.value = StoreViewModel.getters.getProducts();
      } catch (error) {
        console.error('Error fetching products:', error);
      }
    });

    const showProductDetails = (productId) => {
      const storeName = route.params.storeName;
      router.push(`/product-management/${storeName}/${productId}`);
    };

    return {
      products,
      showProductDetails
    };
  }
};
</script>

<style scoped>
.container {
  padding: 20px;
}
.product-list {
  display: flex;
  gap: 20px;
}
.product-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}
.product-image {
  width: 150px;
  height: 150px;
  background-color: #ccc;
  display: flex;
  align-items: center;
  justify-content: center;
}
.product-name {
  margin-top: 10px;
}
</style>
