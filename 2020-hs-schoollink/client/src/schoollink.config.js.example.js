module.exports = {
    auth0: {
        domain: 'schoollink-nz1.au.auth0.com',
        clientID: 'OA2h6VqqUODiAAvvpjzw2Jd5xORdRMOw',
        redirectUri: 'http://localhost:3000/startsession', //https://schoollink.co.nz/startsession
        audience: 'https://scotscollege.schoollink.co.nz/api/v1',
        responseType: 'token id_token',
        scope: 'openid'
    }
}