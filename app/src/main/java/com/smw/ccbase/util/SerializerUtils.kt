package com.smw.ccbase.util

import com.smw.ccbase.enums.MonsterTypeEnum
import com.smw.ccbase.enums.SpellTypeEnum
import com.smw.ccbase.enums.TrapTypeEnum
import io.github.jan.supabase.SupabaseSerializer
import io.github.jan.supabase.serializer.KotlinXSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import java.util.UUID
import kotlin.reflect.KType

class SerializerUtils private constructor() {

    object UUIDSerializer : KSerializer<UUID> {
        override val descriptor =
            PrimitiveSerialDescriptor(UUID::class.java.name, PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder): UUID {
            return UUID.fromString(decoder.decodeString())
        }

        override fun serialize(encoder: Encoder, value: UUID) {
            encoder.encodeString(value.toString())
        }
    }

    object MonsterTypeEnumSerializer : KSerializer<MonsterTypeEnum> {
        override val descriptor =
            PrimitiveSerialDescriptor(MonsterTypeEnum::class.java.name, PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder): MonsterTypeEnum {
            return MonsterTypeEnum.valueOf(decoder.decodeString())
        }

        override fun serialize(encoder: Encoder, value: MonsterTypeEnum) {
            encoder.encodeString(value.name)
        }
    }

    object SpellTypeEnumSerializer : KSerializer<SpellTypeEnum> {
        override val descriptor =
            PrimitiveSerialDescriptor(SpellTypeEnum::class.java.name, PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder): SpellTypeEnum {
            return SpellTypeEnum.valueOf(decoder.decodeString())
        }

        override fun serialize(encoder: Encoder, value: SpellTypeEnum) {
            encoder.encodeString(value.name)
        }
    }

    object TrapTypeEnumSerializer : KSerializer<TrapTypeEnum> {
        override val descriptor =
            PrimitiveSerialDescriptor(TrapTypeEnum::class.java.name, PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder): TrapTypeEnum {
            return TrapTypeEnum.valueOf(decoder.decodeString())
        }

        override fun serialize(encoder: Encoder, value: TrapTypeEnum) {
            encoder.encodeString(value.name)
        }
    }

    object JsonSerializer : SupabaseSerializer {
        private val jsonSerializer: SupabaseSerializer = KotlinXSerializer(Json {
            ignoreUnknownKeys = true
            namingStrategy = JsonNamingStrategy.SnakeCase
            classDiscriminator = "kind"
        })

        override fun <T : Any> decode(type: KType, value: String): T {
            return jsonSerializer.decode(type, value)
        }

        override fun <T : Any> encode(type: KType, value: T): String {
            return jsonSerializer.encode(type, value)
        }
    }
}