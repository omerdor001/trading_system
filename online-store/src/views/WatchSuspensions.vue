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
    const toast = useToast();
    const router = useRouter();

    // Add some test data
    suspendedUsers.value = [
      {
        suspendedUsername: 'user1',
        suspendedStart: new Date().toLocaleString(),
        suspensionDays: 3,
        suspensionHours: 72,
        suspendedEnd: new Date(new Date().getTime() + 3 * 24 * 60 * 60 * 1000).toLocaleString()
      },
      {
        suspendedUsername: 'user2',
        suspendedStart: new Date().toLocaleString(),
        suspensionDays: 1,
        suspensionHours: 24,
        suspendedEnd: new Date(new Date().getTime() + 24 * 60 * 60 * 1000).toLocaleString()
      }
    ];

    onMounted(async () => {
      try {
        const response = await fetch('/api/watchSuspensions?admin=username'); 
        if (!response.ok) {
          throw new Error('Failed to fetch suspension details');
        }
        const data = await response.json();
        suspendedUsers.value = data.map(user => ({
          suspendedUsername: user.suspendedUsername,
          suspendedStart: new Date(user.suspendedStart).toLocaleString(),
          suspensionDays: user.suspensionDays,
          suspensionHours: user.suspensionHours,
          suspendedEnd: new Date(user.suspendedEnd).toLocaleString()
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
