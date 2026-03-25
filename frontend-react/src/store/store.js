import { configureStore } from '@reduxjs/toolkit';
import authReducer from '../features/auth/authSlice';
import productReducer from '../features/products/productSlice';
import transactionReducer from '../features/transactions/transactionSlice';

export const store = configureStore({
    reducer: {
        auth: authReducer,
        products: productReducer,
        transactions: transactionReducer,
    },
});
