import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import axios from 'axios';

const INVENTORY_URL = '/api/transactions/inventory-dashboard';

export const fetchGlobalInventory = createAsyncThunk('transactions/fetchGlobalInventory', async (_, thunkAPI) => {
    try {
        const response = await axios.get(`${INVENTORY_URL}/global`);
        return response.data;
    } catch (error) {
        return thunkAPI.rejectWithValue(error.response?.data?.message || 'Failed to fetch inventory');
    }
});

export const fetchLowStockAlerts = createAsyncThunk('transactions/fetchLowStockAlerts', async (_, thunkAPI) => {
    try {
        const response = await axios.get(`${INVENTORY_URL}/alerts/low-stock`);
        return response.data;
    } catch (error) {
        return thunkAPI.rejectWithValue(error.response?.data?.message || 'Failed to fetch alerts');
    }
});

const initialState = {
    globalInventory: [],
    lowStockAlerts: [],
    loading: false,
    error: null,
};

const transactionSlice = createSlice({
    name: 'transactions',
    initialState,
    reducers: {},
    extraReducers: (builder) => {
        builder
            .addCase(fetchGlobalInventory.pending, (state) => {
                state.loading = true;
                state.error = null;
            })
            .addCase(fetchGlobalInventory.fulfilled, (state, action) => {
                state.loading = false;
                state.globalInventory = action.payload;
            })
            .addCase(fetchGlobalInventory.rejected, (state, action) => {
                state.loading = false;
                state.error = action.payload;
            })
            .addCase(fetchLowStockAlerts.fulfilled, (state, action) => {
                state.lowStockAlerts = action.payload;
            });
    },
});

export default transactionSlice.reducer;
