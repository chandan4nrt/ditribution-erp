import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import axios from 'axios';

const API_URL = '/api/master/products';

export const fetchProducts = createAsyncThunk('products/fetchProducts', async ({ search, page, size }, thunkAPI) => {
    try {
        const response = await axios.get(API_URL, {
            params: { search, page, size }
        });
        return response.data; // Page object
    } catch (error) {
        return thunkAPI.rejectWithValue(error.response?.data?.message || 'Failed to fetch products');
    }
});

const initialState = {
    content: [],
    pageable: null,
    totalElements: 0,
    totalPages: 0,
    loading: false,
    error: null,
};

const productSlice = createSlice({
    name: 'products',
    initialState,
    reducers: {},
    extraReducers: (builder) => {
        builder
            .addCase(fetchProducts.pending, (state) => {
                state.loading = true;
                state.error = null;
            })
            .addCase(fetchProducts.fulfilled, (state, action) => {
                state.loading = false;
                state.content = action.payload.content;
                state.totalElements = action.payload.totalElements;
                state.totalPages = action.payload.totalPages;
                state.pageable = action.payload.pageable;
            })
            .addCase(fetchProducts.rejected, (state, action) => {
                state.loading = false;
                state.error = action.payload;
            });
    },
});

export default productSlice.reducer;
