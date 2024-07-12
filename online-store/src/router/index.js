import { createRouter, createWebHistory } from 'vue-router';
import HomePage from '@/views/HomePage.vue';
import LoginModel from '@/views/LoginModel.vue';
import OpenStore from '@/views/OpenStore.vue';
import CloseStore from '@/views/CloseStore.vue';
import UserRegistration from '@/views/UserRegistration.vue';
import WorkersInStore from '@/views/WorkersInStore.vue';
import CreateSuspension from '@/views/CreateSuspension.vue';
import EndSuspension from '@/views/EndSuspension.vue';
import WatchSuspensions from '@/views/WatchSuspensions.vue';
import AllStoresPage from '@/views/AllStoresPage.vue';
import ProductList from '@/views/ProductListEditor.vue';
import ProductManagement from '@/views/ProductManagement.vue';
import StoresPageManagerVue from '@/views/StoresPageManager.vue';
import StoreDetailsEditor from "@/views/StoreDetailsEditor.vue";
import StoreDetails from '@/views/StoreDetails.vue';
import ProductDetails from '@/views/ProductDetails.vue';
import ShoppingCart from '@/views/ShoppingCart.vue';
import Checkout from '@/views/CheckoutPage.vue';
import PurchaseHistory from '@/views/PurchaseHistory.vue';
import PurchaseHistoryForStore from '@/views/PurchaseHistoryForStore.vue';
import SearchPage from '@/views/SearchPage.vue';
import SearchResults from '@/views/SearchResults.vue';
import StoreSearchPage from '@/views/StoreSearchPage.vue';
import StoreSearchResults from '@/views/StoreSearchResults.vue';
import SuggestOwner from '@/views/SuggestOwner.vue';
import SuggestManager from '@/views/SuggestManager.vue';
import ApproveRoles from '@/views/Approves.vue';
import WatchStoreBids from '@/views/WatchStoreBids.vue';
import PaymentPage from '@/views/PaymentPage.vue';
import ProductsForStore from '@/views/ProductsForStore.vue';
import EditPurchasePolicy from '@/views/PurchasePolicyManagement.vue';
import EditDiscountPolicy from '@/components/DiscountManagement.vue';

const routes = [
    { path: '/', name: 'HomePage', component: HomePage },
    { path: '/open-store', name: 'OpenStore', component: OpenStore },
    { path: '/close-store', name: 'CloseStore', component: CloseStore },
    { path: '/login', name: 'LoginModel', component: LoginModel },
    { path: '/register', name: 'UserRegistration', component: UserRegistration },
    { path: '/workers-in-store', name: 'WorkersInStore', component: WorkersInStore },
    { path: '/store/:storeId/editor', name: 'StoreDetailsEditor', component: StoreDetailsEditor, props: true },
    { path: '/store/:storeId', name: 'StoreDetails', component: StoreDetails, props: true },
    { path: '/create-suspension', name: 'CreateSuspension', component: CreateSuspension },
    { path: '/end-suspension', name: 'EndSuspension', component: EndSuspension },
    { path: '/watch-suspensions', name: 'WatchSuspensions' ,component: WatchSuspensions },
    { path: '/stores-page', name: 'StoresPageManager', component: StoresPageManagerVue },
    { path: '/product/:productId/:storeId', name: 'ProductDetails', component: ProductDetails, props: true },
    { path: '/cart', name: 'ShoppingCart', component: ShoppingCart },
    { path: '/checkout', name: 'Checkout', component: Checkout },
    { path: '/product-list/:storeName/:isEditSupply', name: 'ProductList', component: ProductList, props: true },
    { path: '/product-management/:storeName/:productId', name: 'ProductManagement', component: ProductManagement, props: true },
    { path: '/purchase-history', name: 'PurchaseHistory', component: PurchaseHistory },
    { path: '/purchase-history/:storeName', name: 'PurchaseHistoryForStore', component: PurchaseHistoryForStore },
    { path: '/search', name: 'SearchPage', component: SearchPage },
    { path: '/search-results', name: 'SearchResults', component: SearchResults },
    { path: '/search-store', name: 'StoreSearchPage', component: StoreSearchPage },
    { path: '/store-search-results', name: 'StoreSearchResults', component: StoreSearchResults },
    { path: '/suggest-owner/:storeName', name: 'SuggestOwner', component: SuggestOwner },
    { path: '/suggest-manager/:storeName', name: 'SuggestManager', component: SuggestManager },
    { path: '/approve-roles', name: 'ApproveRoles', component: ApproveRoles },
    { path: '/get-store-bids', name: 'WatchStoreBids', component: WatchStoreBids },
    { path: '/payment', name: 'PaymentPage', component: PaymentPage },
    { path: '/productsForStore/:storeName', name: 'ProductsForStore', component: ProductsForStore },
    { path: '/edit-discount-policy/:storeName', name: 'EditDiscountPolicy', component: EditDiscountPolicy },
    { path: '/edit-purchase-policy/:storeName', name: 'EditPurchasePolicy', component: EditPurchasePolicy },
    { path: '/all-stores', name: 'AllStoresPage', component: AllStoresPage },
    { path: '/workers-in-store/:storeName', name: 'WorkersInStore', component: WorkersInStore }
];

const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes,
});

export default router;
