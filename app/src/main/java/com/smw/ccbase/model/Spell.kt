package com.smw.ccbase.model

import com.smw.ccbase.enums.SpellTypeEnum
import com.smw.ccbase.util.SerializerUtils
import io.github.jan.supabase.encode
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
@SerialName("spell")
class Spell(
    @Serializable(with = SerializerUtils.UUIDSerializer::class)
    private val uid: UUID,
    private val name: String,
    private val type: SpellTypeEnum,
    private val setCode: String,
    private val setNumber: String,
    private val count: Short,
) : BaseCard {
    override fun uid(): UUID {
        return this.uid
    }

    override fun name(): String {
        return this.name
    }

    override fun type(): SpellTypeEnum {
        return this.type
    }

    override fun setCode(): String {
        return this.setCode
    }

    override fun setNumber(): String {
        return this.setNumber
    }

    override fun count(): Short {
        return this.count
    }

    override fun toString(): String {
        return SerializerUtils.JsonSerializer.encode(this)
    }
}