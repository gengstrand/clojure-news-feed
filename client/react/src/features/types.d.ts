import axios from 'axios'

const HOST = 'http://127.0.0.1:8080'

export class OutboundModel {
   readonly from: p.ParticipantModel
   readonly occurred: Date
   readonly subject: string
   readonly story: string
   constructor(from: p.ParticipantModel, occurred: Date, subject: string, story: string) {
      this.from = from
      this.occurred = occurred
      this.subject = subject
      this.story = story
   }
}
export class InboundModel extends OutboundModel {
   readonly to: ParticipantModel
   constructor(from: ParticipantModel, to: ParticipantModel, occurred: Date, subject: string, story: string) {
      super(from, occurred, subject, story)
      this.to = to
   }
}
export class ParticipantModel {
   readonly id: number
   readonly name: string
   constructor(id: number, moniker: string) {
      this.id = id
      this.name = moniker
   }
}
export class FriendsModel {
   readonly id: number
   readonly from: ParticipantModel
   readonly to: ParticipantModel
   constructor(id: number, from: ParticipantModel, to: ParticipantModel) {
      this.id = id
      this.from = from
      this.to = to
   }
}
export class OutboundApi {
  private static instance: OutboundApi
  private constructor() {}
  public static getInstance(): OutboundApi {
    if (!OutboundApi.instance) {
      OutboundApi.instance = new OutboundApi()
    }
    return OutboundApi.instance
  }
  get(id: number): Promise<Array<OutboundModel>> {
    return new Promise((resolve, reject) => {
      resolve(axios.get<OutboundModel[]>(HOST + `/participant/${id}/outbound`).then(resp => {
        if (resp.status === 200) {
          return resp.data
        } else {
          console.log(JSON.stringify(resp))
          return []
        }
      }))})
  }
  add(ob: OutboundModel): void {
  }
}
export class InboundApi {
  private static instance: InboundApi
  private constructor() {}
  public static getInstance(): InboundApi {
    if (!InboundApi.instance) {
      InboundApi.instance = new InboundApi()
    }
    return InboundApi.instance
  }
  public get(id: number): Promise<Array<InboundModel>> {
    return new Promise((resolve, reject) => {
      resolve(axios.get<InboundModel[]>(HOST + `/participant/${id}/inbound`).then(resp => {
        if (resp.status === 200) {
          return resp.data
        } else {
          console.log(JSON.stringify(resp))
          return []
        }
      }))})
  }
  public add(inb: InboundModel): void {
  }
}
export class FriendsApi {
  private static instance: FriendsApi
  private constructor() {}
  public static getInstance(): FriendsApi {
    if (!FriendsApi.instance) {
      FriendsApi.instance = new FriendsApi()
    }
    return FriendsApi.instance
  }
  get(id: number): Promise<Array<FriendsModel>> {
    return new Promise((resolve, reject) => {
      resolve(axios.get<FriendsModel[]>(HOST + `/participant/${id}/friends`).then(resp => {
        if (resp.status === 200) {
          return resp.data
        } else {
          console.log(JSON.stringify(resp))
          return []
        }
      }))})
  }
  add(fb: FriendsModel): void {
  }
}
export class PartcipantApi {
  private static instance: ParticipantApi
  private constructor() {}
  public static getInstance(): ParticipantApi {
    if (!ParticipantApi.instance) {
      ParticipantApi.instance = new ParticipantApi()
    }
    return ParticipantApi.instance
  }
  get(id: number): Promise<ParticipantModel> {
    return new Promise((resolve, reject) => {
      resolve(axios.get<ParticipantModel[]>(HOST + `/participant/${id}`).then(resp => {
        if (resp.status === 200) {
          return resp.data
        } else {
          console.log(JSON.stringify(resp))
          return new ParticipantModel(0, 'error')
        }
      }))})
  }
  add(inb: ParticipantModel): void {
  }
}
