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
                    <label for="conditionType">Select condition type:</label>
                    <Dropdown id="conditionType" v-model="newCondition.type" :options="conditionTypes"
                        optionLabel="label" optionValue="value" />
                </div>
                <template v-if="newCondition.type === 'Category Count'">
                    <div class="p-field">
                        <label for="categorySelect">Select category:</label>
                        <Dropdown id="categorySelect" v-model="newCondition.category" :options="categoryOptions"
                            optionLabel="label" optionValue="value" />
                        <label for="categoryCount">Set category count:</label>
                        <InputNumber id="categoryCount" v-model="newCondition.categoryCount" mode="decimal" min="0" />
                    </div>
                </template>
                <template v-else-if="newCondition.type === 'Product Count'">
                    <div class="p-field">
                        <label for="productId">Set product ID:</label>
                        <InputNumber id="productId" v-model="newCondition.productId" mode="decimal" min="0" />
                        <label for="productCount">Set product count:</label>
                        <InputNumber id="productCount" v-model="newCondition.productCount" mode="decimal" min="0" />
                    </div>
                </template>
                <template v-else-if="newCondition.type === 'Total Sum'">
                    <div class="p-field">
                        <label for="totalSum">Total Sum:</label>
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
        <DataTable :value="discounts" title="Discount" responsiveLayout="scroll" class="p-mt-3" paginator :rows="5"
            :rowsPerPageOptions="[5, 10, 20, 50]">
            <template #header>
                <div class="flex flex-wrap items-center justify-between gap-2">
                    <span class="text-xl font-bold">Discounts</span>
                </div>
            </template>
            <Column field="index" header="Index" style="width: 5%;" :headerStyle="{ 'text-align': 'center' }">
            </Column>
            <Column field="type" header="Type" style="width: 10%;" :headerStyle="{ 'text-align': 'center' }"
                :body="typeTemplate">
            </Column>
            <Column field="details" header="Details" style="width: 20%;" :headerStyle="{ 'text-align': 'center' }"
                :body="valueTemplate"></Column>
            <Column header="Actions" style="width: 30%;">
                <template #body="rowData">
                    <div class="action-buttons">
                        <Button class="action-button"
                            v-if="rowData.data.type === 'Store Discount' || rowData.data.type === 'percentageStore' || rowData.data.type === 'Category Discount' || rowData.data.type === 'Product Discount'"
                            @click="openEditPercentDialog(rowData.index)">Edit percent
                        </Button>

                        <Button class="action-button" v-if="rowData.data.type === 'Category Discount'"
                            @click="openEditCategoryDialog(rowData.index)">Edit category
                        </Button>

                        <Button class="action-button" v-if="rowData.data.type === 'Product Discount'"
                            @click="openEditProductDialog(rowData.index)">Edit product ID
                        </Button>

                        <Button class="action-button"
                            v-if="rowData.data.type === 'Additive Discount' || rowData.data.type === 'Maximum Discount' || rowData.data.type === 'Xor Discount'"
                            @click="openEditFirstDiscountDialog(rowData.index)">Edit first discount
                        </Button>

                        <Button class="action-button"
                            v-if="rowData.data.type === 'Additive Discount' || rowData.data.type === 'Maximum Discount' || rowData.data.type === 'Xor Discount'"
                            @click="openEditSecondDiscountDialog(rowData.index)">Edit second discount
                        </Button>

                        <Button class="action-button"
                            v-if="rowData.data.type === 'Conditional Discount' || rowData.data.type === 'And Discount' || rowData.data.type === 'Or Discount'"
                            @click="openEditFirstConditionDialog(rowData.index)">Edit first condition
                        </Button>

                        <Button class="action-button"
                            v-if="rowData.data.type === 'And Discount' || rowData.data.type === 'Or Discount'"
                            @click="openEditSecondConditionDialog(rowData.index)">Edit second condition
                        </Button>

                        <Button class="action-button"
                            v-if="rowData.data.type === 'Conditional Discount' || rowData.data.type === 'Or Discount' || rowData.data.type === 'And Discount'"
                            @click="openEditThenDiscountDialog(rowData.index)">Edit discount
                        </Button>

                        <Button class="action-button" v-if="rowData.data.type === 'Xor Discount'"
                            @click="openEditDeciderConditionDialog(rowData.index)">Edit decider condition
                        </Button>

                        <Button class="action-button" @click="openDeleteDialog(rowData.index)">Delete Discount</Button>
                    </div>
                </template>
            </Column>
        </DataTable>

        <!-- Edit Percent Dialog -->
        <Dialog v-model="showEditPercentDialog" :visible="showEditPercentDialog" :closable="false" blockScroll
            :draggable="false" modal>
            <template v-slot:header>
                <div class="dialog-header">Edit Percent</div>
            </template>
            <label for="editPercent" class="label">Set new percent:</label>
            <InputNumber id="editPercent" v-model="editPercentValue" />
            <div class="button-container">
                <Button @click="editDiscountPercent()">Save</Button>
                <Button @click="showEditPercentDialog = false">Cancel</Button>
            </div>
        </Dialog>

        <!-- Edit Category Dialog -->
        <Dialog v-model="showEditCategoryDialog" :visible="showEditCategoryDialog" :closable="false" blockScroll
            :draggable="false" modal>
            <template v-slot:header>
                <div class="dialog-header">Edit Category</div>
            </template>
            <div class="input-container">
                <label for="editCategory" class="label">Select new category:</label>
                <Dropdown id="editCategory" v-model="editCategoryValue" :options="categoryOptions" optionLabel="label"
                    :style="{ width: '100%' }" />
            </div>
            <div class="button-container">
                <Button @click="editCategoryDiscountCategory()">Save</Button>
                <Button @click="showEditCategoryDialog = false">Cancel</Button>
            </div>
        </Dialog>

        <!-- Edit Product ID Dialog -->
        <Dialog v-model="showEditProductDialog" :visible="showEditProductDialog" :closable="false" blockScroll
            :draggable="false" modal>
            <template v-slot:header>
                <div class="dialog-header">Edit Product ID</div>
            </template>
            <label for="editProductId" class="label">Set new product ID:</label>
            <InputNumber id="editProductId" v-model="editProductIDValue" />
            <div class="button-container">
                <Button @click="editProductDiscountID()">Save</Button>
                <Button @click="showEditProductDialog = false">Cancel</Button>
            </div>
        </Dialog>

        <!-- Edit First Discount Dialog -->
        <Dialog v-model="showEditFirstDiscountDialog" :visible="showEditFirstDiscountDialog" :closable="false"
            blockScroll :draggable="false" modal>
            <template v-slot:header>
                <div class="dialog-header">Edit First Discount</div>
            </template>
            <label for="editFirstDiscount" class="label">Select first discount to set:</label>
            <Dropdown id="editFirstDiscount" v-model="editFirstDiscountIndex" :options="formattedDiscounts"
                optionLabel="label" optionValue="value" />
            <div class="button-container">
                <Button @click="editFirstDiscount()">Save</Button>
                <Button @click="showEditFirstDiscountDialog = false">Cancel</Button>
            </div>
        </Dialog>

        <!-- Edit Second Discount Dialog -->
        <Dialog v-model="showEditSecondDiscountDialog" :visible="showEditSecondDiscountDialog" :closable="false"
            blockScroll :draggable="false" modal>
            <template v-slot:header>
                <div class="dialog-header">Edit Second Discount</div>
            </template>
            <label for="editSecondDiscount" class="label">Select second discount to set:</label>
            <Dropdown id="editSecondDiscount" v-model="editSecondDiscountIndex" :options="formattedDiscounts"
                optionLabel="label" optionValue="value" />
            <div class="button-container">
                <Button @click="editSecondDiscount()">Save</Button>
                <Button @click="showEditSecondDiscountDialog = false">Cancel</Button>
            </div>
        </Dialog>

        <!-- Edit First Condition Dialog -->
        <Dialog v-model="showEditFirstConditionDialog" :visible="showEditFirstConditionDialog" :closable="false"
            blockScroll :draggable="false" modal>
            <template v-slot:header>
                <div class="dialog-header">Edit First Condition</div>
            </template>
            <label for="editFirstCondition" class="label">Select first condition to set:</label>
            <Dropdown id="editFirstCondition" v-model="editFirstConditionIndex" :options="formattedConditions"
                optionLabel="label" optionValue="value" />
            <div class="button-container">
                <Button @click="editFirstCondition()">Save</Button>
                <Button @click="showEditFirstConditionDialog = false">Cancel</Button>
            </div>
        </Dialog>

        <!-- Edit Second Condition Dialog -->
        <Dialog v-model="showEditSecondConditionDialog" :visible="showEditSecondConditionDialog" :closable="false"
            blockScroll :draggable="false" modal>
            <template v-slot:header>
                <div class="dialog-header">Edit Second Condition</div>
            </template>
            <label for="editSecondCondition" class="label">Select second condition to set:</label>
            <Dropdown id="editSecondCondition" v-model="editSecondConditionIndex" :options="formattedConditions"
                optionLabel="label" optionValue="value" />
            <div class="button-container">
                <Button @click="editSecondCondition()">Save</Button>
                <Button @click="showEditSecondConditionDialog = false">Cancel</Button>
            </div>
        </Dialog>

        <!-- Edit Then Discount Dialog -->
        <Dialog v-model="showEditThenDiscountDialog" :visible="showEditThenDiscountDialog" :closable="false" blockScroll
            :draggable="false" modal>
            <template v-slot:header>
                <div class="dialog-header">Edit Then Discount</div>
            </template>
            <label for="editThenCondition" class="label">Select then condition to set:</label>
            <Dropdown id="editThenCondition" v-model="editThenDiscountIndex" :options="formattedDiscounts"
                optionLabel="label" optionValue="value" />
            <div class="button-container">
                <Button @click="editThenDiscount()">Save</Button>
                <Button @click="showEditThenDiscountDialog = false">Cancel</Button>
            </div>
        </Dialog>

        <!-- Edit Decider Condition Dialog -->
        <Dialog v-model="showEditDeciderConditionDialog" :visible="showEditDeciderConditionDialog" :closable="false"
            blockScroll :draggable="false" modal>
            <template v-slot:header>
                <div class="dialog-header">Edit Decider Condition</div>
            </template>
            <label for="editDeciderCondition" class="label">Select decider condition to set:</label>
            <Dropdown id="editDeciderCondition" v-model="editDeciderConditionIndex" :options="formattedConditions"
                optionLabel="label" optionValue="value" />
            <div class="button-container">
                <Button @click="editDeciderCondition()">Save</Button>
                <Button @click="showEditDeciderConditionDialog = false">Cancel</Button>
            </div>
        </Dialog>

        <!-- Delete Discount Dialog -->
        <Dialog v-model="showDeleteDiscountDialog" :visible="showDeleteDiscountDialog" :closable="false" blockScroll
            :draggable="false" modal>
            <template v-slot:header>
                <div class="dialog-header">Delete Discount?</div>
            </template>
            <div class="button-container">
                <Button @click="deleteDiscount()">Confirm</Button>
                <Button @click="showDeleteDiscountDialog = false">Cancel</Button>
            </div>
        </Dialog>

        <DataTable :value="conditions" responsiveLayout="scroll" class="p-mt-3" paginator :rows="5"
            :rowsPerPageOptions="[5, 10, 20, 50]">
            <template #header>
                <div class="flex flex-wrap items-center justify-between gap-2">
                    <span class="text-xl font-bold">Conditions</span>
                </div>
            </template>
            <Column field="index" header="Index" style="width: 5%;" :headerStyle="{ 'text-align': 'center' }">
            </Column>
            <Column field="type" header="Type" style="width: 10%;" :headerStyle="{ 'text-align': 'center' }"
                :body="typeCondTemplate">
            </Column>
            <Column field="details" header="Details" style="width: 20%;" :headerStyle="{ 'text-align': 'center' }"
                :body="valueTemplate"></Column>
            <Column header="Actions" style="width: 30%;">
                <template #body="rowData">
                    <div class="action-buttons">
                        <Button class="action-button"
                            v-if="rowData.data.type === 'Category Count' || rowData.data.type === 'Product Count'"
                            @click="openEditCountDialog(rowData.index)">Edit count
                        </Button>

                        <Button class="action-button" v-if="rowData.data.type === 'Category Count'"
                            @click="openEditCategoryCondDialog(rowData.index)">Edit category
                        </Button>

                        <Button class="action-button" v-if="rowData.data.type === 'Product Count'"
                            @click="openEditProductCondDialog(rowData.index)">Edit product ID
                        </Button>

                        <Button class="action-button" v-if="rowData.data.type === 'Total Sum'"
                            @click="openEditTotalSumDialog(rowData.index)">Edit total sum
                        </Button>

                        <Button class="action-button" @click="openDeleteCondDialog(rowData.index)">Delete
                            Condition</Button>
                    </div>
                </template>
                <template #header>
                    <span class="flex-1 text-center"></span>
                </template>
            </Column>
        </DataTable>

        <!-- Edit Cond Count Dialog -->
        <Dialog v-model="showEditCountDialog" :visible="showEditCountDialog" header="Edit Count" :closable="false"
            blockScroll :draggable="false" modal>
            <label for="condCount">Set condition count:</label>
            <InputNumber id="condCount" v-model="editCountValue" />
            <div class="button-container">
                <Button @click="editConditionCount()">Save</Button>
                <Button @click="showEditCountDialog = false">Cancel</Button>
            </div>
        </Dialog>

        <!-- Edit Cond Category Dialog -->
        <Dialog v-model="showEditCategoryCondDialog" :visible="showEditCategoryCondDialog" header="Edit Category"
            :closable="false" blockScroll :draggable="false" modal>
            <label for="condCategory">Set condition category:</label>
            <Dropdown id="condCategory" v-model="editCondCategoryValue" :options="categoryOptions"
                optionLabel="label" />
            <div class="button-container">
                <Button @click="editConditionCategory()">Save</Button>
                <Button @click="showEditCategoryCondDialog = false">Cancel</Button>
            </div>
        </Dialog>

        <!-- Edit Cond Product ID Dialog -->
        <Dialog v-model="showEditProductCondDialog" :visible="showEditProductCondDialog" header="Edit Product ID"
            :closable="false" blockScroll :draggable="false" modal>
            <label for="condProductId">Set condition product id:</label>
            <InputNumber id="condProductId" v-model="editCondProductIDValue" />
            <div class="button-container">
                <Button @click="editConditionProductID()">Save</Button>
                <Button @click="showEditProductCondDialog = false">Cancel</Button>
            </div>
        </Dialog>

        <!-- Edit Cond Total Sum Dialog -->
        <Dialog v-model="showEditTotalSumDialog" :visible="showEditTotalSumDialog" header="Edit Total Sum"
            :closable="false" blockScroll :draggable="false" modal>
            <label for="condTotalSum">Set category count:</label>
            <InputNumber id="condTotalSum" v-model="editTotalSumValue" />
            <div class="button-container">
                <Button @click="editConditionTotalSum()">Save</Button>
                <Button @click="showEditTotalSumDialog = false">Cancel</Button>
            </div>
        </Dialog>

        <!-- Delete Cond Dialog -->
        <Dialog v-model="showDeleteCondDialog" :visible="showDeleteCondDialog" header="Delete Condition?"
            :closable="false" blockScroll :draggable="false" modal>
            <div class="button-container">
                <Button @click="deleteCondition()">Confirm</Button>
                <Button @click="showDeleteCondDialog = false">Cancel</Button>
            </div>
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
        const formattedDiscounts = ref([]);
        const conditions = ref([]);
        const formattedConditions = ref([]);
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
        const rowIndex = ref(-1);
        const editCategoryValue = ref(null);
        const editPercentValue = ref(null);
        const editProductIDValue = ref(null);
        const editFirstDiscountIndex = ref(-1);
        const editSecondDiscountIndex = ref(-1);
        const editFirstConditionIndex = ref(-1);
        const editSecondConditionIndex = ref(-1);
        const editThenDiscountIndex = ref(-1);
        const editDeciderConditionIndex = ref(-1);
        const editCountValue = ref(null);
        const editCondCategoryValue = ref(null);
        const editCondProductIDValue = ref(null);
        const editTotalSumValue = ref(null);
        const newDiscount = ref({
            type: '',
            percent: null,
            category: null,
            productId: null,
        });
        const newCondition = ref({
            type: '',
            category: null,
            productId: null,
            categoryCount: null,
            productCount: null,
            totalSum: null
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
                discounts.value = response.data.map((discount, index) => ({
                    index: index,
                    type: getDiscountTypeLabel(discount.type),
                    details: createDetails(discount)
                }));
                formattedDiscounts.value = formatDiscounts();
            } catch (error) {
                console.error('Error fetching discounts:', error);
                toast.add({ severity: 'error', summary: 'Error', detail: error.response.data, life: 3000 });
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
                conditions.value = response.data.map((condition, index) => ({
                    index: index,
                    type: getConditionTypeLabel(condition.type),
                    details: createConditionDetails(condition)
                }));
                formattedConditions.value = formatConditions();
            } catch (error) {
                console.error('Error fetching conditions:', error);
                toast.add({ severity: 'error', summary: 'Error', detail: error.response.data, life: 3000 });
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
                        toast.add({ severity: 'error', summary: 'Error', detail: error.response.data, life: 3000 });
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
                        toast.add({ severity: 'error', summary: 'Error', detail: error.response.data, life: 3000 });
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
                        toast.add({ severity: 'error', summary: 'Error', detail: error.response.data, life: 3000 });
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
                        toast.add({ severity: 'error', summary: 'Error', detail: error.response.data, life: 3000 });
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
                        toast.add({ severity: 'error', summary: 'Error', detail: error.response.data, life: 3000 });
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
                        toast.add({ severity: 'error', summary: 'Error', detail: error.response.data, life: 3000 });
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
                        toast.add({ severity: 'error', summary: 'Error', detail: error.response.data, life: 3000 });
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
                        toast.add({ severity: 'error', summary: 'Error', detail: error.response.data, life: 3000 });
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
                        toast.add({ severity: 'error', summary: 'Error', detail: error.response.data, life: 3000 });
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
                        toast.add({ severity: 'error', summary: 'Error', detail: error.response.data, life: 3000 });
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
                        toast.add({ severity: 'error', summary: 'Error', detail: error.response.data, life: 3000 });
                        return;
                    }
                    break;
                case 'Total Sum':
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
                        toast.add({ severity: 'error', summary: 'Error', detail: error.response.data, life: 3000 });
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
                'productCount': 'Product Count',
                'totalSum': 'Total Sum',
                'categoryCount': 'Category Count',
            }
            return typeMap[type] || type;
        }

        const createDetails = (discount) => {
            switch (discount.type) {
                case 'percentageStore':
                    return discount.details = `Percentage: ${discount.percent * 100}%`;
                case 'percentageCategory':
                    return discount.details = `Category: ${categoryOptions.value.find(opt => opt.value === discount.category)?.label}, Percentage: ${discount.percent * 100}%`;
                case 'percentageProduct':
                    return discount.details = `Product Id: ${discount.productId}, Percentage: ${discount.percent * 100}%`;
                case 'additive':
                    return discount.details = `First discount: ${getDiscountTypeLabel(discount.first.type)} ${createDetails(discount.first)}, Second discount: ${getDiscountTypeLabel(discount.second.type)} ${createDetails(discount.second)}`;
                case 'max':
                    return discount.details = `First discount: ${getDiscountTypeLabel(discount.first.type)} ${createDetails(discount.first)}, Second discount: ${getDiscountTypeLabel(discount.second.type)} ${createDetails(discount.second)}`;
                case 'conditional':
                    return discount.details = `Condition: ${createConditionDetails(discount.condition)}, Discount: ${getDiscountTypeLabel(discount.then.type)} ${createDetails(discount.then)}`;
                case 'and':
                    return discount.details = `First condition: ${createConditionDetails(discount.first)}, Second condition: ${createConditionDetails(discount.second)}, Discount: ${getDiscountTypeLabel(discount.then.type)} ${createDetails(discount.then)}`;
                case 'or':
                    return discount.details = `First condition: ${createConditionDetails(discount.first)}, Second condition: ${createConditionDetails(discount.second)}, Discount: ${getDiscountTypeLabel(discount.then.type)} ${createDetails(discount.then)}`;
                case 'xor':
                    return discount.details = `First discount: ${getDiscountTypeLabel(discount.first.type)} ${createDetails(discount.first)}, Second discount: ${getDiscountTypeLabel(discount.second.type)} ${createDetails(discount.second)}, Decider condition: ${createConditionDetails(discount.decider)}`;
                case 'placeholderDiscount':
                    return discount.details = 'Placeholder'
            }
        }

        const createConditionDetails = (condition) => {
            switch (condition.type) {
                case 'productCount':
                    return condition.details = `Product ID: ${condition.productId}, Count: ${condition.count}`;
                case 'categoryCount':
                    return condition.details = `Category: ${categoryOptions.value.find(opt => opt.value === condition.category)?.label}, Count: ${condition.count}`;
                case 'totalSum':
                    return condition.details = `Required sum: ${condition.requiredSum}`;
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

        const valueTemplate = (rowData) => {
            return h('div', {}, [
                h('span', {}, rowData.details)
            ]);
        };

        const detailsTemplate = (rowData) => {
            return h('div', {}, createDetails(rowData));
        };

        const formatDiscounts = () => {
            return discounts.value.map((discount, index) => ({
                label: `Type: ${discount.type}, Details: ${discount.details}`,
                value: index
            }));
        }

        const formatConditions = () => {
            return conditions.value.map((condition, index) => ({
                label: `Type: ${condition.type}, Details: ${condition.details}`,
                value: index
            }));
        }

        const editDiscountPercent = async () => {
            try {
                const url = `http://localhost:8082/api/trading/store/${storeName}/discounts/setPercentDiscount/${rowIndex.value}/${editPercentValue.value / 100}`;
                await axios.post(url, null, {
                    params: {
                        username: username.value,
                        token: token.value
                    }
                });
                fetchDiscounts();
                fetchConditions();
                showEditPercentDialog.value = false;
                rowIndex.value = -1;
                editPercentValue.value = null;
                toast.add({ severity: 'success', summary: 'Success', detail: 'Percent changed', life: 3000 });
            } catch (error) {
                console.error('Error setting discount percent:', error);
                toast.add({ severity: 'error', summary: 'Error', detail: error.response.data, life: 3000 });
            }
        }

        const editCategoryDiscountCategory = async () => {
            try {
                console.log(`index: ${rowIndex.value}, cat val: ${editCategoryValue}`);
                const url = `http://localhost:8082/api/trading/store/${storeName}/discounts/setCategoryDiscount/${rowIndex.value}/${editCategoryValue.value.value}`;
                await axios.post(url, null, {
                    params: {
                        username: username.value,
                        token: token.value
                    }
                });
                fetchDiscounts();
                fetchConditions();
                showEditCategoryDialog.value = false;
                rowIndex.value = -1;
                editCategoryValue.value = null;
                toast.add({ severity: 'success', summary: 'Success', detail: 'Category changed', life: 3000 });
            } catch (error) {
                console.error('Error setting discount category:', error);
                toast.add({ severity: 'error', summary: 'Error', detail: error.response.data, life: 3000 });
            }
        }

        const editProductDiscountID = async () => {
            try {
                const url = `http://localhost:8082/api/trading/store/${storeName}/discounts/setProductIdDiscount/${rowIndex.value}/${editProductIDValue.value}`;
                await axios.post(url, null, {
                    params: {
                        username: username.value,
                        token: token.value
                    }
                });
                fetchDiscounts();
                fetchConditions();
                showEditProductDialog.value = false;
                rowIndex.value = -1;
                editProductIDValue.value = null;
                toast.add({ severity: 'success', summary: 'Success', detail: 'Product id changed', life: 3000 });
            } catch (error) {
                console.error('Error setting discount product:', error);
                toast.add({ severity: 'error', summary: 'Error', detail: error.response.data, life: 3000 });
            }
        }

        const editFirstDiscount = async () => {
            try {
                const url = `http://localhost:8082/api/trading/store/${storeName}/discounts/setFirstDiscount/${rowIndex.value}/${editFirstDiscountIndex.value}`;
                await axios.post(url, null, {
                    params: {
                        username: username.value,
                        token: token.value
                    }
                });
                fetchDiscounts();
                fetchConditions();
                showEditFirstDiscountDialog.value = false;
                rowIndex.value = -1;
                editFirstDiscountIndex.value = null;
                toast.add({ severity: 'success', summary: 'Success', detail: 'First discount set', life: 3000 });
            } catch (error) {
                console.error('Error setting first discount:', error);
                toast.add({ severity: 'error', summary: 'Error', detail: error.response.data, life: 3000 });
            }
        }

        const editSecondDiscount = async () => {
            try {
                const url = `http://localhost:8082/api/trading/store/${storeName}/discounts/setSecondDiscount/${rowIndex.value}/${editSecondDiscountIndex.value}`;
                await axios.post(url, null, {
                    params: {
                        username: username.value,
                        token: token.value
                    }
                });
                fetchDiscounts();
                fetchConditions();
                showEditSecondDiscountDialog.value = false;
                rowIndex.value = -1;
                editSecondDiscountIndex.value = null;
                toast.add({ severity: 'success', summary: 'Success', detail: 'Second discount set', life: 3000 });
            } catch (error) {
                console.error('Error setting second discount:', error);
                toast.add({ severity: 'error', summary: 'Error', detail: error.response.data, life: 3000 });
            }
        }

        const editFirstCondition = async () => {
            try {
                const url = `http://localhost:8082/api/trading/store/${storeName}/discounts/setFirstCondition/${rowIndex.value}/${editFirstConditionIndex.value}`;
                await axios.post(url, null, {
                    params: {
                        username: username.value,
                        token: token.value
                    }
                });
                fetchDiscounts();
                fetchConditions();
                showEditFirstConditionDialog.value = false;
                rowIndex.value = -1;
                editFirstConditionIndex.value = null;
                toast.add({ severity: 'success', summary: 'Success', detail: 'First condition set', life: 3000 });
            } catch (error) {
                console.error('Error setting first condition:', error);
                toast.add({ severity: 'error', summary: 'Error', detail: error.response.data, life: 3000 });
            }
        }

        const editSecondCondition = async () => {
            try {
                const url = `http://localhost:8082/api/trading/store/${storeName}/discounts/setSecondCondition/${rowIndex.value}/${editSecondConditionIndex.value}`;
                await axios.post(url, null, {
                    params: {
                        username: username.value,
                        token: token.value
                    }
                });
                fetchDiscounts();
                fetchConditions();
                showEditSecondConditionDialog.value = false;
                rowIndex.value = -1;
                editSecondConditionIndex.value = null;
                toast.add({ severity: 'success', summary: 'Success', detail: 'Second condition set', life: 3000 });
            } catch (error) {
                console.error('Error setting second condition:', error);
                toast.add({ severity: 'error', summary: 'Error', detail: error.response.data, life: 3000 });
            }
        }

        const editThenDiscount = async () => {
            try {
                const url = `http://localhost:8082/api/trading/store/${storeName}/discounts/setThenDiscount/${rowIndex.value}/${editThenDiscountIndex.value}`;
                await axios.post(url, null, {
                    params: {
                        username: username.value,
                        token: token.value
                    }
                });
                fetchDiscounts();
                fetchConditions();
                showEditThenDiscountDialog.value = false;
                rowIndex.value = -1;
                editThenDiscountIndex.value = null;
                toast.add({ severity: 'success', summary: 'Success', detail: 'Then discount set', life: 3000 });
            } catch (error) {
                console.error('Error setting then discount:', error);
                toast.add({ severity: 'error', summary: 'Error', detail: error.response.data, life: 3000 });
            }
        }

        const editDeciderCondition = async () => {
            try {
                console.log(editDeciderConditionIndex.value);
                const url = `http://localhost:8082/api/trading/store/${storeName}/discounts/setDeciderDiscount/${rowIndex.value}/${editDeciderConditionIndex.value}`;
                await axios.post(url, null, {
                    params: {
                        username: username.value,
                        token: token.value
                    }
                });
                fetchDiscounts();
                fetchConditions();
                showEditDeciderConditionDialog.value = false;
                rowIndex.value = -1;
                editDeciderConditionIndex.value = null;
                toast.add({ severity: 'success', summary: 'Success', detail: 'Decider changed', life: 3000 });
            } catch (error) {
                console.error('Error setting decider condition:', error);
                toast.add({ severity: 'error', summary: 'Error', detail: error.response.data, life: 3000 });
            }
        }

        const editConditionCount = async () => {
            try {
                const url = `http://localhost:8082/api/trading/store/${storeName}/discounts/setCountCondition/${rowIndex.value}/${editCountValue.value}`;
                await axios.post(url, null, {
                    params: {
                        username: username.value,
                        token: token.value
                    }
                });
                fetchConditions();
                showEditCountDialog.value = false;
                rowIndex.value = -1;
                editCountValue.value = null;
                toast.add({ severity: 'success', summary: 'Success', detail: 'Count changed', life: 3000 });
            } catch (error) {
                console.error('Error setting condition count:', error);
                toast.add({ severity: 'error', summary: 'Error', detail: error.response.data, life: 3000 });
            }
        }

        const editConditionCategory = async () => {
            try {
                const url = `http://localhost:8082/api/trading/store/${storeName}/discounts/setCategoryCondition/${rowIndex.value}/${editCondCategoryValue.value}`;
                await axios.post(url, null, {
                    params: {
                        username: username.value,
                        token: token.value
                    }
                });
                fetchConditions();
                showEditCategoryCondDialog.value = false;
                rowIndex.value = -1;
                editCondCategoryValue.value = null;
                toast.add({ severity: 'success', summary: 'Success', detail: 'Category changed', life: 3000 });
            } catch (error) {
                console.error('Error setting condition category:', error);
                toast.add({ severity: 'error', summary: 'Error', detail: error.response.data, life: 3000 });
            }
        }

        const editConditionProductID = async () => {
            try {
                const url = `http://localhost:8082/api/trading/store/${storeName}/discounts/setProductIdCondition/${rowIndex.value}/${editCondProductIDValue.value}`;
                await axios.post(url, null, {
                    params: {
                        username: username.value,
                        token: token.value
                    }
                });
                fetchConditions();
                showEditProductCondDialog.value = false;
                rowIndex.value = -1;
                editCondProductIDValue.value = null;
                toast.add({ severity: 'success', summary: 'Success', detail: 'Product id changed', life: 3000 });
            } catch (error) {
                console.error('Error setting condition product ID:', error);
                toast.add({ severity: 'error', summary: 'Error', detail: error.response.data, life: 3000 });
            }
        }

        const editConditionTotalSum = async () => {
            try {
                const url = `http://localhost:8082/api/trading/store/${storeName}/discounts/setTotalSum/${rowIndex.value}/${editTotalSumValue.value}`;
                await axios.post(url, null, {
                    params: {
                        username: username.value,
                        token: token.value
                    }
                });
                fetchConditions();
                showEditTotalSumDialog.value = false;
                rowIndex.value = -1;
                editTotalSumValue.value = null;
                toast.add({ severity: 'success', summary: 'Success', detail: 'Total sum changed', life: 3000 });
            } catch (error) {
                console.error('Error setting condition total sum:', error);
                toast.add({ severity: 'error', summary: 'Error', detail: error.response.data, life: 3000 });
            }
        }

        const deleteDiscount = async () => {
            try {
                const url = `http://localhost:8082/api/trading/store/${storeName}/discounts/removeDiscount/${rowIndex.value}`;
                await axios.delete(url, {
                    params: {
                        username: username.value,
                        token: token.value
                    }
                });
                fetchDiscounts();
                showDeleteDiscountDialog.value = false;
                rowIndex.value = -1;
                toast.add({ severity: 'success', summary: 'Success', detail: 'Discount deleted', life: 3000 });
            } catch (error) {
                console.error('Error deleting discount:', error);
                toast.add({ severity: 'error', summary: 'Error', detail: error.response.data, life: 3000 });
            }
        }

        const deleteCondition = async () => {
            try {
                const url = `http://localhost:8082/api/trading/store/${storeName}/discounts/removeCondition/${rowIndex.value}`;
                await axios.delete(url, {
                    params: {
                        username: username.value,
                        token: token.value
                    }
                });
                fetchConditions();
                showDeleteCondDialog.value = false;
                rowIndex.value = -1;
                toast.add({ severity: 'success', summary: 'Success', detail: 'Condition deleted', life: 3000 });
            } catch (error) {
                console.error('Error deleting condition:', error);
                toast.add({ severity: 'error', summary: 'Error', detail: error.response.data, life: 3000 });
            }
        }

        const openEditPercentDialog = (index) => {
            rowIndex.value = index;
            showEditPercentDialog.value = true;
        }

        const openEditCategoryDialog = (index) => {
            rowIndex.value = index;
            showEditCategoryDialog.value = true;
        }

        const openEditProductDialog = (index) => {
            rowIndex.value = index;
            showEditProductDialog.value = true;
        }

        const openEditFirstDiscountDialog = (index) => {
            rowIndex.value = index;
            showEditFirstDiscountDialog.value = true;
        }

        const openEditSecondDiscountDialog = (index) => {
            rowIndex.value = index;
            showEditSecondDiscountDialog.value = true;
        }

        const openEditFirstConditionDialog = (index) => {
            rowIndex.value = index;
            showEditFirstConditionDialog.value = true;
        }

        const openEditThenDiscountDialog = (index) => {
            rowIndex.value = index;
            showEditThenDiscountDialog.value = true;
        }

        const openEditSecondConditionDialog = (index) => {
            rowIndex.value = index;
            showEditSecondConditionDialog.value = true;
        }

        const openEditDeciderConditionDialog = (index) => {
            rowIndex.value = index;
            showEditDeciderConditionDialog.value = true;
        }

        const openDeleteDialog = (index) => {
            rowIndex.value = index;
            showDeleteDiscountDialog.value = true;
        }

        const openEditCountDialog = (index) => {
            rowIndex.value = index;
            showEditCountDialog.value = true;
        };

        const openEditCategoryCondDialog = (index) => {
            rowIndex.value = index;
            showEditCategoryCondDialog.value = true;
        };

        const openEditProductCondDialog = (index) => {
            rowIndex.value = index;
            showEditProductCondDialog.value = true;
        };

        const openEditTotalSumDialog = (index) => {
            rowIndex.value = index;
            showEditTotalSumDialog.value = true;
        };

        const openDeleteCondDialog = (index) => {
            rowIndex.value = index;
            showDeleteCondDialog.value = true;
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
            openEditPercentDialog,
            openEditCategoryDialog,
            openEditProductDialog,
            openEditFirstDiscountDialog,
            openEditSecondDiscountDialog,
            openEditFirstConditionDialog,
            openEditThenDiscountDialog,
            openEditSecondConditionDialog,
            openEditDeciderConditionDialog,
            openDeleteDialog,
            openEditCountDialog,
            openEditCategoryCondDialog,
            openEditProductCondDialog,
            openEditTotalSumDialog,
            openDeleteCondDialog,
            editPercentValue,
            editCategoryValue,
            editProductIDValue,
            editFirstDiscountIndex,
            editSecondDiscountIndex,
            editFirstConditionIndex,
            editSecondConditionIndex,
            editThenDiscountIndex,
            editDeciderConditionIndex,
            editCountValue,
            editCondCategoryValue,
            editCondProductIDValue,
            editTotalSumValue,
        };
    },
};
</script>

<style scoped>
.discount-management {
    padding: 20px;
}

.p-field {
    margin-bottom: 1em;
}

.button-container {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 10px;
    margin-top: 20px;
}

.button-container button {
    margin-bottom: 12px;
}

.input-container {
    margin-bottom: 10px;
}

.label {
    display: block;
}

.action-buttons button {
    margin-right: 12px;
    margin-top: 12px;
}

.p-datatable {
    margin-inline: 4rem;
    margin-top: 1rem;
    margin-bottom: 4rem;
    justify-content: center;
}

.dialog-header {
    display: flex;
    justify-content: center;
    align-items: center;
    font-size: 1.5em;
    width: 100%;
    text-align: center;
}

.p-dropdown-panel {
    max-width: 100% !important;
    width: auto !important;
    box-sizing: border-box;
}

/* Ensure the dropdown panel does not overflow the dialog */
.p-dialog .p-dropdown {
    width: 100%;
}
</style>
