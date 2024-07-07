<template>
    <div class="discount-management">
        <SiteHeader :isLoggedIn="true" :username="username" @logout="logout" />
        <h2>Discount Management</h2>
        <div class="button-container">
            <Button label="Add Discount" icon="pi pi-plus" @click="showAddDiscountDialog" />
            <Button label="Add Condition" icon="pi pi-plus" @click="showAddConditionDialog" />
        </div>
        <Dialog header="Add Discount" v-model="addDiscountDialogVisible" :visible="addDiscountDialogVisible"
            @hide="addDiscountDialogVisible = false" :closable="false" blockScroll :draggable="false" modal>
            <div class="p-fluid">
                <div class="p-field">
                    <label for="discountType">Select discount type</label>
                    <Dropdown id="discountType" v-model="newDiscount.type" :options="discountTypes" optionLabel="label"
                        optionValue="value" @change="updateDiscountFields" />
                </div>
                <template v-if="newDiscount.type === 'Store'">
                    <div class="p-field">
                        <label for="storePercent">Set discount percent</label>
                        <InputNumber id="storePercent" v-model="newDiscount.percent" mode="decimal" min="0" max="100"
                            suffix="%" />
                    </div>
                </template>
                <template v-else-if="newDiscount.type === 'Category'">
                    <div class="p-field">
                        <label for="category">Select category</label>
                        <Dropdown id="category" v-model="newDiscount.category" :options="categoryOptions"
                            optionLabel="label" optionValue="value" />
                    </div>
                    <div class="p-field">
                        <label for="categoryPercent">Set discount percent</label>
                        <InputNumber id="categoryPercent" v-model="newDiscount.percent" mode="decimal" min="0" max="100"
                            suffix="%" />
                    </div>
                </template>
                <template v-else-if="newDiscount.type === 'Product'">
                    <div class="p-field">
                        <label for="productId">Set product ID</label>
                        <InputText id="productId" v-model="newDiscount.productId" />
                    </div>
                    <div class="p-field">
                        <label for="productPercent">Set discount percent</label>
                        <InputNumber id="productPercent" v-model="newDiscount.percent" mode="decimal" min="0" max="100"
                            suffix="%" />
                    </div>
                </template>
                <template v-else-if="newDiscount.type === 'Additive'">
                    <div class="p-field">
                        <label for="discount1">Select discount 1</label>
                        <Dropdown id="discount1" v-model="newDiscount.discount1" :options="discounts" optionLabel="type"
                            optionValue="id" />
                    </div>
                    <div class="p-field">
                        <label for="discount2">Select discount 2</label>
                        <Dropdown id="discount2" v-model="newDiscount.discount2" :options="discounts" optionLabel="type"
                            optionValue="id" />
                    </div>
                </template>
                <template v-else-if="newDiscount.type === 'Max'">
                    <div class="p-field">
                        <label for="discount1">Seelect discount 1</label>
                        <Dropdown id="discount1" v-model="newDiscount.discount1" :options="discounts" optionLabel="type"
                            optionValue="id" />
                    </div>
                    <div class="p-field">
                        <label for="discount2">Select discount 2</label>
                        <Dropdown id="discount2" v-model="newDiscount.discount2" :options="discounts" optionLabel="type"
                            optionValue="id" />
                    </div>
                </template>
                <template v-else-if="newDiscount.type === 'Conditional'">
                    <div class="p-field">
                        <label for="condition">Select condition</label>
                        <Dropdown id="condition" v-model="newDiscount.condition" :options="conditions"
                            optionLabel="type" optionValue="id" />
                    </div>
                    <div class="p-field">
                        <label for="discount">Select Discount</label>
                        <Dropdown id="discount" v-model="newDiscount.discount" :options="discounts" optionLabel="type"
                            optionValue="id" />
                    </div>
                </template>
                <template v-else-if="newDiscount.type === 'And'">
                    <div class="p-field">
                        <label for="condition1">Select condition 1</label>
                        <Dropdown id="condition1" v-model="newDiscount.condition1" :options="conditions"
                            optionLabel="type" optionValue="id" />
                    </div>
                    <div class="p-field">
                        <label for="condition2">Select condition 2</label>
                        <Dropdown id="condition2" v-model="newDiscount.condition2" :options="conditions"
                            optionLabel="type" optionValue="id" />
                    </div>
                    <div class="p-field">
                        <label for="discount">Select discount</label>
                        <Dropdown id="discount" v-model="newDiscount.discount" :options="discounts" optionLabel="type"
                            optionValue="id" />
                    </div>
                </template>
                <template v-else-if="newDiscount.type === 'Or'">
                    <div class="p-field">
                        <label for="condition1">Select condition 1</label>
                        <Dropdown id="condition1" v-model="newDiscount.condition1" :options="conditions"
                            optionLabel="type" optionValue="id" />
                    </div>
                    <div class="p-field">
                        <label for="condition2">Select condition 2</label>
                        <Dropdown id="condition2" v-model="newDiscount.condition2" :options="conditions"
                            optionLabel="type" optionValue="id" />
                    </div>
                    <div class="p-field">
                        <label for="discount">Select discount</label>
                        <Dropdown id="discount" v-model="newDiscount.discount" :options="discounts" optionLabel="type"
                            optionValue="id" />
                    </div>
                </template>
                <template v-else-if="newDiscount.type === 'Xor'">
                    <div class="p-field">
                        <label for="discount1">Select discount 1</label>
                        <Dropdown id="discount1" v-model="newDiscount.discount1" :options="discounts" optionLabel="type"
                            optionValue="id" />
                    </div>
                    <div class="p-field">
                        <label for="discount2">Select discount 2</label>
                        <Dropdown id="discount2" v-model="newDiscount.discount2" :options="discounts" optionLabel="type"
                            optionValue="id" />
                    </div>
                    <div class="p-field">
                        <label for="decider">Select decider condition</label>
                        <Dropdown id="decider" v-model="newDiscount.decider" :options="conditions" optionLabel="type"
                            optionValue="id" />
                    </div>
                </template>
            </div>
            <template #footer>
                <Button label="Cancel" icon="pi pi-times" @click="addDiscountDialogVisible = false"
                    class="p-button-text" />
                <Button label="Save" icon="pi pi-check" @click="saveDiscount" />
            </template>
        </Dialog>
        <Dialog header="Add Condition" v-model="addConditionDialogVisible" :visible="addConditionDialogVisible"
            @hide="addConditionDialogVisible = false" :closable="false" blockScroll :draggable="false" modal>
            <div class="p-fluid">
                <div class="p-field">
                    <label for="conditionType">Condition Type</label>
                    <Dropdown id="conditionType" v-model="newCondition.type" :options="conditionTypes"
                        optionLabel="label" optionValue="value" @change="updateConditionFields" />
                </div>
                <template v-if="newCondition.type === 'Category count'">
                    <div class="p-field">
                        <label for="categoryCount">Category Count</label>
                        <InputNumber id="categoryCount" v-model="newCondition.categoryCount" mode="decimal" min="0" />
                    </div>
                </template>
                <template v-else-if="newCondition.type === 'Product count'">
                    <div class="p-field">
                        <label for="productCount">Product Count</label>
                        <InputNumber id="productCount" v-model="newCondition.productCount" mode="decimal" min="0" />
                    </div>
                </template>
                <template v-else-if="newCondition.type === 'Total Sum'">
                    <div class="p-field">
                        <label for="totalSum">Total Sum</label>
                        <InputNumber id="totalSum" v-model="newCondition.totalSum" mode="decimal" min="0" />
                    </div>
                </template>
            </div>
            <template #footer>
                <Button label="Cancel" icon="pi pi-times" @click="addConditionDialogVisible = false"
                    class="p-button-text" />
                <Button label="Save" icon="pi pi-check" @click="saveCondition" />
            </template>
        </Dialog>
        <DataTable :value="discounts" responsiveLayout="scroll" class="p-mt-3">
            <Column field="type" header="Type" :body="typeTemplate"></Column>
            <Column field="value" header="Value" :body="valueTemplate"></Column>
            <Column header="Actions" :body="actionTemplate"></Column>
        </DataTable>
    </div>
