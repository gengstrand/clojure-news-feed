import { createAsyncThunk, createSlice, PayloadAction } from '@reduxjs/toolkit'
import { RootState } from '../../app/store'
import { OutboundModel, OutboundApi, Util } from '../types.d'

interface OutboundState {
  feed: Array<OutboundModel>
}

export const fetchOutboundByFrom = createAsyncThunk(
  'outbound/fetchByFrom',
  async () => {
    const u = Util.getInstance()
    return await OutboundApi.getInstance(u).get()
  }
)

const addOutbound = createAsyncThunk (
  'outbound/add',
  (inb: OutboundModel, thunkAPI) => {
    const u = Util.getInstance()
    OutboundApi.getInstance(u).add(inb)
    return inb
  }
)

const initialState: OutboundState = {
  feed: []
}

export const outboundSlice = createSlice({
  name: 'outbound',
  initialState,
  reducers: {
  },
  extraReducers: (builder) => {
    builder.addCase(fetchOutboundByFrom.fulfilled, (state, action: PayloadAction<Array<OutboundModel>>) => {
      state.feed = action.payload
    })
    builder.addCase(addOutbound.fulfilled, (state, action: PayloadAction<OutboundModel>) => {
      state.feed.push(action.payload)
    })
  }
})

export const select = (state: RootState) => state.outbound

export default outboundSlice.reducer
