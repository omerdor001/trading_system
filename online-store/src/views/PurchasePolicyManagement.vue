<template>
    <div class="purchase-policy-management">
        <SiteHeader :isLoggedIn="true" :username="username" @logout="logout" />
        <h2>Purchase Policy Management</h2>
        <div class="button-container">
            <Button label="Add Purchase Policy" icon="pi pi-plus" @click="showAddPolicyDialog" />
        </div>
       <Dialog header="Add Purchase Policy" v-model="addPolicyDialogVisible" :visible="addPolicyDialogVisible"
            @hide="addPolicyDialogVisible = false" :closable="false" blockScroll :draggable="false" modal>
            <div class="p-fluid">
                <div class="p-field">
                    <label for="policyType">Select policy type</label>
                    <Dropdown id="policyType" v-model="newPolicy.type" :options="policyTypes" optionLabel="label"
                        optionValue="value" />
                </div>
                <template v-if="newPolicy.type === 'By Age and Category'">
                    <div class="p-field">
                        <label for="ageToCheck">Age to check</label>
                        <InputNumber id="ageToCheck" v-model="newPolicy.ageToCheck" mode="decimal" min="0" />
                    </div>
                    <div class="p-field">
                        <label for="category">Select category</label>
                        <Dropdown id="category" v-model="newPolicy.category" :options="categoryOptions"
                            optionLabel="label" optionValue="value" />
                    </div>
                </template>
                <template v-else-if="newPolicy.type === 'By Category and Date'">
                    <div class="p-field">
                        <label for="category">Select category</label>
                        <Dropdown id="category" v-model="newPolicy.category" :options="categoryOptions"
                            optionLabel="label" optionValue="value" />
                    </div>
                    <div class="p-field">
                        <label for="dateTime">Select date</label>
                        <PrimeCalendar v-model="newPolicy.dateTime" id="dateTime" />
                    </div>
                </template>
                <template v-else-if="newPolicy.type === 'By Date'">
                    <div class="p-field">
                        <label for="dateTime">Select date</label>
                        <PrimeCalendar v-model="newPolicy.dateTime" id="dateTime" />
                    </div>
                </template>
                <template v-else-if="newPolicy.type === 'By Product and Date'">
                    <div class="p-field">
                        <label for="productId">Product ID</label>
                        <InputNumber id="productId" v-model="newPolicy.productId" />
                    </div>
                    <div class="p-field">
                        <label for="dateTime">Select date</label>
                        <PrimeCalendar v-model="newPolicy.dateTime" id="dateTime" />
                    </div>
                </template>
                <template v-else-if="newPolicy.type === 'By Shopping Cart Max Products Unit'">
                    <div class="p-field">
                        <label for="productId">Product ID</label>
                        <InputNumber id="productId" v-model="newPolicy.productId" />
                    </div>
                    <div class="p-field">
                        <label for="numOfQuantity">Max quantity</label>
                        <InputNumber id="numOfQuantity" v-model="newPolicy.numOfQuantity" mode="decimal" min="0" />
                    </div>
                </template>
                <template v-else-if="newPolicy.type === 'By Shopping Cart Min Products'">
                    <div class="p-field">
                        <label for="numOfQuantity">Min quantity</label>
                        <InputNumber id="numOfQuantity" v-model="newPolicy.numOfQuantity" mode="decimal" min="0" />
                    </div>
                </template>
                <template v-else-if="newPolicy.type === 'By Shopping Cart Min Products Unit'">
                    <div class="p-field">
                        <label for="productId">Product ID</label>
                        <InputNumber id="productId" v-model="newPolicy.productId" />
                    </div>
                    <div class="p-field">
                        <label for="numOfQuantity">Min quantity</label>
                        <InputNumber id="numOfQuantity" v-model="newPolicy.numOfQuantity" mode="decimal" min="0" />
                    </div>
                </template>
                <template v-else-if="newPolicy.type === 'And'">
                </template>
                <template v-else-if="newPolicy.type === 'Or'">
                </template>
                <template v-else-if="newPolicy.type === 'Conditioning'">
                </template>
            </div>
            <template #footer>
                <Button label="Cancel" icon="pi pi-times" @click="addPolicyDialogVisible = false"
                    class="p-button-text" />
                <Button label="Save" icon="pi pi-check" @click="savePolicy" />
            </template>
        </Dialog>
        <DataTable :value="policies" title="Purchase Policies" responsiveLayout="scroll" class="p-mt-3">
            <template #header>
                <div class="flex flex-wrap items-center justify-between gap-2">
                    <span class="text-xl font-bold">Purchase Policies</span>
                </div>
            </template>
            <Column field="index" header="Index" style="width: 10%;" :headerStyle="{ 'text-align': 'center' }"></Column>
            <Column field="type" header="Type" style="width: 20%;" :headerStyle="{ 'text-align': 'center' }"
                :body="typeTemplate">
            </Column>
            <Column field="details" header="Details" style="width: 20%;" :headerStyle="{ 'text-align': 'center' }"
                :body="detailsTemplate"></Column>
            <Column header="Actions" style="width: 30%;">
                <template #body="rowData">
                    <div class="action-buttons">
                        <Button v-if="rowData.data.type === 'Age and Category Policy'" @click="() => confirmEditAgePolicy(rowData.data.index)">Edit Age</Button>
                        <Button v-if="rowData.data.type === 'Category and Date Policy' || rowData.data.type === 'Age and Category Policy'" @click="() => confirmEditCategoryPolicy(rowData.data.index)">Edit Category</Button>
                        <Button v-if="rowData.data.type === 'Date Policy' || rowData.data.type === 'Category and Date Policy' || rowData.data.type === 'Product and Date Policy'" @click="() => confirmEditDatePolicy(rowData.data.index)">Edit Date</Button>
                        <Button v-if="rowData.data.type === 'Product and Date Policy' || rowData.data.type === 'Maximum product units in shopping bag Policy' || rowData.data.type === 'Minimum product units in shopping bag Policy'" @click="() => confirmEditProductPolicy(rowData.data.index)">Edit Product</Button>
                        <Button v-if="rowData.data.type === 'Minimum items in shopping bag Policy' || rowData.data.type === 'Maximum product units in shopping bag Policy' || rowData.data.type === 'Minimum product units in shopping bag Policy'" @click="() => confirmEditUnitsPolicy(rowData.data.index)">Edit Units</Button>
                        <Button v-if="rowData.data.type === 'And Policy' || rowData.data.type === 'Or Policy'" @click="() => confirmEditFirstPolicy(rowData.data.index)">Edit First Policy</Button>
                        <Button v-if="rowData.data.type === 'And Policy' || rowData.data.type === 'Or Policy'" @click="() => confirmEditSecondPolicy(rowData.data.index)">Edit Second Policy</Button>
                        <Button v-if="rowData.data.type === 'Conditioning Policy'" @click="() => confirmEditIfPolicy(rowData.data.index)">Edit If Policy</Button>
                        <Button v-if="rowData.data.type === 'Conditioning Policy'" @click="() => confirmEditThenPolicy(rowData.data.index)">Edit Then Policy</Button>
                        <Button @click="() => confirmDeletePolicy(rowData.data.index)">Delete Policy</Button>
                    </div>

                    <!-- Edit Age Dialog -->
                    <Dialog header="Edit Age" v-model="showEditAgeDialog" :visible="showEditAgeDialog"
                        @hide="showEditAgeDialog = false" :closable="false" blockScroll :draggable="false" modal>
                        <InputNumber id="editAge" v-model="editAgeValue" />
                        <div class="dialog-footer">
                            <Button @click="cancelEditAge">Cancel</Button>
                            <Button @click="editPolicyAge(selectedIndex)">Save</Button>
                        </div>
                    </Dialog>
            
                    <!-- Edit Category Dialog -->
                    <Dialog header="Edit Category" v-model="showEditCategoryDialog" :visible="showEditCategoryDialog" 
                            @hide="showEditCategoryDialog = false" :closable="false" blockScroll :draggable="false" modal>
                        <div class="p-fluid">
                            <div class="p-field">
                                <label for="editCategory">Select category</label>
                                <Dropdown id="editCategory" v-model="editCategoryValue" :options="categoryOptions" optionLabel="label" optionValue="value" />
                            </div>
                        </div>
                        <div class="dialog-footer">
                            <Button @click="cancelEditCategory">Cancel</Button>
                            <Button @click="editPolicyCategory(selectedIndex)">Save</Button>
                        </div>
                    </Dialog>

                    <!-- Edit Date Dialog -->
                    <Dialog v-model="showEditDateDialog" :visible="showEditDateDialog" header="Edit Date" 
                            :closable="false" blockScroll :draggable="false" modal>
                        <PrimeCalendar v-model="editDateValue" />
                        <div class="dialog-footer">
                            <Button @click="cancelEditDate">Cancel</Button>
                            <Button @click="editPolicyDate(selectedIndex)">Save</Button>
                        </div>
                    </Dialog>

                    <!-- Edit Product Dialog -->
                    <Dialog v-model="showEditProductDialog" :visible="showEditProductDialog" header="Edit Product Id" 
                            :closable="false" blockScroll :draggable="false" modal>
                        <InputNumber v-model="editProductIdValue" />
                        <div class="dialog-footer">
                            <Button @click="cancelEditProduct">Cancel</Button>
                            <Button @click="editPolicyProduct(selectedIndex)">Save</Button>
                        </div>
                    </Dialog>

                    <!-- Edit Units Dialog -->
                    <Dialog v-model="showEditUnitsDialog" :visible="showEditUnitsDialog" header="Edit Units" 
                            :closable="false" blockScroll :draggable="false" modal>
                        <InputNumber v-model="editUnitsValue" />
                        <div class="dialog-footer">
                            <Button @click="cancelEditUnits">Cancel</Button>
                            <Button @click="editPolicyUnits(selectedIndex)">Save</Button>
                        </div>
                    </Dialog>

                    <!-- Edit First Policy Dialog -->
                    <Dialog v-model="showEditFirstDialog" :visible="showEditFirstDialog" header="Edit First Policy" 
                            :closable="false" blockScroll :draggable="false" modal>
                        <div class="p-fluid">
                            <div class="p-field">
                                <label for="firstPolicy">Select First Policy</label>
                                <Dropdown id="firstPolicy" v-model="editFirstPolicyValue" :options="policyIndexes" optionLabel="label" optionValue="value" />
                            </div>
                        </div>
                        <div class="dialog-footer">
                            <Button @click="cancelEditFirst">Cancel</Button>
                            <Button @click="editPolicyFirst(selectedIndex)">Save</Button>
                        </div>
                    </Dialog>

                    <!-- Edit Second Policy Dialog -->
                    <Dialog v-model="showEditSecondDialog" :visible="showEditSecondDialog" header="Edit Second Policy" 
                            :closable="false" blockScroll :draggable="false" modal>
                        <div class="p-fluid">
                            <div class="p-field">
                                <label for="secondPolicy">Select Second Policy</label>
                                <Dropdown id="secondPolicy" v-model="editSecondPolicyValue" :options="policyIndexes" optionLabel="label" optionValue="value" />
                            </div>
                        </div>
                        <div class="dialog-footer">
                            <Button @click="cancelEditSecond">Cancel</Button>
                            <Button @click="editPolicySecond(selectedIndex)">Save</Button>
                        </div>
                    </Dialog>

                    <!-- Edit If Policy Dialog -->
