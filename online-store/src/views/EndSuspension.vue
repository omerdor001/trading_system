<template>
  <div>
    <SiteHeader :isLoggedIn="true" :username="username" @logout="logout" />
    <div class="suspend-user">
      <h2>End Suspension</h2>
      <form @submit.prevent="submitForm">
        <div class="p-field">
          <label for="toEndSuspend">Username: </label>
          <InputText v-model="toEndSuspend" id="toEndSuspend" required />
        </div>
        <div class="button-group">
          <PrimeButton label="End Suspension" type="submit" class="p-mt-2" />
        </div>
      </form>
      <div v-if="error" class="p-error">{{ error }}</div>
    </div>
    <p-toast></p-toast>
  </div>
</template>

<script>
import axios from 'axios';
import { defineComponent, ref } from 'vue';
import SiteHeader from '@/components/SiteHeader.vue';
import { InputText } from 'primevue/inputtext';
import { PrimeButton } from 'primevue/button';
import { useToast } from 'primevue/usetoast';
import PrimeToast from 'primevue/toast';

export default defineComponent({
  name: 'EndSuspension',
  components: {
    SiteHeader,
    InputText,
    PrimeButton,
    'p-toast': PrimeToast,
  },
  setup() {
    const toast = useToast();
    const toEndSuspend = ref('');
    const username = localStorage.getItem('username'); 
    const token = localStorage.getItem('token'); 

    const submitForm = async () => {
      try {
        const response = await axios.post('http://localhost:8082/api/trading/endSuspendUser', null , { 
          params : {
          token: token,
          admin: username,
          toSuspend: toEndSuspend.value,
          }
        });
        console.log(response.data);
        toast.add({ severity: 'success', summary: 'Success', detail: 'End user suspension successfully', life: 3000 });
        toEndSuspend.value = '';
      } catch (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: error.response.data || 'Failed to end user suspension', life: 3000 });
        console.error('Failed to end user suspension:', error.message);
      }
    };

    return {
      username,
      toEndSuspend,
      submitForm,
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

.p-error {
  margin-top: 20px;
  color: red;
}
</style>
