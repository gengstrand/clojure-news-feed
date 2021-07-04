import React from 'react';
import { render } from '@testing-library/react';
import { Provider } from 'react-redux';
import { store } from '../../app/store';
import OutboundSearch from './OutboundSearch';

test('renders outbound search component', () => {
  const { getByText } = render(
    <Provider store={store}>
      <OutboundSearch />
    </Provider>
  );

  expect(getByText(/subject or story keywords/i)).toBeInTheDocument();
});
