package mr.adkhambek.gsa_processor.ktx

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import mr.adkhambek.gsa_annotation.Arg
import javax.lang.model.type.MirroredTypeException
import javax.lang.model.type.TypeKind
import javax.lang.model.type.TypeMirror


internal const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"


internal fun addArgumentWithDefault(
    argName: String,
    typeName: TypeName,
    isNullable: Boolean,
    default: String
) = ParameterSpec
    .builder(argName, typeName)
    .defaultValue(if (typeName == String::class.asTypeName().copy(isNullable)) "%S" else "%L", default)
    .build()


internal fun TypeMirror.getTypeName(): TypeName {
    return when (kind) {
        TypeKind.DECLARED -> {
            if (toString() == "java.lang.String") String::class.asTypeName()
            else ClassName.bestGuess(toString())
        }
        TypeKind.BOOLEAN -> Boolean::class.asTypeName()
        TypeKind.BYTE -> Byte::class.asTypeName()
        TypeKind.SHORT -> Short::class.asTypeName()
        TypeKind.INT -> Int::class.asTypeName()
        TypeKind.LONG -> Long::class.asTypeName()
        TypeKind.CHAR -> Char::class.asTypeName()
        TypeKind.FLOAT -> Float::class.asTypeName()
        TypeKind.DOUBLE -> Double::class.asTypeName()
        else -> throw Exception("Unknown type: $this, kind: ${this.kind}")
    }
}

internal fun Arg.getTypeName(): TypeMirror = try {
    type
    throw Exception("Expected to get a MirroredTypeException")
} catch (e: MirroredTypeException) {
    e.typeMirror
}