<template>
  <div>
    <SiteHeader :isLoggedIn="isLoggedIn" :username="username" @logout="logout" />
    <div>
    <h2>Manager Approvals</h2>
    <div v-if="approvals.length === 0">No approvals needed.</div>
    <div v-else>
      <table>
        <thead>
          <tr>
            <th>Store</th>
            <th>Appointer</th>
            <th>Watch</th>
            <th>Edit Supply</th>
            <th>Edit Buy Policy</th>
            <th>Edit Discount Policy</th>
            <th>Accept Bids</th>
            <th>Create Lottery</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(approval, index) in approvals" :key="index">
            <td>{{ approval.store }}</td>
            <td>{{ approval.appointer }}</td>
            <td>{{ approval.watch}}</td>
            <td>{{ approval.editSupply}}</td>
            <td>{{ approval.editBuyPolicy}}</td>
            <td>{{ approval.editDiscountPolicy}}</td>
            <td>{{ approval.acceptBids}}</td>
            <td>{{ approval.createLottery }}</td>
            <td>
              <button @click="approveManager(approval.store, approval.appointer)">Approve</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</div>
</template>

<script>
import axios from 'axios';
import { defineComponent, ref, onMounted } from 'vue';
import SiteHeader from '@/components/SiteHeader.vue';
// import { Button as PrimeButton } from 'primevue/button';
// import { Toast as PrimeToast } from 'primevue/toast';
import { useRouter } from 'vue-router';
import { useToast } from 'primevue/usetoast';
// import { parse } from '@vue/compiler-sfc';


export default defineComponent({
  name: 'ApproveManager',
  components: {
    SiteHeader,
    // PrimeButton,
    // PrimeToast,
  },
  setup() {
    const router = useRouter();
    const newManager = ref('');
    const storeNameId = ref('');
    const appoint = ref('');
    const username = localStorage.getItem('username');
    const token = localStorage.getItem('token');
    const isLoggedIn = ref(!!username.value);
    const approvals = ref([]);
    const toast = useToast();


    onMounted(
      async () => {
      try {

        const response = await axios.get('http://localhost:8082/api/trading/requests-for-management', {
           params: {
            username: username,
            token: token,
          }
        });
        console.log(response.data);
        [true,true,true,true,true,true]
        approvals.value = Object.entries(response.data).map(([key,values]) => {
          // const parsedKey = JSON.parse(key);
          const splitted = key.split(',')
          const store_split = splitted[0].substring(1,splitted[0].length)
          const appointer_split = splitted[1].substring(1,splitted[1].length-1)
          toast.add({ severity: 'success', summary: 'success', detail:store_split, life: 3000 });
          toast.add({ severity: 'success', summary: 'success', detail: appointer_split, life: 3000 });
          toast.add({ severity: 'success', summary: 'success', detail: values, life: 3000 });
          return {
          store : store_split,
          appointer : appointer_split,
          watch : values[0],
          editSupply : values[1],
          editBuyPolicy : values[2],
          editDiscountPolicy : values[3],
          acceptBids : values[4],
          createLottery : values[5]
        };
      });
      toast.add({ severity: 'success', summary: 'success', detail: response.data, life: 3000 });

      } catch (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: error.response?.data || 'Failed to load stores', life: 3000 });
      }  
    });


    const approveManager = async (store, appointer) => {
      try {
        const response = await axios.post('http://localhost:8082/api/trading/approveManage', null, {
          params : {
          newManager: username,
          token: token,
          store_name_id: store,
          appoint: appointer,
          watch: true,
          editSupply: true,
          editBuyPolicy: true,
          editDiscountPolicy: true,
          acceptBids : true,
          createLottery : true
          }
      })
      
        showSuccessToast(response.data.message);
        resetForm();

      } catch (err) {
        showErrorToast(err.response?.data?.message || 'An error occurred');
      }
    };

    const logout = () => {
      localStorage.removeItem('username');
      router.push('/login');
    };

    const showSuccessToast = (message) => {
      toast.add({
        severity: 'success',
        summary: 'Success',
        detail: message,
        life: 3000,
      });
    };

    const showErrorToast = (message) => {
      toast.add({
        severity: 'error',
        summary: 'Error',
        detail: message,
        life: 5000,
      });
    };

    const resetForm = () => {
      newManager.value = '';
      storeNameId.value = '';
      appoint.value = '';
    };

    return {
      username,
      isLoggedIn,
      newManager,
      storeNameId,
      appoint,
      approvals,
      approveManager,
      logout,
    };
  },
});
</script>

<style scoped>
.approve-manage {
  background: #fff;
  padding: 30px;
  border-radius: 10px;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
  width: 400px;
  margin: 0 auto;
  text-align: center;
}

.approve-manage h2 {
  color: #e67e22;
  margin-bottom: 20px;
}

.approve-manage .p-field {
  margin-bottom: 15px;
}

.approve-manage label {
  font-weight: bold;
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

table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 20px;
}

thead th, tbody td {
  padding: 10px;
  text-align: center;
  border: 1px solid #ddd;
}
thead th {
  background-color: #f2f2f2;
}

button {
  padding: 5px 10px;
  background-color: #4caf50;
  color: white;
  border: none;
  cursor: pointer;
  font-size: 14px;
  border-radius: 5px;
}
button:hover {
  background-color: #45a049;
}
</style>
