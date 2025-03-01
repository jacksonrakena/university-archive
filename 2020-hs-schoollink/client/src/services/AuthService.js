import auth0 from 'auth0-js';
import { isNullOrUndefined } from 'util';
import config from '../schoollink.config';

export default class AuthService {
    static auth0 = new auth0.WebAuth(config.auth0);

    static login() {
        this.auth0.authorize();
    }

    static getAccessToken() {
        const accessToken = localStorage.getItem('access_token');
        if (!accessToken) {
            throw new Error('No access token found');
        }
        return accessToken;
    }

    static logout(returnAddress) {
        localStorage.clear();
        this.auth0.logout({returnTo:returnAddress, clientID: 'OA2h6VqqUODiAAvvpjzw2Jd5xORdRMOw'});
      }

    static handleAuthentication(history, loc) {
      console.log('handling auth');
        this.auth0.parseHash((err, authResult)=> {
          if (authResult && authResult.accessToken && authResult.idToken) {
            this.setSession(authResult);
            history.push("/");
            loc.reload();
          } else if (err) {
              switch (err.errorDescription) {
                  case "NO_EMAIL":
                      history.push("/error/no_email");
                      break;
                  case "NO_EMAIL_VERIFY":
                      history.push("/error/no_email_verify");
                      break;
                  case "INVALID_EMAIL":
                      history.push("/error/invalid_email");
                      break;
                  default:
                      console.log(err);
                      break;
              }
          }
        });
      }
    
      static setSession(authResult) {
        if (isNullOrUndefined(authResult)
         || isNullOrUndefined(authResult.expiresIn)
         || isNullOrUndefined(authResult.accessToken)
         || isNullOrUndefined(authResult.idToken)) return;

        let expiresAt = JSON.stringify((authResult.expiresIn * 1000) + new Date().getTime());
        localStorage.setItem('access_token', authResult.accessToken);
        localStorage.setItem('id_token', authResult.idToken);
        localStorage.setItem('expires_at', expiresAt);
      }
    
      static isAuthenticated() {
        let raw = localStorage.getItem('expires_at');
        if (!raw) return false;
        let expiresAt = JSON.parse(raw);
        return new Date().getTime() < expiresAt;
      }
}