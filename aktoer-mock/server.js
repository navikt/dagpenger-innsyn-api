const express = require('express');

const app = express();
const port = 3050;

// eslint-disable-next-line max-len
app.get('/aktoerregister/api/v1/identer', (req, res) => res.send({"SIMPLE_OIDC_USER_HARLEY":{"identer":[{"ident":"1000100625562","identgruppe":"AktoerId","gjeldende":false},{"ident":"1000100625575","identgruppe":"AktoerId","gjeldende":false},{"ident":"18128126178","identgruppe":"NorskIdent","gjeldende":true},{"ident":"62268114198","identgruppe":"NorskIdent","gjeldende":false},{"ident":"1000101917358","identgruppe":"AktoerId","gjeldende":true}],"feilmelding":null},"1000009872899":{"identer":null,"feilmelding":"Fant splitting ved traversering av ident: 1000009872899. Identer funnet: [1000101917358, 1000101917936]"},"234234":{"identer":null,"feilmelding":"Den angitte personidenten finnes ikke"}}));


// eslint-disable-next-line max-len
app.get('/br/[0-9]+', (req, res) => res.send('{"organisasjonsnummer":"981566378","navn":"BEKK CONSULTING AS","organisasjonsform":{"kode":"AS","beskrivelse":"Aksjeselskap","_links":{"self":{"href":"https://data.brreg.no/enhetsregisteret/api/organisasjonsformer/AS"}}},"hjemmeside":"www.bekk.no","postadresse":{"land":"Norge","landkode":"NO","postnummer":"0102","poststed":"OSLO","adresse":["Postboks 134  Sentrum"],"kommune":"OSLO","kommunenummer":"0301"},"registreringsdatoEnhetsregisteret":"2000-02-16","registrertIMvaregisteret":true,"naeringskode1":{"beskrivelse":"Konsulentvirksomhet tilknyttet informasjonsteknologi","kode":"62.020"},"antallAnsatte":499,"forretningsadresse":{"land":"Norge","landkode":"NO","postnummer":"0150","poststed":"OSLO","adresse":["Skur 39","Akershusstranda 21"],"kommune":"OSLO","kommunenummer":"0301"},"stiftelsesdato":"2000-01-19","institusjonellSektorkode":{"kode":"2100","beskrivelse":"Private aksjeselskaper mv."},"registrertIForetaksregisteret":true,"registrertIStiftelsesregisteret":false,"registrertIFrivillighetsregisteret":false,"sisteInnsendteAarsregnskap":"2018","konkurs":false,"underAvvikling":false,"underTvangsavviklingEllerTvangsopplosning":false,"maalform":"Bokmål","_links":{"self":{"href":"https://data.brreg.no/enhetsregisteret/api/enheter/981566378"}}}'));
app.get('/', function (req, res) {
  res.status(200);
  res.send("OK");
});


app.listen(port, () => console.log(`Example app listening on port ${port}!`));
