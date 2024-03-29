import axios from 'axios'

const HOST = 'http://127.0.0.1:3000'

export class Util {
  private static instance: Util
  private re: RegExp = new RegExp('/participant/([0-9]+)')
  private token: string = ''
  private id: number = 0
  private request_options: object
  private keywords: string = ''
  private constructor() {}
  public link(pid: number): string {
    return `/participant/$pid`
  }
  public extract(id: string): number {
    const m = this.re.exec(id)
    if (m) {
      return parseInt(m[1])
    }
    return parseInt(id)
  }
  public getId(): number {
    if (this.id === 0) {
       this.getOptions()
    }
    return this.id
  }
  public getToken(): string {
    if (this.token === '') {
       if (window.location.hash) {
          const h = window.location.hash.split('&')
          for (var i in h) {
              const kv = h[i].split('=')
              if (kv[0] === '#access_token') {
                 this.token = kv[1]
              }
          }
       }
    }
    return this.token
  }
  public getOptions(): object {
    if (this.request_options === undefined) {
       this.request_options = {
         'headers': {
            'Authorization': 'Bearer ' + this.getToken()
         },
         'withCredentials': true
       }
       axios.get(HOST + '/test', this.request_options).then(resp => {
         if (resp.status === 200) {
            this.id = parseInt(resp.data.user_id)
         }
       })
    }
    return this.request_options
  }
  public getKeywords(): string {
    return this.keywords
  }
  public setKeywords(value: string): void {
    this.keywords = value
  }
  public static getInstance(): Util {
    if (!Util.instance) {
      Util.instance = new Util()
    }
    return Util.instance
  }
}
export class OutboundModel {
   readonly occurred: string
   readonly subject: string
   readonly story: string
   constructor(occurred: string, subject: string, story: string) {
      this.occurred = occurred
      this.subject = subject
      this.story = story
   }
}
class OutboundData {
   readonly outbound: OutboundModel[]
   constructor(outbound: OutboundModel[]) {
      this.outbound = outbound
   }
}
class OutboundEnvelope {
   readonly data: OutboundData
   constructor(data: OutboundData) {
      this.data = data
   }
}
export class InboundModel extends OutboundModel {
   readonly from: ParticipantModel
   constructor(from: ParticipantModel, occurred: string, subject: string, story: string) {
      super(occurred, subject, story)
      this.from = from
   }
}
class InboundData {
   readonly inbound: InboundModel[]
   constructor(inbound: InboundModel[]) {
      this.inbound = inbound
   }
}
class InboundEnvelope {
   readonly data: InboundData
   constructor(data: InboundData) {
      this.data = data
   }
}
class ParticipantData {
   readonly me: ParticipantModel
   constructor(me: ParticipantModel) {
      this.me = me
   }
}
class ParticipantEnvelope {
   readonly data: ParticipantData
   constructor(data: ParticipantData) {
      this.data = data
   }
}
export class ParticipantModel {
   readonly id: number
   readonly name: string
   readonly link: string
   constructor(id: number, name: string, link: string) {
      this.id = id
      this.name = name
      this.link = link
   }
}
class FriendsData {
   readonly friends: ParticipantModel[]
   constructor(friends: ParticipantModel[]) {
      this.friends = friends
   }
}
class FriendsEnvelope {
   readonly data: FriendsData
   constructor(data: FriendsData) {
      this.data = data
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
class SearchResultData {
   readonly search: SearchResultModel[]
   constructor(search: SearchResultModel[]) {
      this.search = search
   }
}
class SearchResultEnvelope {
   readonly data: SearchResultData
   constructor(data: SearchResultData) {
      this.data = data
   }
}
export class SearchResultModel {
   readonly participant: ParticipantModel
   readonly outbound: OutboundModel
   constructor(participant: ParticipantModel, outbound: OutboundModel) {
      this.participant = participant
      this.outbound = outbound
   }
}
export class OutboundApi {
  private static instance: OutboundApi
  private util: Util
  private constructor(util: Util) {
    this.util = util
  }
  public static getInstance(util: Util): OutboundApi {
    if (!OutboundApi.instance) {
      OutboundApi.instance = new OutboundApi(util)
    }
    return OutboundApi.instance
  }
  get(): Promise<OutboundModel[]> {
    return new Promise((resolve, reject) => {
      resolve(axios.get<OutboundEnvelope>(HOST + '/graphql?query={outbound(id:"0"){occurred,subject,story}}', this.util.getOptions()).then(resp => {
        if (resp.status === 200) {
          return resp.data.data.outbound
        } else {
          console.log(JSON.stringify(resp))
          return []
        }
      }))})
  }
  add(ob: OutboundModel): void {
    axios.post(HOST + '/participant/outbound', ob, this.util.getOptions())
  }
  search(): Promise<SearchResultModel[]> {
    return new Promise((resolve, reject) => {
      resolve(axios.get<SearchResultEnvelope>(HOST + '/graphql?query={search(id:"0",keywords:"' + this.util.getKeywords() + '"){participant{id,name,link},outbound{occurred,subject,story}}}', this.util.getOptions()).then(resp => {
        if (resp.status === 200) {
          return resp.data.data.search
        } else {
          console.log(JSON.stringify(resp))
          return []
        }
      }))})
  }
}
export class InboundApi {
  private static instance: InboundApi
  private util: Util
  private constructor(util: Util) {
    this.util = util
  }
  public static getInstance(util: Util): InboundApi {
    if (!InboundApi.instance) {
      InboundApi.instance = new InboundApi(util)
    }
    return InboundApi.instance
  }
  public get(): Promise<InboundModel[]> {
    return new Promise((resolve, reject) => {
      resolve(axios.get<InboundEnvelope[]>(HOST + '/graphql?query={inbound(id:"0"){from{name},occurred,subject,story}}', this.util.getOptions()).then(resp => {
        if (resp.status === 200) {
          return resp.data.data.inbound
        } else {
          console.log(JSON.stringify(resp))
          return []
        }
      }))})
  }
}
export class FriendsApi {
  private static instance: FriendsApi
  private util: Util
  private constructor(util: Util) {
    this.util = util
  }
  public static getInstance(util: Util): FriendsApi {
    if (!FriendsApi.instance) {
      FriendsApi.instance = new FriendsApi(util)
    }
    return FriendsApi.instance
  }
  get(): Promise<ParticipantModel[]> {
    return new Promise((resolve, reject) => {
      resolve(axios.get<FriendsEnvelope[]>(HOST + '/graphql?query={friends(id:"0"){name}}', this.util.getOptions()).then(resp => {
        if (resp.status === 200) {
          return resp.data.data.friends
        } else {
          console.log(JSON.stringify(resp))
          return []
        }
      }))})
  }
  add(pb: ParticipantModel): void {
    const fb: FriendsModel = new FriendsModel(0, this.util.link(this.util.getId()), pb.link)
    axios.post(HOST + '/participant/friends', fb, this.util.getOptions())
  }
}
export class ParticipantApi {
  private static instance: ParticipantApi
  private util: Util
  private constructor(util: Util) {
    this.util = util
  }
  public static getInstance(util: Util): ParticipantApi {
    if (!ParticipantApi.instance) {
      ParticipantApi.instance = new ParticipantApi(util)
    }
    return ParticipantApi.instance
  }
  get(): Promise<ParticipantModel> {
    return new Promise((resolve, reject) => {
      resolve(axios.get<ParticipantEnvelope>(HOST + '/graphql?query={me(id:"0"){name}}', this.util.getOptions()).then(resp => {
        if (resp.status === 200) {
          return resp.data.data.me
        } else {
          console.log(JSON.stringify(resp))
          return new ParticipantModel(0, 'error', 'error')
        }
      }))})
  }
}
