<template>
  <div>
    <SiteHeader :isLoggedIn="true" :username="username" @logout="logout" />

    <div class="toggle-container">
      <!-- Toggle Dropdown -->
      <div class="toggle-dropdown">
        <label for="toggleSelect">Select Role:</label>
        <PrimeDropdown v-model="selectedRole" optionLabel="label" :options="roleOptions" placeholder="Select a role" />
      </div>

      <!-- Data Table -->
      <div class="data-table">
        <h2 v-if="selectedRole === 'owners'">Owners of Store: {{ storeName }}</h2>
        <h2 v-if="selectedRole === 'managers'">Managers of Store: {{ storeName }}</h2>

        <!-- Owners Table -->
        <PrimeDataTable v-if="selectedRole === 'owners' && owners.length > 0" :value="owners" responsive :paginator="true" :rows="10">
          <PrimeColumn field="username" header="Username"></PrimeColumn>
          <PrimeColumn field="founder" header="Founder">
            <template #body="slotProps">
              <i :class="['pi', slotProps.data.founder ? 'pi-check' : 'pi-times']"></i>
            </template>
          </PrimeColumn>
        </PrimeDataTable>
        <div v-if="selectedRole === 'owners' && owners.length === 0">
          <p>No owners found.</p>
        </div>

        <!-- Managers Table -->
        <PrimeDataTable v-if="selectedRole === 'managers' && managers.length > 0" :value="managers" responsive :paginator="true" :rows="10">
          <PrimeColumn field="username" header="Username"></PrimeColumn>
          <PrimeColumn field="watch" header="Watch Permission">
            <template #body="slotProps">
              <i :class="['pi', slotProps.data.watch ? 'pi-check' : 'pi-times']"></i>
            </template>
          </PrimeColumn>
          <PrimeColumn field="editSupply" header="Edit Supply Permission">
            <template #body="slotProps">
              <i :class="['pi', slotProps.data.editSupply ? 'pi-check' : 'pi-times']"></i>
            </template>
          </PrimeColumn>
          <PrimeColumn field="editBuyPolicy" header="Edit Buy Policy Permission">
            <template #body="slotProps">
              <i :class="['pi', slotProps.data.editBuyPolicy ? 'pi-check' : 'pi-times']"></i>
            </template>
          </PrimeColumn>
          <PrimeColumn field="editDiscountPolicy" header="Edit Discount Policy Permission">
            <template #body="slotProps">
              <i :class="['pi', slotProps.data.editDiscountPolicy ? 'pi-check' : 'pi-times']"></i>
            </template>
          </PrimeColumn>
        </PrimeDataTable>
        <div v-if="selectedRole === 'managers' && managers.length === 0">
          <p>No managers found.</p>
        </div>
      </div>
    </div>

    <PrimeToast ref="toast" :position="'top-right'" :life="3000"></PrimeToast>
  </div>
</template>

<script>
import axios from 'axios';
import { defineComponent, ref, onMounted } from 'vue';
import SiteHeader from '@/components/SiteHeader.vue';
import { PrimeDropdown } from 'primevue/dropdown';
import { PrimeDataTable, PrimeColumn } from 'primevue/datatable';
import { PrimeToast } from 'primevue/toast';
import { useToast } from 'primevue/usetoast';
import { useRouter } from 'vue-router';

export default defineComponent({
  name: 'WorkersInStore',
  components: {
    SiteHeader,
    PrimeDropdown,
    PrimeDataTable,
    PrimeColumn,
    PrimeToast,
  },
  setup() {
    const router = useRouter();
    const storeName = ref(router.currentRoute.value.params.storeName);
    const managers = ref([]);
    const owners = ref([]);
    const username = localStorage.getItem('username');
    const token = localStorage.getItem('token');
    const toast = useToast();
    const selectedRole = ref('managers'); // Default selected role

    const fetchData = async () => {
      try {
        // Fetch managers
        const managersResponse = await axios.get(`http://localhost:8082/api/trading/store/get-managers`, {
          params: {
            username,
            token,
            storeName: storeName.value,
          }
        });
        managers.value = managersResponse.data.map(manager => ({
          username: manager.username,
          watch: manager.watch,
          editSupply: manager.editSupply,
          editBuyPolicy: manager.editBuyPolicy,
          editDiscountPolicy: manager.editDiscountPolicy
        }));

        // Fetch owners
        const ownersResponse = await axios.get(`http://localhost:8082/api/trading/store/get-owners`, {
          params: {
            username,
            token,
            storeName: storeName.value,
          }
        });
        owners.value = ownersResponse.data.map(owner => ({
          founder: owner.founder,
          username: owner.username,
        }));
      } catch (err) {
        toast.add({ severity: 'error', summary: 'Error', detail: err.message, life: 3000 });
      }
    };

    onMounted(() => {
      fetchData();
    });

    const roleOptions = [
      { label: 'Managers', value: 'managers' },
      { label: 'Owners', value: 'owners' }
    ];

    const logout = () => {
      localStorage.removeItem('isLoggedIn');
      localStorage.removeItem('username');
      router.push('/login');
    };

    return {
      username,
      storeName,
      managers,
      owners,
      selectedRole,
      roleOptions,
      logout,
    };
  },
});
</script>

<style scoped>
.toggle-container {
  display: flex;
  justify-content: space-around;
  align-items: flex-start;
  margin-top: 20px;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.toggle-dropdown {
  margin-bottom: 20px;
}

.toggle-dropdown label {
  margin-right: 10px;
}

.data-table {
  width: 80%;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.p-error {
  margin-top: 20px;
  color: red;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}
</style>
