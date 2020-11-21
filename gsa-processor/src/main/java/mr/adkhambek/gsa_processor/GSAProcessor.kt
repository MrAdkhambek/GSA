package mr.adkhambek.gsa_processor


import com.squareup.kotlinpoet.FileSpec
import mr.adkhambek.gsa_annotation.Arg
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
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import javax.tools.Diagnostic


@SupportedSourceVersion(SourceVersion.RELEASE_8)
class GSAProcessor : AbstractProcessor() {

    private lateinit var logger: Messager
    private lateinit var typeUtils: Types
    private lateinit var elementUtils: Elements

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latest()
    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(Args::class.java.name, Arg::class.java.name)
    }

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        logger = processingEnv.messager
        typeUtils = processingEnv.typeUtils
        elementUtils = processingEnv.elementUtils
    }

    private fun note(message: Any?) {
        message ?: return
        logger.printMessage(Diagnostic.Kind.NOTE, message.toString())
    }

    private fun error(message: Any?, element: Element) {
        message ?: return
        logger.printMessage(Diagnostic.Kind.ERROR, message.toString(), element)
    }

    override fun process(
        annotations: MutableSet<out TypeElement>?,
        roundEnv: RoundEnvironment
    ): Boolean {
        val kaptKotlinGeneratedDir: String = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME] ?: return true

        roundEnv
            .getElementsAnnotatedWith(Args::class.java)
            .forEach {

                if (!isAssignableFragment(it)) {
                    error(MESSAGE_FRAGMENT, it)
                    return false
                }

                val argumentsModel = getArgumentModel(it)
                val fragmentName = argumentsModel.fragmentName

                FileSpec.builder(argumentsModel.packageName, "${fragmentName}Nav")
                    .addType(ArgCodeBuilder(argumentsModel).build())
                    .build()
                    .writeTo(File(kaptKotlinGeneratedDir))
            }

        return true
    }

    private fun getArgumentModel(element: Element): ArgumentsModel {
        val packageName: String = processingEnv.elementUtils.getPackageOf(element).toString()
        val modelName: String = element.simpleName.toString()

        val args = element.getAnnotation(Args::class.java)

        /**
         **     check all arguments isPrimitive or isSerializable or isParcelable
         **/
        args.args.forEach { arg ->
            val argumentType = arg.getTypeName()
            if (!isAssignableSerializable(argumentType)) {
                val argumentElement = processingEnv.typeUtils.asElement(argumentType)
                error(MESSAGE_SERIALIZABLE, argumentElement)
                throw Exception()
            }
        }

        val arguments: List<ArgumentModel> = args.args.map {
            ArgumentModel(
                it.name,
                it.getTypeName().getTypeName(),
                it.isNullable,
                it.defaultValue
            )
        }

        return ArgumentsModel(packageName, modelName, arguments)
    }


    private fun isAssignableFragment(element: Element): Boolean {
        val elType: TypeMirror = element.asType()
        val fragmentType: TypeMirror = elementUtils.getTypeElement(PACKAGE_FRAGMENT).asType()
        return typeUtils.isAssignable(elType, fragmentType)
    }

    private fun isAssignableSerializable(typeMirror: TypeMirror): Boolean {
        val typeParcelable: TypeMirror = elementUtils.getTypeElement(PACKAGE_PARCELABLE).asType()
        val typeSerializable: TypeMirror = elementUtils.getTypeElement(PACKAGE_SERIALIZABLE).asType()
        return typeMirror.kind.isPrimitive || typeUtils.isAssignable(typeMirror, typeSerializable) || typeUtils.isAssignable(typeMirror, typeParcelable)
    }

    companion object {
        private const val PACKAGE_FRAGMENT = "androidx.fragment.app.Fragment"
        private const val PACKAGE_SERIALIZABLE = "java.io.Serializable"
        private const val PACKAGE_PARCELABLE = "android.os.Parcelable"

        private const val MESSAGE_FRAGMENT = "class must be implement $PACKAGE_FRAGMENT"
        private const val MESSAGE_SERIALIZABLE = "class must be implement $PACKAGE_SERIALIZABLE or $PACKAGE_PARCELABLE"
    }
}

