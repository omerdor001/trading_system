import { createRouter, createWebHistory } from 'vue-router';
import HomePage from '@/views/HomePage.vue';
import LoginModel from '@/views/LoginModel.vue';
import OpenStore from '@/views/OpenStore.vue';
import CloseStore from '@/views/CloseStore.vue';
import YieldOwnership from '@/views/YieldOwnership.vue';
import UserRegistration from '@/views/UserRegistration.vue';
import MyStoresIOwn from '@/views/MyStoresIOwn.vue';
import CreateSuspension from '@/views/CreateSuspension.vue';
import EndSuspension from '@/views/EndSuspension.vue';
import WatchSuspensions from '@/views/WatchSuspensions.vue';
import StoreNameInput from '@/views/StoreNameInput.vue';
import ProductList from '@/views/ProductListEditor.vue';
import ProductManagement from '@/views/ProductManagement.vue';
import StoresIManage from '@/views/StoresIManage.vue';
import StoreDetailsEditor from "@/views/StoreDetailsEditor.vue";
import StoreDetails from '@/views/StoreDetails.vue';
import ProductDetails from '@/views/ProductDetails.vue';
import ShoppingCart from '@/views/ShoppingCart.vue';
import Checkout from '@/views/CheckoutPage.vue';
import PurchaseHistory from '@/views/PurchaseHistory.vue';
import SearchPage from '@/views/SearchPage.vue';
import SearchResults from '@/views/SearchResults.vue';
import StoreSearchPage from '@/views/StoreSearchPage.vue';
import StoreSearchResults from '@/views/StoreSearchResults.vue';
import PaymentPage from '@/views/PaymentPage.vue';

const routes = [
    { path: '/', name: 'HomePage', component: HomePage },
    { path: '/open-store', name: 'OpenStore', component: OpenStore },
    { path: '/close-store', name: 'CloseStore', component: CloseStore },
    { path: '/yield-ownership', name: 'YieldOwnership', component: YieldOwnership },
    { path: '/login', name: 'LoginModel', component: LoginModel },
    { path: '/register', name: 'UserRegistration', component: UserRegistration },
    { path: '/my-stores-i-own', name: 'MyStoresIOwn', component: MyStoresIOwn },
    { path: '/store/:storeId/editor', name: 'StoreDetailsEditor', component: StoreDetailsEditor, props: true },
    { path: '/store/:storeId', name: 'StoreDetails', component: StoreDetails, props: true },
    { path: '/create-suspension', name: 'CreateSuspension', component: CreateSuspension },
    { path: '/end-suspension', name: 'EndSuspension', component: EndSuspension },
    { path: '/watch-suspensions', name: 'WatchSuspensions', component: WatchSuspensions },
    { path: '/stores-i-manage', name: 'StoresIManage', component: StoresIManage },
    { path: '/product/:productId', name: 'ProductDetails', component: ProductDetails, props: true },
    { path: '/cart', name: 'ShoppingCart', component: ShoppingCart },
    { path: '/checkout', name: 'Checkout', component: Checkout },
    { path: '/store-name-input', name: 'StoreNameInput', component: StoreNameInput },
    { path: '/product-list/:storeName', name: 'ProductList', component: ProductList, props: true },
    { path: '/product-management/:storeName/:productId', name: 'ProductManagement', component: ProductManagement, props: true },
    { path: '/purchase-history', name: 'PurchaseHistory', component: PurchaseHistory },
    { path: '/search', name: 'SearchPage', component: SearchPage },
    { path: '/search-results', name: 'SearchResults', component: SearchResults },
    { path: '/search-store', name: 'StoreSearchPage', component: StoreSearchPage },
    { path: '/store-search-results', name: 'StoreSearchResults', component: StoreSearchResults },
    { path: '/payment', name: 'PaymentPage', component: PaymentPage },


];

const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes
});


export default router;
