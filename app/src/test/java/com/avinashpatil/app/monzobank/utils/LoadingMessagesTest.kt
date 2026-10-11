package com.avinashpatil.app.monzobank.utils

import org.junit.Assert.*
import org.junit.Test

/**
 * Local unit tests for the loading-state copy shown while the app talks to the API:
 * [LoadingMessages] and the [LoadingState] model.
 *
 * The composables themselves ([LoadingOverlay], [SkeletonListItem], …) are UI and belong
 * in an instrumented / screenshot test.
 *
 * Run: `./gradlew :app:testDebugUnitTest`
 */
class LoadingMessagesTest {

    @Test
    fun `every loading type has human copy`() {
        assertEquals("Signing you in...", LoadingMessages.getMessage(LoadingType.AUTHENTICATION))
        assertEquals("Creating your account...", LoadingMessages.getMessage(LoadingType.ACCOUNT_CREATION))
        assertEquals("Processing transaction...", LoadingMessages.getMessage(LoadingType.TRANSACTION_PROCESSING))
        assertEquals("Syncing your data...", LoadingMessages.getMessage(LoadingType.DATA_SYNC))
        assertEquals("Updating card settings...", LoadingMessages.getMessage(LoadingType.CARD_OPERATIONS))
        assertEquals("Processing payment...", LoadingMessages.getMessage(LoadingType.PAYMENT_PROCESSING))
        assertEquals("Performing security checks...", LoadingMessages.getMessage(LoadingType.SECURITY_CHECK))
        assertEquals("Loading...", LoadingMessages.getMessage(LoadingType.GENERAL))
    }

    @Test
    fun `every loading type resolves an icon`() {
        LoadingType.values().forEach { type ->
            assertNotNull("no icon for $type", LoadingMessages.getIcon(type))
        }
    }

    @Test
    fun `LoadingType covers every operation the app can show`() {
        assertEquals(8, LoadingType.values().size)
    }

    @Test
    fun `LoadingState defaults to an idle screen`() {
        val state = LoadingState()
        assertFalse(state.isLoading)
        assertEquals("Loading...", state.message)
        assertNull(state.progress)
        assertFalse(state.canCancel)
    }

    @Test
    fun `LoadingState carries progress and cancel affordance`() {
        val state = LoadingState(
            isLoading = true,
            message = "Uploading statement",
            progress = 0.5f,
            canCancel = true
        )
        assertTrue(state.isLoading)
        assertEquals("Uploading statement", state.message)
        assertEquals(0.5f, state.progress!!, 0.0f)
        assertTrue(state.canCancel)
    }
}
