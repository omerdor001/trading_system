<template>
  <div>
    <SiteHeader :isLoggedIn="true" :username="username" @logout="logout" />
    <div class="main-content">
      <h1>Store Search Results</h1>
      <div v-if="stores.length === 0">
        <p>No stores found.</p>
      </div>
      <div v-else>
        <p v-if="!searchQuery">Showing all stores:</p>
        <p v-else>Search results for "{{ searchQuery }}":</p>
        <ul>
          <li v-for="store in stores" :key="store.id">
            <img :src="store.image" :alt="store.name" width="100" />
            <div>{{ store.name }} (Rating: {{ store.rating }})</div>
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import SiteHeader from '@/components/SiteHeader.vue';

export default {
  name: 'StoreSearchResults',
  components: {
    SiteHeader
  },
  data() {
    return {
      stores: [],
      searchQuery: this.$route.query.name || '',
      username: localStorage.getItem('username') || ''
    };
  },
  created() {
    this.fetchStores();
  },
  methods: {
    async fetchStores() {
      try {
        let response;
        if (this.searchQuery) {
          response = await axios.get(`http://localhost:8082/api/stores/search`, {
            params: { name: this.searchQuery }
          });
        } else {
          response = await axios.get(`http://localhost:8082/api/stores`);
        }
        this.stores = response.data;
      } catch (error) {
        console.error('Error fetching stores:', error);
      }
    },
    logout() {
      localStorage.removeItem('isLoggedIn');
      localStorage.removeItem('username');
      this.$router.push('/login');
    }
  },
  watch: {
    '$route.query.name': function(newVal) {
      this.searchQuery = newVal;
      this.fetchStores();
    }
  }
};
</script>

<style scoped>
.main-content {
  padding: 20px;
}

ul {
  list-style: none;
  padding: 0;
}

li {
  margin: 10px 0;
  display: flex;
  align-items: center;
}

img {
  margin-right: 10px;
}
</style>
