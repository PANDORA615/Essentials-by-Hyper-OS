package io.github.pandora615.hyperessentials.hyperos
import java.io.*

/** Typed local snapshot: preserves Int/Long/Float and StringSet without reflection. */
object PreferenceSnapshot {
    fun encode(values: Map<String, *>): ByteArray = ByteArrayOutputStream().also { bytes ->
        DataOutputStream(bytes).use { out ->
            out.writeInt(0x48595031)
            out.writeInt(values.size)
            values.forEach { (key, value) ->
                out.writeUTF(key)
                when (value) {
                    is Boolean -> { out.writeByte(1); out.writeBoolean(value) }
                    is Int -> { out.writeByte(2); out.writeInt(value) }
                    is Long -> { out.writeByte(3); out.writeLong(value) }
                    is Float -> { out.writeByte(4); out.writeFloat(value) }
                    is String -> { out.writeByte(5); val b=value.toByteArray(Charsets.UTF_8); out.writeInt(b.size); out.write(b) }
                    is Set<*> -> { out.writeByte(6); out.writeInt(value.size); value.forEach { out.writeUTF(it as String) } }
                    else -> error("Unsupported preference type")
                }
            }
        }
    }.toByteArray()
    fun decode(bytes: ByteArray): Map<String, Any>? = runCatching {
        require(bytes.size <= 16 * 1024 * 1024)
        DataInputStream(ByteArrayInputStream(bytes)).use { input ->
            require(input.readInt() == 0x48595031)
            val count=input.readInt(); require(count in 0..100000)
            buildMap<String, Any> {
                repeat(count) {
                    val key=input.readUTF()
                    val v:Any = when(input.readUnsignedByte()) {
                        1 -> input.readBoolean(); 2 -> input.readInt(); 3 -> input.readLong(); 4 -> input.readFloat()
                        5 -> { val n=input.readInt(); require(n in 0..bytes.size); ByteArray(n).also { input.readFully(it) }.toString(Charsets.UTF_8) }
                        6 -> { val n=input.readInt(); require(n in 0..10000); buildSet { repeat(n) { add(input.readUTF()) } } }
                        else -> error("Unknown preference type")
                    }
                    put(key,v)
                }
                require(input.available()==0)
            }
        }
    }.getOrNull()
}
