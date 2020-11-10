package foo

import android.os.Bundle
import androidx.navigation.NavDirections
import kotlin.Int
import kotlin.String

public class SettingsDirections private constructor() {
  private data class Main(
    public val enterReason: String = "DEFAULT"
  ) : NavDirections {
    public override fun getActionId(): Int = R.id.main

    public override fun getArguments(): Bundle {
      val result = Bundle()
      result.putString("enterReason", this.enterReason)
      return result
    }
  }

  private data class Exit(
    public val exitReason: String = "DEFAULT"
  ) : NavDirections {
    public override fun getActionId(): Int = R.id.exit

    public override fun getArguments(): Bundle {
      val result = Bundle()
      result.putString("exitReason", this.exitReason)
      return result
    }
  }

  public companion object {
    public fun main(enterReason: String = "DEFAULT"): NavDirections = Main(enterReason)

    public fun exit(exitReason: String = "DEFAULT"): NavDirections = Exit(exitReason)
  }
}
