package com.gitlab.kordlib.rest.json

import kotlinx.serialization.*

/**
 * This is a very stupid serializer and you should feel ashamed for calling this.
 * Essentially, there's a use case where a [com.gitlab.kordlib.rest.route.Route] may *sometimes* return
 * a value, and sometimes nothing.
 *
 * `nullable` doesn't save you here since it'll expect at least something, and thus throws on an empty input.
 * Thus this crime against control flow was born. This will try to serialize the type as if it wasn't null, and
 * swallow any exceptions while doing so, returning null instead. This is incredibly bad because we won't propagate any
 * actual bugs.
 */
internal val <T> KSerializer<T>.optional: KSerializer<T?>
    get() = object: KSerializer<T?> {

        override val descriptor: SerialDescriptor
            get() = this@optional.descriptor

        override fun deserialize(decoder: Decoder): T? = try {
            decoder.decode(this@optional)
        } catch (e :Exception) {
            null
        }

        override fun serialize(encoder: Encoder, value: T?) {
            if (value == null) return encoder.encodeNull()
            else encoder.encode(this@optional, value)
        }
    }