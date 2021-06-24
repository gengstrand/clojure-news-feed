import { createAsyncThunk, createSlice, PayloadAction } from '@reduxjs/toolkit'
import { RootState } from '../../app/store'
import { InboundModel, InboundApi, Util } from '../types.d'

interface InboundState {
  feed: Array<InboundModel>
}

export const fetchInboundByFrom = createAsyncThunk(
  'inbound/fetchByFrom',
  async () => {
    const u = Util.getInstance()
    return await InboundApi.getInstance(u).get()
  }
)

const initialState: InboundState = {
  feed: []
}

export const inboundSlice = createSlice({
  name: 'inbound',
  initialState,
  reducers: {
  },
  extraReducers: (builder) => {
    builder.addCase(fetchInboundByFrom.fulfilled, (state, action: PayloadAction<Array<InboundModel>>) => {
      state.feed = action.payload
    })
  }
})

export const select = (state: RootState) => state.inbound

export default inboundSlice.reducer
