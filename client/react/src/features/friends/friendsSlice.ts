import { createAsyncThunk, createSlice, PayloadAction } from '@reduxjs/toolkit'
import { RootState } from '../../app/store'
import { FriendsModel, FriendsApi, ParticipantApi, Util } from '../types.d'

interface FriendsState {
  feed: Array<FriendsModel>
}

export const fetchFriendsByFrom = createAsyncThunk(
  'friends/fetchByFrom',
  async (id: number) => {
    const u = Util.getInstance()
    const p = ParticipantApi.getInstance()
    const fa = await FriendsApi.getInstance().get(id)
    const rv = []
    for (var f of fa) {
      var fid = u.extract(f.from)
      if (fid === id) {
        fid = u.extract(f.to)
      }
      const fp = await p.get(fid)
      rv.push(new FriendsModel(f.id, fp.name, ''))
    }
    return rv
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
