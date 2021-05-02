import { configureStore } from '@reduxjs/toolkit'
import inboundReducer from '../features/inbound/inboundSlice'

export const store = configureStore({
  reducer: {
    inbound: inboundReducer,
  }
})

export type RootState = ReturnType<typeof store.getState>
export type AppDispatch = typeof store.dispatch
