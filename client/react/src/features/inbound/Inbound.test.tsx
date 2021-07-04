import React from 'react';
import { render } from '@testing-library/react';
import { Provider } from 'react-redux';
import { store } from '../../app/store';
import Inbound from './Inbound';

test('renders inbound component', () => {
  const { getByText } = render(
    <Provider store={store}>
      <Inbound />
    </Provider>
  );

  expect(getByText(/news feed posts/i)).toBeInTheDocument();
});
