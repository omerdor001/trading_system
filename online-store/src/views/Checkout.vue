<template>
  <div>
    <SiteHeader :isLoggedIn="true" :username="username" @logout="logout" />
    <div class="main-content">
      <div class="content">
        <h2>Checkout</h2>
        <form @submit.prevent="handleCheckout">
          <div class="form-group">
            <label for="address">Delivery Address</label>
            <InputText v-model="address" id="address" required />
          </div>
          <div class="form-group">
            <label for="payment">Payment Details</label>
            <InputText v-model="payment" id="payment" required />
          </div>
          <PrimeButton label="Submit Order" type="submit" class="submit-order-button" />
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { defineComponent, ref } from 'vue';
import SiteHeader from '@/components/SiteHeader.vue';
import PrimeButton from 'primevue/button';
import InputText from 'primevue/inputtext';
import { useRouter } from 'vue-router';

export default defineComponent({
  name: 'CheckoutPage',
  components: {
    SiteHeader,
    PrimeButton,
    InputText
  },
  setup() {
    const router = useRouter();
    const username = ref(localStorage.getItem('username') || '');
    const address = ref('');
    const payment = ref('');

    const handleCheckout = () => {
      console.log('Order submitted with address:', address.value, 'and payment details:', payment.value);
      // Implement order submission logic here
      router.push('/');
    };

    const logout = () => {
      localStorage.removeItem('isLoggedIn');
      localStorage.removeItem('username');
      router.push('/login');
    };

    return {
      username,
      address,
      payment,
      handleCheckout,
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
