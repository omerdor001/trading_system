<template>
    <div>
    <SiteHeader :isLoggedIn="true" :username="username" @logout="logout" />
    <div class="store-bids">
      <h1>{{ storeName }}</h1>
      <div v-if="bids">
        <table>
          <thead>
            <tr>
              <th>
                User Name
              </th>
              <th>
                Product ID
              </th>
              <th>Price</th>
              <th>Customer Approve</th>
              <th>Approved By</th>
              <th>Approve</th>
              <th>Reject</th>
              <th>Counter Offer</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="bid in bids" :key="bid.userName + '-' + bid.productID">
              <td> {{ bid.userName }}</td>
              <td> {{ bid.productID }}</td>
              <td> {{ bid.price }}</td>
              <td> {{ bid.customerApproved ? 'Yes' : 'No' }}</td>
              <td> {{  bid.approvedBy.length > 0 ? bid.approvedBy.join(',') : 'No approvals yet' }}</td>
              <td>
                  <Button v-if="!isApprovedByUser(bid)" @click="approveBid(bid)">Approve</Button>
              </td>
              <td>
                  <Button v-if="!isApprovedByUser(bid)" @click="rejectBid(bid)">Reject</Button>
              </td>
              <td>
                  <input v-if="!isApprovedByUser(bid)" type="number" v-model="counterOffer" placeholder="Enter Counter Offer" />
                  <Button v-if="!isApprovedByUser(bid)" @click="placeCounterOffer(bid)">Counter Offer</Button>
              </td>
            </tr>
          </tbody>
          </table>
        </div>
      <div v-else>
        <p>No bids available.</p>
      </div>
    </div>
    </div>
  </template>
  
  <script>
  import SiteHeader from '@/components/SiteHeader.vue';
  import { ref, defineComponent, onMounted } from 'vue';
  import axios from 'axios';
  import { useRoute } from 'vue-router';
  import { useToast } from 'primevue/usetoast';
  import Button from 'primevue/button';



  export default defineComponent({
  name: 'WatchStoreBids',
  components: {
    SiteHeader,
    Button
  },
  setup() {
    const route = useRoute();
    const storeName = route.params.storeName;
    const username = localStorage.getItem('username');
    const token = localStorage.getItem('token');
    const isLoggedIn = ref(!!username.value);
    const bids = ref([]);
    const error = ref(null);
    const toast = useToast();
    const counterOffer = ref(0);



const isApprovedByUser =  (bid) => {

  if(bid.approvedBy.length != 0)
{
  return bid.approvedBy.includes(username);
}
  else
  {
    return false;
  }
};



    const fetchStoreBids = async () => {
      toast.add({ severity : 'success', summary: 'success', detail: storeName, life: 3000 });

    try {
      const response = await axios.get('http://localhost:8082/api/trading/store/get-store-bids', {
        params: {
           userName : username, 
           token : token,
           storeName : storeName
          } 
      });
      const data = response.data;
      bids.value = data.bids;
      toast.add({ severity : 'success', summary: 'success', detail: bids.value, life: 3000 });

    } catch (error) {
      console.error('Error fetching store bids:', error);
    }
  };

  const approveBid = async (bid) => {
  try {
      const response = await axios.post('http://localhost:8082/api/trading/store/approve-bid', null, {
        params: {
           userName : username, 
           token : token,
           storeName : storeName,
           productID : bid.productID,
           bidUserName: bid.userName
          } 
      });
      toast.add({ severity : 'success', summary: 'success', detail: "Approve bid succeessed", life: 3000 });
      toast.add({ severity : 'success', summary: 'success', detail:  response.data, life: 3000 });

      fetchStoreBids();

    } catch (error) {
      console.error('Error fetching store bids:', error);
    }
};


const placeCounterOffer = async (bid) => {
  try {
      const response = await axios.post('http://localhost:8082/api/trading/store/place-counter-offer', null, {
        params: {
           userName : username, 
           token : token,
           storeName : storeName,
           productID : bid.productID,
           bidUserName: bid.userName,
           newPrice: counterOffer.value
          } 
      });
      toast.add({ severity : 'success', summary: 'success', detail: "Counter offer bid succeessed", life: 3000 });
      toast.add({ severity : 'success', summary: 'success', detail:  response.data, life: 3000 });

      fetchStoreBids();

    } catch (error) {
      console.error('Error fetching store bids:', error);
    }
};

const rejectBid = async (bid) => {
  try {
      const response = await axios.post('http://localhost:8082/api/trading/store/reject-bid', null, {
        params: {
           userName : username, 
           token : token,
           storeName : storeName,
           productID : bid.productID,
           bidUserName: bid.userName
          } 
      });
      toast.add({ severity : 'success', summary: 'success', detail: "Approve bid succeessed", life: 3000 });
      toast.add({ severity : 'success', summary: 'success', detail:  response.data, life: 3000 });

      fetchStoreBids();

    } catch (error) {
      console.error('Error fetching store bids:', error);
    }
};

  onMounted(fetchStoreBids);

    return {
      username,
      isLoggedIn,
      storeName,
      error,
      bids,
      fetchStoreBids,
      toast,
      isApprovedByUser,
      approveBid,
      rejectBid,
      counterOffer,
      placeCounterOffer
    };
  },
});
  </script>
  
  <style scoped>
  .store-bids {
    max-width: 100%;
    margin: 0 auto;
    padding: 1em;
    font-family: Arial, sans-serif;
    background: #f9f9f9;
    border: 1px solid #ddd;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  }
  
  .store-bids h1 {
    font-size: 2em;
    margin-bottom: 0.5em;
    text-align: center;
    color: #333;
  }
  
  .bid {
    background: #fff;
    border: 1px solid #ddd;
    border-radius: 8px;
    padding: 1em;
    margin-bottom: 1em;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  }
  
  .bid h2 {
    font-size: 1.5em;
    margin-bottom: 0.5em;
    color: #555;
  }
  
  .bid p {
    margin: 0.5em 0;
    font-size: 1em;
    color: #666;
  }
  
  .bid strong {
    color: #444;
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
  </style>
  