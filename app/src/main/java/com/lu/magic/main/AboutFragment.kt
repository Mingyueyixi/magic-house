package com.lu.magic.main

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lu.magic.AppInitProxy
import com.lu.magic.BuildConfig
import com.lu.magic.R
import com.lu.magic.databinding.FragmentAboutBinding
import com.lu.magic.main.dialog.PayDialog
import com.lu.magic.main.webview.WebViewActivity
import com.lu.magic.ui.BindingFragment
import com.lu.magic.util.CursorUtil
import com.lu.magic.util.SizeUtil
import com.lu.magic.util.ToastUtil
import com.lu.magic.util.log.LogUtil
import com.lu.magic.util.permission.PermissionUtil
import com.lu.magic.util.ripple.RectangleRippleBuilder
import com.lu.magic.util.ripple.RippleApplyUtil
import com.lu.magic.util.ripple.SimpleRippleUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URLEncoder
import kotlin.random.Random


class AboutFragment : BindingFragment<FragmentAboutBinding>() {
    private val vm by lazy { ViewModelProvider(this)[AboutViewModel::class.java] }
    override fun onViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentAboutBinding {
        return FragmentAboutBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewStyle()
        initViewAction()
    }

    private fun initViewAction() {
        binding.layoutAbout.setOnClickListener {
//            WebViewActivity.start(it.context, "http://192.168.3.116:5500/index.html")
            WebViewActivity.start(it.context, WebViewActivity.URL_LOCAL_ABOUT)
        }

        binding.layoutDonate.setOnClickListener {
            //支付宝个人收钱码，二维码内容
            val qrPersonLink = URLEncoder.encode("https://qr.alipay.com/tsx18437nsf7otyumo1gc2e", "UTF-8")
            // 指定扫码结果并跳过扫码
            val intent = Intent.parseUri(
                "intent://platformapi/startapp?saId=10000007&qrcode=$qrPersonLink"
                        + "#Intent;scheme=alipayqr;package=com.eg.android.AlipayGphone;end",
                Intent.URI_INTENT_SCHEME
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            try {
                startActivity(intent)
            } catch (e: android.content.ActivityNotFoundException) {
                LogUtil.e("支付宝跳转失败")
                showDonateDialog()
            }
        }

        vm.uiToastLive.observe(this.viewLifecycleOwner) {
            ToastUtil.show(it)
        }

    }


    private fun showDonateDialog() {
        val context = binding.root.context
        val isAlipayImg = Random.nextBoolean()
        val payImgResId = if (isAlipayImg) R.mipmap.ic_alipay_qr else R.mipmap.ic_wxpay_qr

        PayDialog.Builder(context)
            .setPayImgResId(payImgResId)
            .setQRIconClickListener{dialog,_->
                dialog.dismiss()
            }
            .setQRIconLongClickListener{dialog,_->
                dialog.dismiss()
                vm.savePayImg(context, payImgResId, isAlipayImg)
            }
            .setDownloadIconClickListener { dialog, _ ->
                dialog.dismiss()
                vm.savePayImg(context, payImgResId, isAlipayImg)
            }
            .show()

    }


    private fun initViewStyle() {
        val cardColor = ContextCompat.getColor(binding.root.context, com.lu.magic.base.R.color.purple_200)
        if (AppInitProxy.isActiveHookModule()) {
            binding.tvModuleStateMain.setText(com.lu.magic.base.R.string.module_have_active)
            binding.tvModuleStateMain.setTextColor(Color.WHITE)
            binding.tvModuleStateSub.setTextColor(Color.WHITE)
            //drawable 着色，可改变矢量图颜色。xml中的tint属性 //drawable?.setTint(Color.WHITE)
            binding.ivModuleStateIcon.foregroundTintList = ColorStateList.valueOf(Color.WHITE)
            binding.ivModuleStateIcon.setBackgroundResource(R.drawable.ic_icon_check)
            applyRippleDrawable(binding.layoutModuleState, cardColor)
        } else {
            binding.ivModuleStateIcon.setBackgroundResource(R.drawable.ic_icon_warning)
            binding.tvModuleStateMain.setText(com.lu.magic.base.R.string.module_not_active)
            applyRippleDrawable(binding.layoutModuleState, 0xFFF7C653.toInt())
        }
        applyRippleDrawable(binding.layoutAbout, cardColor)
        applyRippleDrawable(binding.layoutDonate, Color.TRANSPARENT)
        binding.tvModuleStateSub.text = "v${BuildConfig.VERSION_NAME}-${BuildConfig.BUILD_TYPE}"

    }

    private fun applyRippleDrawable(view: View, contentColor: Int) {
        var radius = view.layoutParams.height / 6
        if (radius == 0) {
            radius = SizeUtil.dp2px(resources, 100f).toInt() / 6
        }
        val rippleColor = SimpleRippleUtil.computeRippleColor(contentColor)
        RippleApplyUtil.apply(view, RectangleRippleBuilder(contentColor, rippleColor, radius))
    }


    override fun onResume() {
        super.onResume()
    }

    class AboutViewModel : ViewModel() {
        val uiToastLive by lazy { MutableLiveData<Int>() }
        private suspend fun savePayImgForQ(context: Context, payImgResId: Int, isAlipayImg: Boolean, fileName: String) {
            //mediaStore数据库返回的相对路径最后可能带有/，比较是否相等时需要注意
            val relativePath = "${Environment.DIRECTORY_DCIM}/magic"
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.RELATIVE_PATH, relativePath)
                put(MediaStore.MediaColumns.DATE_MODIFIED, (System.currentTimeMillis() / 1000).toInt())
            }
            // MediaStore定义了一系列的uri常量，但有些文件比较特殊，不在定义中，此时可使用MediaStore.Files.getContentUri来查询
            // MediaStore.Downloads.INTERNAL_CONTENT_URI、MediaStore.Downloads.EXTERNAL_CONTENT_URI
            // MediaStore.Files.getContentUri("external")
            // val uri = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val resolver = context.contentResolver
            val cursor = resolver.query(
                uri,
                //id和名称
                arrayOf(
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.RELATIVE_PATH
                ),
                //条件：显示名称和相对路径
                "${MediaStore.Images.Media.DISPLAY_NAME} =? AND (${MediaStore.Images.Media.RELATIVE_PATH} =? OR ${MediaStore.Images.Media.RELATIVE_PATH} =?)",
                arrayOf(fileName, relativePath, "$relativePath/"),
                "${MediaStore.Images.Media.DATE_MODIFIED} DESC"
            )
            val queryResult = cursor.use {
                CursorUtil.getAll(it)
            }

            if (!queryResult.isNullOrEmpty()) {
                //已经保存过，更新没毛用，删除吧
                //这两属性不许更新
                //contentValues.remove(MediaStore.MediaColumns.DISPLAY_NAME)
                //contentValues.remove(MediaStore.MediaColumns.RELATIVE_PATH)
                val _id = queryResult[0][MediaStore.Images.Media._ID].toString()
                val rowCount = resolver.delete(uri, "${MediaStore.Images.Media._ID}=?", arrayOf(_id))
                if (rowCount == 0) {
                    LogUtil.w("MediaStore删除失败！！")
                }
            }
            val imageUri = resolver.insert(uri, contentValues)
            imageUri?.let { uri ->
                val payImgByteArray = context.resources.openRawResource(payImgResId).readBytes()
                resolver.openOutputStream(uri, "w").use { out ->
                    out?.write(payImgByteArray)
                }
                contentValues.clear()
                resolver.update(uri, contentValues, null, null)
            }

        }

