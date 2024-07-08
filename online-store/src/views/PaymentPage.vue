<template>
  <div>
    <SiteHeader :isLoggedIn="true" :username="username" @logout="logout" />
    <div class="main-content">
      <div class="content">
        <h2>Payment Page</h2>
        <p>This is a sample for payments. Delivery Address: {{ address }}</p>
        <PrimeButton label="Complete Payment" @click="completePayment" class="complete-payment-button" />
      </div>
    </div>
  </div>
</template>

<script>
import { defineComponent, ref, onMounted } from 'vue';
import SiteHeader from '@/components/SiteHeader.vue';
import PrimeButton from 'primevue/button';
import { useRouter, useRoute } from 'vue-router';
import axios from 'axios';
import { useToast } from 'primevue/usetoast';

export default defineComponent({
  name: 'PaymentPage',
  components: {
    SiteHeader,
    PrimeButton
  },
  setup() {
    const router = useRouter();
    const route = useRoute();
    const username = localStorage.getItem('username') ;
    const token = localStorage.getItem('token');
    const address = ref('');
    const toast = useToast();


    const setAddressCustomer = async() => {
      try {

        const response = await axios.post('http://localhost:8082/api/trading/setAddress', null, { 
          params: {
            username : username,
            token : token,
            address : address.value
          } });
          toast.add({ severity: 'success', summary: 'Success', detail: response.data, life: 3000 });

      } catch (error) {
        console.error('Error set address:', error);

      }
    };
    onMounted(() => {
      address.value = route.query.address || '';
      setAddressCustomer()
    });

    const completePayment = async () => {

      try {

const response = await axios.post('http://localhost:8082/api/trading/purchase/approve', null, { 
  params: {
    username : username,
    token : token,
  } });
  toast.add({ severity: 'success', summary: 'Success', detail: response.data , life: 3000 });
} catch (error) {
console.error('Error fetching products:', error);
toast.add({ severity: 'error', summary: 'error', detail: error.response?.data || 'Failed to approve purchase' });

}      router.push('/');
    };

    const logout = () => {
      localStorage.removeItem('isLoggedIn');
      localStorage.removeItem('username');
      router.push('/login');
    };

    return {
      username,
      address,
      setAddressCustomer,
      completePayment,
      token,
      logout
    };
  }
});
</script>

<style scoped>
.main-content {
  display: flex;
  justify-content: center;
  padding: 20px;
}
.content {
  flex: 2;
  padding: 20px;
}
.complete-payment-button {
  background-color: #e67e22 !important;
  border: none !important;
  padding: 10px 20px !important;
  cursor: pointer !important;
  color: white !important;
  border-radius: 5px !important;
  font-weight: bold !important;
  transition: background-color 0.3s !important;
}
.complete-payment-button:hover {
  background-color: #d35400 !important;
}
</style>
