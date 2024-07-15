<template>
  <div>
    <SiteHeader :isLoggedIn="true" :username="username" @logout="logout" />
    <div class="main-content">
      <div class="content">
        <h2>Checkout</h2>
        <form @submit.prevent="proceedToCheckout">
          <div class="form-group">
            <label for="address">Delivery Address</label>
            <InputText v-model="address" id="address" required />
          </div>
          <PrimeButton label="Proceed to Checkout" type="submit" class="submit-order-button" />
        </form>
      </div>
    </div>
    <PrimeToast ref="toast" />
  </div>
</template>

<script>
import { defineComponent, ref } from 'vue';
import axios from 'axios';
import SiteHeader from '@/components/SiteHeader.vue';
import PrimeButton from 'primevue/button';
import InputText from 'primevue/inputtext';
import PrimeToast from 'primevue/toast';
import { useRouter } from 'vue-router';
import { useToast } from 'primevue/usetoast';

export default defineComponent({
  name: 'CheckoutPage',
  components: {
    SiteHeader,
    PrimeButton,
    InputText,
    PrimeToast
  },
  setup() {
    const router = useRouter();
    const toast = useToast();
    const username = ref(localStorage.getItem('username') || '');
    const token = ref(localStorage.getItem('token') || '');
    const address = ref('');

    const proceedToCheckout = async () => {
      try {
        await axios.post('http://localhost:8082/api/trading/setAddress', null, {
          params: {
            username: username.value,
            token: token.value,
            address: address.value,
          },
        });
        toast.add({ severity: 'success', summary: 'Success', detail: 'Address saved successfully', life: 3000 });
        router.push({ name: 'PaymentPage', query: { address: address.value } });
      } catch (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: error.response?.data || 'Failed to save address', life: 3000 });
      }
    };

    const logout = () => {
      localStorage.removeItem('isLoggedIn');
      localStorage.removeItem('username');
      localStorage.removeItem('token');
      router.push('/login');
    };

    return {
      username,
      address,
      proceedToCheckout,
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
.form-group {
  margin-bottom: 20px;
}
.submit-order-button {
  background-color: #e67e22 !important;
  border: none !important;
  padding: 10px 20px !important;
  cursor: pointer !important;
  color: white !important;
  border-radius: 5px !important;
  font-weight: bold !important;
  transition: background-color 0.3s !important;
}
.submit-order-button:hover {
  background-color: #d35400 !important;
}
</style>
