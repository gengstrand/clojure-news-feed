import axios from 'axios'

const HOST = 'http://127.0.0.1:8080'

export class Util {
  private static instance: Util
  private re: RegExp = new RegExp('/participant/([0-9]+)')
  private token: string = '2'
  private id: number = 2
  private constructor() {}
  public extract(id: string): number {
    const m = this.re.exec(id)
    if (m) {
      return parseInt(m[1])
    }
    return parseInt(id)
  }
  public getId(): number {
    return this.id
  }
  public getToken(): string {
    return this.token
  }
  public static getInstance(): Util {
    if (!Util.instance) {
      Util.instance = new Util()
    }
    return Util.instance
  }
}
export class OutboundModel {
   readonly from: string
   readonly occurred: Date
   readonly subject: string
   readonly story: string
   constructor(from: string, occurred: Date, subject: string, story: string) {
      this.from = from
      this.occurred = occurred
      this.subject = subject
      this.story = story
   }
}
export class InboundModel extends OutboundModel {
   readonly to: string
   constructor(from: string, to: string, occurred: Date, subject: string, story: string) {
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
   readonly from: string
   readonly to: string
   constructor(id: number, from: string, to: string) {
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
  get(token: string): Promise<Array<OutboundModel>> {
    return new Promise((resolve, reject) => {
      resolve(axios.get<OutboundModel[]>(HOST + `/participant/${token}/outbound`).then(resp => {
        if (resp.status === 200) {
          return resp.data
        } else {
          console.log(JSON.stringify(resp))
          return []
        }
      }))})
  }
  add(token: string, ob: OutboundModel): void {
    axios.post(HOST + `/participant/${token}/outbound`, ob)
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
  public get(token: string): Promise<Array<InboundModel>> {
    return new Promise((resolve, reject) => {
      resolve(axios.get<InboundModel[]>(HOST + `/participant/${token}/inbound`).then(resp => {
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
  get(token: string): Promise<Array<FriendsModel>> {
    return new Promise((resolve, reject) => {
      resolve(axios.get<FriendsModel[]>(HOST + `/participant/${token}/friends`).then(resp => {
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
export class ParticipantApi {
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