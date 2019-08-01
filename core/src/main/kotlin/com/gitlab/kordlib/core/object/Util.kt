package com.gitlab.kordlib.core.`object`

import com.gitlab.kordlib.rest.route.Position
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


object Pagination {
    suspend fun <C : Collection<T>, T> after(size: Int, id: (T) -> String, block: suspend (position: Position?, size: Int) -> C): Flow<T> = flow {
        var position: Position? = null
        while (true) {
            val collection = block(position, size)
            for (item in collection) emit(item)
            val last = collection.last().let(id)
            position = Position.After(last)

            if (size > collection.size) break
        }
    }

    suspend fun <C : Collection<T>, T> before(size: Int, id: (T) -> String, block: suspend (position: Position?, size: Int) -> C): Flow<T> = flow {
        var position: Position? = null
        while (true) {
            val collection = block(position, size)
            for (item in collection) emit(item)
            val last = collection.last().let(id)
            position = Position.Before(last)

            if (size > collection.size) break
        }
    }
}
