package com.aiven.qcc

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.text.TextUtils
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.lang.NullPointerException
import java.util.*

class QRCreator {

    private val content: String
    private var size: Int = 0
    private var charSet: String
    private var errorCorrectionLevel: ErrorCorrectionLevel
    private var margin: Int = 0
    private var colorBlack: Int = 0
    private var colorWhite: Int = 0
    private var logoBitmap: Bitmap? = null
    private var logoPercent: Float? = null
    private var recycleLogoBitmap = false
    private var onQRCreateListener: OnQRCreateListener? = null

    private constructor(
        content: String,
        size: Int,
        characterSet: String,
        errorCorrectionLevel: ErrorCorrectionLevel,
        margin: Int,
        colorBlack: Int,
        colorWhite: Int,
        logoBitmap: Bitmap? = null,
        logoPercent: Float? = null,
        recycle: Boolean = false,
        listener: OnQRCreateListener?
    ) {
        this.content = content
        this.size = size
        this.charSet = characterSet
        this.errorCorrectionLevel = errorCorrectionLevel
        this.margin = margin
        this.colorBlack = colorBlack
        this.colorWhite = colorWhite
        this.logoBitmap = logoBitmap
        this.logoPercent = logoPercent
        this.recycleLogoBitmap = recycle
        this.onQRCreateListener = listener
    }

    class Builder {
        private lateinit var _content: String
        private var _size: Int = 100
        private var _charSet: String = "UTF-8"
        private var _errorCorrectionLevel: ErrorCorrectionLevel = ErrorCorrectionLevel.L
        private var _margin: Int = 1
        private var _colorBlack: Int = Color.BLACK
        private var _colorWhite: Int = Color.WHITE
        private var _logoBitmap: Bitmap? = null
        private var _logoPercent: Float? = null
        private var _recycleLogoBitmap = false
        private var onQRCreateListener: OnQRCreateListener? = null

        /**
         * ??????????????????????????????????????????????????????????????????
         * @param content
         * */
        fun setContent(content: String): Builder {
            _content = content
            return this
        }

        /**
         * ?????????????????????????????????????????????????????????????????????????????????size??????????????????????????????
         * ???????????????????????????100px???????????????50?????????50?????????
         * @param size px
         * */
        fun setSize(size: Int): Builder {
            _size = size
            return this
        }

        /**
         * ?????????????????????UTF-8
         * @param characterSet
         * */
        fun setCharacterSet(characterSet: String): Builder {
            _charSet = characterSet
            return this
        }

        /**
         * ???????????????????????????L?????????
         * ?????????????????????????????????????????????
         * @param errorCorrectionLevel
         * */
        fun setErrorCorrectionLevel(errorCorrectionLevel: ErrorCorrectionLevel): Builder {
            _errorCorrectionLevel = errorCorrectionLevel
            return this
        }

        /**
         * ???????????????????????????????????????????????????????????????????????????1???
         * @param margin
         * */
        fun setMargin(margin: Int): Builder {
            _margin = margin
            return this
        }

        /**
         * ?????????????????????????????????????????????????????????
         * @param color
         * */
        fun setColorBlack(color: Int): Builder {
            _colorBlack = color
            return this
        }

        /**
         * ?????????????????????????????????????????????????????????
         * @param color
         * */
        fun setColorWhite(color: Int): Builder {
            _colorWhite = color
            return this
        }

        /**
         * ???????????????logo????????????????????????logo????????????????????????
         * @param logo
         * */
        fun setLogoBitmap(logo: Bitmap): Builder {
            this._logoBitmap = logo
            return this
        }

        /**
         * ??????logo???????????????????????????0~1???????????????logo?????????????????????????????????0.2???
         * @param percent
         * */
        fun setLogoPercent(percent: Float): Builder {
            this._logoPercent = percent
            return this
        }

        /**
         * ?????????????????????????????????
         * @param listener
         * */
        fun setOnQRCreatorListener(listener: OnQRCreateListener): Builder {
            this.onQRCreateListener = listener
            return this
        }

