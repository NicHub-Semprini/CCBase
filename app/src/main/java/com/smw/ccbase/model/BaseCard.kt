package com.smw.ccbase.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
sealed interface BaseCard {
    fun uid(): UUID
    fun name(): String
    fun type(): Enum<*>
    fun setCode(): String
    fun setNumber(): String
    fun count(): Short
}