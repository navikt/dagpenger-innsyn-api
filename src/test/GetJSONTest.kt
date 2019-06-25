import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

@Test
fun GetJSONReturnsCorrectDataForUserPeter() {
    assertEquals(ExpectedJSONResultForUserPeter, getJSONForUser("9999999999"))
}