        fun savePayImg(context: Context, payImgResId: Int, isAlipayImg: Boolean) {
            val fileName = if (isAlipayImg) "alipay_qr.jpg" else "wxpay_qr.jpg"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                viewModelScope.launch {
                    withContext(Dispatchers.IO) {
                        runCatching {
                            savePayImgForQ(context, payImgResId, isAlipayImg, fileName)
                        }.onFailure {
                            uiToastLive.postValue(com.lu.magic.base.R.string.save_fail)
                            LogUtil.e(it)
                        }.onSuccess {
                            uiToastLive.postValue(com.lu.magic.base.R.string.save_success)
                        }
                    }
                }
            } else {
                PermissionUtil.permission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .onResult { code, result, grant ->
                        if (grant) {
                            viewModelScope.launch {
                                runCatching {
                                    val file =
                                        File("${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)}/magic")
                                    val payImgByteArray = context.resources.openRawResource(payImgResId).readBytes()
                                    file.writeBytes(payImgByteArray)
                                }.onFailure {
                                    uiToastLive.postValue(com.lu.magic.base.R.string.save_fail)
                                    LogUtil.e(it)
                                }.onSuccess {
                                    uiToastLive.postValue(com.lu.magic.base.R.string.save_success)
                                }
                            }
                        } else {
                            uiToastLive.postValue(com.lu.magic.base.R.string.save_fail)
                        }
                    }
                    .call()
            }

        }
    }
}
