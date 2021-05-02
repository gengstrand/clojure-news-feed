import axios from 'axios'

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
export class FriendModel {
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
    return new Promise((resolve, reject) => resolve([ new OutboundModel(new ParticipantModel(id, ''), new Date(), '', '') ]))
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
      resolve(axios.get<InboundModel[]>(`http://127.0.0.1:8080/participant/${id}/inbound`).then(resp => {
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
  get(id: number): Promise<Array<FriendModel>> {
    return new Promise((resolve, reject) => resolve([ new FriendModel(0, new ParticipantModel(id, ''), new ParticipantModel(0, '')) ]))
  }
  add(fb: FriendModel): void {
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
    return new Promise((resolve, reject) => resolve(new ParticipantModel(id, '')))
  }
  add(inb: ParticipantModel): void {
  }
}
