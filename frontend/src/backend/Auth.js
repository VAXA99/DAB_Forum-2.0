import {jwtDecode} from "jwt-decode";
import axios from "axios";
import baseUrl from "./base-url";

let BASE_URL = baseUrl + '/auth';
const api = axios.create({
    baseURL: BASE_URL,
});
export default {

    isTokenValid: () => {
        const token = localStorage.getItem('token');

        if (!token) {
            return false;
        }

        try {
            const decodedToken = jwtDecode(token);

            const currentTime = Date.now() / 1000; // Convert to seconds
            if (decodedToken.exp < currentTime) {
                // Token has expired
                return false;
            }

            return true;
        } catch (error) {
            console.error('Error decoding token:', error);
            return false;
        }
    },

    signIn: async (username, password) => {
        try {
            const response = await api.post('/signIn', {
                username,
                password,
            });

            return response.data;
        } catch (error) {
            throw error.response.data;
        }
    },

    signUp: async (username, password) => {
        try {
            const response = await api.post('/signUp', {
                username,
                password
            });
            return {success: response.status === 200, error: null};
        } catch (error) {
            console.error('Error on signUp: ', error);
            return {success: false, error: error.response ? error.response.data : 'Unknown error'};
        }
    },

    logout: async () => {
        localStorage.removeItem('token');

        return false;
    },

    isUsernameValid: async (username) => {
        try {
            const response = await api.get('/existsByUsername',
                {
                    params: {
                        username: username
                    }
                });

            return response.status === 200;
        } catch (error) {
            console.error('Error checking username: ', error);
            return false;
        }
    },

    getUserId: () => {
        try {
            return jwtDecode(localStorage.getItem('token')).userId;
        } catch (error) {
            return null;
        }

    },

    getUsernameFromToken: () => {
        try {
            return jwtDecode(localStorage.getItem('token')).sub;
        } catch (error) {
            return null;
        }
    },

    getUserRole: () => {
        try {
            return jwtDecode(localStorage.getItem('token')).roles;
        } catch (error) {
            return null;
        }
    }
}


