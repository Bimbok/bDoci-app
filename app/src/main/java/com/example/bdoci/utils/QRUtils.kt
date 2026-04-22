package com.example.bdoci.utils

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Base64
import android.util.Log
import com.example.bdoci.models.Doc
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

object QRUtils {

    private val gson = Gson()

    fun encodeDocToBase64(doc: Doc): String {
        val json = gson.toJson(doc)
        val bos = ByteArrayOutputStream()
        GZIPOutputStream(bos).use { it.write(json.toByteArray()) }
        return Base64.encodeToString(bos.toByteArray(), Base64.URL_SAFE or Base64.NO_WRAP)
    }

    fun decodeBase64ToDoc(base64: String): Doc? {
        return try {
            val compressedData = Base64.decode(base64, Base64.URL_SAFE or Base64.NO_WRAP)
            val bis = ByteArrayInputStream(compressedData)
            val json = GZIPInputStream(bis).bufferedReader().use { it.readText() }
            gson.fromJson(json, Doc::class.java)
        } catch (e: Exception) {
            Log.e("QRUtils", "Error decoding doc", e)
            null
        }
    }

    fun generateQRCode(text: String, size: Int = 512): Bitmap? {
        return try {
            val hints = hashMapOf<EncodeHintType, Any>()
            hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.L
            hints[EncodeHintType.MARGIN] = 1

            val bitMatrix: BitMatrix = MultiFormatWriter().encode(
                text,
                BarcodeFormat.QR_CODE,
                size,
                size,
                hints
            )
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
                }
            }
            bitmap
        } catch (e: Exception) {
            Log.e("QRUtils", "Failed to generate QR code. Data size: ${text.length}", e)
            null
        }
    }
}