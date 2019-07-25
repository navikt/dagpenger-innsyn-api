package integration

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import no.nav.dagpenger.innsyn.lookup.getNameFromID
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class NameFromBRTest {
    companion object {
        val server: WireMockServer = WireMockServer(WireMockConfiguration.options().dynamicPort())

        @BeforeAll
        @JvmStatic
        fun start() {
            server.start()
        }

        @AfterAll
        @JvmStatic
        fun stop() {
            server.stop()
        }
    }

    @BeforeEach
    fun configure() {
        WireMock.configureFor(server.port())
    }

    @Test
    fun `Successful fetch of organization name`() {
        val testOrgId = "974760673"
        val testName = "REGISTERENHETEN I BRØNNØYSUND"

        WireMock.stubFor(
                WireMock.get(WireMock.urlEqualTo("/$testOrgId"))
                        .willReturn(WireMock.aResponse().withBody(validJsonBodyWithNorskOrg))
        )
        val orgName = getNameFromID(testOrgId, server.url(""))
        assertEquals(testName, orgName)
    }

    @Test
    fun `Unsuccessful fetch returns only organization id`(){
        val testOrgId = "974760673"

        WireMock.stubFor(
                WireMock.get(WireMock.urlEqualTo("/$testOrgId"))
                        .willReturn(WireMock.notFound())
        )

        val orgId = getNameFromID(testOrgId, server.url(""))
        assertEquals(testOrgId, orgId)
    }

    val validJsonBodyWithNorskOrg = """
        {"organisasjonsnummer":"974760673",
        "navn":"REGISTERENHETEN I BRØNNØYSUND",
        "organisasjonsform":{"kode":"ORGL",
        "beskrivelse":"Organisasjonsledd",
        "_links":{
            "self":{
                "href":"https://data.brreg.no/enhetsregisteret/api/organisasjonsformer/ORGL"
            }
        }},"hjemmeside":"www.brreg.no",
        "postadresse":{
            "land":"Norge",
            "landkode":"NO",
            "postnummer":"8910",
            "poststed":"BRØNNØYSUND",
            "adresse":["Postboks 900"],
            "kommune":"BRØNNØY",
            "kommunenummer":"1813"
        },"registreringsdatoEnhetsregisteret":"1995-08-09",
          "registrertIMvaregisteret":false,
          "naeringskode1":{
            "beskrivelse":"Generell offentlig administrasjon","kode":"84.110"
            },"antallAnsatte":533,"overordnetEnhet":"912660680",
            "forretningsadresse":{
            "land":"Norge","landkode":"NO","postnummer":"8900","poststed":"BRØNNØYSUND",
            "adresse":["Havnegata 48"],"kommune":"BRØNNØY","kommunenummer":"1813"
            },"institusjonellSektorkode":{"kode":"6100","beskrivelse":"Statsforvaltningen"},
            "registrertIForetaksregisteret":false,"registrertIStiftelsesregisteret":false,
            "registrertIFrivillighetsregisteret":false,"konkurs":false,"underAvvikling":false,
            "underTvangsavviklingEllerTvangsopplosning":false,"maalform":"Bokmål",
            "_links":{"self":{"href":"https://data.brreg.no/enhetsregisteret/api/enheter/974760673"},
            "overordnetEnhet":{"href":"https://data.brreg.no/enhetsregisteret/api/enheter/912660680"}}
            }""".trimIndent()

    val validJson = """
        {
           "employerSummaries":[
              {
                 "employmentPeriodes":[
                    {
                       "endDateYearMonth":"2017-09",
                       "startDateYearMonth":"2017-09"
                    },
                    {
                       "endDateYearMonth":"2017-12",
                       "startDateYearMonth":"2017-12"
                    }
                 ],
                 "income":5099.0,
                 "name":"222222",
                 "orgID":"222222"
              },
              {
                 "employmentPeriodes":[
                    {
                       "endDateYearMonth":"2017-09",
                       "startDateYearMonth":"2017-09"
                    }
                 ],
                 "income":501.0,
                 "name":"2222221",
                 "orgID":"2222221"
              },
              {
                 "employmentPeriodes":[
                    {
                       "endDateYearMonth":"2017-10",
                       "startDateYearMonth":"2017-10"
                    }
                 ],
                 "income":11241.43,
                 "name":"55555",
                 "orgID":"55555"
              },
              {
                 "employmentPeriodes":[
                    {
                       "endDateYearMonth":"2017-10",
                       "startDateYearMonth":"2017-10"
                    }
                 ],
                 "income":11512.43,
                 "name":"666666",
                 "orgID":"666666"
              },
              {
                 "employmentPeriodes":[
                    {
                       "endDateYearMonth":"2017-12",
                       "startDateYearMonth":"2017-11"
                    }
                 ],
                 "income":5120.83,
                 "name":"11111",
                 "orgID":"11111"
              }
           ],
           "monthsIncomeInformation":[
              {
                 "employers":[
                    {
                       "incomes":[
                          {
                             "income":5.83,
                             "verdikode":"Fastlønn"
                          }
                       ],
                       "name":"222222",
                       "orgID":"222222"
                    }
                 ],
                 "month":"2016-04"
              },
              {
                 "employers":[
                    {
                       "incomes":[
                          {
                             "income":5099.0,
                             "verdikode":"Fastlønn"
                          }
                       ],
                       "name":"222222",
                       "orgID":"222222"
                    },
                    {
                       "incomes":[
                          {
                             "income":501.0,
                             "verdikode":"Fastlønn"
                          }
                       ],
                       "name":"2222221",
                       "orgID":"2222221"
                    }
                 ],
                 "month":"2017-09"
              },
              {
                 "employers":[
                    {
                       "incomes":[
                          {
                             "income":11241.43,
                             "verdikode":"Fastlønn"
                          }
                       ],
                       "name":"55555",
                       "orgID":"55555"
                    },
                    {
                       "incomes":[
                          {
                             "income":11512.43,
                             "verdikode":"Fastlønn"
                          }
                       ],
                       "name":"666666",
                       "orgID":"666666"
                    }
                 ],
                 "month":"2017-10"
              },
              {
                 "employers":[
                    {
                       "incomes":[
                          {
                             "income":5120.83,
                             "verdikode":"Fastlønn"
                          }
                       ],
                       "name":"11111",
                       "orgID":"11111"
                    }
                 ],
                 "month":"2017-11"
              },
              {
                 "employers":[
                    {
                       "incomes":[
                          {
                             "income":5099.0,
                             "verdikode":"Fastlønn"
                          }
                       ],
                       "name":"222222",
                       "orgID":"222222"
                    },
                    {
                       "incomes":[
                          {
                             "income":50.83,
                             "verdikode":"Fastlønn"
                          }
                       ],
                       "name":"11111",
                       "orgID":"11111"
                    }
                 ],
                 "month":"2017-12"
              }
           ],
           "personnummer":"01D8G6FS9QGRT3JKBTA5KEE64C",
           "totalIncome":38624.520000000004,
           "totalIncome12":0.0
        }
        """.trimIndent()
}