import { DataSource } from 'typeorm';
import { Participant } from './entity/participant';
import { Friend } from './entity/friend';

export const databaseProviders = [
  {
    provide: 'feedDataSource',
    useFactory: async () => {
      const dataSource = new DataSource({
        type: 'mysql',
        host: process.env['MYSQL_HOST'] ?? 'localhost',
        port: 3306,
        username: 'feed',
        password: 'feed1234',
        database: 'feed',
        entities: [Participant, Friend],
        synchronize: false,
      });

      return dataSource.initialize();
    },
  },
];

export const participantProviders = [
  {
    provide: 'PARTICIPANT_REPOSITORY',
    useFactory: (dataSource: DataSource) => {
      return dataSource.getRepository(Participant);
    },
    inject: ['feedDataSource'],
  },
];

export const friendProviders = [
  {
    provide: 'FRIEND_REPOSITORY',
    useFactory: (dataSource: DataSource) => {
      return dataSource.getRepository(Friend);
    },
    inject: ['feedDataSource'],
  },
];
