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
        <h2 v-if="selectedRole.value === 'owners'">Owners of Store: {{ storeName }}</h2>
        <h2 v-if="selectedRole.value === 'managers'">Managers of Store: {{ storeName }}</h2>

        <!-- Owners Table -->
        <PrimeDataTable v-if="selectedRole.value === 'owners' && owners.length > 0" :value="owners" responsive :paginator="true" :rows="10">
          <PrimeColumn field="username" header="Username"></PrimeColumn>
          <PrimeColumn field="founder" header="Founder">
            <template #body="slotProps">
              <i :class="['pi', slotProps.data.founder ? 'pi-check' : 'pi-times']"></i>
            </template>
          </PrimeColumn>
        </PrimeDataTable>
        <div v-if="selectedRole.value === 'owners' && owners.length === 0">
          <p>No owners found.</p>
        </div>

        <!-- Managers Table -->
        <PrimeDataTable v-if="selectedRole.value === 'managers' && managers.length > 0" :value="managers" responsive :paginator="true" :rows="10">
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
          <PrimeColumn field="acceptBids" header="Accept Bids">
            <template #body="slotProps">
              <i :class="['pi', slotProps.data.acceptBids ? 'pi-check' : 'pi-times']"></i>
            </template>
          </PrimeColumn>
          <PrimeColumn field="createLottery" header="Create Lottery">
            <template #body="slotProps">
              <i :class="['pi', slotProps.data.createLottery ? 'pi-check' : 'pi-times']"></i>
            </template>
          </PrimeColumn>
          <PrimeColumn header="Actions">
            <template #body="slotProps">
              <PrimeButton icon="pi pi-pencil" @click="editPermissions(slotProps.data)" />
            </template>
          </PrimeColumn>
        </PrimeDataTable>
        <div v-if="selectedRole.value === 'managers' && managers.length === 0">
          <p>No managers found.</p>
        </div>
      </div>
    </div>

    <!-- Regular Dialog for Edit Permissions -->
    <div class="edit-dialog" v-if="editDialogVisible">
      <div class="edit-permissions-form">
        <h3>Edit Permissions for: {{ selectedManager.username }}</h3>
        <div>
          <label>
            <input type="checkbox" v-model="selectedManager.watch" /> Watch Permission
          </label>
        </div>
        <div>
          <label>
            <input type="checkbox" v-model="selectedManager.editSupply" /> Edit Supply Permission
          </label>
        </div>
        <div>
          <label>
            <input type="checkbox" v-model="selectedManager.editBuyPolicy" /> Edit Buy Policy Permission
          </label>
        </div>
        <div>
          <label>
            <input type="checkbox" v-model="selectedManager.editDiscountPolicy" /> Edit Discount Policy Permission
          </label>
        </div>
        <div>
          <label>
            <input type="checkbox" v-model="selectedManager.acceptBids" /> Accept Bids
          </label>
        </div>
        <div>
          <label>
            <input type="checkbox" v-model="selectedManager.createLottery" /> Create Lottery
          </label>
        </div>
        <div class="dialog-buttons">
          <button @click="savePermissions">Save</button>
          <button @click="closeDialog">Cancel</button>
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
import { Dropdown as PrimeDropdown } from 'primevue/dropdown';
import { DataTable as PrimeDataTable, Column as PrimeColumn } from 'primevue/datatable';
import { Button as PrimeButton } from 'primevue/button';
import { useToast } from 'primevue/usetoast';
import { useRouter } from 'vue-router';

export default defineComponent({
  name: 'WorkersInStore',
  components: {
    SiteHeader,
    PrimeDropdown,
    PrimeDataTable,
    PrimeColumn,
    PrimeButton,
  },
  setup() {
    const router = useRouter();
    const storeName = ref(router.currentRoute.value.params.storeName);
    const managers = ref([]);
    const owners = ref([]);
    const username = localStorage.getItem('username');
    const token = localStorage.getItem('token');
    const toast = useToast();
    const selectedRole = ref('');
    const editDialogVisible = ref(false);
    const selectedManager = ref({});

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
          editDiscountPolicy: manager.editDiscountPolicy,
          acceptBids: manager.acceptBids,
          createLottery: manager.createLottery,
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

    const editPermissions = (manager) => {
      selectedManager.value = { ...manager };
      editDialogVisible.value = true;
    };

    const savePermissions = async () => {
      try {
        await axios.post(`http://localhost:8082/api/trading/store/manager/permission/edit`, {
          username: username,
          token: token,
          managerToEdit: selectedManager.value.username,
          storeName: storeName.value,
          watch: selectedManager.value.watch,
          editSupply: selectedManager.value.editSupply,
          editBuyPolicy: selectedManager.value.editBuyPolicy,
          editDiscountPolicy: selectedManager.value.editDiscountPolicy,
          acceptBids: selectedManager.value.acceptBids,
          createLottery: selectedManager.value.createLottery,
        });

        toast.add({ severity: 'success', summary: 'Success', detail: 'Permissions updated successfully', life: 3000 });
        fetchData();
        closeDialog();
      } catch (err) {
        toast.add({ severity: 'error', summary: 'Error', detail: err.message, life: 3000 });
      }
    };

    const closeDialog = () => {
      editDialogVisible.value = false;
    };

    const roleOptions = [
      { label: 'Managers', value: 'managers' },
      { label: 'Owners', value: 'owners' }
    ];

    return {
      username,
      storeName,
      managers,
      owners,
      selectedRole,
      roleOptions,
      editDialogVisible,
      selectedManager,
      editPermissions,
      savePermissions,
      closeDialog,
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

.edit-dialog {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background-color: white;
  padding: 20px;
  border: 1px solid #ccc;
  border-radius: 5px;
  z-index: 1000;
}

.edit-dialog label {
  display: block;
  margin-bottom: 10px;
}

.dialog-buttons {
  margin-top: 10px;
  display: flex;
  justify-content: flex-end;
}

.dialog-buttons button {
  margin-left: 10px;
}
</style>
