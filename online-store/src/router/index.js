import Vue from 'vue';
import Router from 'vue-router';
import HomePage from '@/views/HomePage.vue';
import LoginModel from '@/views/LoginModel.vue'; // Import the login component
import OpenStore from '@/views/OpenStore.vue';


Vue.use(Router);

const router = new Router({
  mode: 'history',
  routes: [
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
      path: '/login',
      name: 'LoginModel',
      component: LoginModel
    }
  ]
});

// Redirect to home if logged in
router.beforeEach((to, from, next) => {
  const loggedIn = localStorage.getItem('loggedIn');
  if (to.path === '/login' && loggedIn) {
    next('/');
  } else {
    next();
  }
});

export default router;
