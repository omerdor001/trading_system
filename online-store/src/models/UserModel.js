import axios from 'axios';

class UserModel {
    static async login(token, usernameV, username, password) {
        try {
            const response = await axios.post('/api/login', {
                token,
                usernameV,
                username,
                password
            });
            return response.data;
        } catch (error) {
            throw new Error(error.response.data || 'Login failed.');
        }
    }
}

export default UserModel;
