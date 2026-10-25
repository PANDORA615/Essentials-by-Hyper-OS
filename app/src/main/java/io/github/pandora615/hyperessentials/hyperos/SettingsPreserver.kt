package io.github.pandora615.hyperessentials.hyperos
import android.content.Context
import android.content.SharedPreferences
import android.util.AtomicFile
import java.io.File
import java.util.concurrent.Executors

object SettingsPreserver {
    private val executor=Executors.newSingleThreadExecutor()
    private var listener:SharedPreferences.OnSharedPreferenceChangeListener?=null
    fun initialize(context:Context) {
        if(listener!=null) return
        val prefs=context.getSharedPreferences("essentials_prefs", Context.MODE_PRIVATE)
        val backup=AtomicFile(File(context.noBackupFilesDir,"hyper-settings.snapshot"))
        if(prefs.all.isEmpty() && backup.baseFile.exists()) {
            runCatching { PreferenceSnapshot.decode(backup.readFully()) }.getOrNull()?.let { values ->
                prefs.edit().also { editor ->
                    values.forEach { (key,v) -> when(v) {
                        is Boolean -> editor.putBoolean(key,v)
                        is Int -> editor.putInt(key,v)
                        is Long -> editor.putLong(key,v)
                        is Float -> editor.putFloat(key,v)
                        is String -> editor.putString(key,v)
                        is Set<*> -> editor.putStringSet(key,v.filterIsInstance<String>().toSet())
                    } }
                }.commit()
            }
        }
        fun save() {
            val values=HashMap(prefs.all)
            executor.execute {
                runCatching {
                    val data=PreferenceSnapshot.encode(values)
                    val stream=backup.startWrite()
                    try { stream.write(data); backup.finishWrite(stream) }
                    catch(e:Exception) { backup.failWrite(stream); throw e }
                }
            }
        }
        listener=SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if(key?.startsWith("hyper_last_")!=true && key!="hyper_native_volume_down" && key!="hyper_hardware_status") save()
        }.also { prefs.registerOnSharedPreferenceChangeListener(it) }
        save()
    }
}
