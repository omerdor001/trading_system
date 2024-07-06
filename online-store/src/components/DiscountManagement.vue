<template>
  <div>
    <SiteHeader :isLoggedIn="isLoggedIn" :username="username" @logout="logout" />
    <div class="discount-management">
      <h2>Discount Management</h2>
      <PrimeButton label="Add New Discount" icon="pi pi-plus" @click="addDiscount" />
      <PrimeDataTable :value="discounts" responsiveLayout="scroll" class="p-mt-3">
        <PrimeColumn field="type" header="Type" :body="typeTemplate"></PrimeColumn>
        <PrimeColumn field="percent" header="Value" :body="valueTemplate"></PrimeColumn>
        <PrimeColumn header="Actions" :body="actionTemplate"></PrimeColumn>
      </PrimeDataTable>
      <PrimeDialog header="Edit Discount" v-model="editDialogVisible">
        <div class="p-fluid">
          <div class="p-field">
            <label for="type">Type</label>
            <PrimeDropdown id="type" v-model="selectedDiscount.type" :options="discountTypes" optionLabel="label" optionValue="value" />
          </div>
          <div class="p-field" v-if="selectedDiscount.type === 'percentageStore'">
            <label for="percent">Percent</label>
            <InputNumber id="percent" v-model="selectedDiscount.percent" mode="decimal" min="0" max="1" />
          </div>
        </div>
        <template #footer>
          <PrimeButton label="Cancel" icon="pi pi-times" @click="editDialogVisible = false" class="p-button-text" />
          <PrimeButton label="Save" icon="pi pi-check" @click="saveDiscount" />
        </template>
      </PrimeDialog>
      <PrimeToast ref="toast" position="top-right" :life="3000"></PrimeToast>
    </div>
  </div>
</template>

<script>
import { defineComponent, ref, onMounted, h } from 'vue';
import { useToast } from 'primevue/usetoast';
import { useRouter } from 'vue-router';
import SiteHeader from '@/components/SiteHeader.vue';
import PrimeButton from 'primevue/button';
import PrimeDataTable from 'primevue/datatable';
import PrimeColumn from 'primevue/column';
import PrimeDropdown from 'primevue/dropdown';
import InputNumber from 'primevue/inputnumber';
import PrimeDialog from 'primevue/dialog';
import PrimeToast from 'primevue/toast';

export default defineComponent({
  name: 'EditDiscountPolicy',
  components: {
    SiteHeader,
    PrimeButton,
    PrimeDataTable,
    PrimeColumn,
    PrimeDropdown,
    InputNumber,
    PrimeDialog,
    PrimeToast,
  },
  setup() {
    const router = useRouter();
    const toast = useToast();
    const discounts = ref([]);
    const discountTypes = ref([]);
    const editDialogVisible = ref(false);
    const selectedDiscount = ref({});
    const storeName = ref(router.currentRoute.value.params.storeName);
    const username = ref(localStorage.getItem('username'));
    const isLoggedIn = ref(!!username.value);

    onMounted(async () => {
      await fetchDiscounts();
      await fetchDiscountTypes();
    });

    const fetchDiscounts = async () => {
      try {
        const response = await fetch(`/api/store/${storeName.value}/discount-policies`);
        discounts.value = await response.json();
      } catch (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: 'Failed to fetch discounts', life: 3000 });
      }
    };

    const fetchDiscountTypes = async () => {
      try {
        const response = await fetch(`/api/store/${storeName.value}/discount-conditions`);
        discountTypes.value = await response.json();
      } catch (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: 'Failed to fetch discount types', life: 3000 });
      }
    };

    const addDiscount = () => {
      selectedDiscount.value = { type: '', percent: 0 };
      editDialogVisible.value = true;
    };

    const saveDiscount = async () => {
      try {
        const response = await fetch(`/api/store/${storeName.value}/discount-policies`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(selectedDiscount.value)
        });
        if (!response.ok) throw new Error('Failed to save discount');
        await fetchDiscounts();
        toast.add({ severity: 'success', summary: 'Success', detail: 'Discount saved', life: 3000 });
      } catch (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: 'Failed to save discount', life: 3000 });
      } finally {
        editDialogVisible.value = false;
      }
    };

    const typeTemplate = (discount) => discount.type;
    const valueTemplate = (discount) => discount.percent;

    const actionTemplate = (discount) => {
      return h(PrimeButton, {
        icon: 'pi pi-pencil',
        class: 'p-button-rounded p-button-text',
        onClick: () => editDiscount(discount)
      });
    };

    const editDiscount = (discount) => {
      selectedDiscount.value = { ...discount };
      editDialogVisible.value = true;
    };

    const logout = () => {
      localStorage.removeItem('username');
      localStorage.removeItem('token');
      router.push('/login');
    };

    return {
      discounts,
      discountTypes,
      editDialogVisible,
      selectedDiscount,
      addDiscount,
      saveDiscount,
      typeTemplate,
      valueTemplate,
      actionTemplate,
      isLoggedIn,
      username,
      logout,
    };
  },
});
</script>

<style scoped>
.discount-management {
  padding: 20px;
}

.p-field {
  margin-bottom: 1em;
}

.p-mt-3 {
  margin-top: 1.5em;
}
</style>
