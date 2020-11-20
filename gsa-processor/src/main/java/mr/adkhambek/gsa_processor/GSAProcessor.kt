package mr.adkhambek.gsa_processor


import com.squareup.kotlinpoet.FileSpec
import mr.adkhambek.gsa_annotation.Args
import mr.adkhambek.gsa_processor.codegen.ArgCodeBuilder
import mr.adkhambek.gsa_processor.ktx.KAPT_KOTLIN_GENERATED_OPTION_NAME
import mr.adkhambek.gsa_processor.ktx.getTypeName
import mr.adkhambek.gsa_processor.models.ArgumentModel
import mr.adkhambek.gsa_processor.models.ArgumentsModel
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Types


@SupportedSourceVersion(SourceVersion.RELEASE_8)
class GSAProcessor : AbstractProcessor() {

    private lateinit var logger: Messager
    private lateinit var typeUtils: Types

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latest()
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(Args::class.java.name)
    }

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        logger = processingEnv.messager
        typeUtils = processingEnv.typeUtils
    }

    override fun process(
        annotations: MutableSet<out TypeElement>?,
        roundEnv: RoundEnvironment
    ): Boolean {
        val kaptKotlinGeneratedDir =
            processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME] ?: return true

        roundEnv
            .getElementsAnnotatedWith(Args::class.java)
            .forEach {
                val modelData = getModelData(it)
                val fragmentName = modelData.fragmentName

                FileSpec.builder(modelData.packageName, "${fragmentName}Nav")
                    .addType(
                        ArgCodeBuilder(
                            modelData.packageName,
                            fragmentName,
                            modelData.arguments
                        ).build()
                    )
                    .build()
                    .writeTo(File(kaptKotlinGeneratedDir))
            }

        return true
    }

    private fun getModelData(element: Element): ArgumentsModel {
        val packageName: String = processingEnv.elementUtils.getPackageOf(element).toString()
        val modelName: String = element.simpleName.toString()

        val annotation = element.getAnnotation(Args::class.java)
        val args = annotation.args.map {
            ArgumentModel(
                it.name,
                it.getTypeName(),
                it.isNullable,
                it.defaultValue
            )
        }

        return ArgumentsModel(packageName, modelName, args)
    }
}

