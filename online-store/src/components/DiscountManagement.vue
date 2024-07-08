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
                        optionValue="value" />
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
                        <InputNumber id="productId" v-model="newDiscount.productId" />
                    </div>
                    <div class="p-field">
                        <label for="productPercent">Set discount percent</label>
                        <InputNumber id="productPercent" v-model="newDiscount.percent" mode="decimal" min="0" max="100"
                            suffix="%" />
                    </div>
                </template>
                <template v-else-if="newDiscount.type === 'Additive'">
                </template>
                <template v-else-if="newDiscount.type === 'Max'">
                </template>
                <template v-else-if="newDiscount.type === 'Conditional'">
                </template>
                <template v-else-if="newDiscount.type === 'And'">
                </template>
                <template v-else-if="newDiscount.type === 'Or'">
                </template>
                <template v-else-if="newDiscount.type === 'Xor'">
                </template>
            </div>
            <template #footer>
                <Button label="Cancel" icon="pi pi-times" @click="addDiscountDialogVisible = false"
                    class="p-button-text" />
                <Button label="Save" icon="pi pi-check" @click="saveDiscount" />
            </template>
        </Dialog>
        <Dialog header="Add Condition" v-model="addConditionDialogVisible" style="width: 20%;"
            :headerStyle="{ 'text-align': 'center' }" :visible="addConditionDialogVisible"
            @hide="addConditionDialogVisible = false" :closable="false" blockScroll :draggable="false" modal>
            <div class="p-fluid">
                <div class="p-field">
                    <label for="conditionType">Select condition type</label>
                    <Dropdown id="conditionType" v-model="newCondition.type" :options="conditionTypes"
                        optionLabel="label" optionValue="value" />
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
                <template v-else-if="newCondition.type === 'Product Count'">
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
        <DataTable :value="discounts" title="Discount" responsiveLayout="scroll" class="p-mt-3">
            <template #header>
                <div class="flex flex-wrap items-center justify-between gap-2">
                    <span class="text-xl font-bold">Discounts</span>
                </div>
            </template>
            <Column field="type" header="Type" style="width: 20%;" :headerStyle="{ 'text-align': 'center' }"
                :body="typeTemplate">
            </Column>
            <Column field="details" header="Details" style="width: 20%;" :headerStyle="{ 'text-align': 'center' }"
                :body="valueTemplate"></Column>
            <Column header="Actions" style="width: 30%;">
                <template #body="rowData">
                    <div class="action-buttons">
                        <Button
                            v-if="rowData.data.type === 'Store Discount' || rowData.data.type === 'percentageStore' || rowData.data.type === 'Category Discount' || rowData.data.type === 'Product Discount'"
                            @click="showEditPercentDialog = true">Edit percent
                        </Button>

                        <Button v-if="rowData.data.type === 'Category Discount'" @click="showEditCategoryDialog = true">Edit
                            category
                        </Button>

                        <Button v-if="rowData.data.type === 'Product Discount'" @click="showEditProductDialog = true">Edit
                            product ID
                        </Button>

                        <Button
                            v-if="rowData.data.type === 'Additive Discount' || rowData.data.type === 'Maximum Discount' || rowData.data.type === 'Xor Discount'"
                            @click="showEditFirstDiscountDialog = true">Edit first discount
                        </Button>

                        <Button
                            v-if="rowData.data.type === 'Additive Discount' || rowData.data.type === 'Maximum Discount' || rowData.data.type === 'Xor Discount'"
                            @click="showEditSecondDiscountDialog = true">Edit second discount
                        </Button>

                        <Button
                            v-if="rowData.data.type === 'Conditional Discount' || rowData.data.type === 'And Discount' || rowData.data.type === 'Or Discount'"
                            @click="showEditFirstConditionDialog = true">Edit first condition
                        </Button>

                        <Button
                            v-if="rowData.data.type === 'Conditional Discount' || rowData.data.type === 'Or Discount' || rowData.data.type === 'And Discount'"
                            @click="showEditThenDiscountDialog = true">Edit discount
                        </Button>

                        <Button v-if="rowData.data.type === 'And Discount' || rowData.data.type === 'Or Discount'"
                            @click="showEditSecondConditionDialog = true">Edit second condition
                        </Button>

                        <Button v-if="rowData.data.type === 'Xor Discount'"
                            @click="showEditDeciderConditionDialog = true">Edit decider condition
                        </Button>

                        <Button @click="showDeleteDiscountDialog = true">Delete Discount

                        </Button>
                    </div>
                    <!-- Edit Percent Dialog -->
                    <Dialog v-model="showEditPercentDialog" :visible="showEditPercentDialog" header="Edit Percent" closable="false">
                        <InputNumber v-model="editPercentValue" />
                        <Button @click="editDiscountPercent(rowData.index, editPercentValue)">Save</Button>
                        <Button @click="showEditPercentDialog = false">Cancel</Button>
                    </Dialog>

                    <!-- Edit Category Dialog -->
                    <Dialog v-model="showEditCategoryDialog" :visible="showEditCategoryDialog" header="Edit Category">
                        <Dropdown v-model="editCategoryValue" :options="categoryOptions" optionLabel="label" />
                        <Button @click="editCategoryDiscountCategory(rowData.index, editCategoryValue)">Save</Button>
                        <Button @click="showEditCategoryDialog = false">Cancel</Button>
                    </Dialog>

                    <!-- Edit Product ID Dialog -->
                    <Dialog v-model="showEditProductDialog" :visible="showEditProductDialog" header="Edit Product ID">
                        <InputNumber v-model="editProductIDValue" />
                        <Button @click="editProductDiscountID(rowData.index, editProductDiscountID)">Save</Button>
                        <Button @click="showEditProductDialog = false">Cancel</Button>
                    </Dialog>

                    <!-- Edit First Discount Dialog -->
                    <Dialog v-model="showEditFirstDiscountDialog" :visible="showEditFirstDiscountDialog"
                        header="Edit First Discount">
                        <Dropdown v-model="editFirstDiscountIndex" :options=formattedDiscounts() optionLabel="label"
                            optionValue="value" />
                        <Button @click="editFirstDiscount(rowData.index, editFirstDiscountIndex)">Save</Button>
                        <Button @click="showEditFirstDiscountDialog = false">Cancel</Button>
                    </Dialog>

                    <!-- Edit Second Discount Dialog -->
                    <Dialog v-model="showEditSecondDiscountDialog" :visible="showEditSecondDiscountDialog"
                        header="Edit Second Discount">
                        <Dropdown v-model="editSecondDiscountIndex" :options=formattedDiscounts() optionLabel="label"
                            optionValue="value" />
                        <Button @click="editSecondDiscount(rowData.index, editSecondDiscountIndex)">Save</Button>
                        <Button @click="showEditSecondDiscountDialog = false">Cancel</Button>
                    </Dialog>

                    <!-- Edit First Condition Dialog -->
                    <Dialog v-model="showEditFirstConditionDialog" :visible="showEditFirstConditionDialog"
                        header="Edit First Condition">
                        <Dropdown v-model="editFirstConditionIndex" :options=formattedConditions() optionLabel="label"
                            optionValue="value" />
                        <Button @click="editFirstCondition(rowData.index, editFirstConditionIndex)">Save</Button>
                        <Button @click="showEditFirstConditionDialog = false">Cancel</Button>
                    </Dialog>

                    <!-- Edit Second Condition Dialog -->
                    <Dialog v-model="showEditSecondConditionDialog" :visible="showEditSecondConditionDialog"
                        header="Edit Second Condition">
                        <Dropdown v-model="editSecondConditionIndex" :options=formattedConditions() optionLabel="label"
                            optionValue="value" />
                        <Button @click="editSecondCondition(rowData.index, editSecondConditionIndex)">Save</Button>
                        <Button @click="showEditSecondConditionDialog = false">Cancel</Button>
                    </Dialog>

                    <!-- Edit Then Discount Dialog -->
                    <Dialog v-model="showEditThenDiscountDialog" :visible="showEditThenDiscountDialog"
                        header="Edit Then Discount">
                        <Dropdown v-model="editThenDiscountIndex" :options=formattedDiscounts() optionLabel="label"
                            optionValue="value" />
                        <Button @click="editThenDiscount(rowData.index, editThenDiscountIndex)">Save</Button>
                        <Button @click="showEditThenDiscountDialog = false">Cancel</Button>
                    </Dialog>

                    <!-- Edit Decider Condition Dialog -->
                    <Dialog v-model="showEditDeciderConditionDialog" :visible="showEditDeciderConditionDialog"
                        header="Edit Decider Condition">
                        <Dropdown v-model="editThenDiscountIndex" :options=formattedConditions() optionLabel="label"
                            optionValue="value" />
                        <Button @click="editDeciderCondition(rowData.index, editDeciderConditionIndex)">Save</Button>
                        <Button @click="showEditDeciderConditionDialog = false">Cancel</Button>
                    </Dialog>

                    <!-- Delete Discount Dialog -->
                    <Dialog v-model="showDeleteDiscountDialog" :visible="showDeleteDiscountDialog"
                        header="Delete Discount">
                        <Button @click="deleteDiscount(rowData.index)">Save</Button>
                        <Button @click="showDeleteDiscountDialog = false">Cancel</Button>
                    </Dialog>
                </template>
                <template #header>
                    <span class="flex-1 text-center"></span>
                </template>
            </Column>
        </DataTable>

        <DataTable :value="conditions" responsiveLayout="scroll" class="p-mt-3">
            <template #header>
                <div class="flex flex-wrap items-center justify-between gap-2">
                    <span class="text-xl font-bold">Conditions</span>
                </div>
            </template>
            <Column field="type" header="Type" style="width: 20%;" :headerStyle="{ 'text-align': 'center' }"
                :body="typeCondTemplate">
            </Column>
            <Column field="details" header="Details" style="width: 20%;" :headerStyle="{ 'text-align': 'center' }"
                :body="valueTemplate"></Column>
            <Column header="Actions" style="width: 30%;">
                <template #body="rowData">
                    <div class="action-buttons">
                        <Button v-if="rowData.data.type === 'Category Count' || rowData.data.type === 'Product Count'"
                            @click="showEditCountDialog = true">Edit count
                        </Button>

                        <Button v-if="rowData.data.type === 'Category Count'" @click="showEditCategoryCondDialog = true">Edit
                            category
                        </Button>

                        <Button v-if="rowData.data.type === 'Product Count'" @click="showEditProductCondDialog = true">
                            Edit product ID
                        </Button>

                        <Button v-if="rowData.data.type === 'Total Sum'" @click="showEditTotalSumDialog = true">Edit
                            total sum
                        </Button>

                        <Button @click="showDeleteCondDialog = true">
                            Delete Condition
                        </Button>
                    </div>
                    <!-- Edit Cond Count Dialog -->
                    <Dialog v-model="showEditCountDialog" :visible="showEditCountDialog" header="Edit Count">
                        <label for="condCount">Set condition count</label>
                        <InputNumber id="condCount" v-model="editCountValue" />
                        <Button @click="editConditionCount(rowData.index, editCountValue)">Save</Button>
                        <Button @click="showEditCountDialog = false">Cancel</Button>
                    </Dialog>

                    <!-- Edit Cond Category Dialog -->
                    <Dialog v-model="showEditCategoryCondDialog" :visible="showEditCategoryCondDialog"
                        header="Edit Category">
                        <label for="condCategory">Set condition category</label>
                        <Dropdown id="condCategory" v-model="editCondCategoryValue" :options="categoryOptions"
                            optionLabel="label" />
                        <Button @click="editConditionCategory(rowData.index, editCondCategoryValue)">Save</Button>
                        <Button @click="showEditCategoryCondDialog = false">Cancel</Button>
                    </Dialog>

                    <!-- Edit Cond Product ID Dialog -->
                    <Dialog v-model="showEditProductCondDialog" :visible="showEditProductCondDialog"
                        header="Edit Product ID">
                        <label for="condProductId">Set condition product id</label>
                        <InputNumber id="condProductId" v-model="editCondProductIDValue" />
                        <Button @click="editConditionProductID(rowData.index, editCondProductIDValue)">Save</Button>
                        <Button @click="showEditProductCondDialog = false">Cancel</Button>
                    </Dialog>

                    <!-- Edit Cond Total Sum Dialog -->
                    <Dialog v-model="showEditTotalSumDialog" :visible="showEditTotalSumDialog" header="Edit Total Sum">
                        <label for="condTotalSum">Set category count</label>
                        <InputNumber id="condTotalSum" v-model="editTotalSumValue" />
                        <Button @click="editConditionTotalSum(rowData.index, editTotalSumValue)">Save</Button>
                        <Button @click="showEditTotalSumDialog = false">Cancel</Button>
                    </Dialog>

                    <!-- Delete Cond Dialog -->
                    <Dialog v-model="showDeleteCondDialog" :visible="showDeleteCondDialog" header="Delete Condition">
                        <Button @click="deleteCondition(rowData.index)">Save</Button>
                        <Button @click="showDeleteCondDialog = false">Cancel</Button>
                    </Dialog>
                </template>
                <template #header>
                    <span class="flex-1 text-center"></span>
                </template>
            </Column>
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
import InputNumber from 'primevue/inputnumber';
import Dialog from 'primevue/dialog';

