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

export default defineComponent({
  name: 'PaymentPage',
  components: {
    SiteHeader,
    PrimeButton
  },
  setup() {
    const router = useRouter();
    const route = useRoute();
    const username = ref(localStorage.getItem('username') || '');
    const address = ref('');

    onMounted(() => {
      address.value = route.query.address || '';
    });

    const completePayment = () => {
      alert('Payment Successful');
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
