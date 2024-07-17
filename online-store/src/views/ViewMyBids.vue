<template>
  <div>
    <SiteHeader :isLoggedIn="isLoggedIn" :username="username" @logout="logout" />
    <div class="approve-manage">
      <h2>My Bids</h2>
      <div v-if=" allBids && Object.keys(allBids).length">
          <div v-for="(store, storeName) in allBids" :key="storeName">
          <h2 style="text-align: left;">{{ storeName }}</h2>
          <table class="manager-requests">
            <thead>
              <tr>
                <th>Product ID</th>
                <th>Product Name</th>
                <th>Price</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(bid, productID) in store" :key="productID">
                <td>{{ bid.productID }}</td>
                <td>{{ bid.productName  }}</td>
                <td>{{ bid.price }}</td>
                <td>
              <button v-if=" !bid.customerApproved" @click="approveCounterOffer(storeName, bid.productID, bid.price)" class="action-button">Approve Counter Offer</button>
              <button v-if=" !bid.customerApproved" @click="rejectCounterOffer(storeName, bid.productID)" class="action-button">Reject Counter Offer</button>
            </td>
              </tr>
            </tbody>
          </table>
      </div>
        </div>
        <div v-else>
          <p>No bids Found.</p>
        </div>
      <p v-if="error" class="p-error">{{ error }}</p> 
    </div>
    
    <Dialog header="Place Counter Offer" v-model="counterOfferModalVisible" :visible="counterOfferModalVisible" :draggable="false" :modal="true" :closable="false">
      <div>
        <span>New Price: </span>
        <InputNumber v-model="newPrice" :min="0" :step="0.01" showButtons />
      </div>
      <div class="dialog-footer">
        <Button label="Submit" icon="pi pi-check" @click="placeCounterOffer" />
        <Button label="Cancel" icon="pi pi-times" @click="closeCounterOfferModal" />
      </div>
    </Dialog>
    <Toast />
  </div>
</template>

<script>
import axios from 'axios';
import { defineComponent, ref, onMounted } from 'vue';
import SiteHeader from '@/components/SiteHeader.vue';
import { useToast } from 'primevue/usetoast';
import Dialog from 'primevue/dialog';
import InputNumber from 'primevue/inputnumber';
import Button from 'primevue/button';
import Toast from 'primevue/toast';

export default defineComponent({
  name: 'ViewMyBids',
  components: {
    SiteHeader,
    Dialog,
    InputNumber,
    Button,
    Toast
  },
  setup() {
    const error = ref(null);
    const toast = useToast();
    const username = localStorage.getItem('username');
    const token = localStorage.getItem('token');
    const isLoggedIn = !!username;
    const allBids = ref({});
    const counterOfferModalVisible = ref(false);
    const newPrice = ref(0);
    const selectedBid = ref({});

    const fetchMyBids = async () => {
      try {
        const response = await axios.get('http://localhost:8082/api/trading/store/get-my-bids', {
          params: {
            userName: username,
            token: token,
          },
        });
        allBids.value = response.data;
        console.log(response.data)

      } catch (err) {
        error.value = err.response?.data?.message || 'An error occurred';
        toast.add({ severity: 'error', summary: 'Error', detail: error.value, life: 3000 });
      }
    };

    const openCounterOfferModal = (storeName, productID, bidUserName, price) => {
      console.log("Opening Counter Offer Modal", storeName, productID, bidUserName, price); // Debug log
      selectedBid.value = { storeName, productID, bidUserName };
      newPrice.value = price;
      counterOfferModalVisible.value = true;
      console.log("Counter Offer Modal Visible:", counterOfferModalVisible.value); // Debug log
    };

    const closeCounterOfferModal = () => {
      counterOfferModalVisible.value = false;
    };

    const placeCounterOffer = async () => {
      try {
        await axios.post('http://localhost:8082/api/trading/store/place-counter-offer', null, {
          params: {
            userName: username,
            token: token,
            storeName: selectedBid.value.storeName,
            productID: selectedBid.value.productID,
            bidUserName: selectedBid.value.bidUserName,
            newPrice: newPrice.value,
          },
        });
        toast.add({ severity: 'success', summary: 'Success', detail: 'Counter offer placed successfully', life: 3000 });
        closeCounterOfferModal();
        fetchMyBids(); // Refresh the list after placing counter offer
      } catch (err) {
        const errorMessage = err.response?.data?.message || 'An error occurred';
        toast.add({ severity: 'error', summary: 'Error', detail: errorMessage, life: 3000 });
      }
    };

    const approveCounterOffer = async (storeName, productID, price) => {
      try {
        await axios.post('http://localhost:8082/api/trading/store/approve-counter-offer', null, {
          params: {
            userName: username,
            token: token,
            storeName: storeName,
            productID: productID,
            price: price,
          },
        });
        toast.add({ severity: 'success', summary: 'Success', detail: 'Counter offer approved successfully', life: 3000 });
        fetchMyBids(); // Refresh the list after approving counter offer
      } catch (err) {
        const errorMessage = err.response?.data?.message || 'An error occurred';
        toast.add({ severity: 'error', summary: 'Error', detail: errorMessage, life: 3000 });
      }
    };


    const rejectCounterOffer = async (storeName, productID) => {
      try {
        await axios.post('http://localhost:8082/api/trading/store/reject-counter-offer', null, {
          params: {
            userName: username,
            token: token,
            storeName: storeName,
            productID: productID,
          },
        });
        toast.add({ severity: 'success', summary: 'Success', detail: 'Reject offer approved successfully', life: 3000 });
        fetchMyBids(); // Refresh the list after approving counter offer
      } catch (err) {
        const errorMessage = err.response?.data?.message || 'An error occurred';
        toast.add({ severity: 'error', summary: 'Error', detail: errorMessage, life: 3000 });
      }
    };

    onMounted(fetchMyBids);

    return {
      isLoggedIn,
      username,
      error,
      fetchMyBids,
      openCounterOfferModal,
      closeCounterOfferModal,
      placeCounterOffer,
      approveCounterOffer,
      rejectCounterOffer,
      counterOfferModalVisible,
      newPrice,
      selectedBid,
      toast,
      allBids,
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

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

.action-button {
  margin-right: 10px; /* Add space between buttons */
}
</style>