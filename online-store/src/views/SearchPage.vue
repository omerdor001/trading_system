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
          <div v-for="category in categories" :key="category.name" class="category-item">
            <input type="checkbox" v-model="category.checked" />
            <label>{{ category.name }}</label>
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
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import SiteHeader from '@/components/SiteHeader.vue';

export default {
  name: 'SearchPage',
  components: {
    SiteHeader
  },
  setup() {
    const router = useRouter();
    const username = ref(localStorage.getItem('username') || '');
    const searchQuery = ref('');
    const priceMin = ref('');
    const priceMax = ref('');
    const categories = ref([
      { name: 'Fashion', checked: true },
      { name: 'Electronics', checked: true },
      { name: 'Toys', checked: true },
      { name: 'Books', checked: true },
      { name: 'Home', checked: true }
    ]);
    const selectedRating = ref(0);

    const search = () => {
      const filters = {
        query: searchQuery.value,
        priceMin: priceMin.value,
        priceMax: priceMax.value,
        categories: categories.value.filter(category => category.checked).map(category => category.name),
        rating: selectedRating.value
      };
      router.push({ name: 'SearchResults', query: filters });
    };

    const selectRating = (rating) => {
      selectedRating.value = rating;
    };

    const resetFilters = () => {
      searchQuery.value = '';
      priceMin.value = '';
      priceMax.value = '';
      categories.value.forEach(category => category.checked = true);
      selectedRating.value = 0;
    };

    const logout = () => {
      localStorage.removeItem('isLoggedIn');
      localStorage.removeItem('username');
      router.push('/login');
    };

    return {
      username,
      searchQuery,
      priceMin,
      priceMax,
      categories,
      selectedRating,
      search,
      selectRating,
      resetFilters,
      logout
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
