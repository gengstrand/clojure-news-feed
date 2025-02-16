import { Test, TestingModule } from '@nestjs/testing';
import { SearchService } from '../search.service';
import { OutboundApiController } from './outbound.controller';
import { OutboundService } from './outbound.service';

describe('OutboundApiController', () => {
    let appController: OutboundApiController | null = null;
    
    beforeEach(async () => {
        const app: TestingModule = await Test.createTestingModule({
            imports: [],
            controllers: [OutboundApiController],
            providers: [
                {
                    provide: SearchService,
                    useValue: {
                        search: jest.fn().mockImplementation(() => {
                            return Promise.resolve([{
                                sender: '/participant/1',
                                subject: 'test subject',
                                story: 'test story',
                            }]);
                        }),
                    },
                },
                OutboundService,
            ],
        }).compile();
    
        appController = app.get<OutboundApiController>(OutboundApiController);
    });
    describe('search api', () => {
        it('should return results from keyword search', async () => {
            const result = await appController?.searchOutbound('test');
            expect(result?.length).toBe(1);
            expect(result).toEqual(['/participant/1']);
        });
    });
});