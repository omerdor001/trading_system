<template>
  <div>
    <SiteHeader :isLoggedIn="true" />
    <div class="stores-list">
          <h3>Store</h3>
          <select v-model="selectedStore" @change="selectStore(selectedStore)">
            <option v-for="store in stores" :key="store.id" :value="store">{{ store }}</option>
          </select>
    </div>
    <div>
      <h3>Products</h3>
      <table>
        <thead>
          <tr>
            <th>Product ID</th>
            <th>Product Name</th>
            <th>Description</th>
            <th>Price</th>
            <th>Quantity</th>
            <th>Rating</th>
            <th>Category</th>
            <th>Keywords</th>
          </tr>
        </thead>
        <tbody>
          
          <tr v-for="product in products" :key="product.product_id">
            <td>{{ product.product_id }}</td>
            <td><input v-model="product.product_name" /></td>
            <td><input v-model="product.product_description" /></td>
            <td><input v-model.number="product.product_price" /></td>
            <td><input v-model.number="product.product_quantity" /></td>
            <td>{{ product.rating }}</td>
            <td>{{ product.category }}</td>
          <td>
              <ul>
                <li v-for="(keyword, index) in product.keyWords" :key="index">{{ keyword }}</li>
              </ul>
            </td>
            <td>
              <button @click="saveProduct(product)">Save</button>
            </td>
            <td>
              <button @click="deleteProduct(product)">Delete</button>
            </td>
          </tr>
        </tbody>
      </table>
      
    </div>
    <div>
      <h3>Products</h3>
      <form @submit.prevent="addProduct">

        <label for="newProductID">Product ID:</label><br>
        <input type="text" id="newProductID" v-model.number="newProductID" required><br><br>

      <label for="newProductName">Product Name:</label><br>
      <input type="text" id="newProductName" v-model="newProductName" required><br><br>

      <label for="newProductDescription">Product Description:</label><br>
      <textarea id="newProductDescription" v-model="newProductDescription" rows="4" required></textarea><br><br>

      <label for="newProductPrice">Product Price:</label><br>
      <input type="number" id="newProductPrice" v-model.number="newProductPrice" required><br><br>

      <label for="newProductQuantity">Product Quantity:</label><br>
      <input type="number" id="newProductQuantity" v-model.number="newProductQuantity" required><br><br>

      <label for="newProductRating">Product Rating:</label><br>
      <input type="number" id="newProductRating" v-model.number="newProductRating" required><br><br>

      <label for="newProductCategory">Product Category:</label><br>
      <select id="newProductCategory" v-model="newProductCategory" required>
        <option value="1">Electronics</option>
        <option value="2">Clothing</option>
        <option value="3">Books</option>
        <!-- Add more options as needed -->
      </select><br><br>

      <button type="submit">Add Product</button>
    </form>
    </div>
    <div>
    <button @click="openProductFormWindow">Add Product</button>
    </div>
  </div>
  
</template>

<script>
import axios from 'axios';
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
// import { InputText } from 'primevue/inputtext';
// import { Button as PrimeButton } from 'primevue/button';
import SiteHeader from '@/components/SiteHeader.vue';
import ProductViewModel from '@/ViewModel/ProductViewModel';
import { useToast } from 'primevue/usetoast';


