package live

import dev.kord.common.annotation.KordPreview
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.createTextChannel
import dev.kord.core.behavior.createCategory
import dev.kord.core.entity.Guild
import dev.kord.core.entity.channel.Category
import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.live.AbstractLiveKordEntity
import dev.kord.rest.builder.channel.CategoryCreateBuilder
import dev.kord.rest.builder.channel.TextChannelCreateBuilder
import dev.kord.rest.builder.guild.GuildCreateBuilder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.AfterTest
import kotlin.test.assertEquals

@OptIn(KordPreview::class)
abstract class AbstractLiveEntityTest<LIVE : AbstractLiveKordEntity> {

    private var token: String = System.getenv("KORD_TEST_TOKEN")

    protected lateinit var kord: Kord

    protected lateinit var live: LIVE

    protected var guild: Guild? = null

    @BeforeAll
    open fun onBeforeAll() = runBlocking {
        kord = createKord()
        kordLoginAsync()
    }

    @AfterAll
    open fun onAfterAll() = runBlocking<Unit> {
        try {
            guild?.delete()
        } finally {
            if (kord.isActive) {
                kord.logout()
                kord.shutdown()
            }
        }
    }

    @AfterTest
    open fun onAfter() {
        if (this::live.isInitialized && live.isActive) {
            live.shutdown()
        }
    }


    protected suspend fun createKord(): Kord = Kord(token)

    protected fun kordLoginAsync(kord: Kord = this.kord) {
        GlobalScope.launch {
            kord.login()
        }
    }

    protected suspend inline fun createGuild(
        name: String = UUID.randomUUID().toString(),
        builder: GuildCreateBuilder.() -> Unit = {}
    ): Guild = kord.createGuild(name, builder)

    protected suspend inline fun createCategory(
        guild: Guild,
        name: String = UUID.randomUUID().toString(),
        builder: CategoryCreateBuilder.() -> Unit = {}
    ): Category = guild.createCategory(name, builder)

    protected suspend inline fun createTextChannel(
        category: Category,
        name: String = UUID.randomUUID().toString(),
        builder: TextChannelCreateBuilder.() -> Unit = {}
    ): TextChannel = category.createTextChannel(name, builder)

    protected fun requireGuild() = guild!!

    protected inline fun countdownContext(
        count: Int,
        expectedCount: Long = 0,
        waitMs: Long = 5000,
        crossinline action: suspend CountDownLatch.() -> Unit
    ) = runBlocking {
        val countdown = CountDownLatch(count)

        action(countdown)

        countdown.await(waitMs, TimeUnit.MILLISECONDS)
        assertEquals(expectedCount, countdown.count)
    }
}