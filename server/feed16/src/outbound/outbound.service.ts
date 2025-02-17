import { Injectable, NotFoundException } from '@nestjs/common';
import { SearchService, SearchDocument } from '../search.service';

@Injectable()
export class OutboundService {
  constructor(private readonly searchService: SearchService) {}
  public async searchOutbound(keywords: string): Promise<string[]> {
    const outbound: (SearchDocument | null)[] =
      await this.searchService.search(keywords);
    if (!outbound) {
      throw new NotFoundException(`no match for keywords: ${keywords}`);
    }
    return outbound.filter((doc) => doc !== null).map((doc) => doc.sender);
  }
}
