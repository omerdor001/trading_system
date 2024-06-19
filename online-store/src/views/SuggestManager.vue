<template>
  <div>
    <SiteHeader :isLoggedIn="isLoggedIn" :username="username" @logout="logout" />
    <div class="suggest-manage">
      <h2>Suggest Manager</h2>
      <form @submit.prevent="suggestManage">
        <div class="p-field">
          <label for="newManager">New Manager: </label>
          <InputText id="newManager" v-model="newManager" required />
        </div>
        <div class="p-field">
          <label for="storeNameId">Store Name: </label>
          <InputText id="storeNameId" v-model="storeNameId" required />
        </div>
        <div class="p-field">
          <label>Options:</label>
          <div class="toggle-buttons-container">
            <ToggleButton id="watch" v-model="watch" onIcon="pi pi-check" offIcon="pi pi-times" onLabel="Watch" offLabel="Watch" class="small-toggle" />
            <ToggleButton id="editSupply" v-model="editSupply" onIcon="pi pi-check" offIcon="pi pi-times" onLabel="Edit Supply" offLabel="Edit Supply" class="small-toggle" />
            <ToggleButton id="editBuyPolicy" v-model="editBuyPolicy" onIcon="pi pi-check" offIcon="pi pi-times" onLabel="Edit Buy Policy" offLabel="Edit Buy Policy" class="small-toggle" />
            <ToggleButton id="editDiscountPolicy" v-model="editDiscountPolicy" onIcon="pi pi-check" offIcon="pi pi-times" onLabel="Edit Discount Policy" offLabel="Edit Discount Policy" class="small-toggle" />
          </div>
        </div>
        <div class="button-group">
          <PrimeButton label="Submit" type="submit" class="p-mt-2 submit-button" />
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
import { ToggleButton } from 'primevue/togglebutton'; // Import ToggleButton from PrimeVue
import { Toast as PrimeToast } from 'primevue/toast';
import { useRouter } from 'vue-router';

export default defineComponent({
  name: 'SuggestManage',
  components: {
    SiteHeader,
    InputText,
    PrimeButton,
    PrimeToast,
    ToggleButton // Use ToggleButton instead of Checkbox
  },
  setup() {
    const router = useRouter();
    const newManager = ref('');
    const storeNameId = ref('');
    const watch = ref(false);
    const editSupply = ref(false);
    const editBuyPolicy = ref(false);
    const editDiscountPolicy = ref(false);
    const error = ref(null);
    const username = ref(localStorage.getItem('username') || '');
    const isLoggedIn = ref(!!username.value);

    const suggestManage = async () => {
      try {
        error.value = null;
        const response = await axios.post('/api/suggest-manage', {
          newManager: newManager.value,
          storeNameId: storeNameId.value,
          watch: watch.value,
          editSupply: editSupply.value,
          editBuyPolicy: editBuyPolicy.value,
          editDiscountPolicy: editDiscountPolicy.value
          // Add other options here
        });

        // Display success toast
        showSuccessToast(response.data.message);
        
        // Reset form fields
        newManager.value = '';
        storeNameId.value = '';
        watch.value = false;
        editSupply.value = false;
        editBuyPolicy.value = false;
        editDiscountPolicy.value = false;
        // Reset other options here
      } catch (err) {
        error.value = err.response?.data?.message || 'An error occurred';
        // Display error toast
        showErrorToast(error.value);
      }
    };

    const logout = () => {
      localStorage.removeItem('username');
      router.push('/login');
    };

    const showSuccessToast = (message) => {
      // Display success toast
      showCustomToast('success', 'Success', message);
    };

    const showErrorToast = (message) => {
      // Display error toast
      showCustomToast('error', 'Error', message);
    };

    const showCustomToast = (severity, summary, detail) => {
      // Display custom toast
      const toast = ref.$refs.toast;
      toast.add({
        severity: severity,
        summary: summary,
        detail: detail,
        life: 3000
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
      error,
      suggestManage,
      logout,
    };
  },
});
</script>

<style scoped>
.suggest-manage {
  background: #fff;
  padding: 30px;
  border-radius: 10px;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
  width: 400px;
  margin: 0 auto;
  text-align: center;
}

.suggest-manage h2 {
  color: #e67e22;
  margin-bottom: 20px;
}

.suggest-manage .p-field {
  margin-bottom: 15px;
}

.button-group {
  display: flex;
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

.small-toggle .p-button {
  padding: 0.5rem 1rem; /* Adjust padding for smaller size */
  font-size: 0.9rem; /* Adjust font size for smaller size */
}
</style>
