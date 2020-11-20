package mr.adkhambek.gsa_processor.models


internal data class ArgumentsModel(
    val packageName: String,
    val fragmentName: String,
    val arguments: List<ArgumentModel>
)