export default {
    components: {
        SiteHeader,
        Button,
        DataTable,
        Column,
        Dropdown,
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
        const showEditCategoryDialog = ref(false);
        const showEditProductDialog = ref(false);
        const showEditFirstDiscountDialog = ref(false);
        const showEditSecondDiscountDialog = ref(false);
        const showEditFirstConditionDialog = ref(false);
        const showEditThenDiscountDialog = ref(false);
        const showEditSecondConditionDialog = ref(false);
        const showEditDeciderConditionDialog = ref(false);
        const showDeleteDiscountDialog = ref(false);
        const showEditCountDialog = ref(false);
        const showEditCategoryCondDialog = ref(false);
        const showEditProductCondDialog = ref(false);
        const showEditTotalSumDialog = ref(false);
        const showDeleteCondDialog = ref(false);
        const newDiscount = ref({
            type: '',
            percent: 0,
            category: null,
            productId: -1,
        });
        const newCondition = ref({
            type: '',
            category: null,
            productId: -1,
            categoryCount: -1,
            productCount: -1,
            totalSum: -1
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
                conditions.value = response.data.map(condition => ({
                    type: getConditionTypeLabel(condition.type),
                    details: createConditionDetails(condition)
                }));
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
                productId: -1,
            };
            addDiscountDialogVisible.value = true;
        };

        const showAddConditionDialog = () => {
            newCondition.value = {
                type: '',
                category: null,
                productId: -1,
                categoryCount: -1,
                productCount: -1,
                totalSum: -1
            };
            addConditionDialogVisible.value = true;
        };

        const saveDiscount = async () => {
            switch (newDiscount.value.type) {
                case 'Store':
                    try {
                        const url = `http://localhost:8082/api/trading/store/${storeName}/discounts/store`;
                        await axios.post(url, null, {
                            params: {
                                username: username.value,
                                token: token.value,
                                storeName: storeName,
                                discountPercent: newDiscount.value.percent / 100
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
                                discountPercent: newDiscount.value.percent / 100
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
                                productId: newDiscount.value.productId,
                                discountPercent: newDiscount.value.percent / 100
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
                'xor': 'Xor Discount',
                'placeholderDiscount': 'Placeholder Discount'
            };
            return typeMap[type] || type;
        };

        const getConditionTypeLabel = (type) => {
            const typeMap = {
                'productCount' : 'Product Count',
                'totalSum' : 'Total Sum',
                'categoryCount' : 'Category Count',
            }
            return typeMap[type] || type;
        }

        const getConditionTypeLabel = (type) => {
            const typeMap = {
                'productCount' : 'Product Count',
                'totalSum' : 'Total Sum',
                'categoryCount' : 'Category Count',
            }
            return typeMap[type] || type;
        }

        const createDetails = (discount) => {
            switch (discount.type) {
                case 'percentageStore':
                    return discount.details = `Percentage: ${discount.percent * 100}%`;
                case 'percentageCategory':
                    return discount.details = `Category: ${categoryOptions.value.find(opt => opt.value === discount.category)?.label} Percentage: ${discount.percent * 100}%`;
                case 'percentageProduct':
                    return discount.details = `Product Id: ${discount.productId} Percentage: ${discount.percent * 100}%`;
                case 'additive':
                    return discount.details = `First discount: ${getDiscountTypeLabel(discount.first.type)} ${createDetails(discount.first)} Second discount: ${getDiscountTypeLabel(discount.first.type)} ${createDetails(discount.second)}`;
                case 'max':
                    return discount.details = `First discount: ${getDiscountTypeLabel(discount.first.type)} ${createDetails(discount.first)} Second discount: ${getDiscountTypeLabel(discount.first.type)} ${createDetails(discount.second)}`;
                case 'conditional':
                    return discount.details = `Condition: ${createConditionDetails(discount.condition)} discount: ${getDiscountTypeLabel(discount.then.type)} ${createDetails(discount.then)}`;
                case 'and':
                    return discount.details = `First condition: ${createConditionDetails(discount.first)} Second condition: ${createConditionDetails(discount.second)} Discount: ${getDiscountTypeLabel(discount.then.type)} ${createDetails(discount.then)}`;
                case 'or':
                    return discount.details = `First condition: ${createConditionDetails(discount.first)} Second condition: ${createConditionDetails(discount.second)} Discount: ${getDiscountTypeLabel(discount.then.type)} ${createDetails(discount.then)}`;
                case 'xor':
                    return discount.details = `First discount: ${getDiscountTypeLabel(discount.first.type)} ${createDetails(discount.first)} Second discount: ${getDiscountTypeLabel(discount.first.type)} ${createDetails(discount.second)} Decider condition: ${createConditionDetails(discount.decider)}`;
                case 'placeholderDiscount':
                    return discount.details = 'Placeholder'
            }
        }

        const createConditionDetails = (condition) => {
            switch (condition.type) {
                case 'productCount':
                    return condition.details = `Product ID: ${condition.productId} Count: ${condition.count}`;
                case 'categoryCount':
                    return condition.details = `Category: ${categoryOptions.value.find(opt => opt.value === condition.category)?.label} Count: ${condition.count}`;
                case 'totalSum':
                    return condition.details = `Required sum ${condition.requiredSum}`;
                case 'placeholderCondition':
                    return condition.details = 'Placeholder';
            }
        };

        const typeTemplate = (rowData) => {
            return getDiscountTypeLabel(rowData.data.type);
        };

        const typeCondTemplate = (rowData) => {
            return getConditionTypeLabel(rowData.data.type);
        };

        const typeCondTemplate = (rowData) => {
        alert(rowData);
            return getConditionTypeLabel(rowData.type);
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
                const url = `http://localhost:8082/api/trading/store/${storeName}/discounts/removeDiscount/${selectedIndex}`;
                await axios.delete(url, {
                    params: {
                        username: username.value,
                        token: token.value
                    }
                });
                fetchDiscounts();
                showDeleteDiscountDialog.value = false;
            } catch (error) {
                console.error('Error deleting discount:', error);
                toast.add({ severity: 'error', summary: 'Error', detail: error.message, life: 3000 });
            }
        }

        const formattedDiscounts = () => {
            return discounts.value.map((discount, index) => ({
                label: discount.details,
                value: index
            }));
        }

        const formattedConditions = () => {
            return conditions.value.map((condition, index) => ({
                label: condition.details,
                value: index
            }));
        }

        const editDiscountPercent = async (index, editPercentValue) => {
            try {
                const url = `http://localhost:8082/api/trading/store/${storeName}/discounts/setPercentDiscount/${index}/${editPercentValue}`;
                await axios.post(url, null, {
                    params: {
                        username: username.value,
                        token: token.value
                    }
                });
                fetchDiscounts();
                fetchConditions();
            } catch (error) {
                console.error('Error setting discount percent:', error);
                toast.add({ severity: 'error', summary: 'Error', detail: error.message, life: 3000 });
            }
        }

        const editCategoryDiscountCategory = async (index, editCategoryValue) => {
            try {
                const url = `http://localhost:8082/api/trading/store/${storeName}/discounts/setCategoryDiscount/${index}/${editCategoryValue}`;
                await axios.post(url, null, {
                    params: {
                        username: username.value,
                        token: token.value
                    }
                });
                fetchDiscounts();
                fetchConditions();
            } catch (error) {
                console.error('Error setting discount category:', error);
                toast.add({ severity: 'error', summary: 'Error', detail: error.message, life: 3000 });
            }
        }

        const editProductDiscountID = async (index, editProductDiscountID) => {
            try {
                const url = `http://localhost:8082/api/trading/store/${storeName}/discounts/setProductIdDiscount/${index}/${editProductDiscountID}`;
                await axios.post(url, null, {
                    params: {
                        username: username.value,
                        token: token.value
                    }
                });
                fetchDiscounts();
                fetchConditions();
            } catch (error) {
                console.error('Error setting discount product:', error);
                toast.add({ severity: 'error', summary: 'Error', detail: error.message, life: 3000 });
            }
        }

        const editFirstDiscount = async (index, editFirstDiscountIndex) => {
            try {
                const url = `http://localhost:8082/api/trading/store/${storeName}/discounts/setFirstDiscount/${index}/${editFirstDiscountIndex}`;
                await axios.post(url, null, {
                    params: {
                        username: username.value,
                        token: token.value
                    }
                });
                fetchDiscounts();
                fetchConditions();
            } catch (error) {
                console.error('Error setting first discount:', error);
                toast.add({ severity: 'error', summary: 'Error', detail: error.message, life: 3000 });
            }
        }

        const editSecondDiscount = async (index, editSecondDiscountIndex) => {
            try {
                const url = `http://localhost:8082/api/trading/store/${storeName}/discounts/setSecondDiscount/${index}/${editSecondDiscountIndex}`;
                await axios.post(url, null, {
                    params: {
                        username: username.value,
                        token: token.value
                    }
                });
                fetchDiscounts();
                fetchConditions();
            } catch (error) {
                console.error('Error setting second discount:', error);
                toast.add({ severity: 'error', summary: 'Error', detail: error.message, life: 3000 });
            }
        }

        const editFirstCondition = async (index, editFirstConditionIndex) => {
            try {
                const url = `http://localhost:8082/api/trading/store/${storeName}/discounts/setFirstCondition/${index}/${editFirstConditionIndex}`;
                await axios.post(url, null, {
                    params: {
                        username: username.value,
                        token: token.value
                    }
                });
                fetchDiscounts();
                fetchConditions();
            } catch (error) {
                console.error('Error setting first condition:', error);
                toast.add({ severity: 'error', summary: 'Error', detail: error.message, life: 3000 });
            }
        }

        const editSecondCondition = async (index, editSecondConditionIndex) => {
            try {
                const url = `http://localhost:8082/api/trading/store/${storeName}/discounts/setSecondCondition/${index}/${editSecondConditionIndex}`;
                await axios.post(url, null, {
                    params: {
                        username: username.value,
                        token: token.value
                    }
                });
                fetchDiscounts();
                fetchConditions();
            } catch (error) {
                console.error('Error setting second condition:', error);
                toast.add({ severity: 'error', summary: 'Error', detail: error.message, life: 3000 });
            }
        }

        const editThenDiscount = async (index, editThenDiscountIndex) => {
            try {
                const url = `http://localhost:8082/api/trading/store/${storeName}/discounts/setThenDiscount/${index}/${editThenDiscountIndex}`;
                await axios.post(url, null, {
                    params: {
                        username: username.value,
                        token: token.value
                    }
                });
                fetchDiscounts();
                fetchConditions();
            } catch (error) {
                console.error('Error setting then discount:', error);
                toast.add({ severity: 'error', summary: 'Error', detail: error.message, life: 3000 });
            }
        }

        const editDeciderCondition = async (index, editDeciderConditionIndex) => {
            try {
                const url = `http://localhost:8082/api/trading/store/${storeName}/discounts/setDeciderDiscount/${index}/${editDeciderConditionIndex}`;
                await axios.post(url, null, {
                    params: {
                        username: username.value,
                        token: token.value
                    }
                });
                fetchDiscounts();
                fetchConditions();
            } catch (error) {
                console.error('Error setting decider condition:', error);
                toast.add({ severity: 'error', summary: 'Error', detail: error.message, life: 3000 });
            }
        }

        const editConditionCount = async (index, editCountValue) => {
            try {
                const url = `http://localhost:8082/api/trading/store/${storeName}/discounts/setCountCondition/${index}/${editCountValue}`;
                await axios.post(url, null, {
                    params: {
                        username: username.value,
                        token: token.value
                    }
                });
                fetchConditions();
            } catch (error) {
                console.error('Error setting condition count:', error);
                toast.add({ severity: 'error', summary: 'Error', detail: error.message, life: 3000 });
            }
        }

        const editConditionCategory = async (index, editCondCategoryValue) => {
            try {
                const url = `http://localhost:8082/api/trading/store/${storeName}/discounts/setCategoryCondition/${index}/${editCondCategoryValue}`;
                await axios.post(url, null, {
                    params: {
                        username: username.value,
                        token: token.value
                    }
                });
                fetchConditions();
            } catch (error) {
                console.error('Error setting condition category:', error);
                toast.add({ severity: 'error', summary: 'Error', detail: error.message, life: 3000 });
            }
        }

        const editConditionProductID = async (index, editCondProductIDValue) => {
            try {
                const url = `http://localhost:8082/api/trading/store/${storeName}/discounts/setProductIdDiscount/${index}/${editCondProductIDValue}`;
                await axios.post(url, null, {
                    params: {
                        username: username.value,
                        token: token.value
                    }
                });
                fetchConditions();
            } catch (error) {
                console.error('Error setting condition product ID:', error);
                toast.add({ severity: 'error', summary: 'Error', detail: error.message, life: 3000 });
            }
        }

        const editConditionTotalSum = async (index, editTotalSumValue) => {
            try {
                const url = `http://localhost:8082/api/trading/store/${storeName}/discounts/setTotalSum/${index}/${editTotalSumValue}`;
                await axios.post(url, null, {
                    params: {
                        username: username.value,
                        token: token.value
                    }
                });
                fetchConditions();
            } catch (error) {
                console.error('Error setting condition total sum:', error);
                toast.add({ severity: 'error', summary: 'Error', detail: error.message, life: 3000 });
            }
        }

        const deleteCondition = async (index) => {
            try {
                const url = `http://localhost:8082/api/trading/store/${storeName}/discounts/removeCondition/${index}`;
                await axios.delete(url, {
                    params: {
                        username: username.value,
                        token: token.value
                    }
                });
                fetchConditions();
            } catch (error) {
                console.error('Error deleting condition:', error);
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
            showEditCategoryDialog,
            showEditProductDialog,
            showEditFirstDiscountDialog,
            showEditSecondDiscountDialog,
            showEditFirstConditionDialog,
            showEditThenDiscountDialog,
            showEditSecondConditionDialog,
            showEditDeciderConditionDialog,
            showDeleteDiscountDialog,
            showEditCountDialog,
            showEditCategoryCondDialog,
            showEditProductCondDialog,
            showEditTotalSumDialog,
            showDeleteCondDialog,
            newDiscount,
            newCondition,
            showAddDiscountDialog,
            showAddConditionDialog,
            saveDiscount,
            saveCondition,
            typeTemplate,
            typeCondTemplate,
            valueTemplate,
            detailsTemplate,
            deleteDiscount,
            formattedDiscounts,
            formattedConditions,
            editDiscountPercent,
            editCategoryDiscountCategory,
            editProductDiscountID,
            editFirstDiscount,
            editSecondDiscount,
            editFirstCondition,
            editSecondCondition,
            editThenDiscount,
            editDeciderCondition,
            editConditionCount,
            editConditionCategory,
            editConditionProductID,
            editConditionTotalSum,
            deleteCondition,
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
    margin-top: 12px;
}

.p-datatable {
    margin-inline: 4rem;
}

.p-datatable {
    justify-content: center;
}
</style>