<Dialog v-model="showEditIfDialog" :visible="showEditIfDialog" header="Edit If Policy" 
        :closable="false" blockScroll :draggable="false" modal>
    <div class="p-fluid">
        <div class="p-field">
            <label for="ifPolicy">Select If Policy</label>
            <Dropdown id="ifPolicy" v-model="editFirstPolicyValue" :options="policyIndexes" optionLabel="label" optionValue="value" />
        </div>
    </div>
    <div class="dialog-footer">
        <Button @click="cancelEditFirst">Cancel</Button>
        <Button @click="editPolicyFirst(selectedIndex)">Save</Button>
    </div>
</Dialog>

<!-- Edit Then Policy Dialog -->
<Dialog v-model="showEditThenDialog" :visible="showEditThenDialog" header="Edit Then Policy" 
        :closable="false" blockScroll :draggable="false" modal>
    <div class="p-fluid">
        <div class="p-field">
            <label for="thenPolicy">Select Then Policy</label>
            <Dropdown id="thenPolicy" v-model="editSecondPolicyValue" :options="policyIndexes" optionLabel="label" optionValue="value" />
        </div>
    </div>
    <div class="dialog-footer">
        <Button @click="cancelEditSecond">Cancel</Button>
        <Button @click="editPolicySecond(selectedIndex)">Save</Button>
    </div>
