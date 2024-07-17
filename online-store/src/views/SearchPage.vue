<template>
  <div>
    <SiteHeader :isLoggedIn="true" :username="username" @logout="logout" />
    <div class="main-content">
      <h1>Explore and discover stores</h1>
      <div class="search-bar">
        <input type="text" placeholder="Search products" v-model="searchQuery" />
        <button @click="search">Search</button>
      </div>
      <div class="price-range">
        <h2>Price Range</h2>
        <input type="number" v-model="priceMin" placeholder="Min Price" />
        <input type="number" v-model="priceMax" placeholder="Max Price" />
      </div>
      <div class="categories">
        <h2>Categories</h2>
        <div class="category-checkboxes">
          <div v-for="category in categories" :key="category.index" class="category-item">
            <input type="checkbox" v-model="category.checked" />
            <label>{{ category.label }}</label>
          </div>
        </div>
      </div>
      <div class="ratings">
        <h2>Ratings</h2>
        <div class="rating-stars">
          <span v-for="star in 5" :key="star" class="star" :class="{ 'filled': star <= selectedRating }" @click="selectRating(star)">&#9733;</span>
        </div>
      </div>
      <button @click="resetFilters">Reset</button>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import SiteHeader from '@/components/SiteHeader.vue';
import { useToast } from 'primevue/usetoast';
import axios from 'axios';



export default {
  name: 'SearchPage',
  components: {
    SiteHeader
  },
  setup() {
    const router = useRouter();
    const username = localStorage.getItem('username');
    const token = localStorage.getItem('token');
    const toast = useToast();

    const searchQuery = ref('');
    const priceMin = ref(0);
    const priceMax = ref(0);
    const categories = ref([]);
    const selectedRating = ref(0);

    const search = () => {


      const filters = {
        query: searchQuery.value,
        priceMin: priceMin.value,
        priceMax: priceMax.value,
        categories: categories.value.filter(category => category.checked).map(category => category.index),
        rating: selectedRating.value
      };
      router.push({ name: 'SearchResults', query: filters });
    };

    const selectRating = (rating) => {
      selectedRating.value = rating;
    };

    const fetchCategories = async () => {
      try {
        const response = await axios.get('http://localhost:8082/api/trading/categories', {
          params: {
            username: username,
            token: token,
          }
        });
        const categoryArray = response.data.slice(1, response.data.length - 1).split(',');
        categories.value = categoryArray.map((category, index) => ({ label: category, value: category, index: index + 1 }));

      } catch (error) {
        console.error('Failed to fetch categories', error);
        toast.add({ severity: 'error', summary: 'Error', detail: 'Failed to fetch categories' });
      }
    };

    onMounted(() => {
        fetchCategories();
    });


    const resetFilters = () => {
      searchQuery.value = '';
      priceMin.value = '';
      priceMax.value = '';
      categories.value.forEach(category => category.checked = true);
      selectedRating.value = 0;
    };

    return {
      username,
      token,
      searchQuery,
      priceMin,
      priceMax,
      categories,
      selectedRating,
      fetchCategories,
      search,
      selectRating,
      resetFilters
    };
  }
};
</script>

<style scoped>
.main-content {
  padding: 20px;
}

.search-bar {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.search-bar input {
  flex: 1;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 5px;
}

.search-bar button {
  padding: 10px 20px;
  background-color: #007bff;
  border: none;
  border-radius: 5px;
  color: white;
  cursor: pointer;
}

.price-range, .categories, .ratings {
  margin-bottom: 20px;
}

.price-range input {
  width: 100px;
  margin-right: 10px;
}

.category-checkboxes, .rating-stars {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.category-item {
  display: flex;
  align-items: center;
}

.rating-stars .star {
  font-size: 24px;
  cursor: pointer;
}

.rating-stars .star.filled {
  color: gold;
}

button {
  margin-top: 10px;
  padding: 10px 20px;
  background-color: #007bff;
  border: none;
  border-radius: 5px;
  color: white;
  cursor: pointer;
}
</style>
