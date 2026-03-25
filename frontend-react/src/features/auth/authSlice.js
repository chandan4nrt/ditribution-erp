import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import axios from 'axios';

// The baseUrl for API is empty or environment specific, let's assume /api
const API_URL = '/api'; // We'll configure proxy in vite later

export const login = createAsyncThunk('auth/login', async (credentials, thunkAPI) => {
    try {
        const response = await axios.post(`${API_URL}/auth/login`, credentials);
        if (response.data.token) {
            localStorage.setItem('token', response.data.token);
            axios.defaults.headers.common['Authorization'] = `Bearer ${response.data.token}`;
        }
        return response.data; // { token, username, roles }
    } catch (error) {
        return thunkAPI.rejectWithValue(error.response?.data?.message || 'Login failed');
    }
});

export const logout = createAsyncThunk('auth/logout', async () => {
    localStorage.removeItem('token');
    delete axios.defaults.headers.common['Authorization'];
    return null;
});

const initialState = {
    user: null,
    token: localStorage.getItem('token') || null,
    isAuthenticated: !!localStorage.getItem('token'),
    loading: false,
    error: null,
};

const authSlice = createSlice({
    name: 'auth',
    initialState,
    reducers: {
        clearError: (state) => {
            state.error = null;
        },
    },
    extraReducers: (builder) => {
        builder
            .addCase(login.pending, (state) => {
                state.loading = true;
                state.error = null;
            })
            .addCase(login.fulfilled, (state, action) => {
                state.loading = false;
                state.isAuthenticated = true;
                state.token = action.payload.token;
                // Currently response returns token. We might need to decode it to get roles, or just stick to standard.
                state.user = { username: action.payload.username, roles: [] }; // Expand as needed
            })
            .addCase(login.rejected, (state, action) => {
                state.loading = false;
                state.error = action.payload;
            })
            .addCase(logout.fulfilled, (state) => {
                state.user = null;
                state.token = null;
                state.isAuthenticated = false;
            });
    },
});

export const { clearError } = authSlice.actions;
export default authSlice.reducer;
