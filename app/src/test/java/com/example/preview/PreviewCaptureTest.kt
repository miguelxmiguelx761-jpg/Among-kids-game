package com.example.preview

import android.os.Looper
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.example.components.CafeteriaComputerDialog
import com.example.model.Role
import com.example.ui.AmongKidsRootApp
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PlayerCyan
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File
import java.time.Duration

/**
 * Renders the real Compose UI of the app to PNGs, which the Base44 dev environment
 * serves as the browsable preview gallery (see .base44/preview/run.sh).
 *
 * Every screen here is produced by composing [AmongKidsRootApp] and tapping the same
 * controls a player taps. The file names are the ids the gallery expects; see
 * .base44/preview/index.html before renaming anything.
 *
 * Run with: gradle :app:recordRoborazziDebug --tests com.example.preview.PreviewCaptureTest
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class PreviewCaptureTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val outDir: File = File(System.getenv("PREVIEW_OUT_DIR") ?: "build/preview-out")

    private fun launchApp() {
        composeTestRule.setContent { MyApplicationTheme { AmongKidsRootApp() } }
        composeTestRule.waitForIdle()
    }

    private fun capture(name: String) {
        outDir.mkdirs()
        composeTestRule.onRoot().captureRoboImage(filePath = File(outDir, "$name.png").absolutePath)
    }

    private fun tap(tag: String, scrollTo: Boolean = false) {
        val node = composeTestRule.onNodeWithTag(tag)
        if (scrollTo) runCatching { node.performScrollTo() }
        node.performClick()
        composeTestRule.waitForIdle()
    }

    /** Taps the first node showing [label] — used for tabs, which carry no test tag. */
    private fun tapLabel(label: String) {
        composeTestRule.onAllNodesWithText(label, substring = true)[0].performClick()
        composeTestRule.waitForIdle()
    }

    /**
     * Lets the meeting's own timers run so the capture shows the chat and the votes the
     * bots produce while a meeting is open, instead of a just-opened, empty screen.
     */
    private fun letBotsChat(seconds: Long) {
        shadowOf(Looper.getMainLooper()).idleFor(Duration.ofSeconds(seconds))
        composeTestRule.waitForIdle()
    }

    @Test
    fun menuPrincipal() {
        launchApp()
        capture("01-menu-principal")
    }

    @Test
    fun personalizacao() {
        launchApp()
        tap("customize_visual_button", scrollTo = true)
        capture("02-personalizacao")
    }

    @Test
    fun chancesDosPapeis() {
        launchApp()
        tap("btn_configure_chances", scrollTo = true)
        capture("03-chances-papeis")
    }

    @Test
    fun mapaDaNave() {
        launchApp()
        tap("btn_play_practice", scrollTo = true)
        capture("04-mapa-da-nave")
    }

    @Test
    fun papelSorteado() {
        launchApp()
        tap("btn_play_real_challenge", scrollTo = true)
        capture("05-papel-sorteado")
    }

    @Test
    fun computadorDaCafeteria() {
        // In the game this panel opens from the laptop in the Cafeteria; render the
        // dialog itself, the same composable GamePlayScreen shows.
        composeTestRule.setContent {
            MyApplicationTheme {
                CafeteriaComputerDialog(
                    currentRole = Role.INOCENTE,
                    isPracticeMode = true,
                    playerColor = PlayerCyan,
                    playerHat = "🧢",
                    onSelectRole = {},
                    onTogglePracticeMode = {},
                    onChangeColor = {},
                    onChangeHat = {},
                    onRollRole = {},
                    onResetPracticeDummies = {},
                    onDismiss = {}
                )
            }
        }
        composeTestRule.waitForIdle()
        capture("06-computador-papeis")
    }

    @Test
    fun reuniaoDeEmergencia() {
        launchApp()
        tap("btn_play_practice", scrollTo = true)
        tap("meeting_quick_button")
        letBotsChat(seconds = 14)
        capture("07-reuniao-emergencia")
    }

    @Test
    fun votacao() {
        launchApp()
        tap("btn_play_practice", scrollTo = true)
        tap("meeting_quick_button")
        letBotsChat(seconds = 20)
        tapLabel("Votos")
        capture("08-votacao")
    }

    @Test
    fun chatRapido() {
        launchApp()
        tap("btn_play_practice", scrollTo = true)
        tap("meeting_quick_button")
        letBotsChat(seconds = 14)
        tap("open_quick_chat_button")
        capture("09-chat-rapido")
    }
}
