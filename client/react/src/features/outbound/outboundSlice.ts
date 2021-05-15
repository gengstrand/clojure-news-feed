import { createAsyncThunk, createSlice, PayloadAction } from '@reduxjs/toolkit'
import { RootState } from '../../app/store'
import { OutboundModel, OutboundApi } from '../types.d'

interface OutboundState {
  feed: Array<OutboundModel>
}

export const fetchOutboundByFrom = createAsyncThunk(
  'outbound/fetchByFrom',
  async (id: number) => {
    return await OutboundApi.getInstance().get(id)
  }
)

const addOutbound = createAsyncThunk (
  'outbound/add',
  async (inb: OutboundModel, thunkAPI) => {
    await OutboundApi.getInstance().add(inb)
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
