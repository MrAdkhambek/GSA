package mr.adkhambek.gsa.ui.main

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import mr.adkhambek.gsa.R
import mr.adkhambek.gsa.model.SomeParcelable
import mr.adkhambek.gsa.model.SomeSerializable
import mr.adkhambek.gsa_annotation.Arg
import mr.adkhambek.gsa_annotation.Args

const val age = 45

@Args(
    args = [
        Arg("user_id", Long::class, "1", isNullable = true),
        Arg("user_age", Int::class, "22", isNullable = true),
        Arg("user_name", String::class, "Adam", isNullable = true),
        Arg("some_parcelable", SomeParcelable::class,"SomeParcelable($age)", isNullable = true),
        Arg("some_serializable", SomeSerializable::class, isNullable = true)
    ]
)
class MainFragment : Fragment(R.layout.main_fragment) {

    private lateinit var viewModel: MainViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        Log.d("MainFragmentArgs", MainFragmentNav.userIdArg(requireArguments()).toString())
        Log.d("MainFragmentArgs", MainFragmentNav.userAgeArg(requireArguments()).toString())
        Log.d("MainFragmentArgs", MainFragmentNav.userNameArg(requireArguments()).toString())

        Log.d("MainFragmentArgs", MainFragmentNav.someParcelableArg(requireArguments()).toString())
        Log.d("MainFragmentArgs", MainFragmentNav.someSerializableArg(requireArguments()).toString())
    }
}