</template>

<script>
import axios from 'axios';
import { ref, onMounted, h } from 'vue';
import { useToast } from 'primevue/usetoast';
import { useRoute } from 'vue-router';
import SiteHeader from './SiteHeader.vue';
import Button from 'primevue/button';
import DataTable from 'primevue/datatable';
import Column from 'primevue/column';
import Dropdown from 'primevue/dropdown';
import InputText from 'primevue/inputtext';
import InputNumber from 'primevue/inputnumber';
import Dialog from 'primevue/dialog';

export default {
    components: {
        SiteHeader,
        Button,
        DataTable,
        Column,
        Dropdown,
        InputText,
        InputNumber,
        Dialog
    },

    setup() {
        const username = ref(localStorage.getItem('username') || '');
        const token = ref(localStorage.getItem('token') || '');
        const toast = useToast();
        const route = useRoute();
        const storeName = route.params.storeName;
        const discounts = ref([]);
        const conditions = ref([]);
        const addDiscountDialogVisible = ref(false);
        const addConditionDialogVisible = ref(false);
        const newDiscount = ref({
            type: '',
            percent: 0,
            category: null,
            productId: '',
            discount1: null,
            discount2: null,
            condition: null,
            condition1: null,
            condition2: null,
            decider: null
        });
        const newCondition = ref({
            type: '',
            categoryCount: 0,
            productCount: 0,
            totalSum: 0
        });
        const discountTypes = ref([
            { label: 'Store', value: 'Store' },
            { label: 'Category', value: 'Category' },
            { label: 'Product', value: 'Product' },
            { label: 'Additive', value: 'Additive' },
            { label: 'Max', value: 'Max' },
            { label: 'Conditional', value: 'Conditional' },
            { label: 'And', value: 'And' },
            { label: 'Or', value: 'Or' },
            { label: 'Xor', value: 'Xor' }
        ]);
        const categoryOptions = ref([
            { label: 'Sport', value: 1 },
            { label: 'Art', value: 2 },
            { label: 'Food', value: 3 },
            { label: 'Clothes', value: 4 },
            { label: 'Films', value: 5 }
        ]);
        const conditionTypes = ref([
            { label: 'Category count', value: 'Category count' },
            { label: 'Product count', value: 'Product count' },
            { label: 'Total Sum', value: 'Total Sum' }
        ]);

        onMounted(async () => {
            await fetchDiscounts();
            await fetchConditions();
        });

        const fetchDiscounts = async () => {
            try {
                const url = `http://localhost:8082/api/trading/store/${storeName}/discount-policies`;
                const response = await axios.get(url, {
                    params: {
                        username: username.value,
                        token: token.value
                    }
                });
                discounts.value = response.data;
            } catch (error) {
                console.error('Error fetching discounts:', error);
                toast.add({ severity: 'error', summary: 'Error', detail: error.message, life: 3000 });
            }
        };

        const fetchConditions = async () => {
            try {
                const url = `http://localhost:8082/api/trading/store/${storeName}/discount-conditions`;
                const response = await axios.get(url, {
                    params: {
                        username: username.value,
                        token: token.value
                    }
                });
                conditions.value = response.data;
            } catch (error) {
                console.error('Error fetching conditions:', error);
                toast.add({ severity: 'error', summary: 'Error', detail: error.message, life: 3000 });
            }
        };

        const showAddDiscountDialog = () => {
            newDiscount.value = {
                type: '',
                percent: 0,
                category: null,
                productId: '',
                discount1: null,
                discount2: null,
                condition: null,
                condition1: null,
                condition2: null,
                decider: null
            };
            addDiscountDialogVisible.value = true;
        };

        const showAddConditionDialog = () => {
            newCondition.value = {
                type: '',
                categoryCount: 0,
                productCount: 0,
                totalSum: 0
            };
            addConditionDialogVisible.value = true;
        };

        const saveDiscount = () => {
            // Implement save logic for discount
            addDiscountDialogVisible.value = false;
            toast.add({ severity: 'success', summary: 'Success', detail: 'Discount saved', life: 3000 });
        };

        const saveCondition = () => {
            // Implement save logic for condition
            addConditionDialogVisible.value = false;
            toast.add({ severity: 'success', summary: 'Success', detail: 'Condition saved', life: 3000 });
        };

        const updateDiscountFields = () => {
            // Implement logic to update additional fields based on selected discount type
        };

        const updateConditionFields = () => {
            // Implement logic to update additional fields based on selected condition type
        };

        const typeTemplate = (discount) => discount.type;
        const valueTemplate = (discount) => discount.value;

        const actionTemplate = (discount) => {
            return h(Button, {
                icon: 'pi pi-pencil',
                class: 'p-button-rounded p-button-text',
                onClick: () => editDiscount(discount)
            });
        };

        const editDiscount = (/*discount*/) => {
            // Implement edit functionality
        };

        return {
            username,
            token,
            discounts,
            conditions,
            discountTypes,
            categoryOptions,
            conditionTypes,
            addDiscountDialogVisible,
            addConditionDialogVisible,
            newDiscount,
            newCondition,
            showAddDiscountDialog,
            showAddConditionDialog,
            saveDiscount,
            saveCondition,
            updateDiscountFields,
            updateConditionFields,
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

.button-container button {
    margin-right: 12px;
    margin-bottom: 12px;
}
</style>