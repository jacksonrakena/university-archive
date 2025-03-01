import React from 'react';
import ReactDOM from 'react-dom';
import AuthService from './services/AuthService';
import UnauthorizedContainer from './pages/unauthorized/UnauthorizedContainer';
import AuthorizedContainer from './pages/authorized/AuthorizedContainer';

ReactDOM.render(AuthService.isAuthenticated() ? <AuthorizedContainer /> : <UnauthorizedContainer />, document.getElementById('root'));
