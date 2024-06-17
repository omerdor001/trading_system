<template>
  <div class="suspend-user">
    <h2>Suspend User</h2>
    <form @submit.prevent="submitForm">
      <div class="p-field">
        <label for="admin">Admin Username:</label>
        <InputText v-model="admin" id="admin" required />
      </div>
      <div class="p-field">
        <label for="toSuspend">User to Suspend:</label>
        <InputText v-model="toSuspend" id="toSuspend" required />
      </div>
      <div class="p-field">
        <label for="endSuspention">End of Suspension:</label>
        <PrimeCalendar v-model="endSuspention" id="endSuspention" showTime showSeconds required />
      </div>
      <PrimeButton label="Suspend User" type="submit" class="p-mt-2" />
    </form>
    <div v-if="error" class="p-error">{{ error }}</div>
  </div>
</template>

<script>
import axios from 'axios';
import { defineComponent, ref } from 'vue';
import { InputText } from 'primevue/inputtext';
import { PrimeCalendar } from 'primevue/calendar';
import { PrimeButton } from 'primevue/button';

export default defineComponent {
  name: 'CreateSuspension',
  components: {
    InputText,
    PrimeCalendar,
    PrimeButton,
  },
  setup() {
    const admin = ref('');
    const toSuspend = ref('');
    const endSuspention = ref(null);
    const error = ref(null);

    const submitForm = async () => {
      try {
        error.value = null;
        await axios.post('/api/suspendUser', {
          admin: admin.value,
          toSuspend: toSuspend.value,
          endSuspention: endSuspention.value,
        });
        alert('User suspended successfully');
      } catch (err) {
        error.value = err.response.data.message || 'An error occurred';
      }
    };

    return {
      admin,
      toSuspend,
      endSuspention,
      error,
      submitForm,
    };
  },
};
</script>

<style scoped>
.suspend-user {
  max-width: 400px;
  margin: 0 auto;
  padding: 20px;
  border: 1px solid #ccc;
  border-radius: 5px;
}
.suspend-user h2 {
  margin-bottom: 20px;
}
.suspend-user .p-field {
  margin-bottom: 15px;
}
.p-error {
  margin-top: 20px;
  color: red;
}
</style>
