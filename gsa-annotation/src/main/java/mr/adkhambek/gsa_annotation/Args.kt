package mr.adkhambek.gsa_annotation


@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class Args(
    val args: Array<Arg>
)