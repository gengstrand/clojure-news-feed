import { createAsyncThunk, createSlice, PayloadAction } from '@reduxjs/toolkit'
import { RootState } from '../../app/store'
import { InboundModel, InboundApi, ParticipantApi, Util } from '../types.d'

interface InboundState {
  feed: Array<InboundModel>
}

export const fetchInboundByFrom = createAsyncThunk(
  'inbound/fetchByFrom',
  async () => {
    const u = Util.getInstance()
    const p = ParticipantApi.getInstance()
    const ia = await InboundApi.getInstance().get(u.getToken())
    const rv = []
    const am = new Map()
    const ak: string[] = []
    for (var i of ia) {
      if (ak.includes(i.from)) {
        rv.push(new InboundModel(am.get(i.from), i.to, i.occurred, i.subject, i.story))
      } else {
        const fid = u.extract(i.from)
        const fp = await p.get(fid)
        rv.push(new InboundModel(fp.name, i.to, i.occurred, i.subject, i.story))
        am.set(i.from, fp.name)
        ak.push(i.from)
      }
    }
    return rv
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
