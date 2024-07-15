<template>
  <div>
    <SiteHeader :isLoggedIn="true" :username="username" @logout="logout" />
    <div class="main-content">
      <div class="content">
        <h2>Payment Page</h2>
        <p>Delivery Address: {{ address }}</p>
        <p>Total Amount: {{ totalAmount }} {{ currency }}</p>

        <form @submit.prevent="completePayment">
          <div class="form-group">
            <label for="currency">Currency</label>
            <InputText v-model="currency" id="currency" placeholder="e.g., USD" required />
          </div>
          <div class="form-group">
            <label for="cardNumber">Card Number</label>
            <InputText v-model="cardNumber" id="cardNumber" placeholder="e.g., 4111111111111111" required />
          </div>
          <div class="form-group">
            <label for="month">Expiration Month</label>
            <InputText v-model="month" id="month" placeholder="e.g., 12" required />
          </div>
          <div class="form-group">
            <label for="year">Expiration Year</label>
            <InputText v-model="year" id="year" placeholder="e.g., 2025" required />
          </div>
          <div class="form-group">
            <label for="holder">Card Holder</label>
            <InputText v-model="holder" id="holder" placeholder="e.g., John Doe" required />
          </div>
          <div class="form-group">
            <label for="ccv">CCV</label>
            <InputText v-model="ccv" id="ccv" placeholder="e.g., 123" required />
          </div>
          <div class="form-group">
            <label for="id">ID</label>
            <InputText v-model="id" id="id" placeholder="e.g., 123456789" required />
          </div>

          <PrimeButton label="Complete Payment" type="submit" class="complete-payment-button" />
        </form>
      </div>
    </div>
    <PrimeToast ref="toast" />
  </div>
</template>

<script>
import { defineComponent, ref, onMounted } from 'vue';
import axios from 'axios';
import SiteHeader from '@/components/SiteHeader.vue';
import PrimeButton from 'primevue/button';
import InputText from 'primevue/inputtext';
import PrimeToast from 'primevue/toast';
import { useRouter, useRoute } from 'vue-router';
import { useToast } from 'primevue/usetoast';

export default defineComponent({
  name: 'PaymentPage',
  components: {
    SiteHeader,
    PrimeButton,
    InputText,
    PrimeToast
  },
  setup() {
    const router = useRouter();
    const route = useRoute();
    const username = localStorage.getItem('username');
    const token = localStorage.getItem('token');
    const address = ref(route.query.address || '');
    const totalAmount = ref(0);
    const currency = ref('USD'); // Default currency
    const cardNumber = ref('');
    const month = ref('');
    const year = ref('');
    const holder = ref('');
    const ccv = ref('');
    const id = ref('');
    const toast = useToast();

    const fetchTotalAmount = async () => {
      try {
        const response = await axios.get('http://localhost:8082/api/trading/cart/price/calculate', {
          params: {
            username,
            token
          }
        });
        totalAmount.value = parseFloat(response.data); // Assuming the response is a plain number string
        // Set the currency if returned from the backend, otherwise keep it default
      } catch (error) {
        console.error('Error fetching total amount:', error);
        toast.add({ severity: 'error', summary: 'Error', detail: error.response?.data || 'Failed to fetch total amount', life: 3000 });
      }
    };

    const completePayment = async () => {
      try {
        const response = await axios.post('http://localhost:8082/api/trading/purchase/approve', null, {
          params: {
            username,
            token,
            address: address.value,
            amount: totalAmount.value,
            currency: currency.value,
            cardNumber: cardNumber.value,
            month: month.value,
            year: year.value,
            holder: holder.value,
            ccv: ccv.value,
            id: id.value
          }
        });
        toast.add({ severity: 'success', summary: 'Success', detail: response.data, life: 3000 });
        router.push('/');
      } catch (error) {
        console.error('Error completing payment:', error);
        toast.add({ severity: 'error', summary: 'Error', detail: error.response?.data || 'Failed to complete payment', life: 3000 });
      }
    };

    const logout = () => {
      localStorage.removeItem('isLoggedIn');
      localStorage.removeItem('username');
      localStorage.removeItem('token');
      router.push('/login');
    };

    onMounted(() => {
      fetchTotalAmount();
    });

    return {
      username,
      address,
      totalAmount,
      currency,
      cardNumber,
      month,
      year,
      holder,
      ccv,
      id,
      completePayment,
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
