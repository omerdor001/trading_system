<template>
<div class="discount-management">
    <h2>Discount Management</h2>
    <Button label="Add New Discount" icon="pi pi-plus" @click="addDiscount" />
    <DataTable :value="discounts" responsiveLayout="scroll" class="p-mt-3">
        <Column field="type" header="Type" :body="typeTemplate"></Column>
        <Column field="value" header="Value" :body="valueTemplate"></Column>
        <Column header="Actions" :body="actionTemplate"></Column>
    </DataTable>
    <Dialog header="Edit Discount" v-model="editDialogVisible">
        <div class="p-fluid">
            <div class="p-field">
                <label for="type">Type</label>
                <Dropdown id="type" v-model="selectedDiscount.type" :options="discountTypes" optionLabel="label" optionValue="value" />
            </div>
            <div class="p-field" v-if="selectedDiscount.type === 'percentageStore'">
                <label for="percent">Percent</label>
                <InputNumber id="percent" v-model="selectedDiscount.percent" mode="decimal" min="0" max="1" />
            </div>
        </div>
        <template #footer>
            <Button label="Cancel" icon="pi pi-times" @click="editDialogVisible = false" class="p-button-text" />
            <Button label="Save" icon="pi pi-check" @click="saveDiscount" />
        </template>
    </Dialog>
</div>
</template>

<script>
import { ref, onMounted, h } from 'vue';
import { useToast } from 'primevue/usetoast';
import Button from 'primevue/button';
import DataTable from 'primevue/datatable';
import Column from 'primevue/column';
import Dropdown from 'primevue/dropdown';
import InputNumber from 'primevue/inputnumber';
import Dialog from 'primevue/dialog';

export default {
    components: {
        Button,
        DataTable,
        Column,
        Dropdown,
        InputNumber,
        Dialog
    },
    setup() {
        const toast = useToast();
        const discounts = ref([]);
        const discountTypes = ref([]);
        const editDialogVisible = ref(false);
        const selectedDiscount = ref(null);

        onMounted(async () => {
            await fetchDiscounts();
            await fetchDiscountTypes();
        });

        const fetchDiscounts = async () => {
            try {
                const response = await fetch('/store/{storeName}/discount-policies');
                discounts.value = await response.json();
            } catch (error) {
                toast.add({ severity: 'error', summary: 'Error', detail: 'Failed to fetch discounts', life: 3000 });
            }
        };

        const fetchDiscountTypes = async () => {
            try {
                const response = await fetch('/store/{storeName}/discount-conditions');
                discountTypes.value = await response.json();
            } catch (error) {
                toast.add({ severity: 'error', summary: 'Error', detail: 'Failed to fetch discount types', life: 3000 });
            }
        };

        const addDiscount = () => {
            selectedDiscount.value = { type: '', percent: 0 };
            editDialogVisible.value = true;
        };

        const saveDiscount = () => {
        // Implement save logic here
        editDialogVisible.value = false;
        toast.add({ severity: 'success', summary: 'Success', detail: 'Discount saved', life: 3000 });
        };

        const typeTemplate = (discount) => discount.type;
        const valueTemplate = (discount) => discount.percent;

        const actionTemplate = (discount) => {
            return h(Button, {
            icon: 'pi pi-pencil',
            class: 'p-button-rounded p-button-text',
            onClick: () => editDiscount(discount)
            });
        };

        const editDiscount = (discount) => {
            selectedDiscount.value = { ...discount };
            editDialogVisible.value = true;
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
            actionTemplate
        };
    }
};
</script>

<style scoped>
.discount-management {
    padding: 20px;
}

.p-field {
    margin-bottom: 1em;
}
</style>
