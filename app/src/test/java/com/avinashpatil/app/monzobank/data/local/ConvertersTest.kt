package com.avinashpatil.app.monzobank.data.local

import com.avinashpatil.app.monzobank.data.local.entity.AccountType
import com.avinashpatil.app.monzobank.data.local.entity.CardType
import com.avinashpatil.app.monzobank.data.local.entity.DeliveryStatus
import com.avinashpatil.app.monzobank.data.local.entity.TransactionStatus
import com.avinashpatil.app.monzobank.data.local.entity.TransactionType
import org.junit.Assert.*
import org.junit.Test
import java.math.BigDecimal
import java.util.Date

/**
 * Local unit tests for [Converters] — the Room type converters behind the offline cache.
 *
 * The converters are plain JVM code (Gson + `java.util`), so they can be exercised without
 * an emulator. These tests pin the on-disk representation, which matters because a wrong
 * conversion here silently corrupts the cached transactions and cards.
 *
 * Run: `./gradlew :app:testDebugUnitTest`
 */
class ConvertersTest {

    private val converters = Converters()

    @Test
    fun `timestamps round trip through Date`() {
        val now = 1_700_000_000_000L
        assertEquals(Date(now), converters.fromTimestamp(now))
        assertEquals(now, converters.dateToTimestamp(Date(now)))

        assertNull(converters.fromTimestamp(null))
        assertNull(converters.dateToTimestamp(null))
    }

    @Test
    fun `BigDecimal keeps its scale as text`() {
        assertEquals("12.50", converters.fromBigDecimal(BigDecimal("12.50")))
        assertEquals(BigDecimal("12.50"), converters.toBigDecimal("12.50"))
        assertEquals("0", converters.fromBigDecimal(BigDecimal.ZERO))

        assertNull(converters.fromBigDecimal(null))
        assertNull(converters.toBigDecimal(null))
    }

    @Test
    fun `account types round trip`() {
        assertEquals("SAVINGS", converters.fromAccountType(AccountType.SAVINGS))
        assertEquals(AccountType.SAVINGS, converters.toAccountType("SAVINGS"))
        assertEquals(AccountType.CURRENT, converters.toAccountType("CURRENT"))

        assertNull(converters.fromAccountType(null))
        assertNull(converters.toAccountType(null))
    }

    @Test
    fun `transaction status and type round trip`() {
        assertEquals("PENDING", converters.fromTransactionStatus(TransactionStatus.PENDING))
        assertEquals(TransactionStatus.PENDING, converters.toTransactionStatus("PENDING"))
        assertEquals(TransactionStatus.COMPLETED, converters.toTransactionStatus("COMPLETED"))

        assertEquals("TRANSFER_IN", converters.fromTransactionType(TransactionType.TRANSFER_IN))
        assertEquals(TransactionType.TRANSFER_IN, converters.toTransactionType("TRANSFER_IN"))
        assertEquals(TransactionType.CARD_PAYMENT, converters.toTransactionType("CARD_PAYMENT"))

        assertNull(converters.fromTransactionStatus(null))
        assertNull(converters.toTransactionType(null))
    }

    @Test
    fun `card type and delivery status round trip`() {
        assertEquals("VIRTUAL", converters.fromCardType(CardType.VIRTUAL))
        assertEquals(CardType.VIRTUAL, converters.toCardType("VIRTUAL"))
        assertEquals(CardType.DEBIT, converters.toCardType("DEBIT"))

        assertEquals("OUT_FOR_DELIVERY", converters.fromDeliveryStatus(DeliveryStatus.OUT_FOR_DELIVERY))
        assertEquals(DeliveryStatus.OUT_FOR_DELIVERY, converters.toDeliveryStatus("OUT_FOR_DELIVERY"))
        assertEquals(DeliveryStatus.DELIVERED, converters.toDeliveryStatus("DELIVERED"))

        assertNull(converters.fromDeliveryStatus(null))
        assertNull(converters.toCardType(null))
    }

    @Test
    fun `string maps survive the JSON round trip`() {
        val prefs = mapOf("notifications" to "true", "theme" to "dark")

        val json = converters.fromStringMap(prefs)
        assertNotNull(json)
        assertEquals(prefs, converters.toStringMap(json!!))

        assertNull(converters.fromStringMap(null))
        assertNull(converters.toStringMap(null))
    }

    @Test
    fun `string lists survive the JSON round trip`() {
        val accounts = listOf("Current", "Savings", "Joint")

        val json = converters.fromStringList(accounts)
        assertNotNull(json)
        assertEquals(accounts, converters.toStringList(json!!))

        assertNull(converters.fromStringList(null))
        assertNull(converters.toStringList(null))
    }
}
