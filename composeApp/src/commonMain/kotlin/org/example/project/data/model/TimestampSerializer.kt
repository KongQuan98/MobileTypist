package org.example.project.data.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.longOrNull
import kotlin.time.Clock
import kotlin.time.Instant

object TimestampSerializer : KSerializer<Long> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("TimestampSerializer", PrimitiveKind.LONG)

    override fun serialize(encoder: Encoder, value: Long) {
        encoder.encodeLong(value)
    }

    override fun deserialize(decoder: Decoder): Long {
        if (decoder is JsonDecoder) {
            val element = decoder.decodeJsonElement()
            if (element is JsonPrimitive) {
                element.longOrNull?.let { return it }
                val content = element.content
                content.toLongOrNull()?.let { return it }
                try {
                    return Instant.parse(content).toEpochMilliseconds()
                } catch (_: Exception) {
                }
            }
        } else {
            try {
                return decoder.decodeLong()
            } catch (_: Exception) {
            }
        }
        return Clock.System.now().toEpochMilliseconds()
    }
}

object NullableTimestampSerializer : KSerializer<Long?> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("NullableTimestampSerializer", PrimitiveKind.LONG)

    @OptIn(ExperimentalSerializationApi::class)
    override fun serialize(encoder: Encoder, value: Long?) {
        if (value != null) {
            encoder.encodeLong(value)
        } else {
            encoder.encodeNull()
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun deserialize(decoder: Decoder): Long? {
        if (decoder.decodeNotNullMark()) {
            if (decoder is JsonDecoder) {
                val element = decoder.decodeJsonElement()
                if (element is JsonPrimitive) {
                    element.longOrNull?.let { return it }
                    val content = element.content
                    content.toLongOrNull()?.let { return it }
                    try {
                        return Instant.parse(content).toEpochMilliseconds()
                    } catch (_: Exception) {
                    }
                }
            } else {
                try {
                    return decoder.decodeLong()
                } catch (_: Exception) {
                }
            }
        } else {
            decoder.decodeNull()
            return null
        }
        return null
    }
}