export default {
  name: 'ProductManagement',
  components: {
    // InputText,
    // PrimeButton,
    SiteHeader
  },
  setup() {
    const username = localStorage.getItem('username')
    const token = localStorage.getItem('token')
    const fields = ref(['Name', 'Description', 'Price', 'Quantity', 'Rating', 'Category', 'Add Key-Word', 'Remove Key-Word']);
    const selectedField = ref('');
    const newDetail = ref('');
    const route = useRoute();
    const router = useRouter();
    const selectedStore = ref(null);
    const stores = ref([]);
    const toast = useToast();
    var products = ref([]);
    var newProductID = ref(null);
    var newProductName = ref('');
    var newProductDescription = ref('');
    var newProductPrice = ref(null);
    var newProductRating = ref(null);
    var newProductCategory = ref(null);
    var newProductQuantity = ref(null);
    var newKeyWords =  ['new', 'best'].join(',');


    const selectField = (field) => {
      selectedField.value = field;
    };

    const updateField = async () => {
      const storeName = route.params.storeName;
      const productId = route.params.productId;
      const userName = localStorage.getItem('username');
      const token = localStorage.getItem('token');
      try {
        await ProductViewModel.actions.updateProductField(userName, token, storeName, productId, selectedField.value, newDetail.value);
        alert('Product field updated successfully');
      } catch (error) {
        alert('Failed to update product field');
      }
    };

    onMounted(
      async () => {
      try {

        const response = await axios.get('http://localhost:8082/api/trading/stores-I-own', {
           params: {
            userName: username,
            token: token,
          }
        });
        console.log(response.data);
        stores.value = response.data.substring(1,response.data.length-1).split(',');
      } catch (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: error.response?.data || 'Failed to load stores', life: 3000 });
      }  
    });

    const saveProduct = async (product) => {
      try { 
        const response = await axios.put('http://localhost:8082/api/trading/editProduct', null, {
          params : {
            username : username,
            token : token,
            storeName : selectedStore.value,
            productId : product.product_id,
            productName : product.product_name,
            productDescription : product.product_description,
            productPrice : product.product_price,
            productQuantity : product.product_quantity
          }
        })

        toast.add({severity: 'success', summary: 'success', detail : response.data, life:3000})
        fetchProducts(selectedStore.value)
      }
      catch (err){
        toast.add({severity:'error', summary: 'error', detail : err, life:3000})
      }

    }

    
    const deleteProduct = async (product) => {
      try { 
        const response = await axios.delete('http://localhost:8082/api/trading/product/remove', {
          params : {
            username : username,
            token : token,
            storeName : selectedStore.value,
            productId : product.product_id,
          }
        });

        toast.add({severity: 'success', summary: 'success', detail : response.data, life:3000})
        fetchProducts(selectedStore.value)
      }
      catch (err){
        toast.add({severity:'error', summary: 'error', detail : err, life:3000})
      }

    }

    const addProduct = async () => {
      try { 
        if (newProductID == null || newProductName === '' || newProductDescription === '' || newProductPrice == null | newProductQuantity == null || newProductRating == null || newProductCategory == null)
        {
            alert("you didnt fill all fields");
            return;
        } 
        const response = await axios.post('http://localhost:8082/api/trading/product/add', null, {
          params : {
            username : username,
            token : token,
            productId : newProductID.value,
            storeName : selectedStore.value,
            productName : newProductName.value,
            productDescription : newProductDescription.value,
            productPrice : newProductPrice.value,
            productQuantity: newProductQuantity.value,
            rating: newProductRating.value,
            category: newProductCategory.value,
            keyWords: newKeyWords
          }
        });

        toast.add({severity: 'success', summary: 'success', detail : response.data, life:3000});
        fetchProducts(selectedStore.value)
      }
      catch (err){
        toast.add({severity:'error', summary: 'error', detail : err, life:3000});
      }

    }



    const fetchProducts = async (store) => {
      try {
        const response = await axios.get('http://localhost:8082/api/trading/getStoreProducts', {
          params : {
            userName : username,
            token : token,
            store_name : store
          }
        });
        products.value = Object.values(response.data.products);
        toast.add({severity: 'success', summary : 'success', detail : products.value, life:7000});

      }
      catch (err) {
        toast.add({ severity: 'error', summary: 'Error', detail: err.response?.data || 'Failed to load stores', life: 3000 });

      }
    }
    const selectStore = (store) => {
      selectedStore.value = store;
      console.log('Selected Store:', store);
      fetchProducts(store)
      // You can perform additional actions here based on the selected store
    };

    const finish = () => {
      router.push('/');
    };

    return {
      username,
      newProductCategory,
      newProductDescription,
      newProductID,
      newProductName,
      newProductPrice,
      newProductQuantity,
      newProductRating,
      newKeyWords,
      token,
      fields,
      selectedField,
      stores,
      selectStore,
      selectedStore,
      newDetail,
      selectField,
      products,
      fetchProducts,
      updateField,
      deleteProduct,
      addProduct,
      saveProduct,
      finish
    };
  }
};
</script>

<style scoped>
.container {
  padding: 20px;
}

.fields {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.button-group {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.finish-button {
  width: 100px;
}

table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 20px;
}
thead th, tbody td {
  padding: 10px;
  text-align: left;
  border: 1px solid #ddd;
}
thead th {
  background-color: #f2f2f2;
}

</style>
