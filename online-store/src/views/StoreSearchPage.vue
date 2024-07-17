<template>
  <div>
    <SiteHeader :isLoggedIn="true" :username="username" @logout="logout" />
    <div class="main-content">
      <h1>Search for Stores</h1>
      <div class="search-container">
        <div class="search-bar">
          <InputText v-model="searchStoreName" placeholder="Enter store name" />
          <Button label="Search" icon="pi pi-search" @click="searchStore" class="p-button-rounded p-button-primary" />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { defineComponent, ref } from 'vue';
import { useRouter } from 'vue-router';
import SiteHeader from '@/components/SiteHeader.vue';
import InputText from 'primevue/inputtext';
import Button from 'primevue/button';

export default defineComponent({
  name: 'StoreSearchPage',
  components: {
    SiteHeader,
    InputText,
    Button
  },
  setup() {
    const router = useRouter();
    const username = ref(localStorage.getItem('username') || '');
    const searchStoreName = ref('');

    const searchStore = () => {
      router.push({ name: 'StoreSearchResults', query: { name: searchStoreName.value } });
    };

    return {
      username,
      searchStoreName,
      searchStore,
    };
  }
});
</script>

<style scoped>
.main-content {
  padding: 20px;
}

.search-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.search-bar {
  display: flex;
  gap: 10px;
}

.search-bar .p-inputtext {
  flex: 1;
}

.search-bar .p-button {
  white-space: nowrap;
}
</style>
