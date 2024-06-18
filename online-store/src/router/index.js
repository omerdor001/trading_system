import { createRouter, createWebHistory } from 'vue-router';
import HomePage from '@/views/HomePage.vue';
import LoginModel from '@/views/LoginModel.vue';
import OpenStore from '@/views/OpenStore.vue';
import CloseStore from '@/views/CloseStore.vue';
import YieldOwnership from '@/views/YieldOwnership.vue';
import UserRegistration from '@/views/UserRegistration.vue';
import MyStoresIOwn from '@/views/MyStoresIOwn.vue'; // Import the new component
import StoreDetails from '@/views/StoreDetails.vue'; // Import the store details component
import CreateSuspension from '@/views/CreateSuspension.vue';
import EndSuspension from '@/views/EndSuspension.vue';
import WatchSuspensions from '@/views/WatchSuspensions.vue';

const routes = [
    {
        path: '/',
        name: 'HomePage',
        component: HomePage
    },
    {
        path: '/open-store',
        name: 'OpenStore',
        component: OpenStore
    },
    {
        path: '/close-store',
        name: 'CloseStore',
        component: CloseStore
    },
    {
        path: '/yield-ownership',
        name: 'YieldOwnership',
        component: YieldOwnership
    },
    {
        path: '/login',
        name: 'LoginModel',
        component: LoginModel
    },
    {
        path: '/register',
        name: 'UserRegistration',
        component: UserRegistration
    },
    {
        path: '/my-stores-i-own',
        name: 'MyStoresIOwn',
        component: MyStoresIOwn // Ensure the route is correctly defined
    },
    {
        path: '/store/:storeId',
        name: 'StoreDetails',
        component: StoreDetails,
        props: true // Pass route params as props
    },
    {
        path: '/create-suspension',
        name: 'CreateSuspension',
        component: CreateSuspension
    },
    {
        path: '/end-suspension',
        name: 'EndSuspension',
        component: EndSuspension
    },
    {
        path: '/watch-suspensions',
        name: 'WatchSuspensions',
        component: WatchSuspensions
    }

];

const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes
});

router.beforeEach((to, from, next) => {
    const loggedIn = localStorage.getItem('isLoggedIn'); // Adjusted localStorage key
    if (to.name !== 'LoginModel' && to.name !== 'UserRegistration' && !loggedIn) {
        // If navigating to any route other than 'LoginModel' or 'UserRegistration' and not logged in,
        // redirect to login page
        next({ name: 'LoginModel' });
    } else if ((to.name === 'LoginModel' || to.name === 'UserRegistration') && loggedIn) {
        // If navigating to 'LoginModel' or 'UserRegistration' route and already logged in,
        // redirect to home page
        next({ name: 'HomePage' });
    } else {
        // Continue navigation as usual
        next();
    }
});

export default router;
