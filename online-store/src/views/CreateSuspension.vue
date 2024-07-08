<template>
  <div>
    <SiteHeader :isLoggedIn="true" :username="username" @logout="logout" />
    <div class="suspend-user">
      <h2>Suspend User</h2>
      <form @submit.prevent="submitForm">
        <div class="p-field">
          <label for="toSuspend">Username: </label>
          <InputText v-model="toSuspend" id="toSuspend" required />
        </div>
        <div class="p-field">
          <label for="endSuspension">End-Date: </label>
          <PrimeCalendar v-model="endSuspension" id="endSuspension" showTime showSeconds required />
        </div>
        <div class="button-group">
            <PrimeButton label="Suspend" type="submit" class="p-mt-2" />
        </div>
      </form>
    </div>
    <p-toast></p-toast>
  </div>
</template>

<script>
import axios from 'axios';
import { defineComponent, ref } from 'vue';
import SiteHeader from '@/components/SiteHeader.vue';
import { InputText } from 'primevue/inputtext';
import { PrimeCalendar } from 'primevue/calendar';
import { PrimeButton } from 'primevue/button';
import { useRouter } from 'vue-router';
import { useToast } from 'primevue/usetoast';
import PrimeToast from 'primevue/toast';

export default defineComponent({
  name: 'CreateSuspension',
  components: {
    SiteHeader,
    InputText,
    PrimeCalendar,
    PrimeButton,
    'p-toast': PrimeToast,
  },
  setup() {
    const router = useRouter();
    const toast = useToast();
    const toSuspend = ref('');
    const endSuspension = ref(null);
    const username = localStorage.getItem('username'); 
    const token = localStorage.getItem('token'); 

    const submitForm = async () => {
      try {
        const formattedEndSuspension = new Date(endSuspension.value).toISOString().slice(0, -1); 
        const response = await axios.put('http://localhost:8082/api/trading/suspendUser', null , { 
          params : {
          token: token,
          admin: username,
          toSuspend: toSuspend.value,
          endSuspension: formattedEndSuspension,
          }
        });
        console.log(response.data);
        toast.add({ severity: 'success', summary: 'Success', detail: 'User suspended successfully', life: 3000 });
        toSuspend.value = '';
        endSuspension.value = null;
      } catch (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: error.response.data || 'Failed to suspend user', life: 3000 });
        console.error('Failed to suspend user:', error.message);
      }
    };

    const logout = () => {
      router.push('/login');
    };

    return {
      toSuspend,
      endSuspension,
      username,
      submitForm,
      logout,
    };
  },
});
</script>

<style scoped>
.suspend-user {
  background: #fff;
  padding: 30px;
  border-radius: 10px;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
  width: 400px;
  margin: 0 auto;
  text-align: center;
}

.suspend-user h2 {
  color: #e67e22;
  margin-bottom: 20px;
}

.suspend-user .p-field {
  margin-bottom: 15px;
}

.button-group {
  display: flex;
  justify-content: center; 
  align-items: center;
  margin-top: 20px;
}

.form-group {
  margin-bottom: 15px;
  text-align: left;
}

.form-group label {
  display: block;
  color: #333;
  margin-bottom: 5px;
}

.form-group input,
.form-group textarea {
  width: calc(100% - 20px);
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
  background-color: #f9f9f9;
}

</style>
