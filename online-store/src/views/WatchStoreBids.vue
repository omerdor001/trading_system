<template>
    <div>
    <SiteHeader :isLoggedIn="true" :username="username" @logout="logout" />
    <div class="store-bids">
      <h1>{{ storeName }}</h1>
      <div v-if="bids.length">
        <div class="bid" v-for="(bid, index) in bids" :key="index">
          <h2>Bid {{ index + 1 }}</h2>
          <p><strong>User Name:</strong> {{ bid.userName }}</p>
          <p><strong>Product ID:</strong> {{ bid.productID }}</p>
          <p><strong>Price:</strong> {{ bid.price }}</p>
          <p><strong>All Owners Approve:</strong> {{ bid.allOwnersApprove ? 'Yes' : 'No' }}</p>
          <p><strong>Approved By:</strong> {{ bid.approvedBy.join(', ') }}</p>
        </div>
      </div>
      <div v-else>
        <p>No bids available.</p>
      </div>
    </div>
    </div>
  </template>
  
  <script setup>
  import SiteHeader from '@/components/SiteHeader.vue';
  import { ref, onMounted } from 'vue';
  import axios from 'axios';
  import { useRouter } from 'vue-router';
  
  const storeName = ref('');
  const bids = ref([]);
  
  const router = useRouter();
  const userName = 'yourUserName'; // Replace with actual userName
  const token = 'yourToken'; // Replace with actual token
  
  const fetchStoreBids = async () => {
    try {
      const response = await axios.get(`/store/${router.params.storeName}/get-store-bids`, {
        params: { userName, token }
      });
      const data = response.data;
      storeName.value = data.storeName;
      bids.value = data.bids;
    } catch (error) {
      console.error('Error fetching store bids:', error);
    }
  };
  
  onMounted(fetchStoreBids);
  </script>
  
  <style scoped>
  .store-bids {
    max-width: 800px;
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
  </style>
  