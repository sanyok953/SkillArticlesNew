package ru.skillbranch.skillarticles

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import ru.skillbranch.skillarticles.data.PrefManager


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4::class)
class InstrumentalTest2 {

    @Test(timeout = 30000)
    fun module6() {
        val pref = PrefManager(InstrumentationRegistry.getInstrumentation().targetContext)
        Assert.assertEquals("prefs testBoolean default value", false,  pref.testBoolean)
        Assert.assertEquals("prefs testDouble default value", Double.MAX_VALUE,  pref.testDouble, 0.1)
        Assert.assertEquals("prefs testString default value", "test",  pref.testString)
        Assert.assertEquals("prefs testFloat default value", Float.MAX_VALUE,  pref.testFloat)
        Assert.assertEquals("prefs testInt default value", Int.MAX_VALUE,  pref.testInt)
        Assert.assertEquals("prefs testLong default value", Long.MAX_VALUE,  pref.testLong)

        pref.testBoolean = true
        pref.testDouble = Double.MIN_VALUE
        pref.testString = "test string value"
        pref.testFloat = Float.MIN_VALUE
        pref.testInt = Int.MIN_VALUE
        pref.testLong = Long.MIN_VALUE

        Assert.assertEquals("prefs testBoolean value after set",true,  pref.testBoolean)
        Assert.assertEquals("prefs testDouble value after set",Double.MIN_VALUE,  pref.testDouble, 0.1)
        Assert.assertEquals("prefs testString value after set","test string value",  pref.testString)
        Assert.assertEquals("prefs testFloat value after set", Float.MIN_VALUE,  pref.testFloat)
        Assert.assertEquals("prefs testInt value after set", Int.MIN_VALUE,  pref.testInt)
        Assert.assertEquals("prefs testLong value after set",Long.MIN_VALUE,  pref.testLong)

    }
}



