package com.lu.magic.util

import com.lu.magic.util.sh.OnceShell
import java.io.Closeable

object CmdUtil {
    private const val LINE_SEP = "\n"

    @JvmStatic
    fun run(vararg commands: String?) {
        if (NullUtil.isAllNull(commands)) {
            return
        }
        OnceShell.open(false).exec(commands, false)
    }

    @JvmStatic
    fun exec(vararg command: String?): Result {
        return exec(arrayOf(*command), false)
    }

    @JvmStatic
    fun exec(commands: Array<String?>?, root: Boolean = false): Result {
        return OnceShell.open(root).exec(commands, true)
    }

    interface Shell : Closeable {
        fun exec(commands: Array<out String?>?, isNeedResult: Boolean): Result
    }


    class Result(var code: Int, var success: String, var error: String, var commands: Array<out String?>?) {
        override fun toString(): String {
            return """CmdUtil.Result: {
    "code": $code,
    "successMsg": "$success",
    "errorMsg": "$error",
    "commands": "${commands.contentToString()}"
}""".trimMargin()
        }
    }
}