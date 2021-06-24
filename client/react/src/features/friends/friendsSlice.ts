import { createAsyncThunk, createSlice, PayloadAction } from '@reduxjs/toolkit'
import { RootState } from '../../app/store'
import { ParticipantModel, FriendsApi, Util } from '../types.d'

interface FriendsState {
  feed: Array<ParticipantModel>
}

export const fetchFriendsByFrom = createAsyncThunk(
  'friends/fetchByFrom',
  async () => {
    const u = Util.getInstance()
    return await FriendsApi.getInstance(u).get()
  }
)

const addFriends = createAsyncThunk (
  'friends/add',
  async (inb: ParticipantModel, thunkAPI) => {
    const u = Util.getInstance()
    await FriendsApi.getInstance(u).add(inb)
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
    builder.addCase(fetchFriendsByFrom.fulfilled, (state, action: PayloadAction<Array<ParticipantModel>>) => {
      state.feed = action.payload
    })
    builder.addCase(addFriends.fulfilled, (state, action: PayloadAction<ParticipantModel>) => {
      state.feed.push(action.payload)
    })
  }
})

export const select = (state: RootState) => state.friends

export default friendsSlice.reducer
