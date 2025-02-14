    // redis.module.ts
    import { Module, Global } from '@nestjs/common';
    import { CacheModule } from '@nestjs/cache-manager';
    import { redisStore } from 'cache-manager-redis-yet';  

    @Global()
    @Module({
        imports: [
            CacheModule.registerAsync({
                useFactory: async () => {
                    const store = await redisStore({
                        socket: {  
                            host: process.env["REDIS_HOST"],  
                            port: 6379,  
                          }
                    });
                    return {
                        store,
                    };
                },
                isGlobal: true,
            }),
        ],
        exports: [CacheModule],
    })
    export class RedisModule {}