import { Module, Global } from '@nestjs/common';

import { ElasticsearchModule } from '@nestjs/elasticsearch';
import { SearchService } from './search.service';

@Global()
@Module({
  imports: [
    ElasticsearchModule.registerAsync({
      useFactory: () => ({
        node: `http://${process.env['SEARCH_HOST'] ?? 'localhost'}:9200`,
        maxRetries: 10,
        requestTimeout: 60000,
        pingTimeout: 60000,
        sniffOnStart: false,
	sniffInterval: false,
      }),
    }),
  ],
  providers: [SearchService],
  exports: [SearchService],
})
export class SearchModule {}
