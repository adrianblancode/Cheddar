package co.adrianblan.hackernews.api

import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer

@Serializer(forClass = StoryId::class)
object StoryIdSerializer: KSerializer<StoryId> {

    override fun serialize(encoder: Encoder, obj: StoryId) =
        encoder.encodeLong(obj.id)

    override fun deserialize(decoder: Decoder): StoryId =
        StoryId(decoder.decodeLong())
}

@Serializer(forClass = CommentId::class)
object CommentIdSerializer: KSerializer<CommentId> {

    override fun serialize(encoder: Encoder, obj: CommentId) =
        encoder.encodeLong(obj.id)

    override fun deserialize(decoder: Decoder): CommentId =
        CommentId(decoder.decodeLong())
}