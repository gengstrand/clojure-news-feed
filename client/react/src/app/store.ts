import { configureStore } from '@reduxjs/toolkit'
import inboundReducer from '../features/inbound/inboundSlice'
import outboundReducer from '../features/outbound/outboundSlice'
import outboundSearchReducer from '../features/outbound/outboundSearchSlice'
import friendsReducer from '../features/friends/friendsSlice'

export const store = configureStore({
  reducer: {
    inbound: inboundReducer,
    outbound: outboundReducer,
    outboundSearch: outboundSearchReducer,
    friends: friendsReducer
  }
})

export type RootState = ReturnType<typeof store.getState>
export type AppDispatch = typeof store.dispatch
