package mr.adkhambek.gsa_processor.codegen

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import mr.adkhambek.gsa_annotation.ArgsDSL
import mr.adkhambek.gsa_processor.ktx.addArgumentWithDefault
import mr.adkhambek.gsa_processor.ktx.camelToSnakeCase
import mr.adkhambek.gsa_processor.ktx.snakeToLowerCamelCase
import mr.adkhambek.gsa_processor.models.ArgumentModel
import mr.adkhambek.gsa_processor.models.ArgumentsModel


internal class ArgCodeBuilder(
    private val argumentsModel: ArgumentsModel
) {

    private val fragmentName: String
        get() = argumentsModel.fragmentName

    private val packageName: String
        get() = argumentsModel.packageName

    private val arguments: List<ArgumentModel>
        get() = argumentsModel.arguments

    fun build(): TypeSpec = TypeSpec.objectBuilder("${fragmentName}Nav")
        .apply {
            addFactoryMethod()
            addArgsMethod()
            addArgClassMethod()
        }.build()

    private fun TypeSpec.Builder.addFactoryMethod(): TypeSpec.Builder = apply {
        val funcName = fragmentName.camelToSnakeCase().snakeToLowerCamelCase()
        val func: FunSpec.Builder = FunSpec
            .builder(funcName)
            .addAnnotation(ArgsDSL::class)
            .addAnnotation(JvmStatic::class)
            .addModifiers(KModifier.PUBLIC)
            .returns(ClassName(packageName, fragmentName))

        arguments.forEach {
            if (it.defaultValue.isEmpty()) func.addParameter(
                it.name.snakeToLowerCamelCase(),
                it.typeName
            )
            else func.addParameter(
                addArgumentWithDefault(
                    it.name.snakeToLowerCamelCase(),
                    it.typeName,
                    it.isNullable,
                    it.defaultValue
                )
            )
        }

        func.addStatement("val args = androidx.core.os.bundleOf(")
        arguments.forEach {
            func.addStatement(
                "\"%L\" to %L,",
                it.name.camelToSnakeCase(),
                it.name.snakeToLowerCamelCase()
            )
        }
        func.addStatement(")")

        func.addStatement("return %L().apply { arguments = args }", fragmentName)
        addFunction(func.build())
    }

    private fun TypeSpec.Builder.addArgsMethod(): TypeSpec.Builder = apply {
        arguments.forEach {
            val func: FunSpec.Builder = FunSpec
                .builder("${it.name.snakeToLowerCamelCase()}Arg")
                .addAnnotation(ArgsDSL::class)
                .addAnnotation(JvmStatic::class)
                .addModifiers(KModifier.PUBLIC)
                .returns(it.typeName)

            func.addParameter(
                "arg",
                ClassName("android.os", "Bundle")
            )

            func.addStatement(
                "return arg.get(\"%L\") as %L",
                it.name.camelToSnakeCase(),
                it.typeName
            )

            addFunction(func.build())
        }
    }

    private fun TypeSpec.Builder.addArgClassMethod(): TypeSpec.Builder = apply {
        val clazz = ClassName(packageName, "${fragmentName}Args")
        val funcName = fragmentName.camelToSnakeCase().snakeToLowerCamelCase()

        val func: FunSpec.Builder = FunSpec
            .builder("${funcName}Args")
            .addAnnotation(ArgsDSL::class)
            .addAnnotation(JvmStatic::class)
            .addModifiers(KModifier.PUBLIC)
            .addParameter("arg", ClassName("android.os", "Bundle"))
            .returns(clazz)

        func.addStatement("val args =  ${fragmentName}Args(")
        arguments.forEach {
            func.addStatement(
                "%L = arg.get(\"%L\") as %L,",
                it.name.snakeToLowerCamelCase(),
                it.name.camelToSnakeCase(),
                it.typeName
            )
        }
        func.addStatement(")")
        func.addStatement("return args")

        addFunction(func.build())
    }
}