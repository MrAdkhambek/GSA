package mr.adkhambek.gsa.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable


data class SomeSerializable(
    val productId: Long
) : Serializable


@Parcelize
data class SomeParcelable(
    val month: Int
) : Parcelable