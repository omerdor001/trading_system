
<template>
    <div>
      <SiteHeader :isLoggedIn="isLoggedIn" :username="username" @logout="logout" />
        <h2>My Bids</h2>
    </div>
  </template>
  
  <script>
  import axios from 'axios';
  import { defineComponent, ref, onMounted } from 'vue';
  import SiteHeader from '@/components/SiteHeader.vue';
  import { useToast } from 'primevue/usetoast';

  export default defineComponent({
    name: 'ViewMyBids',
    components: {
      SiteHeader,
    },
    setup() {
      const error = ref(null);
      const toast = useToast();
      const username = localStorage.getItem('username');
      const token = localStorage.getItem('token');
      const isLoggedIn = !!username;
      const allBids = ref([]);

  
      const fetchMyBids = async () => {
        try {
          const response = await axios.get('http://localhost:8082/api/trading/store/get-my-bids', {
            params: {
              userName: username,
              token: token,
            },
          });
          allBids.value = response.data
          toast.add({ severity: 'success', summary: 'Success', detail:response.data, life: 3000 });
        } catch (err) {
          error.value = err.response?.data?.message || 'An error occurred';
        }
      };
  
      onMounted(fetchMyBids);
  
      return {
        isLoggedIn,
        username,
        error,
        fetchMyBids,
        toast,
        allBids
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
    width: 800px; /* Adjust width as needed */
    margin: 0 auto;
    text-align: center;
  }
  
  .approve-manage h2 {
    color: #e67e22;
    margin-bottom: 20px;
  }
  
  .manager-requests {
    width: 100%;
    border-collapse: collapse;
    margin-top: 20px;
  }
  
  .manager-requests th,
  .manager-requests td {
    border: 1px solid #ddd;
    padding: 8px;
    text-align: left;
  }
  
  .manager-requests th {
    background-color: #f2f2f2;
  }
  
  .p-error {
    margin-top: 20px;
    color: red;
  }
  </style>
  