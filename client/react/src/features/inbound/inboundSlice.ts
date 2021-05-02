import { createAsyncThunk, createSlice, PayloadAction } from '@reduxjs/toolkit'
import { RootState } from '../../app/store'
import { InboundModel, InboundApi } from '../types.d'

interface InboundState {
  feed: Array<InboundModel>
}

export const fetchInboundByFrom = createAsyncThunk(
  'inbound/fetchByFrom',
  async (id: number) => {
    return await InboundApi.getInstance().get(id)
  }
)

const addInbound = createAsyncThunk (
  'inbound/add',
  async (inb: InboundModel, thunkAPI) => {
    await InboundApi.getInstance().add(inb)
    return inb
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
    builder.addCase(addInbound.fulfilled, (state, action: PayloadAction<InboundModel>) => {
      state.feed.push(action.payload)
    })
  }
})

export const select = (state: RootState) => state.inbound

export default inboundSlice.reducer
