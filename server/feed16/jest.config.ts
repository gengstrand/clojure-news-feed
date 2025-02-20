import type {Config} from 'jest';

const config: Config = {
  verbose: true,
  testRegex: '.*/.*\\.(test|spec)\\.ts$',
  transform: {
    '^.+\\.(t|j)s$': 'ts-jest',
  },
};

export default config;