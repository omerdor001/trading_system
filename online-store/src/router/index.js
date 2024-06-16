import { createRouter, createWebHistory } from 'vue-router';
import HomePage from '@/views/HomePage.vue';
import LoginModel from '@/views/LoginModel.vue';
import OpenStore from '@/views/OpenStore.vue';
import CloseStore from '@/views/CloseStore.vue'
import YieldOwnership from '@/views/YieldOwnership.vue'

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
    }
];

const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes
});

router.beforeEach((to, from, next) => {
  const loggedIn = localStorage.getItem('isLoggedIn'); // Adjusted localStorage key
  if (to.name !== 'LoginModel' && !loggedIn) {
    // If navigating to any route other than 'LoginModel' and not logged in,
    // redirect to login page
    next({ name: 'LoginModel' });
  } else if (to.name === 'LoginModel' && loggedIn) {
    // If navigating to 'LoginModel' route and already logged in,
    // redirect to home page
    next({ name: 'HomePage' });
  } else {
    // Continue navigation as usual
    next();
  }
});

export default router;
