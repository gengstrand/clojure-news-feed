import { createAsyncThunk, createSlice, PayloadAction } from '@reduxjs/toolkit'
import { RootState } from '../../app/store'
import { FriendsModel, FriendsApi } from '../types.d'

interface FriendsState {
  feed: Array<FriendsModel>
}

export const fetchFriendsByFrom = createAsyncThunk(
  'friends/fetchByFrom',
  async (id: number) => {
    return await FriendsApi.getInstance().get(id)
  }
)

const addFriends = createAsyncThunk (
  'friends/add',
  async (inb: FriendsModel, thunkAPI) => {
    await FriendsApi.getInstance().add(inb)
    return inb
  }
)

const initialState: FriendsState = {
  feed: []
}

export const friendsSlice = createSlice({
  name: 'friends',
  initialState,
  reducers: {
  },
  extraReducers: (builder) => {
    builder.addCase(fetchFriendsByFrom.fulfilled, (state, action: PayloadAction<Array<FriendsModel>>) => {
      state.feed = action.payload
    })
    builder.addCase(addFriends.fulfilled, (state, action: PayloadAction<FriendsModel>) => {
      state.feed.push(action.payload)
    })
  }
})

export const select = (state: RootState) => state.friends

export default friendsSlice.reducer
