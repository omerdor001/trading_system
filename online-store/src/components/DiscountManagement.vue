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
                    <!-- <div class="p-field">
                        <label for="discount1">Select discount 1</label>
                        <Dropdown id="discount1" v-model="newDiscount.discount1" :options="discounts" optionLabel="type"
                            optionValue="id" />
                    </div>
                    <div class="p-field">
                        <label for="discount2">Select discount 2</label>
                        <Dropdown id="discount2" v-model="newDiscount.discount2" :options="discounts" optionLabel="type"
                            optionValue="id" />
                    </div> -->
                </template>
                <template v-else-if="newDiscount.type === 'Max'">
                    <!-- <div class="p-field">
                        <label for="discount1">Seelect discount 1</label>
                        <Dropdown id="discount1" v-model="newDiscount.discount1" :options="discounts" optionLabel="type"
                            optionValue="id" />
                    </div>
                    <div class="p-field">
                        <label for="discount2">Select discount 2</label>
                        <Dropdown id="discount2" v-model="newDiscount.discount2" :options="discounts" optionLabel="type"
                            optionValue="id" />
                    </div> -->
                </template>
                <template v-else-if="newDiscount.type === 'Conditional'">
                    <!-- <div class="p-field">
                        <label for="condition">Select condition</label>
                        <Dropdown id="condition" v-model="newDiscount.condition" :options="conditions"
                            optionLabel="type" optionValue="id" />
                    </div>
                    <div class="p-field">
                        <label for="discount">Select Discount</label>
                        <Dropdown id="discount" v-model="newDiscount.discount" :options="discounts" optionLabel="type"
                            optionValue="id" />
                    </div> -->
                </template>
                <template v-else-if="newDiscount.type === 'And'">
                    <!-- <div class="p-field">
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
                    </div> -->
                </template>
                <template v-else-if="newDiscount.type === 'Or'">
                    <!-- <div class="p-field">
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
                    </div> -->
                </template>
                <template v-else-if="newDiscount.type === 'Xor'">
                    <!-- <div class="p-field">
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
                    </div> -->
                </template>
            </div>
            <template #footer>
                <Button label="Cancel" icon="pi pi-times" @click="addDiscountDialogVisible = false"
                    class="p-button-text" />
                <Button label="Save" icon="pi pi-check" @click="saveDiscount" />
            </template>
        </Dialog>
        <Dialog header="Add Condition" v-model="addConditionDialogVisible" style="width: 20%;"
            :headerStyle="{ 'text-align': 'center' }"> :visible="addConditionDialogVisible"
            @hide="addConditionDialogVisible = false" :closable="false" blockScroll :draggable="false" modal>
            <div class="p-fluid">
                <div class="p-field">
                    <label for="conditionType">Select condition type</label>
                    <Dropdown id="conditionType" v-model="newCondition.type" :options="conditionTypes"
                        optionLabel="label" optionValue="value" @change="updateConditionFields" />
                </div>
                <template v-if="newCondition.type === 'Category count'">
                    <div class="p-field">
                        <label for="categorySelect">Select category</label>
                        <Dropdown id="categorySelect" v-model="newCondition.category" :options="categoryOptions"
                            optionLabel="label" optionValue="value" />
                        <label for="categoryCount">Set category count</label>
                        <InputNumber id="categoryCount" v-model="newCondition.categoryCount" mode="decimal" min="0" />
                    </div>
                </template>
                <template v-else-if="newCondition.type === 'Product count'">
                    <div class="p-field">
                        <label for="productId">Set product ID</label>
                        <InputNumber id="productId" v-model="newCondition.productId" mode="decimal" min="0" />
                        <label for="productCount">Set product count</label>
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
            <Column field="type" header="Type" style="width: 20%;" :headerStyle="{ 'text-align': 'center' }"
                :body="typeTemplate">
            </Column>
            <Column field="details" header="Details" style="width: 20%;" :headerStyle="{ 'text-align': 'center' }"
                :body="valueTemplate"></Column>
            <Column header="Actions" style="width: 30%;">
                <template #body="rowData">
                    <div class="action-buttons">
                        <Button
                            v-if="rowData.type === 'Store Discount' || rowData.type === 'Category Discount' || rowData.type === 'Product Discount'"
                            @click="showEditPercentDialog = true">Edit percent</Button>

                        <Button v-if="rowData.type === 'Category Discount'" @click="showEditCategoryDialog = true">Edit
                            category</Button>

                        <Button v-if="rowData.type === 'Product Discount'" @click="showEditProductDialog = true">Edit
                            product ID</Button>

                        <Button
                            v-if="rowData.type === 'Additive Discount' || rowData.type === 'Maximum Discount' || rowData.type === 'Xor Discount'"
                            @click="showEditFirstDiscountDialog = true">Edit first discount</Button>

                        <Button
                            v-if="rowData.type === 'Additive Discount' || rowData.type === 'Maximum Discount' || rowData.type === 'Xor Discount'"
                            @click="showEditSecondDiscountDialog = true">Edit second discount</Button>

                        <Button
                            v-if="rowData.type === 'Conditional Discount' || rowData.type === 'And Discount' || rowData.type === 'Or Discount'"
                            @click="showEditFirstConditionDialog = true">Edit first condition</Button>

                        <Button
                            v-if="rowData.type === 'Conditional Discount' || rowData.type === 'Or Discount' || rowData.type === 'And Discount'"
                            @click="showEditThenDiscountDialog = true">Edit discount</Button>

                        <Button v-if="rowData.type === 'And Discount' || rowData.type === 'Or Discount'"
                            @click="showEditSecondConditionDialog = true">Edit
                            second condition</Button>

                        <Button v-if="rowData.type === 'Xor Discount'"
                            @click="showEditDeciderConditionDialog = true">Edit
                            decider condition</Button>

                        <Button @click="showDeleteDiscountDialog = true">Delete Discount</Button>
                    </div>
                </template>
                <template #header>
                    <span class="flex-1 text-center"></span>
                </template>
            </Column>
        </DataTable>
        <!-- Edit Percent Dialog -->
        <Dialog v-model="showEditPercentDialog" :visible="showEditPercentDialog" header="Edit Percent">
            <InputText v-model="editPercentValue" />
            <Button @click="editDiscountPercent(rowData.index)">Save</Button>
            <Button @click="showEditPercentDialog = false">Cancel</Button>
        </Dialog>

        <!-- Edit Category Dialog -->
        <Dialog v-model="showEditCategoryDialog" :visible="showEditCategoryDialog" header="Edit Category">
            <Dropdown v-model="editCategoryValue" :options="categoryOptions" optionLabel="label" />
            <Button @click="editCategoryDiscountCategory(rowData.index)">Save</Button>
            <Button @click="showEditCategoryDialog = false">Cancel</Button>
        </Dialog>

        <!-- Edit Product ID Dialog -->
        <Dialog v-model="showEditProductDialog" :visible="showEditProductDialog" header="Edit Product ID">
            <InputText v-model="editProductIDValue" />
            <Button @click="editProductDiscountID(rowData.index)">Save</Button>
            <Button @click="showEditProductDialog = false">Cancel</Button>
        </Dialog>

        <!-- Edit First Discount Dialog -->
        <Dialog v-model="showEditFirstDiscountDialog" :visible="showEditFirstDiscountDialog"
            header="Edit First Discount">
            <!-- Example inputs, adjust as per your application -->
            <InputText v-model="editFirstDiscountValue" />
            <Button @click="editFirstDiscount(rowData.index)">Save</Button>
            <Button @click="showEditFirstDiscountDialog = false">Cancel</Button>
        </Dialog>

        <!-- Edit Second Discount Dialog -->
        <Dialog v-model="showEditSecondDiscountDialog" :visible="showEditSecondDiscountDialog"
            header="Edit Second Discount">
            <InputText v-model="editSecondDiscountValue" />
            <Button @click="editSecondDiscount(rowData.index)">Save</Button>
            <Button @click="showEditSecondDiscountDialog = false">Cancel</Button>
        </Dialog>

        <!-- Edit First Condition Dialog -->
        <Dialog v-model="showEditFirstConditionDialog" :visible="showEditFirstConditionDialog"
            header="Edit First Condition">
            <InputText v-model="editFirstConditionValue" />
            <Button @click="editFirstCondition(rowData.index)">Save</Button>
            <Button @click="showEditFirstConditionDialog = false">Cancel</Button>
        </Dialog>

        <!-- Edit Then Discount Dialog -->
        <Dialog v-model="showEditThenDiscountDialog" :visible="showEditThenDiscountDialog" header="Edit Then Discount">
            <InputText v-model="editThenDiscountValue" />
            <Button @click="editThenDiscount(rowData.index)">Save</Button>
            <Button @click="showEditThenDiscountDialog = false">Cancel</Button>
        </Dialog>

        <!-- Edit Second Condition Dialog -->
        <Dialog v-model="showEditSecondConditionDialog" :visible="showEditSecondConditionDialog"
            header="Edit Second Condition">
            <InputText v-model="editSecondConditionValue" />
            <Button @click="editSecondCondition(rowData.index)">Save</Button>
            <Button @click="showEditSecondConditionDialog = false">Cancel</Button>
        </Dialog>

        <!-- Edit Decider Condition Dialog -->
        <Dialog v-model="showEditDeciderConditionDialog" :visible="showEditDeciderConditionDialog"
            header="Edit Decider Condition">
            <InputText v-model="editDeciderConditionValue" />
            <Button @click="editDeciderCondition(rowData.index)">Save</Button>
            <Button @click="showEditDeciderConditionDialog = false">Cancel</Button>
        </Dialog>

        <!-- Delete Discount Dialog -->
        <Dialog v-model="showDeleteDiscountDialog" :visible="showDeleteDiscountDialog" header="Delete Discount">
            <InputText v-model="editDeciderConditionValue" />
            <Button @click="editDeciderCondition(rowData.index)">Save</Button>
            <Button @click="showEditDeciderConditionDialog = false">Cancel</Button>
        </Dialog>
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
        const showEditPercentDialog = ref(false);
        const showEditFirstDiscountDialog = ref(false);
        const showEditSecondDiscountDialog = ref(false);
        const showEditFirstConditionDialog = ref(false);
        const showEditThenDiscountDialog = ref(false);
        const showEditSecondConditionDialog = ref(false);
        const showEditDeciderConditionDialog = ref(false);
        const showDeleteDiscountDialog = ref(false);
        const newDiscount = ref({
            type: '',
            percent: 0,
            category: null,
            productId: -1,
            discount1: null,
            discount2: null,
            condition: null,
            condition1: null,
            condition2: null,
            decider: null
        });
        const newCondition = ref({
            type: '',
            category: null,
            productId: -1,
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
            { label: 'Category count', value: 'Category Count' },
            { label: 'Product count', value: 'Product Count' },
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
                discounts.value = response.data.map(discount => ({
                    type: getDiscountTypeLabel(discount.type),
                    details: createDetails(discount)
                }));
                console.log(discounts.value);
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

        const saveDiscount = async () => {
            switch (newDiscount.value.type) {
                case 'Store':
                    try {
                        console.log(`${storeName} store discount with value: ${newDiscount.value.percent}`)
                        const url = `http://localhost:8082/api/trading/store/${storeName}/discounts/store`;
                        await axios.post(url, null, {
                            params: {
                                username: username.value,
                                token: token.value,
                                storeName: storeName,
                                discountPercent: newDiscount.value.percent
                            }
                        });
                        fetchDiscounts();
                    } catch (error) {
                        toast.add({ severity: 'error', summary: 'Error', detail: error.message, life: 3000 });
                        return;
                    }
                    break;
                case 'Category':
                    try {
                        const url = `http://localhost:8082/api/trading/store/${storeName}/discounts/category-percentage`;
                        await axios.post(url, null, {
                            params: {
                                username: username.value,
                                token: token.value,
                                storeName: storeName,
                                category: newDiscount.value.category,
                                discountPercent: newDiscount.value.percent
                            }
                        });
                        fetchDiscounts();
                    } catch (error) {
                        toast.add({ severity: 'error', summary: 'Error', detail: error.message, life: 3000 });
                        return;
                    }
                    break;
                case 'Product':
                    try {
                        const url = `http://localhost:8082/api/trading/store/${storeName}/discounts/product-percentage`;
                        await axios.post(url, null, {
                            params: {
                                username: username.value,
                                token: token.value,
                                storeName: storeName,
                                productId: newDiscount.value.category,
                                discountPercent: newDiscount.value.percent
                            }
                        });
                        fetchDiscounts();
                    } catch (error) {
                        toast.add({ severity: 'error', summary: 'Error', detail: error.message, life: 3000 });
                        return;
                    }
                    break;
                case 'Additive':
                    try {
                        const url = `http://localhost:8082/api/trading/store/${storeName}/discounts/additive`;
                        await axios.post(url, null, {
                            params: {
                                username: username.value,
                                token: token.value,
                                storeName: storeName,
                            }
                        });
                        fetchDiscounts();
                    } catch (error) {
                        toast.add({ severity: 'error', summary: 'Error', detail: error.message, life: 3000 });
                        return;
                    }
                    break;
                case 'Max':
                    try {
                        const url = `http://localhost:8082/api/trading/store/${storeName}/discounts/max`;
                        await axios.post(url, null, {
                            params: {
                                username: username.value,
                                token: token.value,
                                storeName: storeName,
                            }
                        });
                        fetchDiscounts();
                    } catch (error) {
                        toast.add({ severity: 'error', summary: 'Error', detail: error.message, life: 3000 });
                        return;
                    }
                    break;
                case 'Conditional':
                    try {
                        const url = `http://localhost:8082/api/trading/store/${storeName}/discounts/conditional`;
                        await axios.post(url, null, {
                            params: {
                                username: username.value,
                                token: token.value,
                                storeName: storeName,
                            }
                        });
                        fetchDiscounts();
                    } catch (error) {
                        toast.add({ severity: 'error', summary: 'Error', detail: error.message, life: 3000 });
                        return;
                    }
                    break;
                case 'And':
                    try {
                        const url = `http://localhost:8082/api/trading/store/${storeName}/discounts/and`;
                        await axios.post(url, null, {
                            params: {
                                username: username.value,
                                token: token.value,
                                storeName: storeName,
                            }
                        });
                        fetchDiscounts();
                    } catch (error) {
                        toast.add({ severity: 'error', summary: 'Error', detail: error.message, life: 3000 });
                        return;
                    }
                    break;
                case 'Or':
                    try {
                        const url = `http://localhost:8082/api/trading/store/${storeName}/discounts/or`;
                        await axios.post(url, null, {
                            params: {
                                username: username.value,
                                token: token.value,
                                storeName: storeName,
                            }
                        });
                        fetchDiscounts();
                    } catch (error) {
                        toast.add({ severity: 'error', summary: 'Error', detail: error.message, life: 3000 });
                        return;
                    }
                    break;
                case 'Xor':
                    try {
                        const url = `http://localhost:8082/api/trading/store/${storeName}/discounts/xor`;
                        await axios.post(url, null, {
                            params: {
                                username: username.value,
                                token: token.value,
                                storeName: storeName,
                            }
                        });
                        fetchDiscounts();
                    } catch (error) {
                        toast.add({ severity: 'error', summary: 'Error', detail: error.message, life: 3000 });
                        return;
                    }
                    break;
                default:
                    console.log('Unknown discount type:', newDiscount.value.type);
            }
            addDiscountDialogVisible.value = false;
            toast.add({ severity: 'success', summary: 'Success', detail: 'Discount saved', life: 3000 });
        };

        const saveCondition = async () => {
            switch (newCondition.value.type) {
                case 'Category Count':
                    try {
                        const url = `http://localhost:8082/api/trading/store/${storeName}/conditions/category-count`;
                        await axios.post(url, null, {
                            params: {
                                username: username.value,
                                token: token.value,
                                storeName: storeName,
                                category: newCondition.value.category,
                                count: newCondition.value.categoryCount,
                            }
                        });
                        fetchConditions();
                    } catch (error) {
                        toast.add({ severity: 'error', summary: 'Error', detail: error.message, life: 3000 });
                        return;
                    }
                    break;
                case 'Product Count':
                    try {
                        const url = `http://localhost:8082/api/trading/store/${storeName}/conditions/product-count`;
                        await axios.post(url, null, {
                            params: {
                                username: username.value,
                                token: token.value,
                                storeName: storeName,
                                productId: newCondition.value.productId,
                                count: newCondition.value.productCount,
                            }
                        });
                        fetchConditions();
                    } catch (error) {
                        toast.add({ severity: 'error', summary: 'Error', detail: error.message, life: 3000 });
                        return;
                    }
                    break;
                case 'Toal Sum':
                    try {
                        const url = `http://localhost:8082/api/trading/store/${storeName}/conditions/total-sum`;
                        await axios.post(url, null, {
                            params: {
                                username: username.value,
                                token: token.value,
                                storeName: storeName,
                                requiredSum: newCondition.value.totalSum,
                            }
                        });
                        fetchConditions();
                    } catch (error) {
                        toast.add({ severity: 'error', summary: 'Error', detail: error.message, life: 3000 });
                        return;
                    }
                    break;
                default:
                    console.log('Unknown condition type:', newDiscount.value.type);
            }
            addConditionDialogVisible.value = false;
            toast.add({ severity: 'success', summary: 'Success', detail: 'Condition saved', life: 3000 });
        };

        const getDiscountTypeLabel = (type) => {
            const typeMap = {
                'percentageStore': 'Store Discount',
                'percentageCategory': 'Category Discount',
                'percentageProduct': 'Product Discount',
                'additive': 'Additive Discount',
                'max': 'Maximum Discount',
                'conditional': 'Conditional Discount',
                'and': 'And Discount',
                'or': 'Or Discount',
                'xor': 'Xor Discount'
            };
            return typeMap[type] || type;
        };

        const createDetails = (discount) => {
            switch (discount.type) {
                case 'percentageStore':
                    return discount.details = `Percentage: ${discount.percent}%`;
                case 'percentageCategory':
                    return discount.details = `Category: ${categoryOptions.value.find(opt => opt.value === discount.category)?.label} Percentage: ${discount.percent}%`;
                case 'percentageProduct':
                    return discount.details = `Product Id: ${discount.productId} Percentage: ${discount.percent}%`;
                case 'additive':
                    return discount.details = `First discount: ${getDiscountTypeLabel(discount.first.type)} ${createDetails(discount.first)} Second discount: ${getDiscountTypeLabel(discount.first.type)} ${createDetails(discount.second)}`;
                case 'max':
                    return discount.details = `First discount: ${getDiscountTypeLabel(discount.first.type)} ${createDetails(discount.first)} Second discount: ${getDiscountTypeLabel(discount.first.type)} ${createDetails(discount.second)}`;
                case 'conditional':
                    return discount.details = `Condition: ${discount.first} discount: ${getDiscountTypeLabel(discount.second.type)} ${createDetails(discount.second)}`;
                case 'and':
                    return discount.details = `First condition: ${discount.first} Second condition: ${discount.second} Discount: ${getDiscountTypeLabel(discount.then.type)} ${createDetails(discount.then)}`;
                case 'or':
                    return discount.details = `First condition: ${discount.first} Second condition: ${discount.second} Discount: ${getDiscountTypeLabel(discount.then.type)} ${createDetails(discount.then)}`;
                case 'xor':
                    return discount.details = `First discount: ${getDiscountTypeLabel(discount.first.type)} ${createDetails(discount.first)} Second discount: ${getDiscountTypeLabel(discount.first.type)} ${createDetails(discount.second)} Decider condition: ${discount.decider}`;
            }
        }

        const typeTemplate = (rowData) => {
            return getDiscountTypeLabel(rowData.type);
        };

        const valueTemplate = (rowData) => {
            return h('div', {}, [
                h('span', {}, rowData.details)
            ]);
        };

        const detailsTemplate = (rowData) => {
            return h('div', {}, createDetails(rowData));
        };

        const deleteDiscount = async (selectedIndex) => {
            try {
                const url = `http://localhost:8082/api/trading/store/${storeName}/discounts/remove/${selectedIndex}`;
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
        }

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
            showEditPercentDialog,
            showEditFirstDiscountDialog,
            showEditSecondDiscountDialog,
            showEditFirstConditionDialog,
            showEditThenDiscountDialog,
            showEditSecondConditionDialog,
            showEditDeciderConditionDialog,
            showDeleteDiscountDialog,
            newDiscount,
            newCondition,
            showAddDiscountDialog,
            showAddConditionDialog,
            saveDiscount,
            saveCondition,
            typeTemplate,
            valueTemplate,
            detailsTemplate,
            deleteDiscount
        };
    },
    methods: {}
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

.action-buttons button {
    margin-right: 12px;
}

.p-datatable {
    margin-inline: 24rem;
}

.p-datatable {
    justify-content: center;
}
</style>