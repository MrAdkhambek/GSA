package mr.adkhambek.gsa_annotation

import kotlin.reflect.KClass

annotation class Arg(
    val name: String,
    val type: KClass<*>,
    val defaultValue: String = "",
    val isNullable: Boolean = false
)