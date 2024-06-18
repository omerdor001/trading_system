<template>
  <div>
    <SiteHeader :isLoggedIn="true" />
    <div class="container">
      <h2>Choose What To Change</h2>
      <div class="fields">
        <PrimeButton v-for="field in fields" :key="field" :label="field" @click="selectField(field)" />
      </div>
      <div v-if="selectedField">
        <h3>{{ selectedField }}</h3>
        <InputText v-model="newDetail" placeholder="New Detail" />
        <PrimeButton label="Change" @click="updateField" />
      </div>
      <div class="button-group">
        <PrimeButton label="Finish" @click="finish" class="finish-button" />
      </div>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { InputText } from 'primevue/inputtext';
import { Button as PrimeButton } from 'primevue/button';
import SiteHeader from '@/components/SiteHeader.vue';
import ProductViewModel from '@/ViewModel/ProductViewModel';

export default {
  name: 'ProductManagement',
  components: {
    InputText,
    PrimeButton,
    SiteHeader
  },
  setup() {
    const fields = ref(['Name', 'Description', 'Price', 'Quantity', 'Rating', 'Category', 'Add Key-Word', 'Remove Key-Word']);
    const selectedField = ref('');
    const newDetail = ref('');
    const route = useRoute();
    const router = useRouter();

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

    const finish = () => {
      router.push('/');
    };

    return {
      fields,
      selectedField,
      newDetail,
      selectField,
      updateField,
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
</style>
