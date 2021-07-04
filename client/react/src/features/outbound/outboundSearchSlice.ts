import { createAsyncThunk, createSlice, PayloadAction } from '@reduxjs/toolkit'
import { RootState } from '../../app/store'
import { SearchResultModel, OutboundApi, Util } from '../types.d'

interface OutboundSearchState {
  matches: Array<SearchResultModel>
}

export const searchOutboundByKeywords = createAsyncThunk(
  'outbound/searchByKeywords',
  async () => {
    const u = Util.getInstance()
    return await OutboundApi.getInstance(u).search()
  }
)

const initialState: OutboundSearchState = {
  matches: []
}

export const outboundSearchSlice = createSlice({
  name: 'outboundSearch',
  initialState,
  reducers: {
  },
  extraReducers: (builder) => {
    builder.addCase(searchOutboundByKeywords.fulfilled, (state, action: PayloadAction<Array<SearchResultModel>>) => {
      if (action) {
         state.matches = action.payload
      }
    })
  }
})

export const select = (state: RootState) => state.outboundSearch

export default outboundSearchSlice.reducer
