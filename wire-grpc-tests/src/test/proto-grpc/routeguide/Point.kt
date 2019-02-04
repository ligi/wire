// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: routeguide/RouteGuideProto.proto
package routeguide

import com.squareup.wire.FieldEncoding
import com.squareup.wire.Message
import com.squareup.wire.ProtoAdapter
import com.squareup.wire.ProtoReader
import com.squareup.wire.ProtoWriter
import com.squareup.wire.TagHandler
import com.squareup.wire.WireField
import kotlin.Deprecated
import kotlin.DeprecationLevel
import kotlin.Int
import kotlin.jvm.JvmField
import okio.ByteString

data class Point(
    @field:WireField(tag = 1, adapter = "com.squareup.wire.ProtoAdapter#INT32") val latitude: Int? =
            null,
    @field:WireField(tag = 2, adapter = "com.squareup.wire.ProtoAdapter#INT32") val longitude: Int?
            = null,
    val unknownFields: ByteString = ByteString.EMPTY
) : Message<Point, Point.Builder>(ADAPTER, unknownFields) {
    @Deprecated(
            message = "Shouldn't be used in Kotlin",
            level = DeprecationLevel.HIDDEN
    )
    override fun newBuilder(): Builder = Builder(this.copy())

    class Builder(private val message: Point) : Message.Builder<Point, Builder>() {
        override fun build(): Point = message
    }

    companion object {
        @JvmField
        val ADAPTER: ProtoAdapter<Point> = object : ProtoAdapter<Point>(
            FieldEncoding.LENGTH_DELIMITED, 
            Point::class.java
        ) {
            override fun encodedSize(value: Point): Int = 
                ProtoAdapter.INT32.encodedSizeWithTag(1, value.latitude) +
                ProtoAdapter.INT32.encodedSizeWithTag(2, value.longitude) +
                value.unknownFields.size

            override fun encode(writer: ProtoWriter, value: Point) {
                ProtoAdapter.INT32.encodeWithTag(writer, 1, value.latitude)
                ProtoAdapter.INT32.encodeWithTag(writer, 2, value.longitude)
                writer.writeBytes(value.unknownFields)
            }

            override fun decode(reader: ProtoReader): Point {
                var latitude: Int? = null
                var longitude: Int? = null
                val unknownFields = reader.forEachTag { tag ->
                    when (tag) {
                        1 -> latitude = ProtoAdapter.INT32.decode(reader)
                        2 -> longitude = ProtoAdapter.INT32.decode(reader)
                        else -> TagHandler.UNKNOWN_TAG
                    }
                }
                return Point(
                    latitude = latitude,
                    longitude = longitude,
                    unknownFields = unknownFields
                )
            }
        }
    }
}
