const express = require('express');

const app = express();
const port = 3050;

// eslint-disable-next-line max-len
app.get('/aktoerregister/api/v1/identer', (req, res) => res.send({"SIMPLE_OIDC_USER_HARLEY":{"identer":[{"ident":"1000100625562","identgruppe":"AktoerId","gjeldende":false},{"ident":"1000100625575","identgruppe":"AktoerId","gjeldende":false},{"ident":"18128126178","identgruppe":"NorskIdent","gjeldende":true},{"ident":"62268114198","identgruppe":"NorskIdent","gjeldende":false},{"ident":"1000101917358","identgruppe":"AktoerId","gjeldende":true}],"feilmelding":null},"1000009872899":{"identer":null,"feilmelding":"Fant splitting ved traversering av ident: 1000009872899. Identer funnet: [1000101917358, 1000101917936]"},"234234":{"identer":null,"feilmelding":"Den angitte personidenten finnes ikke"}}));

app.listen(port, () => console.log(`Example app listening on port ${port}!`));
