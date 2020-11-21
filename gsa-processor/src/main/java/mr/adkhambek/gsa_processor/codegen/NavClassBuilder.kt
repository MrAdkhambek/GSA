package mr.adkhambek.gsa_processor.codegen

import com.squareup.kotlinpoet.FileSpec
import mr.adkhambek.gsa_processor.models.ArgumentsModel


internal class NavClassBuilder(
    private val argumentsModel: ArgumentsModel
) {

    private val fragmentNav: String
        get() = "${argumentsModel.fragmentName}Nav"

    private val packageName: String
        get() = argumentsModel.packageName

    fun build(): FileSpec = FileSpec
        .builder(packageName, fragmentNav)
        .addType(ArgCodeBuilder(argumentsModel).build())
        .build()
}