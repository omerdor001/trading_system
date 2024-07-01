<template>   
  <div>
    <SiteHeader :isLoggedIn="isLoggedIn" :username="username" @logout="logout" />
    <h2>Suspended Users</h2>
    <p-toast></p-toast>
    <p-table :value="suspendedUsers" responsiveLayout="scroll">
      <p-column field="suspendedUsername" header="Username"></p-column>
      <p-column field="suspendedStart" header="Start of Suspension"></p-column>
      <p-column field="suspensionDays" header="Suspension Days"></p-column>
      <p-column field="suspensionHours" header="Suspension Hours"></p-column>
      <p-column field="suspendedEnd" header="End of Suspension"></p-column>
    </p-table>
    <div class="button-group">
      <PrimeButton type="button" label="Back" class="back-button" @click="goBack" />
    </div>
  </div>
</template>

<script>
import { ref, defineComponent, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';
import SiteHeader from '@/components/SiteHeader.vue';
import { useToast } from 'primevue/usetoast';
import PrimeToast from 'primevue/toast';
import PrimeColumn from 'primevue/column';
import PrimeDataTable from 'primevue/datatable';
import PrimeButton from 'primevue/button';

export default defineComponent({
  name: 'WatchSuspensions',
  components: {
    SiteHeader,
    'p-toast': PrimeToast,
    'p-table': PrimeDataTable,
    'p-column': PrimeColumn,
    PrimeButton
  },
  setup() {
    const suspendedUsers = ref([]);
    const username = ref(localStorage.getItem('username') || '');
    const token = ref(localStorage.getItem('token') || '');
    const toast = useToast();
    const router = useRouter();

    onMounted(async () => {
      try {
        const response = await axios.get('http://localhost:8082/api/trading/watchSuspensions', {
          params: { 
            token: token.value,
            admin: username.value 
          }
        });
          suspendedUsers.value = response.data.map(user => ({
          suspendedUsername: user.Username,
          suspendedStart: new Date(user["Start of suspension"]).toLocaleString(),
          suspensionDays: user["Time of suspension (in days)"],
          suspensionHours: user["Time of suspension (in hours)"],
          suspendedEnd: new Date(user["End of suspension"]).toLocaleString()
        }));
      } catch (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: error.message, life: 3000 });
      }
    });

    const logout = () => {
      router.push('/login');
    };

    const goBack = () => {
      router.push('/');
    };

    return {
      username,
      suspendedUsers,
      logout,
      goBack,
    };
  }
});
</script>

<style scoped>
h2 {
  color: #e67e22;
  text-align: center;
  margin-bottom: 20px;
}

.back-button {
  background-color: #007bff;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 5px;
  cursor: pointer;
  font-weight: bold;
}

.back-button:hover {
  background-color: #0056b3;
}

.p-datatable {
  margin: auto;
  width: 80%;
}

.button-group {
  text-align: center;
  margin-top: 20px;
}
</style>
