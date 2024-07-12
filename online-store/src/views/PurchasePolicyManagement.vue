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
                        <Button @click="() => confirmDeletePolicy(rowData.data.index)">Delete Policy</Button>
                    </div>

                    <!-- Edit Age Dialog -->
                    <Dialog header="Edit Age" v-model="showEditAgeDialog" :visible="showEditAgeDialog"
                        @hide="showEditAgeDialog = false" :closable="false" blockScroll :draggable="false" modal>
                        <InputNumber id="editAge" v-model="editAgeValue" />
                        <div class="dialog-footer">
                            <Button @click="showEditAgeDialog = false">Cancel</Button>
                            <Button @click="editPolicyAge(selectedIndex)">Save</Button>
                        </div>
                    </Dialog>
            
                    <!-- Edit Category Dialog -->
                    <Dialog header="Edit Category" v-model="showEditCategoryDialog" :visible="showEditCategoryDialog" 
                            @hide="howEditCategoryDialog = false" :closable="false" blockScroll :draggable="false" modal>
                        <div class="p-fluid">
                            <div class="p-field">
                                <label for="editCategory">Select category</label>
                                <Dropdown id="editCategory" v-model="editCategoryValue" :options="categoryOptions" optionLabel="label" optionValue="value" />
                            </div>
                        </div>
                        <div class="dialog-footer">
                            <Button @click="showEditCategoryDialog = false">Cancel</Button>
                            <Button @click="editPolicyCategory(selectedIndex)">Save</Button>
                        </div>
                    </Dialog>

                    <!-- Edit Date Dialog -->
                    <Dialog v-model="showEditDateDialog" :visible="showEditDateDialog" header="Edit Date" 
                            :closable="false" blockScroll :draggable="false" modal>
                        <PrimeCalendar v-model="editDateValue" />
                        <div class="dialog-footer">
                            <Button @click="showEditDateDialog = false">Cancel</Button>
                            <Button @click="editPolicyDate(selectedIndex)">Save</Button>
                        </div>
                    </Dialog>

                    <!-- Edit Product Dialog -->
                    <Dialog v-model="showEditProductDialog" :visible="showEditProductDialog" header="Edit Product Id" 
                            :closable="false" blockScroll :draggable="false" modal>
                        <InputNumber v-model="editProductIdValue" />
                        <div class="dialog-footer">
                            <Button @click="showEditProductDialog = false">Cancel</Button>
                            <Button @click="editPolicyProduct(selectedIndex)">Save</Button>
                        </div>
                    </Dialog>

                    <!-- Edit Units Dialog -->
                    <Dialog v-model="showEditUnitsDialog" :visible="showEditUnitsDialog" header="Edit Units" 
                            :closable="false" blockScroll :draggable="false" modal>
                        <InputNumber v-model="editQuantityValue" />
                        <div class="dialog-footer">
                            <Button @click="showEditUnitsDialog = false">Cancel</Button>
                            <Button @click="editPolicyUnits(selectedIndex, editQuantityValue)">Save</Button>
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
        const showDeletePolicyDialog = ref(false);
        const selectedIndex = ref(null);
        const editCategoryValue = ref(null);
        const editAgeValue = ref(null);
        const editDateValue = ref(null);
        const editProductIdValue = ref(null);
        const newPolicy = ref({
            type: '',
            ageToCheck: 0,
            category: null,
            dateTime: '',
            productId: -1,
            numOfQuantity: 0
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
                type: '',
                ageToCheck: 0,
                category: null,
                dateTime: '',
                productId: -1,
                numOfQuantity: 0
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

    switch (newPolicy.value.type) {
        // Handle the different types of policies here
        case 'By Age and Category':
            if (!validateCategory()) return;
            try {
                const url = `http://localhost:8082/api/trading/store/purchase-policies/addPurchasePolicyByAge`;
                await axios.post(url, null, {
                    params: {
                        username: username.value,
                        token: token.value,
                        storeName: storeName,
                        ageToCheck: newPolicy.value.ageToCheck,
                        category: newPolicy.value.category
                    }
                });
                fetchPolicies();
            } catch (error) {
                toast.add({ severity: 'error', summary: 'Error', detail: error.message, life: 3000 });
                return;
            }
            break;
        case 'By Category and Date':
            if (!validateCategory() || !validateDate()) return;
            try {
                const url = `http://localhost:8082/api/trading/store/purchase-policies/addPurchasePolicyByCategoryAndDate`;
                await axios.post(url, null, {
                    params: {
                        username: username.value,
                        token: token.value,
                        storeName: storeName,
                        category: newPolicy.value.category,
                        dateTime: formattedDateTime
                    }
                });
                fetchPolicies();
            } catch (error) {
                toast.add({ severity: 'error', summary: 'Error', detail: error.message, life: 3000 });
                return;
            }
            break;
        case 'By Date':
            if (!validateDate()) return;
            try {
                const url = `http://localhost:8082/api/trading/store/purchase-policies/addPurchasePolicyByDate`;
                await axios.post(url, null, {
                    params: {
                        username: username.value,
                        token: token.value,
                        storeName: storeName,
                        dateTime: formattedDateTime
                    }
                });
                fetchPolicies();
            } catch (error) {
                toast.add({ severity: 'error', summary: 'Error', detail: error.message, life: 3000 });
                return;
            }
            break;
        case 'By Product and Date':
            if (!validateDate()) return;
            try {
                const url = `http://localhost:8082/api/trading/store/purchase-policies/addPurchasePolicyByProductAndDate`;
                await axios.post(url, null, {
                    params: {
                        username: username.value,
                        token: token.value,
                        storeName: storeName,
                        productId: newPolicy.value.productId,
                        dateTime: formattedDateTime
                    }
                });
                fetchPolicies();
            } catch (error) {
                toast.add({ severity: 'error', summary: 'Error', detail: error.message, life: 3000 });
                return;
            }
            break;
        case 'By Shopping Cart Max Products Unit':
            try {
                const url = `http://localhost:8082/api/trading/store/purchase-policies/addPurchasePolicyByShoppingCartMaxProductsUnit`;
                await axios.post(url, null, {
                    params: {
                        username: username.value,
                        token: token.value,
                        storeName: storeName,
                        productId: newPolicy.value.productId,
                        numOfQuantity: newPolicy.value.numOfQuantity
                    }
                });
                fetchPolicies();
            } catch (error) {
                toast.add({ severity: 'error', summary: 'Error', detail: error.message, life: 3000 });
                return;
            }
            break;
        case 'By Shopping Cart Min Products':
            try {
                const url = `http://localhost:8082/api/trading/store/purchase-policies/addPurchasePolicyByShoppingCartMinProducts`;
                await axios.post(url, null, {
                    params: {
                        username: username.value,
                        token: token.value,
                        storeName: storeName,
                        numOfQuantity: newPolicy.value.numOfQuantity
                    }
                });
                fetchPolicies();
            } catch (error) {
                toast.add({ severity: 'error', summary: 'Error', detail: error.message, life: 3000 });
                return;
            }
            break;
        case 'By Shopping Cart Min Products Unit':
            try {
                const url = `http://localhost:8082/api/trading/store/purchase-policies/addPurchasePolicyByShoppingCartMinProductsUnit`;
                await axios.post(url, null, {
                    params: {
                        username: username.value,
                        token: token.value,
                        storeName: storeName,
                        productId: newPolicy.value.productId,
                        numOfQuantity: newPolicy.value.numOfQuantity
                    }
                });
                fetchPolicies();
            } catch (error) {
                toast.add({ severity: 'error', summary: 'Error', detail: error.message, life: 3000 });
                return;
            }
            break;
        case 'And':
            try {
                const url = `http://localhost:8082/api/trading/store/purchase-policies/addAndPurchasePolicy`;
                await axios.post(url, null, {
                    params: {
                        username: username.value,
                        token: token.value,
                        storeName: storeName,
                    }
                });
                fetchPolicies();
            } catch (error) {
                toast.add({ severity: 'error', summary: 'Error', detail: error.message, life: 3000 });
                return;
            }
            break;
        case 'Or':
            try {
                const url = `http://localhost:8082/api/trading/store/purchase-policies/addOrPurchasePolicy`;
                await axios.post(url, null, {
                    params: {
                        username: username.value,
                        token: token.value,
                        storeName: storeName,
                    }
                });
                fetchPolicies();
            } catch (error) {
                toast.add({ severity: 'error', summary: 'Error', detail: error.message, life: 3000 });
                return;
            }
            break;
        case 'Conditioning':
            try {
                const url = `http://localhost:8082/api/trading/store/purchase-policies/addConditioningPurchasePolicy`;
                await axios.post(url, null, {
                    params: {
                        username: username.value,
                        token: token.value,
                        storeName: storeName,
                    }
                });
                fetchPolicies();
            } catch (error) {
                toast.add({ severity: 'error', summary: 'Error', detail: error.message, life: 3000 });
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

        const editPolicyAge = async (index) => {
            try {
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
                editAgeValue.value = null;
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
                editCategoryValue.value = null;
                showEditCategoryDialog.value = false;
                toast.add({ severity: 'success', summary: 'Success', detail: 'Category updated', life: 3000 });
            } catch (error) {
                console.error('Error updating category:', error);
                toast.add({ severity: 'error', summary: 'Error', detail: error.response.data || 'Failed to update category', life: 3000 });
            }
        };

        const editPolicyDate = async (index) => {
            try {
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
                editDateValue.value = null;
                showEditDateDialog.value = false;
                toast.add({ severity: 'success', summary: 'Success', detail: 'Date updated', life: 3000 });
            } catch (error) {
                console.error('Error updating date:', error);
                toast.add({ severity: 'error', summary: 'Error',detail: error.response.data || 'Failed to update date', life: 3000 });
            }
        };

        const editPolicyProduct = async (index) => {
            try {
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
                editProductIdValue.value = null;
                showEditProductDialog.value = false;
                toast.add({ severity: 'success', summary: 'Success', detail: 'Product ID updated', life: 3000 });
            } catch (error) {
                console.error('Error updating product ID:', error);
                toast.add({ severity: 'error', summary: 'Error', detail: error.response.data || 'Failed to update product Id', life: 3000 });
            }
        };

        const editPolicyUnits = async (index, numOfQuantity) => {
            try {
                const url = `http://localhost:8082/api/trading/store/purchase-policies/setPurchasePolicyNumOfQuantity`;
                const response = await axios.put(url, null, {
                    params: {
                        username: username.value,
                        token: token.value,
                        storeName: storeName,
                        selectedIndex: index,
                        numOfQuantity: numOfQuantity
                    }
                });
                console.log(response.data);
                fetchPolicies();
                showEditUnitsDialog.value = false;
                toast.add({ severity: 'success', summary: 'Success', detail: 'Quantity updated', life: 3000 });
            } catch (error) {
                console.error('Error updating quantity:', error);
                toast.add({ severity: 'error', summary: 'Error',  detail: error.response.data || 'Failed to update units', life: 3000 });
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
            addPolicyDialogVisible,
            showEditAgeDialog,
            showEditCategoryDialog,
            showEditDateDialog,
            showEditProductDialog,
            showEditUnitsDialog,
            showDeletePolicyDialog,
            newPolicy,
            selectedIndex,
            editCategoryValue,
            editAgeValue,
            editDateValue,
            editProductIdValue,
            showAddPolicyDialog,
            savePolicy,
            typeTemplate,
            detailsTemplate,
            editPolicyAge,
            editPolicyCategory,
            editPolicyDate,
            editPolicyProduct,
            editPolicyUnits,
            confirmDeletePolicy,
            confirmEditAgePolicy,
            confirmEditCategoryPolicy,
            confirmEditDatePolicy,
            confirmEditProductPolicy,
            confirmEditUnitsPolicy,
            deletePolicy,
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
how can i clear the contents in the stuff inside the edit dialogs after cancel/save?