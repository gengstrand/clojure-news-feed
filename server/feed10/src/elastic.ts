import * as request from 'request-promise-native'

export class SearchService {
   readonly host: string
   readonly path: string
   constructor(host: string, path: string) {
       this.host = host
       this.path = path
   }
   public async index(from: number, story: string) {
     const entropy = Math.random() * 1000000
     const key = Date.now().toString().concat('-').concat(entropy.toString());
     const doc = JSON.stringify({ 'id': key, 'sender': from, 'story': story });
     const options = {
	uri: 'http://' + this.host + ':9200' + this.path.concat('/').concat(from.toString()).concat('-').concat(key), 
	body: doc, 
	headers: {
	    'Content-type': 'application/json',
	    'Content-Length': Buffer.byteLength(doc)
	}
     }
     request.put(options)
   }
   public async search(terms: string): Promise<number[]> {
     const options = {
	uri: 'http://' + this.host + ':9200' + this.path.concat('/_search?q=').concat(terms), 
	headers: {
	    'Content-type': 'application/json',
	    'accept': 'application/json'
	}
     }
     const response = await request.get(options)
     const r = JSON.parse(response);
     if (r['hits']) {
        const outer = r['hits']
        if (outer['hits']) {
           const senders = outer['hits'].map((hit) => {
              if (hit['_source']) {
                 const s = hit['_source']
                 if (s['sender']) {
                    return s['sender']
                 }
              }	      
           })
	   return senders
        }
        return []
     }
   }
}