@file:UseSerializers(InstantSerializer::class)

package co.adrianblan.hackernews.api

import kotlinx.serialization.UseSerializers
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.Instant

@Serializer(forClass = Instant::class)
object InstantSerializer : KSerializer<Instant> {

    override fun serialize(encoder: Encoder, value: Instant) =
        encoder.encodeLong(value.epochSecond)

    override fun deserialize(decoder: Decoder): Instant =
        Instant.ofEpochSecond(decoder.decodeLong())
}

@Serializer(forClass = ApiComment::class)
object ApiCommentSerializer

/**
 * Sometimes the api just returns null literal for certain comments,
 * we must then convert it to null comment.
 */
object NullableApiCommentSerializer : KSerializer<ApiComment?> {

    override val descriptor: SerialDescriptor
        get() = ApiCommentSerializer.descriptor

    override fun serialize(encoder: Encoder, value: ApiComment?) {
        if (value != null) {
            ApiCommentSerializer.serialize(encoder, value)
        } else {
            encoder.encodeNull()
        }
    }

    override fun deserialize(decoder: Decoder): ApiComment? =
        try {
            decoder.decodeNull()
            null
        } catch (e: Exception) {
            ApiCommentSerializer.deserialize(decoder)
        }
}