package co.adrianblan.hackernews.api

import kotlinx.serialization.*
import kotlinx.serialization.modules.serializersModuleOf
import java.time.Instant


@Serializer(forClass = StoryId::class)
object StoryIdSerializer : KSerializer<StoryId> {

    override fun serialize(encoder: Encoder, value: StoryId) =
        encoder.encodeLong(value.id)

    override fun deserialize(decoder: Decoder): StoryId =
        StoryId(decoder.decodeLong())
}

@Serializer(forClass = StoryUrl::class)
object StoryUrlSerializer : KSerializer<StoryUrl?> {

    override fun serialize(encoder: Encoder, value: StoryUrl?) =
        encoder.encodeString(value?.url.orEmpty())

    override fun deserialize(decoder: Decoder): StoryUrl? =
        decoder.decodeString()
            .takeIf { it.isNotEmpty() }
            ?.let { StoryUrl(it) }
}

@Serializer(forClass = CommentId::class)
object CommentIdSerializer : KSerializer<CommentId> {

    override fun serialize(encoder: Encoder, value: CommentId) =
        encoder.encodeLong(value.id)

    override fun deserialize(decoder: Decoder): CommentId =
        CommentId(decoder.decodeLong())
}

@Serializer(forClass = Instant::class)
object InstantSerializer : KSerializer<Instant> {

    override fun serialize(encoder: Encoder, value: Instant) =
        encoder.encodeLong(value.epochSecond)

    override fun deserialize(decoder: Decoder): Instant =
        Instant.ofEpochSecond(decoder.decodeLong())
}