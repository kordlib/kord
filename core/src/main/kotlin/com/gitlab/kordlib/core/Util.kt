package com.gitlab.kordlib.core


import com.gitlab.kordlib.core.entity.Entity
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.rest.request.RequestException
import com.gitlab.kordlib.rest.route.Position
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.flow.*
import java.time.Instant
import java.time.format.DateTimeFormatter

internal fun String?.toSnowflakeOrNull(): Snowflake? = when {
    this == null -> null
    else -> Snowflake(this)
}

internal fun Long?.toSnowflakeOrNull(): Snowflake? = when {
    this == null -> null
    else -> Snowflake(this)
}

internal fun String.toInstant() = DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(this, Instant::from)
internal fun Int.toInstant() = Instant.ofEpochMilli(toLong())
internal fun Long.toInstant() = Instant.ofEpochMilli(this)

internal inline fun <T> catchNotFound(block: () -> T): T? = try {
    block()
} catch (exception: RequestException) {
    if (exception.code == 404) null
    else throw exception
}

fun <T : Entity> Flow<T>.sorted(): Flow<T> = flow {
    for (entity in toList().sorted()) {
        emit(entity)
    }
}

suspend inline fun <T : Any> Flow<T>.firstOrNull(crossinline predicate: suspend (T) -> Boolean): T? =
        filter { predicate(it) }.take(1).singleOrNull()


suspend inline fun <T : Any> Flow<T>.any(crossinline predicate: suspend (T) -> Boolean): Boolean =
        firstOrNull(predicate) != null

internal fun <T> Flow<T>.switchIfEmpty(flow: Flow<T>): Flow<T> = flow {
    var empty = true
    collect {
        empty = false
        emit(it)
    }

    if (empty) {
        flow.collect {
            emit(it)
        }
    }
}

internal suspend inline fun <T> Flow<T>.indexOfFirstOrNull(crossinline predicate: suspend (T) -> Boolean): Int? {
    val counter = atomic(0)
    return map { counter.getAndIncrement() to it }
            .filter { predicate(it.second) }
            .take(1)
            .singleOrNull()?.first
}

internal fun <C : Collection<T>, T> paginate(
        start: String,
        batchSize: Int,
        idSelector: (T) -> String,
        directionSelector: (String) -> Position,
        request: suspend (position: Position) -> C
): Flow<T> = flow {
    var position = directionSelector(start)
    var size = batchSize

    while (true) {
        val response = request(position)
        for (item in response) emit(item)
        val id = response.lastOrNull()?.let(idSelector) ?: break
        position = directionSelector(id)

        if (response.size < size) break
        size = response.size
    }
}

internal fun <C : Collection<T>, T> paginateForwards(start: Snowflake = Snowflake("0"), batchSize: Int, idSelector: (T) -> String, request: suspend (position: Position) -> C): Flow<T> =
        paginate(start.value, batchSize, idSelector, Position::After, request)

internal fun <C : Collection<T>, T : Entity> paginateForwards(start: Snowflake = Snowflake("0"), batchSize: Int, request: suspend (position: Position) -> C): Flow<T> =
        paginate(start.value, batchSize, { it.id.value }, Position::After, request)

internal fun <C : Collection<T>, T> paginateBackwards(start: Snowflake = Snowflake(Long.MAX_VALUE), batchSize: Int, idSelector: (T) -> String, request: suspend (position: Position) -> C): Flow<T> =
        paginate(start.value, batchSize, idSelector, Position::Before, request)

internal fun <C : Collection<T>, T : Entity> paginateBackwards(start: Snowflake = Snowflake(Long.MAX_VALUE), batchSize: Int, request: suspend (position: Position) -> C): Flow<T> =
        paginate(start.value, batchSize, { it.id.value }, Position::Before, request)
