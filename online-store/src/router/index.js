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
import ProductList from '@/views/ProductList.vue';
import ProductManagement from '@/views/ProductManagement.vue';
import StoresIManage from '@/views/StoresIManage.vue';
import StoreDetailsEditor from "@/views/StoreDetailsEditor.vue";
import StoreDetails from '@/views/StoreDetails.vue';
import ProductDetails from '@/views/ProductDetails.vue';
import ShoppingCart from '@/views/ShoppingCart.vue';
import Checkout from '@/views/Checkout.vue';


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
    {path: '/store-name-input', name: 'StoreNameInput', component: StoreNameInput},
    {path: '/product-list/:storeName', name: 'ProductList', component: ProductList, props: true},
    {path: '/product-management/:storeName/:productId', name: 'ProductManagement', component: ProductManagement, props: true},
];


const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes
});

router.beforeEach((to, from, next) => {
    const loggedIn = localStorage.getItem('isLoggedIn');
    if (to.name !== 'LoginModel' && to.name !== 'UserRegistration' && !loggedIn) {
        next({ name: 'LoginModel' });
    } else if ((to.name === 'LoginModel' || to.name === 'UserRegistration') && loggedIn) {
        next({ name: 'HomePage' });
    } else {
        next();
    }
});

export default router;
