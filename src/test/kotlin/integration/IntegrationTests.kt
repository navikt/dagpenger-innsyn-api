package integration

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.github.tomakehurst.wiremock.matching.EqualToPattern
import org.junit.Rule
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import parsing.getJSONParsed
import processing.convertInntektDataIntoProcessedRequest

class IntegrationTests {

    @Rule
    @JvmField
    var wireMockRule = WireMockRule(WireMockConfiguration.wireMockConfig().dynamicPort())

    @Test
    fun `Successful fetch of details`() {
        WireMock.stubFor(
                WireMock.get(WireMock.urlEqualTo("/inntekt"))
                        .withHeader("cookies", EqualToPattern("nav-esso=2416281490ghj; beregningsdato=2019-06-01"))
                        .willReturn(WireMock.aResponse().withBody(validJson))
        )
    }

    // TODO: Add tests here that actually check that things work
    @Test
    fun canConvertInntektDataIntoProcessedRequest() {
        println(convertInntektDataIntoProcessedRequest(getJSONParsed("Gabriel")))
    }

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