@file:UseSerializers(InstantSerializer::class)

package co.adrianblan.hackernews.api

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.Instant

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = Instant::class)
object InstantSerializer : KSerializer<Instant> {

    override fun serialize(encoder: Encoder, value: Instant) =
        encoder.encodeLong(value.epochSecond)

    override fun deserialize(decoder: Decoder): Instant =
        Instant.ofEpochSecond(decoder.decodeLong())
}