        /**
         * ?????????????????????????????????logo???bitmap??????????????????
         * @param recycle
         * */
        fun setRecycleLogoBitmap(recycle: Boolean): Builder {
            this._recycleLogoBitmap = recycle
            return this
        }

        fun builder(): QRCreator {
            checkParam()
            return QRCreator(
                content              =_content,
                size                 =_size,
                characterSet         = _charSet,
                errorCorrectionLevel = _errorCorrectionLevel,
                margin               = _margin,
                colorBlack           = _colorBlack,
                colorWhite           = _colorWhite,
                logoBitmap           = _logoBitmap,
                logoPercent          = _logoPercent,
                recycle              = _recycleLogoBitmap,
                listener             = onQRCreateListener
            )
        }

        private fun checkParam() {
            if (_content.isEmpty()) {
                throw NullPointerException("Content is null")
            }
            if (_size < 50) {
                _size = 50
            }
            if (_margin < 0) {
                throw IllegalArgumentException("Margin can't be less 0 px")
            }
            if (_logoBitmap != null) {
                if (_logoPercent == null) {
                    _logoPercent = 0.2f
                } else if (_logoPercent!! < 0.00000f || _logoPercent!! > 1.0000000f) {
                    throw IllegalArgumentException("percent: 0 ~ 1")
                }
            }
        }
    }

    fun createQRCodeBitmap() {
        val observable = Observable.create<Bitmap> { emitter ->
            val hints = Hashtable<EncodeHintType, String>()
            hints[EncodeHintType.CHARACTER_SET] = charSet
            hints[EncodeHintType.ERROR_CORRECTION] = errorCorrectionLevel.value
            hints[EncodeHintType.MARGIN] = margin.toString()
            val bitMatrix =
                QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, size, size, hints)
            val pixels = IntArray(size * size)
            for (y in 0 until size) {
                for (x in 0 until size) {
                    pixels[y * size + x] =
                        if (bitMatrix[x, y]) {
                            colorBlack
                        } else {
                            colorWhite
                        }
                }
            }
            val srcBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888).apply {
                setPixels(pixels, 0, size, 0, 0, size, size)
            }
            if (logoBitmap != null) {
                val targetBitmap = addLogo(srcBitmap, logoBitmap!!, logoPercent!!)
                srcBitmap.recycle()
                if (recycleLogoBitmap) {
                    logoBitmap!!.recycle()
                }
                emitter.onNext(targetBitmap)
            } else {
                emitter.onNext(srcBitmap)
            }
        }
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    onQRCreateListener?.onSuccess(it)
                },{
                    val error = it.toString()
                    onQRCreateListener?.onFailure(error.substring(error.indexOf(':') + 1))
                }
            )
    }

    private fun addLogo(srcBitmap: Bitmap, logoBitmap: Bitmap, percent: Float): Bitmap {
        val srcWidth = srcBitmap.width
        val srcHeight = srcBitmap.height
        val logoWidth = logoBitmap.width
        val logoHeight = logoBitmap.height
        val scaleWidth = srcWidth * percent / logoWidth
        val scaleHeight = srcHeight * percent / logoHeight
        val bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawBitmap(srcBitmap, 0f, 0f, null)
        canvas.scale(scaleWidth, scaleHeight, srcWidth / 2f, srcHeight / 2f)
        canvas.drawBitmap(logoBitmap, srcWidth / 2f - logoWidth / 2f, srcHeight / 2f - logoHeight / 2f, null)
        return bitmap
    }
}

