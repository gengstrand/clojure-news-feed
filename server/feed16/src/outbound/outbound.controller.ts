import { Controller, Get, Query } from '@nestjs/common';
import { ApiTags, ApiOperation, ApiResponse } from '@nestjs/swagger';
import { OutboundService } from './outbound.service';

@ApiTags('outbound')
@Controller('outbound')
export class OutboundApiController {
  constructor(private readonly outboundService: OutboundService) {}

  @Get()
  @ApiOperation({
    summary:
      'Retrieve participants who posted stories containing these keywords',
  })
  @ApiResponse({
    status: 200,
    description: 'Successful operation',
    type: [String],
  })
  public async searchOutbound(
    @Query('keywords') keywords: string,
  ): Promise<string[]> {
    return await this.outboundService.searchOutbound(keywords);
  }
}
