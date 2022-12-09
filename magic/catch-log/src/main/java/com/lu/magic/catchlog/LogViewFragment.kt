package com.lu.magic.catchlog

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.lu.magic.catchlog.databinding.FragmentLogviewBinding
import com.lu.magic.ui.BindingFragment
import com.lu.magic.ui.recycler.MultiAdapter
import com.lu.magic.ui.recycler.MultiViewHolder
import com.lu.magic.ui.recycler.SimpleItemType
import com.lu.magic.util.SizeUtil
import com.lu.magic.util.dialog.DialogUtil
import com.lu.magic.util.log.LogUtil
import java.io.File


class LogViewFragment : BindingFragment<FragmentLogviewBinding>() {
    private var agreeRoot = false

    private val logviewAdapter by lazy {
        MultiAdapter<String>()
    }
    private val vm by lazy {
        ViewModelProvider(this)[LogViewModel::class.java]
    }
    private val mLinearLayoutManager by lazy {
        LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false).also {
            it.stackFromEnd = true
        }
    }

    companion object {
        private const val MAX_SIZE = 1080
    }

    override fun onViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentLogviewBinding {
        return FragmentLogviewBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dp100 = SizeUtil.dp2px(resources!!, 100F)
        binding.rvLogLayout.layoutManager = mLinearLayoutManager
        binding.rvLogLayout.addItemDecoration(LastItemMarginBottomDecoration(dp100.toInt()))
        binding.rvLogLayout.itemAnimator = null

        binding.rvLogLayout.adapter = logviewAdapter.addItemType(object : SimpleItemType<String>() {
            override fun createViewHolder(
                adapter: MultiAdapter<String>, parent: ViewGroup, viewType: Int
            ): MultiViewHolder<String> {
                return object : MultiViewHolder<String>(TextView(context)) {
                    var lastTextColor = 0xFFBBBBBB.toInt()
                    override fun onBindView(adapter: MultiAdapter<String>, itemModel: String, position: Int) {
                        val textView = itemView as TextView
                        adapter.getItem(layoutPosition)?.let {
                            textView.text = it
                            val m = Regex("\\s([VDIWE])\\s").find(it)
                            if (m != null) {
                                when (m.groupValues[1]) {
                                    "V", "D" -> 0xFFBBBBBB
                                    "I" -> 0xFF1BB19D
                                    "W" -> 0xFFBBB529
                                    "E", "A" -> 0xFFFF6B68
                                    else -> lastTextColor
                                }.toInt().let {
                                    textView.setTextColor(it)
                                    lastTextColor = it
                                }
                            } else {
                                textView.setTextColor(lastTextColor)
                            }
                        }

                    }
                }
            }
        })

        binding.tvLogPath.text = vm.genLogTextPath()

        vm.updateLogLive.observe(this.viewLifecycleOwner) {
            updateLogView()
        }
        binding.tvLogPath.setOnClickListener {
            runCatching {
                val file = File(vm.logTextPath)
                val intent = Intent(Intent.ACTION_VIEW)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
                    intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    val uri = FileProvider.getUriForFile(it.context, "com.lu.magic.fileprovider", file)
                    intent.setDataAndType(uri, "text/directory")
                } else {
                    val uri = Uri.fromFile(file)
                    intent.setDataAndType(uri, "text/directory")
                }
                if (intent.resolveActivity(it.context.packageManager) != null) {
                    startActivity(intent)
                }

            }.onFailure {
                LogUtil.e(it)
            }
        }
        binding.btnStartLogcat.setOnClickListener {
            if (!agreeRoot) {
                DialogUtil.buildAlertDialog(it.context)
                    .setMessage(R.string.request_root_for_logcat_des)
                    .setNegativeButton(com.lu.magic.base.R.string.agree) { dialog, whick ->
                        agreeRoot = true
                        clickStartLogcatAction()
                    }
                    .setNeutralButton(com.lu.magic.base.R.string.cancel) { dialog, which ->
                        dialog.cancel()
                    }
                    .setOnCancelListener {
                        agreeRoot = false
                    }.show()
            } else {
                clickStartLogcatAction()
            }
        }
    }

    private fun clickStartLogcatAction() {
        if (vm.alive) {
            if (vm.pauseReadLog) {
                //恢复
                vm.pauseReadLog = false
                binding.btnStartLogcat.setText(com.lu.magic.base.R.string.pause)
            } else {
                //去暂停
                vm.pauseReadLog = true
                binding.btnStartLogcat.setText(com.lu.magic.base.R.string.start)
            }

        } else {
            //去启动
            vm.startLogcat()
            binding.btnStartLogcat.postDelayed({
                vm.startLoadLog()
                binding.btnStartLogcat.setText(com.lu.magic.base.R.string.pause)
            }, 500)
        }
    }

    private fun updateLogView() {
        if (binding.rvLogLayout.isInLayout || binding.rvLogLayout.isComputingLayout) {
            return
        }
        kotlin.runCatching {
            val data = logviewAdapter.getData()
            synchronized(data) {
                //cacheLines存在多线程操作/访问，需要同步，否则，因size不同步计算错误崩溃，同步修改崩溃
                val onLastBottom = mLinearLayoutManager.findLastVisibleItemPosition() == data.lastIndex
                synchronized(vm.cacheLines) {
                    if (vm.cacheLines.size >= MAX_SIZE) {
                        //大于最大数量，必须刷新
                        val subList = vm.cacheLines.subList(vm.cacheLines.size - MAX_SIZE, vm.cacheLines.lastIndex)
                        logviewAdapter.setData(subList.toMutableList())
                        logviewAdapter.notifyItemRangeChanged(0, subList.size)
                        vm.cacheLines.clear()
                    } else if (onLastBottom) {
                        //在底部，虽然不大于最大数量，也刷新
                        // max{vm.cacheLines.size}.value == 107
                        if (vm.cacheLines.size + data.size >= MAX_SIZE) {
                            //原数据取最后几个 + vm.cacheLines.size == TOTAL
                            val retainSize = (MAX_SIZE - vm.cacheLines.size)
                            val mergeList = data.subList(data.size - retainSize, data.lastIndex).also {
                                it.addAll(vm.cacheLines)
                            }.toMutableList()
                            //addAll 的类型是suList 同步修改异常，崩掉
                            logviewAdapter.setData(mergeList)
                            logviewAdapter.notifyItemRangeChanged(0, data.size)
                        } else {
                            val positionStart = data.lastIndex
                            val appendData = vm.cacheLines.toMutableList()
                            logviewAdapter.addData(appendData)
                            logviewAdapter.notifyItemRangeChanged(positionStart, appendData.size)
                        }
                        vm.cacheLines.clear()
                    } else {
                        //不在底部、数量也也小于最大数量
                        //忽略
                    }
                    if (onLastBottom) {
                        binding.rvLogLayout.smoothScrollToPosition(logviewAdapter.itemCount - 1)
                    }

                }
            }
        }.onFailure {
            LogUtil.e(it)
        }

    }

}