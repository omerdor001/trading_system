<template>
    <div>
      <h2>Add New Product</h2>
      <form @submit.prevent="handleSubmit">
        <label>Product Name:</label>
        <input v-model="formData.product_name" required>
        <label>Description:</label>
        <input v-model="formData.product_description">
        <label>Price:</label>
        <input type="number" v-model.number="formData.product_price" required>
        <label>Quantity:</label>
        <input type="number" v-model.number="formData.product_quantity" required>
        <!-- Add more fields as needed -->
  
        <button type="submit">Add Product</button>
      </form>
    </div>
  </template>
  
  <script>
  export default {
    props: {
      selectedStore: {
        type: Object,
        required: true
      }
    },
    data() {
      return {
        formData: {
          product_name: '',
          product_description: '',
          product_price: null,
          product_quantity: null
          // Add more fields as needed
        }
      };
    },
    mounted() {
      // Pre-fill form fields with selectedStore data
      if (this.selectedStore) {
        this.formData.product_name = ''; // Example: Replace with actual field from selectedStore
        this.formData.product_description = ''; // Example: Replace with actual field from selectedStore
        this.formData.product_price = null; // Example: Replace with actual field from selectedStore
        this.formData.product_quantity = null; // Example: Replace with actual field from selectedStore
        // Add more assignments for other fields
      }
    },
    methods: {
      handleSubmit() {
        // Validate form data if needed
        this.$emit('productAdded', this.formData); // Emit event with new product data
        this.resetForm();
      },
      resetForm() {
        this.formData = {
          product_name: '',
          product_description: '',
          product_price: null,
          product_quantity: null
          // Reset other fields as needed
        };
      }
    }
  };
  </script>
  
  <style scoped>
  form {
    display: flex;
    flex-direction: column;
  }
  
  label {
    margin-bottom: 8px;
  }
  
  input, button {
    margin-bottom: 16px;
    padding: 8px;
    border: 1px solid #ccc;
    border-radius: 4px;
  }
  
  button {
    background-color: #007bff;
    color: #fff;
    border: none;
    cursor: pointer;
  }
  </style>
  