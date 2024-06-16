<template>
  <div class="about">
    <Card>
      <template #header>
        <h2>About the Website</h2>
      </template>
      <p>Welcome to LSMAONY Online Store, your one-stop shop for all your needs. We offer a wide range of products including electronics, fashion, home appliances, and more. Our mission is to provide high-quality products at affordable prices. We pride ourselves on our excellent customer service and fast shipping.</p>
      <p>At LSMAONY, we believe in making shopping easy and accessible for everyone. Whether you're looking for the latest gadgets, trendy fashion items, or essential home appliances, you'll find everything you need in one place. Our user-friendly platform ensures a smooth and enjoyable shopping experience.</p>
      <p>We continuously update our inventory to bring you the latest and greatest products on the market. Our team works tirelessly to ensure that every product meets our high standards of quality. Plus, our secure payment options and fast delivery services provide you with peace of mind.</p>
      <p>Join thousands of satisfied customers who have made LSMAONY their preferred shopping destination. Discover our extensive collection today and experience the best in online shopping. Thank you for choosing LASMONY!</p>
    </Card>
  </div>
</template>

<script>
// Import the necessary components from PrimeVue
import { defineComponent } from 'vue';
import Card from 'primevue/card';

export default defineComponent({
  name: 'AboutSection',
  components: {
    Card
  }
});
</script>

<style scoped>
.about {
  margin: 20px;
  font-family: 'Arial', sans-serif;
  color: #333;
}

h2 {
  margin-bottom: 10px;
  font-size: 24px;
  color: #425965;
}

p {
  text-align: justify;
  line-height: 1.6;
  margin-bottom: 10px;
}
</style>
