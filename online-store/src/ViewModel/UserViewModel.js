import { reactive } from 'vue';
import UserModel from '../models/UserModel';

const state = reactive({
    username: '',
    token: '',
    loginStatus: '',
    error: null
});

const actions = {
    async login(usernameV, username, password) {
        try {
            const response = await UserModel.login(state.token, usernameV, username, password);
            state.username = response.username;
            state.token = response.token;
            state.loginStatus = 'Login successful';
            state.error = null;
        } catch (error) {
            state.error = error.message;
            state.loginStatus = 'Login failed';
        }
    },
    logout() {
        localStorage.removeItem('isLoggedIn');
        localStorage.removeItem('roles');
        localStorage.removeItem('username');
        state.username = '';
        state.token = '';
        state.loginStatus = '';
        state.error = null;
    },
    async yieldOwnership(userName, token, storeName){
        try {
            await UserModel.yieldOwnership(userName, token, storeName);
            this.error = null
        } catch(error) {
            state.error = error.message
            state.openStatus = 'Failed to yield ownership';
        }
    }
};

const getters = {
    isLoggedIn() {
        return !!state.token;
    },
    getUsername() {
        return state.username;
    },
    getLoginStatus() {
        return state.loginStatus;
    },
    getError() {
        return state.error;
    }
};

export default {
    state,
    actions,
    getters
};