//package com.aiven.qcc
//
//import android.graphics.Bitmap
//import android.graphics.Canvas
//import android.graphics.Color
//import android.text.TextUtils
//import com.google.zxing.BarcodeFormat
//import com.google.zxing.EncodeHintType
//import com.google.zxing.qrcode.QRCodeWriter
//import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
//import io.reactivex.rxjava3.core.Observable
//import io.reactivex.rxjava3.schedulers.Schedulers
//import java.lang.NullPointerException
//import java.util.*
//
//class QRCreator {
//
//    private val content: String
//    private var size: Int = 0
//    private var charSet: String
//    private var errorCorrectionLevel: ErrorCorrectionLevel
//    private var margin: Int = 0
//    private var colorBlack: Int = 0
//    private var colorWhite: Int = 0
//    private var logoBitmap: Bitmap? = null
//    private var logoPercent: Float? = null
//    private var recycleLogoBitmap = false
//    private var onQRCreateListener: OnQRCreateListener? = null
//
//    private constructor(
//        content: String,
//        size: Int,
//        characterSet: String,
//        errorCorrectionLevel: ErrorCorrectionLevel,
//        margin: Int,
//        colorBlack: Int,
//        colorWhite: Int,
//        logoBitmap: Bitmap? = null,
//        logoPercent: Float? = null,
//        recycle: Boolean = false,
//        listener: OnQRCreateListener?
//    ) {
//        this.content = content
//        this.size = size
//        this.charSet = characterSet
//        this.errorCorrectionLevel = errorCorrectionLevel
//        this.margin = margin
//        this.colorBlack = colorBlack
//        this.colorWhite = colorWhite
//        this.logoBitmap = logoBitmap
//        this.logoPercent = logoPercent
//        this.recycleLogoBitmap = recycle
//        this.onQRCreateListener = listener
//    }
//
//    class Builder {
//        private lateinit var _content: String
//        private var _size: Int = 100
//        private var _charSet: String = "UTF-8"
//        private var _errorCorrectionLevel: ErrorCorrectionLevel = ErrorCorrectionLevel.L
//        private var _margin: Int = 1
//        private var _colorBlack: Int = Color.BLACK
//        private var _colorWhite: Int = Color.WHITE
//        private var _logoBitmap: Bitmap? = null
//        private var _logoPercent: Float? = null
//        private var _recycleLogoBitmap = false
//        private var onQRCreateListener: OnQRCreateListener? = null
//
//        /**
//         * ??????????????????????????????????????????????????????????????????
//         * @param content
//         * */
//        fun setContent(content: String): Builder {
//            _content = content
//            return this
//        }
//
//        /**
//         * ?????????????????????????????????????????????????????????????????????????????????size??????????????????????????????
//         * ???????????????????????????100px???????????????50?????????50?????????
//         * @param size px
//         * */
//        fun setSize(size: Int): Builder {
//            _size = size
//            return this
//        }
//
//        /**
//         * ?????????????????????UTF-8
//         * @param characterSet
//         * */
//        fun setCharacterSet(characterSet: String): Builder {
//            _charSet = characterSet
//            return this
//        }
//
//        /**
//         * ???????????????????????????L?????????
//         * ?????????????????????????????????????????????
//         * @param errorCorrectionLevel
//         * */
//        fun setErrorCorrectionLevel(errorCorrectionLevel: ErrorCorrectionLevel): Builder {
//            _errorCorrectionLevel = errorCorrectionLevel
//            return this
//        }
//
//        /**
//         * ???????????????????????????????????????????????????????????????????????????1???
//         * @param margin
//         * */
//        fun setMargin(margin: Int): Builder {
//            _margin = margin
//            return this
//        }
//
//        /**
//         * ?????????????????????????????????????????????????????????
//         * @param color
//         * */
//        fun setColorBlack(color: Int): Builder {
//            _colorBlack = color
//            return this
//        }
//
//        /**
//         * ?????????????????????????????????????????????????????????
//         * @param color
//         * */
//        fun setColorWhite(color: Int): Builder {
//            _colorWhite = color
//            return this
//        }
//
//        /**
//         * ???????????????logo????????????????????????logo????????????????????????
//         * @param logo
//         * */
//        fun setLogoBitmap(logo: Bitmap): Builder {
//            this._logoBitmap = logo
//            return this
//        }
//
//        /**
//         * ??????logo???????????????????????????0~1???????????????logo?????????????????????????????????0.2???
//         * @param percent
//         * */
//        fun setLogoPercent(percent: Float): Builder {
//            this._logoPercent = percent
//            return this
//        }
//
//        /**
//         * ?????????????????????????????????
//         * @param listener
//         * */
//        fun setOnQRCreatorListener(listener: OnQRCreateListener): Builder {
//            this.onQRCreateListener = listener
//            return this
//        }
//
//        /**
//         * ?????????????????????????????????logo???bitmap??????????????????
//         * @param recycle
//         * */
//        fun setRecycleLogoBitmap(recycle: Boolean): Builder {
//            this._recycleLogoBitmap = recycle
//            return this
//        }
//
//        fun builder(): QRCreator {
//            checkParam()
//            return QRCreator(
//                content              =_content,
//                size                 =_size,
//                characterSet         = _charSet,
//                errorCorrectionLevel = _errorCorrectionLevel,
//                margin               = _margin,
//                colorBlack           = _colorBlack,
//                colorWhite           = _colorWhite,
//                logoBitmap           = _logoBitmap,
//                logoPercent          = _logoPercent,
//                recycle              = _recycleLogoBitmap,
//                listener             = onQRCreateListener
//            )
//        }
//
//        private fun checkParam() {
//            if (_content.isEmpty()) {
//                throw NullPointerException("Content is null")
//            }
//            if (_size < 50) {
//                _size = 50
//            }
//            if (_margin < 0) {
//                throw IllegalArgumentException("Margin can't be less 0 px")
//            }
//            if (_logoBitmap != null) {
//                if (_logoPercent == null) {
//                    _logoPercent = 0.2f
//                } else if (_logoPercent!! < 0.00000f || _logoPercent!! > 1.0000000f) {
//                    throw IllegalArgumentException("percent: 0 ~ 1")
//                }
//            }
//        }
//    }
//
//    fun createQRCodeBitmap() {
//        val observable = Observable.create<Bitmap> { emitter ->
//            val hints = Hashtable<EncodeHintType, String>()
//            hints[EncodeHintType.CHARACTER_SET] = charSet
//            hints[EncodeHintType.ERROR_CORRECTION] = errorCorrectionLevel.value
//            hints[EncodeHintType.MARGIN] = margin.toString()
//            val bitMatrix =
//                QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, size, size, hints)
//            val pixels = IntArray(size * size)
//            for (y in 0 until size) {
//                for (x in 0 until size) {
//                    pixels[y * size + x] =
//                        if (bitMatrix[x, y]) {
//                            colorBlack
//                        } else {
//                            colorWhite
//                        }
//                }
//            }
//            val srcBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888).apply {
//                setPixels(pixels, 0, size, 0, 0, size, size)
//            }
//            if (logoBitmap != null) {
//                val targetBitmap = addLogo(srcBitmap, logoBitmap!!, logoPercent!!)
//                if (targetBitmap != srcBitmap) {
//                    srcBitmap.recycle()
//                }
//                if (recycleLogoBitmap) {
//                    logoBitmap!!.recycle()
//                }
//                emitter.onNext(targetBitmap)
//            } else {
//                emitter.onNext(srcBitmap)
//            }
//        }
//        observable.subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe(
//                {
//                    onQRCreateListener?.onSuccess(it)
//                },{
//                    val error = it.toString()
//                    onQRCreateListener?.onFailure(error.substring(error.indexOf(':') + 1))
//                }
//            )
//    }
//
//    private fun addLogo(srcBitmap: Bitmap, logoBitmap: Bitmap, percent: Float): Bitmap {
//        val srcWidth = srcBitmap.width
//        val srcHeight = srcBitmap.height
//        val logoWidth = logoBitmap.width
//        val logoHeight = logoBitmap.height
//        val scaleWidth = srcWidth * percent / logoWidth
//        val scaleHeight = srcHeight * percent / logoHeight
//        if (logoWidth == 0 || logoHeight == 0) {
//            return srcBitmap
//        }
//        runCatching {
//            val bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888)
//            val canvas = Canvas(bitmap)
//            canvas.drawBitmap(srcBitmap, 0f, 0f, null)
//            canvas.scale(scaleWidth, scaleHeight, srcWidth / 2f, srcHeight / 2f)
//            canvas.drawBitmap(
//                logoBitmap,
//                (srcWidth - logoWidth) / 2f,
//                (srcHeight - logoHeight) / 2f,
//                null
//            )
////            canvas.save()
////            canvas.restore()
//            bitmap
//        }.onSuccess {
//            return it
//        }
//        return srcBitmap
//    }
//}