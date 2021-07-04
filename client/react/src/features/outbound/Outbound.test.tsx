import React from 'react';
import { render } from '@testing-library/react';
import { Provider } from 'react-redux';
import { store } from '../../app/store';
import Outbound from './Outbound';

test('renders outbound component', () => {
  const { getByText } = render(
    <Provider store={store}>
      <Outbound />
    </Provider>
  );

  expect(getByText(/news feed posts/i)).toBeInTheDocument();
});