</Dialog>

                    <!-- Delete Policy Dialog -->
                    <Dialog v-model="showDeletePolicyDialog" :visible="showDeletePolicyDialog" header="Delete Policy" 
                            :closable="false" blockScroll :draggable="false" modal>
                        <div class="dialog-footer">
                            <Button @click="showDeletePolicyDialog = false" style="margin-left: 10px;">Cancel</Button>
                            <Button @click="deletePolicy(selectedIndex)">Save</Button>
                        </div>
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
import SiteHeader from '@/components/SiteHeader.vue';
import Button from 'primevue/button';
import DataTable from 'primevue/datatable';
import Column from 'primevue/column';
import Dropdown from 'primevue/dropdown';
import InputNumber from 'primevue/inputnumber';
import Dialog from 'primevue/dialog';
import PrimeCalendar from 'primevue/calendar';

export default {
    components: {
        SiteHeader,
        Button,
        DataTable,
        Column,
        Dropdown,
        InputNumber,
        Dialog,
        PrimeCalendar
    },

    setup() {
        const username = ref(localStorage.getItem('username') || '');
        const token = ref(localStorage.getItem('token') || '');
        const toast = useToast();
        const route = useRoute();
        const storeName = route.params.storeName;
        const policies = ref([]);
        const addPolicyDialogVisible = ref(false);
        const showEditAgeDialog = ref(false);
        const showEditCategoryDialog = ref(false);
        const showEditDateDialog = ref(false);
        const showEditProductDialog = ref(false);
        const showEditUnitsDialog = ref(false);
        const showEditFirstDialog = ref(false);
        const showEditSecondDialog = ref(false);
        const showEditIfDialog = ref(false);
        const showEditThenDialog = ref(false);
        const showDeletePolicyDialog = ref(false);
        const selectedIndex = ref(null);
        const editCategoryValue = ref(null);
        const editAgeValue = ref(null);
        const editDateValue = ref(null);
        const editProductIdValue = ref(null);
        const editUnitsValue = ref(null);
        const editFirstPolicyValue = ref(null);
        const editSecondPolicyValue = ref(null);
        const newPolicy = ref({
            type: '',
            ageToCheck: '',
            category: null,
            dateTime: '',
            productId: '',
            numOfQuantity: ''
        });
        const policyTypes = ref([
            { label: 'By Age and Category', value: 'By Age and Category' },
            { label: 'By Category and Date', value: 'By Category and Date' },
            { label: 'By Date', value: 'By Date' },
            { label: 'By Product and Date', value: 'By Product and Date' },
            { label: 'By Maximum product units in shopping bag', value: 'By Shopping Cart Max Products Unit' },
            { label: 'By Minimum items in shopping bag', value: 'By Shopping Cart Min Products' },
            { label: 'By Minimum product units in shopping bag', value: 'By Shopping Cart Min Products Unit' },
            { label: 'And', value: 'And' },
            { label: 'Or', value: 'Or' },
            { label: 'Conditioning', value: 'Conditioning' }
        ]);
        const categoryOptions = ref([
            { label: 'Sport', value: 1 },
            { label: 'Art', value: 2 },
            { label: 'Food', value: 3 },
            { label: 'Clothes', value: 4 },
            { label: 'Films', value: 5 }
        ]);
        const policyIndexes = ref([]);

        onMounted(async () => {
            await fetchPolicies();
        });

        const fetchPolicies = async () => {
            try {
                const url = `http://localhost:8082/api/trading/store/purchase-policies/getPurchasePoliciesInfo`;
                const response = await axios.get(url, {
                    params: {
                        username: username.value,
                        token: token.value,
                        storeName: storeName
                    }
                });

                if (response.data && Array.isArray(response.data)) {
                    policies.value = response.data.map((policy) => ({
                        index: policy.index,
                        type: getPolicyTypeLabel(policy.policy.type),
                        details: createDetails(policy.policy)
                    }));

                    // Update policyIndexes after fetching policies
                    policyIndexes.value = policies.value.map(policy => ({
                        label: `Policy ${policy.index}`,
                        value: policy.index
                    }));
                } else {
                    console.error('Unexpected response format:', response.data);
                    toast.add({ severity: 'error', summary: 'Error', detail: 'Unexpected response format', life: 3000 });
                }
            } catch (error) {
                console.error('Error fetching policies:', error);
                toast.add({ severity: 'error', summary: 'Error', detail: error.response?.data || error.message, life: 3000 });
            }
        };

        const showAddPolicyDialog = () => {
            newPolicy.value = {
                type: null,
                ageToCheck: null,
                category: null,
                dateTime: null,
                productId: null,
                numOfQuantity: null
            };
            addPolicyDialogVisible.value = true;
        };

        const savePolicy = async () => {
            // Format the dateTime to ISO string without trailing 'Z'
            const formattedDateTime = newPolicy.value.dateTime
                ? new Date(newPolicy.value.dateTime).toISOString().slice(0, -1)
                : null;

            if (!newPolicy.value.type) {
                toast.add({ severity: 'warn', summary: 'Warning', detail: 'Policy type cannot be empty or unknown', life: 3000 });
                return;
            }

            const validateCategory = () => {
                if (!newPolicy.value.category) {
                    toast.add({ severity: 'warn', summary: 'Warning', detail: 'Category cannot be empty', life: 3000 });
                    return false;
                }
                return true;
            };

            const validateDate = () => {
                if (!newPolicy.value.dateTime) {
                    toast.add({ severity: 'warn', summary: 'Warning', detail: 'Date cannot be empty', life: 3000 });
                    return false;
                }
                return true;
            };

            const validateProductId = () => {
                if (newPolicy.value.productId === null) {
                    toast.add({ severity: 'warn', summary: 'Warning', detail: 'Product ID cannot be empty', life: 3000 });
                    return false;
                }
                return true;
            };

            const validateQuantity = () => {
                if (newPolicy.value.numOfQuantity === null) {
                    toast.add({ severity: 'warn', summary: 'Warning', detail: 'Quantity cannot be empty', life: 3000 });
                    return false;
                }
                return true;
            };

            const validateAge = () => {
                if (!newPolicy.value.ageToCheck) {
                    toast.add({ severity: 'warn', summary: 'Warning', detail: 'Age cannot be empty', life: 3000 });
                    return false;
                }
                return true;
            };

            switch (newPolicy.value.type) {
                // Handle the different types of policies here
                case 'By Age and Category':
                    if (!validateCategory() || !validateAge()) return;
                    try {
                        const url = `http://localhost:8082/api/trading/store/purchase-policies/addPurchasePolicyByAge`;
                        const response = await axios.post(url, null, {
                            params: {
                                username: username.value,
                                token: token.value,
                                storeName: storeName,
                                ageToCheck: newPolicy.value.ageToCheck,
                                category: newPolicy.value.category
                            }
                        });
                        console.log(response.data);
                        fetchPolicies();
                    } catch (error) {
                        toast.add({ severity: 'error', summary: 'Error', detail: error.response.data || 'Failed to add age and category policy', life: 3000 });
                        return;
                    }
                    break;
                case 'By Category and Date':
                    if (!validateCategory() || !validateDate()) return;
                    try {
                        const url = `http://localhost:8082/api/trading/store/purchase-policies/addPurchasePolicyByCategoryAndDate`;
                        const response = await axios.post(url, null, {
                            params: {
                                username: username.value,
                                token: token.value,
                                storeName: storeName,
                                category: newPolicy.value.category,
                                dateTime: formattedDateTime
                            }
                        });
                        console.log(response.data);
                        fetchPolicies();
                    } catch (error) {
                        toast.add({ severity: 'error', summary: 'Error', detail: error.response.data || 'Failed to add category and date policy', life: 3000 });
                        return;
                    }
                    break;
                case 'By Date':
                    if (!validateDate()) return;
                    try {
                        const url = `http://localhost:8082/api/trading/store/purchase-policies/addPurchasePolicyByDate`;
                        const response = await axios.post(url, null, {
                            params: {
                                username: username.value,
                                token: token.value,
                                storeName: storeName,
                                dateTime: formattedDateTime
                            }
                        });
                        console.log(response.data);
                        fetchPolicies();
                    } catch (error) {
                        toast.add({ severity: 'error', summary: 'Error', detail: error.response.data || 'Failed to add date policy', life: 3000 });
                        return;
                    }
                    break;
                case 'By Product and Date':
                    if (!validateProductId() || !validateDate()) return;
                    try {
                        const url = `http://localhost:8082/api/trading/store/purchase-policies/addPurchasePolicyByProductAndDate`;
                        const response = await axios.post(url, null, {
                            params: {
                                username: username.value,
                                token: token.value,
                                storeName: storeName,
                                productId: newPolicy.value.productId,
                                dateTime: formattedDateTime
                            }
                        });
                        console.log(response.data);
                        fetchPolicies();
                    } catch (error) {
                        toast.add({ severity: 'error', summary: 'Error', detail: error.response.data || 'Failed to add product and date policy', life: 3000 });
                        return;
                    }
                    break;
                case 'By Shopping Cart Max Products Unit':
                    if (!validateProductId() || !validateQuantity()) return;
                    try {
                        const url = `http://localhost:8082/api/trading/store/purchase-policies/addPurchasePolicyByShoppingCartMaxProductsUnit`;
                        const response = await axios.post(url, null, {
                            params: {
                                username: username.value,
                                token: token.value,
                                storeName: storeName,
                                productId: newPolicy.value.productId,
                                numOfQuantity: newPolicy.value.numOfQuantity
                            }
                        });
                        console.log(response.data);
                        fetchPolicies();
                    } catch (error) {
                        toast.add({ severity: 'error', summary: 'Error', detail: error.response.data || 'Failed to add maximum product units policy', life: 3000 });
                        return;
                    }
                    break;
                case 'By Shopping Cart Min Products':
                    if (!validateQuantity()) return;
                    try {
                        const url = `http://localhost:8082/api/trading/store/purchase-policies/addPurchasePolicyByShoppingCartMinProducts`;
                        const response = await axios.post(url, null, {
                            params: {
                                username: username.value,
                                token: token.value,
                                storeName: storeName,
                                numOfQuantity: newPolicy.value.numOfQuantity
                            }
                        });
                        console.log(response.data);
                        fetchPolicies();
                    } catch (error) {
                        toast.add({ severity: 'error', summary: 'Error', detail: error.response.data || 'Failed to add minimum units policy', life: 3000 });
                        return;
                    }
                    break;
                case 'By Shopping Cart Min Products Unit':
                    if (!validateProductId() || !validateQuantity()) return;
                    try {
                        const url = `http://localhost:8082/api/trading/store/purchase-policies/addPurchasePolicyByShoppingCartMinProductsUnit`;
                        const response = await axios.post(url, null, {
                            params: {
                                username: username.value,
                                token: token.value,
                                storeName: storeName,
                                productId: newPolicy.value.productId,
                                numOfQuantity: newPolicy.value.numOfQuantity
                            }
                        });
                        console.log(response.data);
                        fetchPolicies();
                    } catch (error) {
                        toast.add({ severity: 'error', summary: 'Error', detail: error.response.data || 'Failed to add minimum product units policy', life: 3000 });
                        return;
                    }
                    break;
                case 'And':
                    try {
                        const url = `http://localhost:8082/api/trading/store/purchase-policies/addAndPurchasePolicy`;
                        const response = await axios.post(url, null, {
                            params: {
                                username: username.value,
                                token: token.value,
                                storeName: storeName,
                            }
                        });
                        console.log(response.data);
                        fetchPolicies();
                    } catch (error) {
                        toast.add({ severity: 'error', summary: 'Error', detail: error.response.data || 'Failed to add and policy', life: 3000 });
                        return;
                    }
                    break;
                case 'Or':
                    try {
                        const url = `http://localhost:8082/api/trading/store/purchase-policies/addOrPurchasePolicy`;
                        const response = await axios.post(url, null, {
                            params: {
                                username: username.value,
                                token: token.value,
                                storeName: storeName,
                            }
                        });
                        console.log(response.data);
                        fetchPolicies();
                    } catch (error) {
                        toast.add({ severity: 'error', summary: 'Error', detail: error.response.data || 'Failed to add or policy', life: 3000 });
                        return;
                    }
                    break;
                case 'Conditioning':
                    try {
                        const url = `http://localhost:8082/api/trading/store/purchase-policies/addConditioningPurchasePolicy`;
                        const response = await axios.post(url, null, {
                            params: {
                                username: username.value,
                                token: token.value,
                                storeName: storeName,
                            }
                        });
                        console.log(response.data);
                        fetchPolicies();
                    } catch (error) {
                        toast.add({ severity: 'error', summary: 'Error', detail: error.response.data || 'Failed to add conditioning policy', life: 3000 });
                        return;
                    }
                    break;
                default:
                    toast.add({ severity: 'warn', summary: 'Warning', detail: 'Unknown policy type', life: 3000 });
                    return;
            }
            addPolicyDialogVisible.value = false;
            toast.add({ severity: 'success', summary: 'Success', detail: 'Policy saved', life: 3000 });
        };

        const getPolicyTypeLabel = (type) => {
            const typeMap = {
                'Age and Category Policy': 'Age and Category Policy',
                'Category and Date Policy': 'Category and Date Policy',
                'Date Policy': 'Date Policy',
                'Product and Date Policy': 'Product and Date Policy',
                'Maximum product units in shopping bag Policy': 'Maximum product units in shopping bag Policy',
                'Minimum items in shopping bag Policy': 'Minimum items in shopping bag Policy',
                'Minimum product units in shopping bag Policy': 'Minimum product units in shopping bag Policy',
                'And Policy': 'And Policy',
                'Or Policy': 'Or Policy',
                'Conditioning Policy': 'Conditioning Policy'
            };
            return typeMap[type] || type;
        };

        const createDetails = (policy) => {
            switch (policy.type) {
                case 'Age and Category Policy':
                    return `Age: ${policy.age}, Category: ${categoryOptions.value.find(opt => opt.value === policy.category)?.label}`;
                case 'Category and Date Policy':
                    return `Category: ${categoryOptions.value.find(opt => opt.value === policy.category)?.label}, Date: ${policy.date}`;
                case 'Date Policy':
                    return `Date: ${policy.date}`;
                case 'Product and Date Policy':
                    return `Product ID: ${policy.productId}, Date: ${policy.date}`;
                case 'Maximum product units in shopping bag Policy':
                    return `Product ID: ${policy.productId}, Units: ${policy.units}`;
                case 'Minimum items in shopping bag Policy':
                    return `Units: ${policy.units}`;
                case 'Minimum product units in shopping bag Policy':
                    return `Product ID: ${policy.productId}, Units: ${policy.units}`;
                case 'And Policy':
                    return `First: ${createDetails(policy.first)}, Second: ${createDetails(policy.second)}`;
                case 'Or Policy':
                    return `First: ${createDetails(policy.first)}, Second: ${createDetails(policy.second)}`;
                case 'Conditioning Policy':
                    return `If: ${createDetails(policy.first)}, Then: ${createDetails(policy.second)}`;
                case 'Placeholder Policy':
                    return 'Placeholder';
                default:
                    return 'Unknown Policy Type';
            }
        };

        const typeTemplate = (rowData) => {
            return getPolicyTypeLabel(rowData.data.type);
        };

        const detailsTemplate = (rowData) => {
            return h('div', {}, [
                h('span', {}, rowData.details)
            ]);
        };

        // Reset functions for each dialog
        const resetEditAge = () => {
            editAgeValue.value = null;
        };

        const resetEditCategory = () => {
            editCategoryValue.value = null;
        };

        const resetEditDate = () => {
            editDateValue.value = null;
        };

        const resetEditProduct = () => {
            editProductIdValue.value = null;
        };

        const resetEditUnits = () => {
            editUnitsValue.value = null;
        };

        const resetEditFirst = () => {
            editFirstPolicyValue.value = null;
        };

        const resetEditSecond = () => {
            editSecondPolicyValue.value = null;
        };

        // Cancel methods for each dialog
        const cancelEditAge = () => {
            resetEditAge();
            showEditAgeDialog.value = false;
        };

        const cancelEditCategory = () => {
            resetEditCategory();
            showEditCategoryDialog.value = false;
        };

        const cancelEditDate = () => {
            resetEditDate();
            showEditDateDialog.value = false;
        };

        const cancelEditProduct = () => {
            resetEditProduct();
            showEditProductDialog.value = false;
        };

        const cancelEditUnits = () => {
            resetEditUnits();
            showEditUnitsDialog.value = false;
        };

        const cancelEditFirst = () => {
            resetEditFirst();
            showEditFirstDialog.value = false;
            showEditIfDialog.value = false;
        };

        const cancelEditSecond = () => {
            resetEditSecond();
            showEditSecondDialog.value = false;
            showEditThenDialog.value = false;
        };

        const editPolicyAge = async (index) => {
            try {
                if (!editAgeValue.value) {
                    toast.add({ severity: 'warn', summary: 'Warning', detail: 'Age value cannot be empty', life: 3000 });
                    return;
                }
                const url = `http://localhost:8082/api/trading/store/purchase-policies/setPurchasePolicyAge`;
                const response = await axios.put(url, null, {
                    params: {
                        username: username.value,
                        token: token.value,
                        storeName: storeName,
                        selectedIndex: index,
                        age: editAgeValue.value
                    }
                });
                console.log(response.data);
                fetchPolicies();
                resetEditAge();
                showEditAgeDialog.value = false;
                toast.add({ severity: 'success', summary: 'Success', detail: 'Age updated', life: 3000 });
            } catch (error) {
                console.error('Error updating age:', error);
                toast.add({ severity: 'error', summary: 'Error', detail: error.response.data || 'Failed to update age', life: 3000 });
            }
        };

        const editPolicyCategory = async (index) => {
            try {
                if (!editCategoryValue.value) {
                    toast.add({ severity: 'warn', summary: 'Warning', detail: 'Category value cannot be empty', life: 3000 });
                    return;
                }
                const url = `http://localhost:8082/api/trading/store/purchase-policies/setPurchasePolicyCategory`;
                const response = await axios.put(url, null, {
                    params: {
                        username: username.value,
                        token: token.value,
                        storeName: storeName,
                        selectedIndex: index,
                        category: editCategoryValue.value
                    }
                });
                console.log(response.data);
                fetchPolicies();
                resetEditCategory();
                showEditCategoryDialog.value = false;
                toast.add({ severity: 'success', summary: 'Success', detail: 'Category updated', life: 3000 });
            } catch (error) {
                console.error('Error updating category:', error);
                toast.add({ severity: 'error', summary: 'Error', detail: error.response.data || 'Failed to update category', life: 3000 });
            }
        };

        const editPolicyDate = async (index) => {
            try {
                if (!editDateValue.value) {
                    toast.add({ severity: 'warn', summary: 'Warning', detail: 'Date cannot be empty', life: 3000 });
                    return false;
                }
                const formattedDate = new Date(editDateValue.value).toISOString().slice(0, -1);
                const url = `http://localhost:8082/api/trading/store/purchase-policies/setPurchasePolicyDateTime`;
                const response = await axios.put(url, null, {
                    params: {
                        username: username.value,
                        token: token.value,
                        storeName: storeName,
                        selectedIndex: index,
                        dateTime: formattedDate
                    }
                });
                console.log(response.data);
                fetchPolicies();
                resetEditDate();
                showEditDateDialog.value = false;
                toast.add({ severity: 'success', summary: 'Success', detail: 'Date updated', life: 3000 });
            } catch (error) {
                console.error('Error updating date:', error);
                toast.add({ severity: 'error', summary: 'Error', detail: error.response.data || 'Failed to update date', life: 3000 });
            }
        };

        const editPolicyProduct = async (index) => {
            try {
                if (editProductIdValue.value === null) {
                    toast.add({ severity: 'warn', summary: 'Warning', detail: 'Product Id value cannot be empty', life: 3000 });
                    return;
                }
                const url = `http://localhost:8082/api/trading/store/purchase-policies/setPurchasePolicyProductId`;
                const response = await axios.put(url, null, {
                    params: {
                        username: username.value,
                        token: token.value,
                        storeName: storeName,
                        selectedIndex: index,
                        productId: editProductIdValue.value
                    }
                });
                console.log(response.data);
                fetchPolicies();
                resetEditProduct();
                showEditProductDialog.value = false;
                toast.add({ severity: 'success', summary: 'Success', detail: 'Product ID updated', life: 3000 });
            } catch (error) {
                console.error('Error updating product ID:', error);
                toast.add({ severity: 'error', summary: 'Error', detail: error.response.data || 'Failed to update product Id', life: 3000 });
            }
        };

        const editPolicyUnits = async (index) => {
            try {
                if (editUnitsValue.value === null) {
                    toast.add({ severity: 'warn', summary: 'Warning', detail: 'Units value cannot be empty', life: 3000 });
                    return;
                }
                const url = `http://localhost:8082/api/trading/store/purchase-policies/setPurchasePolicyNumOfQuantity`;
                const response = await axios.put(url, null, {
                    params: {
                        username: username.value,
                        token: token.value,
                        storeName: storeName,
                        selectedIndex: index,
                        numOfQuantity: editUnitsValue.value
                    }
                });
                console.log(response.data);
                fetchPolicies();
                resetEditUnits();
                showEditUnitsDialog.value = false;
                toast.add({ severity: 'success', summary: 'Success', detail: 'Quantity updated', life: 3000 });
            } catch (error) {
                console.error('Error updating quantity:', error);
                toast.add({ severity: 'error', summary: 'Error', detail: error.response.data || 'Failed to update units', life: 3000 });
            }
        };

        const editPolicyFirst = async (index) => {
            try {
                const url = `http://localhost:8082/api/trading/store/purchase-policies/setFirstPurchasePolicy`;
                const response = await axios.put(url, null, {
                    params: {
                        username: username.value,
                        token: token.value,
                        storeName: storeName,
                        selectedPolicyIndex: index,
                        selectedFirstIndex: editFirstPolicyValue.value
                    }
                });
                console.log(response.data);
                fetchPolicies();
                resetEditFirst();
                showEditFirstDialog.value = false;
                showEditIfDialog.value = false;
                toast.add({ severity: 'success', summary: 'Success', detail: 'First policy updated', life: 3000 });
            } catch (error) {
                console.error('Error updating first policy:', error);
                toast.add({ severity: 'error', summary: 'Error', detail: error.response.data || 'Failed to update first policy', life: 3000 });
            }
        };

        const editPolicySecond = async (index) => {
            try {
                const url = `http://localhost:8082/api/trading/store/purchase-policies/setSecondPurchasePolicy`;
                const response = await axios.put(url, null, {
                    params: {
                        username: username.value,
                        token: token.value,
                        storeName: storeName,
                        selectedPolicyIndex: index,
                        selectedSecondIndex: editSecondPolicyValue.value
                    }
                });
                console.log(response.data);
                fetchPolicies();
                resetEditSecond();
                showEditSecondDialog.value = false;
                showEditThenDialog.value = false;
                toast.add({ severity: 'success', summary: 'Success', detail: 'Second policy updated', life: 3000 });
            } catch (error) {
                console.error('Error updating second policy:', error);
                toast.add({ severity: 'error', summary: 'Error', detail: error.response.data || 'Failed to update second policy', life: 3000 });
            }
        };

        const confirmDeletePolicy = (index) => {
            selectedIndex.value = index;
            showDeletePolicyDialog.value = true;
        };

        const confirmEditAgePolicy = (index) => {
            selectedIndex.value = index;
            showEditAgeDialog.value = true;
        };

        const confirmEditCategoryPolicy = (index) => {
            selectedIndex.value = index;
            showEditCategoryDialog.value = true;
        };

        const confirmEditDatePolicy = (index) => {
            selectedIndex.value = index;
            showEditDateDialog.value = true;
        };

        const confirmEditProductPolicy = (index) => {
            selectedIndex.value = index;
            showEditProductDialog.value = true;
        };

        const confirmEditUnitsPolicy = (index) => {
            selectedIndex.value = index;
            showEditUnitsDialog.value = true;
        };

        const confirmEditFirstPolicy = (index) => {
            selectedIndex.value = index;
            showEditFirstDialog.value = true;
        };

        const confirmEditSecondPolicy = (index) => {
            selectedIndex.value = index;
            showEditSecondDialog.value = true;
        };

        const confirmEditIfPolicy = (index) => {
            selectedIndex.value = index;
            showEditIfDialog.value = true;
        };

        const confirmEditThenPolicy = (index) => {
            selectedIndex.value = index;
            showEditThenDialog.value = true;
        };


        const deletePolicy = async (index) => {
            try {
                const url = `http://localhost:8082/api/trading/store/purchase-policies/removePurchasePolicy`;
                await axios.delete(url, {
                    params: {
                        username: username.value,
                        token: token.value,
                        storeName: storeName,
                        selectedIndex: index
                    }
                });
                fetchPolicies();
                showDeletePolicyDialog.value = false;
                toast.add({ severity: 'success', summary: 'Success', detail: 'Policy removed', life: 3000 });
            } catch (error) {
                console.error('Error deleting policy:', error);
                toast.add({ severity: 'error', summary: 'Error', detail: error.message, life: 3000 });
            }
        };

        return {
            username,
            token,
            policies,
            policyTypes,
            categoryOptions,
            policyIndexes,
            addPolicyDialogVisible,
            showEditAgeDialog,
            showEditCategoryDialog,
            showEditDateDialog,
            showEditProductDialog,
            showEditUnitsDialog,
            showEditFirstDialog,
            showEditSecondDialog,
            showEditIfDialog,
            showEditThenDialog,
            showDeletePolicyDialog,
            newPolicy,
            selectedIndex,
            editCategoryValue,
            editAgeValue,
            editDateValue,
            editProductIdValue,
            editUnitsValue,
            editFirstPolicyValue,
            editSecondPolicyValue,
            showAddPolicyDialog,
            savePolicy,
            typeTemplate,
            detailsTemplate,
            editPolicyAge,
            editPolicyCategory,
            editPolicyDate,
            editPolicyProduct,
            editPolicyUnits,
            editPolicyFirst,
            editPolicySecond,
            confirmDeletePolicy,
            confirmEditAgePolicy,
            confirmEditCategoryPolicy,
            confirmEditDatePolicy,
            confirmEditProductPolicy,
            confirmEditUnitsPolicy,
            confirmEditFirstPolicy,
            confirmEditSecondPolicy,
            confirmEditIfPolicy,
            confirmEditThenPolicy,
            deletePolicy,
            cancelEditAge,
            cancelEditCategory,
            cancelEditDate,
            cancelEditProduct,
            cancelEditUnits,
            cancelEditFirst,
            cancelEditSecond
        };
    }
};
</script>

<style scoped>
.purchase-policy-management {
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

.dialog-footer {
    display: flex;
    justify-content: space-between;
    margin-top: 20px;
}

.p-dialog-header {
    background-color: #f4f4f4;
    border-bottom: 1px solid #ddd;
}

.p-dialog-content {
    padding: 20px;
}

.p-dialog-footer {
    background-color: #f4f4f4;
    border-top: 1px solid #ddd;
    display: flex;
    justify-content: flex-end;
    padding: 10px;
}

.p-button {
    margin-left: 10px;
}
</style>