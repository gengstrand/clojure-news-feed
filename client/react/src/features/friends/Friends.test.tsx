import React from 'react';
import { render } from '@testing-library/react';
import { Provider } from 'react-redux';
import { store } from '../../app/store';
import Friends from './Friends';

test('renders friends grid', () => {
  const { getByText } = render(
    <Provider store={store}>
      <Friends />
    </Provider>
  );

  expect(getByText(/friends/i)).toBeInTheDocument();
});
