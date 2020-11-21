package mr.adkhambek.gsa_processor.codegen

import com.squareup.kotlinpoet.*
import mr.adkhambek.gsa_processor.ktx.snakeToLowerCamelCase
import mr.adkhambek.gsa_processor.models.ArgumentModel
import mr.adkhambek.gsa_processor.models.ArgumentsModel


internal class ArgClassBuilder(
    private val argumentsModel: ArgumentsModel
) {

    private val fragmentArgs: String
        get() = "${argumentsModel.fragmentName}Args"

    private val packageName: String
        get() = argumentsModel.packageName

    private val arguments: List<ArgumentModel>
        get() = argumentsModel.arguments

    fun build(): FileSpec = FileSpec
        .builder(packageName, fragmentArgs)
        .addType(constructorWithArguments().build())
        .build()

    private fun constructorWithArguments(): TypeSpec.Builder =
        TypeSpec.classBuilder(fragmentArgs).apply {
            addModifiers(KModifier.DATA)

            val constructorBuilder = FunSpec.constructorBuilder()
            arguments.forEach {
                val propertyName = it.name.snakeToLowerCamelCase()
                constructorBuilder.addParameter(propertyName, it.typeName)

                val property: PropertySpec = PropertySpec
                    .builder(propertyName, it.typeName)
                    .initializer(propertyName).build()

                addProperty(property)
            }
            primaryConstructor(constructorBuilder.build())
        }
}