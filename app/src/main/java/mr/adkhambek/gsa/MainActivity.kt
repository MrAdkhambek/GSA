package mr.adkhambek.gsa

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import mr.adkhambek.gsa.model.SomeParcelable
import mr.adkhambek.gsa.model.SomeSerializable
import mr.adkhambek.gsa.ui.main.MainFragmentNav

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, getMainFragment())
                .commitNow()
        }
    }

    private fun getMainFragment() = MainFragmentNav.onMainFragment(
//        userId = 45L,
//        userAge = 22,
//        userName = "Peter",
        someParcelable = SomeParcelable(12),
        someSerializable = SomeSerializable(36)
    )
}