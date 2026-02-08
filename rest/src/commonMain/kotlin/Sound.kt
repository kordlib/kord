package dev.kord.rest

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.io.Source
import kotlinx.io.buffered
import kotlinx.io.files.FileSystem
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readByteArray
import kotlin.io.encoding.Base64

/**
 * Representation of a Discord sound.
 *
 * @property data the binary data of the sound
 * @property format the [Format] of the sound
 *
 * @see Image
 */
public class Sound private constructor(public val data: ByteArray, public val format: Format) {

    /**
     * The [data uri](https://en.wikipedia.org/wiki/Data_URI_scheme) representation of this sound, which is used
     * by Discord.
     */
    public val dataUri: String get() = "data:${format.mimeType};base64,${Base64.encode(data)}"

    public companion object {
        /**
         * Creates a new [Sound] by reading the [path].
         *
         * @param fileSystem the [FileSystem] to read [path] from
         */
        public fun fromFile(path: Path, fileSystem: FileSystem = SystemFileSystem): Sound =
            fileSystem.source(path).buffered()
                .use { fromSource(it, Format.fromExtension(path.name.substringAfterLast('.'))) }

        /**
         * Creates a new [Sound] by reading [source] in [format].
         *
         * **This does not close [source]**
         */
        public fun fromSource(source: Source, format: Format): Sound = raw(source.readByteArray(), format)

        /**
         * Creates a new [Sound] from [data] in [format].
         */
        public fun raw(data: ByteArray, format: Format): Sound = Sound(data, format)

        /**
         * Creates a new sound by fetching [url] using [client].
         */
        public suspend fun fromUrl(client: HttpClient, url: String): Sound {
            val call = client.get(url)
            val contentType = call.contentType()
                ?: error("expected 'Content-Type' header in image request")

            val bytes = call.body<ByteArray>()

            return Sound(bytes, Format.fromContentType(contentType))
        }
    }

    /**
     * Possible formats for [Sound].
     *
     * @property mimeType the [mime type][ContentType] of the format
     * @property extensions a list of possible file extensions
     */
    public sealed class Format(public val mimeType: ContentType, public val extensions: List<String>) {
        protected constructor(mimeType: ContentType, vararg extensions: String) : this(mimeType, extensions.toList())

        /**
         * The default extension.
         */
        public val extension: String get() = extensions.first()

        /**
         * [MP3](https://en.wikipedia.org/wiki/MP3).
         */
        public data object MP3 : Format(ContentType.Audio.MPEG, "mp3", "bit")

        /**
         * [Ogg](https://en.wikipedia.org/wiki/Ogg).
         */
        public data object OGG : Format(ContentType.Audio.OGG, "ogg", "ogv", "oga", "ogx", "ogm", "spx", "opus")

        public companion object {
            /**
             * All formats.
             */
            public val values: Set<Format>
                get() = setOf(MP3, OGG)

            /**
             * Checks whether a file with [fileName] is supported.
             */
            public fun isSupported(fileName: String): Boolean {
                return values.any {
                    it.extensions.any { extension -> fileName.endsWith(extension, true) }
                }
            }

            /**
             * Finds the corresponding [Format] for [extension].
             *
             * @throws IllegalArgumentException if the file extension is unsupported
             */
            public fun fromExtension(extension: String): Format = values.firstOrNull { extension in it.extensions }
                ?: throw IllegalArgumentException("Unsupported file extension: $extension")

            /**
             * Finds the corresponding [Format] for [type].
             *
             * @throws IllegalArgumentException if the specified type is unsupported
             */
            public fun fromContentType(type: String): Format = fromContentType(ContentType.parse(type))

            /**
             * Finds the corresponding [Format] for [type].
             *
             * @throws IllegalArgumentException if the specified type is unsupported
             */
            public fun fromContentType(type: ContentType): Format = when (type) {
                ContentType.Audio.MPEG -> MP3
                ContentType.Audio.OGG -> OGG
                else -> throw IllegalArgumentException("Unsupported type: $type")
            }
        }
    }
}
