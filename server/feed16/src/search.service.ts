import { Injectable } from '@nestjs/common';
import { ElasticsearchService } from '@nestjs/elasticsearch';

export class SearchDocument {
  constructor (
    public readonly id: string,
    public readonly sender: string,
    public readonly story: string,
  ) {}
}

@Injectable()
export class SearchService {
  constructor(private readonly esService: ElasticsearchService) {}

  async index(doc: SearchDocument) {
    const entropy = Math.random() * 1000000
    const key = Date.now().toString().concat('-').concat(entropy.toString());
    await this.esService.index({
      index: 'feed',
      id: key,
      body: new SearchDocument(key, doc.sender, doc.story),
    });
  }

  async search(q: string): Promise<(SearchDocument | null)[]> {
    const result = await this.esService.search<SearchDocument>({
      index: 'feed',
      body: {
        query: {
          match: {
            story: q,
          },
        },
      },
    });
    return result.hits.hits.map((hit) => {
      if (hit._id !== undefined && hit._source !== undefined) { 
        return new SearchDocument(hit._id, hit._source?.sender, hit._source?.story);
      } else {
        return null
      }
    });
  }
}