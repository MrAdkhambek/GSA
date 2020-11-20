package mr.adkhambek.gsa_processor.models

import com.squareup.kotlinpoet.TypeName

internal data class ArgumentModel(
    val name: String,
    val typeName: TypeName,
    val isNullable: Boolean,
    val defaultValue: String
)