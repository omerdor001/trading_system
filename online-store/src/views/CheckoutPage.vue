<template>
  <div>
    <SiteHeader :isLoggedIn="true" :username="username" @logout="logout" />
    <div class="main-content">
      <div class="content">
        <h2>Checkout</h2>
        <p>Please enter your address details:</p>

        <form @submit.prevent="proceedToPayment">
          <div class="form-group">
            <label for="street">Street</label>
            <InputText v-model="street" id="street" placeholder="e.g., 1234 El Street" required />
          </div>
          <div class="form-group">
            <label for="city">City</label>
            <InputText v-model="city" id="city" placeholder="e.g., Springfield" required />
          </div>
          <div class="form-group">
            <label for="state">State</label>
            <InputText v-model="state" id="state" placeholder="e.g., IL" required />
          </div>
          <div class="form-group">
            <label for="postalCode">Postal Code</label>
            <InputText v-model="postalCode" id="postalCode" placeholder="e.g., 62704-5678" required />
          </div>
          <div class="form-group">
            <label for="country">Country</label>
            <InputText v-model="country" id="country" placeholder="e.g., USA" required />
          </div>

          <PrimeButton label="Proceed to Payment" type="submit" class="submit-order-button" />
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
    const street = ref('');
    const city = ref('');
    const state = ref('');
    const postalCode = ref('');
    const country = ref('');

    const proceedToPayment = async () => {
      const address = `${street.value}, ${city.value}, ${state.value}, ${postalCode.value}, ${country.value}`;

      try {
        await axios.post('http://localhost:8082/api/trading/setAddress', null, {
          params: {
            username: username.value,
            token: token.value,
            address: address,
          },
        });
        toast.add({ severity: 'success', summary: 'Success', detail: 'Address saved successfully', life: 3000 });
        router.push({ name: 'PaymentPage', query: { address: address } });
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
      street,
      city,
      state,
      postalCode,
      country,
      proceedToPayment,
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
