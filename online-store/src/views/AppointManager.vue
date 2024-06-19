<template>
  <div>
    <SiteHeader :isLoggedIn="isLoggedIn" :username="username" @logout="logout" />
    <div class="appoint-manager">
      <h2>Appoint Manager</h2>
      <form @submit.prevent="appointManager">
        <div class="p-field">
          <label for="newManager">New Manager: </label>
          <InputText id="newManager" v-model="newManager" required />
        </div>
        <div class="p-field">
          <label for="storeNameId">Store Name: </label>
          <InputText id="storeNameId" v-model="storeNameId" required />
        </div>
        <div class="p-field">
          <label>Permissions:</label>
          <div>
            <ToggleButton id="watch" v-model="watch" onIcon="pi pi-check" offIcon="pi pi-times" onLabel="Watch" offLabel="Watch" />
            <ToggleButton id="editSupply" v-model="editSupply" onIcon="pi pi-check" offIcon="pi pi-times" onLabel="Edit Supply" offLabel="Edit Supply" />
            <ToggleButton id="editBuyPolicy" v-model="editBuyPolicy" onIcon="pi pi-check" offIcon="pi pi-times" onLabel="Edit Buy Policy" offLabel="Edit Buy Policy" />
            <ToggleButton id="editDiscountPolicy" v-model="editDiscountPolicy" onIcon="pi pi-check" offIcon="pi pi-times" onLabel="Edit Discount Policy" offLabel="Edit Discount Policy" />
          </div>
        </div>
        <div class="button-group">
          <PrimeButton label="Appoint Manager" type="submit" class="submit-button" />
        </div>
      </form>
      <PrimeToast ref="toast" position="top-right" :life="3000"></PrimeToast>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import { defineComponent, ref } from 'vue';
import SiteHeader from '@/components/SiteHeader.vue';
import { InputText } from 'primevue/inputtext';
import { Button as PrimeButton } from 'primevue/button';
import { Toast as PrimeToast } from 'primevue/toast';
import { ToggleButton } from 'primevue/togglebutton';
import { useRouter } from 'vue-router';

export default defineComponent({
  name: 'AppointManager',
  components: {
    SiteHeader,
    InputText,
    PrimeButton,
    PrimeToast,
    ToggleButton,
  },
  setup() {
    const router = useRouter();
    const newManager = ref('');
    const storeNameId = ref('');
    const watch = ref(false);
    const editSupply = ref(false);
    const editBuyPolicy = ref(false);
    const editDiscountPolicy = ref(false);
    const username = ref(localStorage.getItem('username') || '');
    const isLoggedIn = ref(!!username.value);

    const appointManager = async () => {
      try {
        const response = await axios.post('/api/appoint-manager', {
          appoint: username.value,
          newManager: newManager.value,
          storeNameId: storeNameId.value,
          watch: watch.value,
          editSupply: editSupply.value,
          editBuyPolicy: editBuyPolicy.value,
          editDiscountPolicy: editDiscountPolicy.value,
        });
        showSuccessToast(response.data.message);
        newManager.value = '';
        storeNameId.value = '';
        watch.value = false;
        editSupply.value = false;
        editBuyPolicy.value = false;
        editDiscountPolicy.value = false;
      } catch (err) {
        showErrorToast(err.response?.data?.message || 'An error occurred');
      }
    };

    const logout = () => {
      localStorage.removeItem('username');
      router.push('/login');
    };

    const showSuccessToast = (message) => {
      const toast = ref.$refs.toast;
      toast.add({
        severity: 'success',
        summary: 'Success',
        detail: message,
        life: 3000,
      });
    };

    const showErrorToast = (message) => {
      const toast = ref.$refs.toast;
      toast.add({
        severity: 'error',
        summary: 'Error',
        detail: message,
        life: 5000,
      });
    };

    return {
      username,
      isLoggedIn,
      newManager,
      storeNameId,
      watch,
      editSupply,
      editBuyPolicy,
      editDiscountPolicy,
      appointManager,
      logout,
    };
  },
});
</script>

<style scoped>
.appoint-manager {
  background: #fff;
  padding: 30px;
  border-radius: 10px;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
  width: 400px;
  margin: 0 auto;
  text-align: center;
}

.appoint-manager h2 {
  color: #e67e22;
  margin-bottom: 20px;
}

.appoint-manager .p-field {
  margin-bottom: 15px;
}

.button-group {
  display: flex;
  justify-content: center;
  justify-content: space-between;
  margin-top: 20px;
}

.submit-button {
  background-color: #e67e22 !important;
  border: none;
  padding: 10px 20px;
  color: white;
  border-radius: 5px;
  cursor: pointer;
  font-weight: bold;
}

.submit-button:hover {
  background-color: #d35400 !important;
}

.p-error {
  margin-top: 20px;
  color: red;
}
</style>
